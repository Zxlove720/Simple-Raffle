package com.wzb.server.impl;

import com.wzb.pojo.entity.Prize;
import com.wzb.server.ShopService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    @Override
    public List<Prize> showAllPrize() {
        return List.of();
    }

    @Override
    public void addPrize(Prize prize) {

    }

    @Override
    public void updatePrize(Prize prize) {

    }

}
