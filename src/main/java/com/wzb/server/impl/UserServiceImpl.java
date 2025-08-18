package com.wzb.server.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wzb.mapper.UserMapper;
import com.wzb.pojo.dto.UserLoginDTO;
import com.wzb.pojo.entity.User;
import com.wzb.server.UserService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    // 需要服务端请求的微信服务的接口地址
    public static final String WECHAT_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
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
        // 2.1如果是新用户，那么自动完成注册，将其保存到数据库
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .status(0)
                    .nickname("用户" + RandomUtil.randomString(5))
                    .chance(0)
                    .prizes(new ArrayList<>())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            // 2.2自动注册
            userMapper.insert(user);
        }
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
        map.put("appid", "wxf248bb3cbecdf8b0");
        // 封装小程序对应的密钥
        map.put("secret", "e1476dfdb10be16941a4010e4c94d435");
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
            URIBuilder builder = new URIBuilder(WECHAT_LOGIN);
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

}
