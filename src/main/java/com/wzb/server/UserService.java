package com.wzb.server;

import com.wzb.pojo.dto.UserLoginDTO;
import com.wzb.pojo.entity.User;
import com.wzb.pojo.vo.PrizeVO;

import java.util.List;

public interface UserService {

    String login(UserLoginDTO userLoginDTO);

    List<PrizeVO> showPrize();

}
