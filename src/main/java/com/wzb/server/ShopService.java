package com.wzb.server;

import com.wzb.pojo.entity.Prize;

import java.util.List;

public interface ShopService {

    List<Prize> showAllPrize();

    void addPrize(Prize prize);

    void updatePrize(Prize prize);

}
