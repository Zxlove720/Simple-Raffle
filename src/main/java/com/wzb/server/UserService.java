package com.wzb.server;

import com.wzb.pojo.dto.UserLoginDTO;
import com.wzb.pojo.entity.User;

public interface UserService {

    User login(UserLoginDTO userLoginDTO);

}
