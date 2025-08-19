package com.wzb.server.impl;

import com.wzb.mapper.ShopMapper;
import com.wzb.pojo.entity.Prize;
import com.wzb.server.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    public final ShopMapper shopMapper;

    @Autowired
    public ShopServiceImpl(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    @Override
    public List<Prize> showAllPrize() {
        return shopMapper.getAllPrize();
    }

    @Override
    public void addPrize(Prize prize) {
        shopMapper.addPrize(prize);
    }

    @Override
    public void updatePrize(Prize prize) {
        shopMapper.updatePrize(prize);
    }

}
