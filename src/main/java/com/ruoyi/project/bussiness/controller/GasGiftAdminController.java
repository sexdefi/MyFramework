package com.ruoyi.project.bussiness.controller;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.Utils.SignValiditor;
import com.ruoyi.project.bus.operate.domain.GasOperateLog;
import com.ruoyi.project.bus.operate.service.IGasOperateLogService;
import com.ruoyi.project.bus.order.domain.GasWithdrawLog;
import com.ruoyi.project.bus.order.service.IGasWithdrawLogService;
import com.ruoyi.project.bus.transferLog.domain.GasTransferLog;
import com.ruoyi.project.bus.transferLog.service.IGasTransferLogService;
import com.ruoyi.project.bussiness.common.BusConfigService;
import com.ruoyi.project.bussiness.entity.GasParams;
import com.ruoyi.project.bussiness.entity.GasParamsLite;
import com.ruoyi.project.bussiness.entity.OperateLogVO;
import com.ruoyi.project.bussiness.entity.TransferLogVO;
import com.ruoyi.project.bussiness.service.BussService;
import com.ruoyi.project.bussiness.service.GasGiftService;
import com.ruoyi.project.swap.ad.domain.AdConfig;
import com.ruoyi.project.swap.ad.service.IAdConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/GasGiftAdmin")
@ApiOperation(value = "GasGiftAdmin", notes = "个人领gas管理员")
@Api(value = "GasGiftAdmin", tags = {"GasGiftAdmin"})
@DataSource(value = DataSourceType.MASTER)
public class GasGiftAdminController {
    @Autowired
    GasGiftService gasService;

    @Autowired
    IGasOperateLogService gasOperateLogService;

    @Autowired
    BusConfigService config;

    @Autowired
    IGasTransferLogService gasTransferLogService;

    @Autowired
    IGasWithdrawLogService gasWithdrawLogService;

    @Autowired
    BussService bussService;

    @Autowired
    IAdConfigService adConfigService;

    @PostMapping("/crypto")
    @ResponseBody
    @ApiOperation(value = "crypto", notes = "加密")
    public AjaxResult crypto(String str){
        return AjaxResult.success(gasService.encode(str));
    }

    @PostMapping("/stake")
    @ResponseBody
    @ApiOperation(value = "stake", notes = "质押")
    public AjaxResult stake(String address, String hash, String timestamp) {

        // 如果他的状态是stake或gas，则直接返回
        GasOperateLog last = new GasOperateLog();
        last.setUserAddr(address);
        GasOperateLog log1 = gasOperateLogService.selectGasOperateLast(last);
        if (log1 != null && log1.getType().equals("gas")) {
            return AjaxResult.success("质押成功");
        }else if(log1 != null && (log1.getType().equals("stake"))){
            // 修改质押时间
            last.setOptime(timestamp);
            gasOperateLogService.updateGasOperateLog(last);
            return AjaxResult.success("质押成功");
        }
        // 如果成功，则记录质押记录，插入gas_operate_log表
        GasOperateLog log = new GasOperateLog();
        log.setUserAddr(address);
        log.setAmount("100");
        log.setType("stake");
        log.setOptime(timestamp);
        log.setRemark(hash);
        gasOperateLogService.insertGasOperateLog(log);

        return AjaxResult.success("手动质押成功");
    }


    @PostMapping("/withdraw")
    @ResponseBody
    @ApiOperation(value = "withdraw", notes = "解除质押")
    public AjaxResult withdraw(String address) {

        GasOperateLog log = new GasOperateLog();
        log.setUserAddr(address);
        log.setType("withdraw");
        log.setOptime(String.valueOf(new Date().getTime() / 1000));
        log.setAmount("0");
        int i = gasOperateLogService.insertGasOperateLog(log);
        if (i > 0) {
            return AjaxResult.success("手动解除质押成功");
        } else {
            return AjaxResult.error("手动解除质押失败");
        }
    }
//
//    @PostMapping("/transfer")
//    @ResponseBody
//    @ApiOperation(value = "transfer", notes = "转账")
//    public AjaxResult transfer(String address, String amount){
//        BigDecimal bigDecimal = new BigDecimal(amount);
//        String s = gasService.ethTransfer(address, bigDecimal);
//        if(s == null){
//            return AjaxResult.error("转账失败");
//        }
//        return AjaxResult.success("转账成功");
//    }
//
//    @PostMapping("/airdrop")
//    @ResponseBody
//    @ApiOperation(value = "airdrop", notes = "空投")
//    public AjaxResult airdrop(String airdroplist){
//        String s = bussService.airdropForList(airdroplist);
//        if(s == null){
//            return AjaxResult.error("空投失败");
//        }
//        return AjaxResult.success(s);
//    }


    // 插入订单
    public boolean insertOrder(String useraddr, String amount, String orderid) {
        GasWithdrawLog gasWithdrawLog = new GasWithdrawLog();
        gasWithdrawLog.setUserAddr(useraddr);
        gasWithdrawLog.setAmount(amount);
        gasWithdrawLog.setOptime(String.valueOf(new Date().getTime() / 1000));
        gasWithdrawLog.setTxhash("new");
        gasWithdrawLog.setRemark(orderid);
        int i = gasWithdrawLogService.insertGasWithdrawLog(gasWithdrawLog);
        return i > 0;
    }


    //用这个json生成数据库create语句
    //{"code":0,"msg":"ok","data":[{"id":8,"title":"Cloak","des":"审核资料","jumpUrl":"https://swap.maplesales.org/swap","image":"https://statics.maplesales.org/s3/maplesale/swap.jpg","classification":"swap","weights":0,"dataStatus":1,"createTime":1661416510000,"updateTime":1661416513000},{"id":9,"title":"Cloak","des":"审核资料","jumpUrl":"https://www.maplesales.org/","image":"https://dhzzgq6xv1wux.cloudfront.net/s3/maplesale/maplev2.jpg","classification":"swap","weights":0,"dataStatus":1,"createTime":1661416510000,"updateTime":1661416513000}]}
    //mysql语句如下：
    //CREATE TABLE `ad_config` (
    //  `id` int(11) NOT NULL AUTO_INCREMENT,
    //  `title` varchar(255) DEFAULT NULL,
    //  `des` varchar(255) DEFAULT NULL,
    //  `jump_url` varchar(255) DEFAULT NULL,
    //  `image` varchar(255) DEFAULT NULL,
    //  `classification` varchar(255) DEFAULT NULL,
    //  `weights` int(11) DEFAULT NULL,
    //  `data_status` int(11) DEFAULT NULL,
    //  `create_time` datetime DEFAULT NULL,
    //  `update_time` datetime DEFAULT NULL,
    //  PRIMARY KEY (`id`)
    //) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;
    @GetMapping("/adlist")
    @ResponseBody
    @ApiOperation(value = "adlist", notes = "广告列表")
    public AjaxResult adlist(@RequestParam String classification) {
        AdConfig adConfig = new AdConfig();
        adConfig.setClassification(classification);
        adConfig.setDataStatus(1l);
        List<AdConfig> adConfigs = adConfigService.selectAdConfigList(adConfig);
        return AjaxResult.success(adConfigs);
    }
}
