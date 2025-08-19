package com.wzb.server.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wzb.constant.RedisConstant;
import com.wzb.constant.WechatConstant;
import com.wzb.mapper.UserMapper;
import com.wzb.pojo.dto.UserLoginDTO;
import com.wzb.pojo.entity.User;
import com.wzb.properties.WechatProperties;
import com.wzb.server.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final WechatProperties wechatProperties;

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, WechatProperties wechatProperties, StringRedisTemplate stringRedisTemplate) {
        this.userMapper = userMapper;
        this.wechatProperties = wechatProperties;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录DTO
     * @return User实体
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        // 1.通过封装的getOpenid函数获得用户的openid
        String openid = getOpenid(userLoginDTO.getCode());
        // 1.1判断是否成功获得openid，若openid为空则登录失败，抛出异常
        if (openid == null) {
            throw new RuntimeException("登录失败");
        }
        // 2.判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);
        if (user == null) {
            // 2.1如果是新用户，那么自动完成注册，将其保存到数据库
            user = register(openid);
        }
        String token = UUID.randomUUID(false).toString();
        Map<String, Object> userMap = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions
                        .create()
                        .ignoreNullValue()
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        stringRedisTemplate.opsForHash().putAll(RedisConstant.USER_LOGIN_KEY + token, userMap);
        stringRedisTemplate.expire(RedisConstant.USER_LOGIN_KEY + token, RedisConstant.USER_LOGIN_TTL, TimeUnit.HOURS);
        // 3返回该用户
        return user;
    }


    /**
     * 调用微信的接口服务，获取微信用户的openid
     *
     * @param code 登录授权码
     * @return 用户openid
     */
    private String getOpenid(String code) {
        // 这个hashMap是为了存储向微信服务器发送请求时需要的参数，就是请求参数，封装成map
        Map<String, String> map = new HashMap<>();
        // 封装小程序的appid
        map.put("appid", wechatProperties.getAppid());
        // 封装小程序对应的密钥
        map.put("secret", wechatProperties.getSecret());
        // 封装小程序的临时登录凭证
        map.put("js_code", code);
        // 封装授权类型，这里的授权类型是：表示通过使用授权码（临时登录凭证）获取openid    
        map.put("grant_type", "authorization_code");
        // 这是微信接口返回的json格式数据，其中包含了openid
        String json = doGet(map);
        // 用JSON处理工具将字符串形式的json数据转换为JsonObject对象，便于操作
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("openid");
    }

    private static String doGet(Map<String, String> paramMap) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(WechatConstant.WECHAT_LOGIN);
            if (paramMap != null) {
                for (String key : paramMap.keySet()) {
                    builder.addParameter(key, paramMap.get(key));
                }
            }
            URI uri = builder.build();
            //创建GET请求
            HttpGet httpGet = new HttpGet(uri);
            //发送请求
            response = httpClient.execute(httpGet);
            //判断响应状态
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 新用户注册
     *
     * @param openid openid
     * @return User实体
     */
    private User register(String openid) {
        User user = User.builder()
                .openid(openid)
                .status(0)
                .nickname("用户" + RandomUtil.randomString(5))
                .chance(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        // 2.2自动注册
        userMapper.insert(user);
        return user;
    }

}
