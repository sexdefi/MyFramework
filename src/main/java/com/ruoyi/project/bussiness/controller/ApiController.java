package com.ruoyi.project.bussiness.controller;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.bus.coinlist.domain.RateCoin;
import com.ruoyi.project.bus.coinlist.service.IRateCoinService;
import com.ruoyi.project.bussiness.common.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@Controller
@ApiOperation(value = "api接口", tags = {"api接口"})
@RequestMapping("/GasGift")
@DataSource(value = DataSourceType.MASTER)
public class ApiController {
    @Autowired
    IRateCoinService rateCoinService;

    // 内存化改造

    @ApiOperation(value = "上报币种", notes = "上报币种")
    @GetMapping(value = "/report")
    public Result report(@Param("coinName") String coinName, @Param("tokenAddress") String tokenAddress, @RequestHeader(name = "chainType",defaultValue = "bsc") String chainType) {
        coinName = coinName.toLowerCase();
        tokenAddress = tokenAddress.toLowerCase();

        RateCoin coin = new RateCoin();
        coin.setTokenAddress(tokenAddress);
        coin.setChainType(chainType);
        List<RateCoin> rateCoins = rateCoinService.selectRateCoinList(coin);
        // 如果已经存在，直接返回，否则插入
        if (rateCoins.size() > 0) {
            return Result.ok();
        }
        coin.setCoinName(coinName);
        coin.setDataStatus(1);
        coin.setCreateTime(new java.util.Date());
        coin.setUpdateTime(new java.util.Date());
        rateCoinService.insertRateCoin(coin);
        return Result.ok();
    }

    @ApiOperation(value = "查询币种", notes = "查询币种")
    @GetMapping(value = "/ratecoin")
    public Result ratecoin(@Param("tokenAddress") String tokenAddress, @RequestHeader("chainType") String chainType) {
        RateCoin coin = new RateCoin();
        if(chainType == null || chainType.equals("")){
            chainType = "bsc";
        }
        if(tokenAddress == null || tokenAddress.equals("")){

        }else{
            tokenAddress = tokenAddress.toLowerCase();
            coin.setTokenAddress(tokenAddress);
        }
        coin.setChainType(chainType);
        List<RateCoin> rateCoinList = rateCoinService.selectRateCoinList(coin);

        HashMap map = new HashMap();
        map.put("rateCoin", 0);
        if (rateCoinList != null && rateCoinList.size() > 0) {
            map.put("rateCoin", 1);
        }
        map.put("list", rateCoinList);

        return Result.ok(map);
    }

    @ApiOperation(value = "删除币种", notes = "删除币种")
    @RequestMapping(value = "/ratecoin/delete", method = RequestMethod.DELETE)
    public Result deleteRatecoin(@Param("tokenAddress") String tokenAddress, @RequestHeader("chainType") String chainType) {
        tokenAddress = tokenAddress.toLowerCase();
        RateCoin coin = new RateCoin();
        coin.setTokenAddress(tokenAddress);
        coin.setChainType(chainType);
        List<RateCoin> rateCoins = rateCoinService.selectRateCoinList(coin);
        if(rateCoins.size() == 0){
            return Result.ok();
        }
        Long id = rateCoins.get(0).getId();
        rateCoinService.deleteRateCoinById(id);
        return Result.ok();
    }
}
