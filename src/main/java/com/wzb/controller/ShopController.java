package com.wzb.controller;

import com.wzb.pojo.entity.Prize;
import com.wzb.pojo.entity.Result;
import com.wzb.server.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 店家Controller
 */
@Slf4j
@RestController
@RequestMapping("/shop")
public class ShopController {

    public final ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * 查询所有奖品
     *
     * @return List<Prize>奖品列表
     */
    @PostMapping("/shop")
    public Result<List<Prize>> showAllPrize() {
        List<Prize> prizeList = shopService.showAllPrize();
        return Result.success(prizeList);
    }

    /**
     * 添加奖品
     *
     * @param prize 奖品类
     */
    @PostMapping("/add")
    public Result<Void> addPrize(Prize prize) {
        shopService.addPrize(prize);
        return Result.success();
    }

    /**
     * 更新奖品
     *
     * @param prize 奖品类
     */
    @PostMapping("/update")
    public Result<Void> updatePrize(Prize prize) {
        shopService.updatePrize(prize);
        return Result.success();
    }

}
