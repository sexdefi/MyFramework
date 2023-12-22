package com.ruoyi.project.bussiness.controller;

import com.ruoyi.common.utils.CacheUtils;
import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.service.ConfigService;
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
import com.ruoyi.project.bussiness.service.GasGiftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/GasGift2")
@ApiOperation(value = "GasGift2", notes = "个人领gas")
@Api(value = "GasGift2", tags = {"GasGift2"})
public class GasGift2Controller {
    @Resource
    private RestTemplate restTemplate;

    @Autowired
    BusConfigService config;

    @PostMapping("/stake")
    @ResponseBody
    @ApiOperation(value = "stake", notes = "质押")
    public AjaxResult stake(@RequestBody GasParams params) {
        try{
            String serverUrl = config.getConfig("GAS2_API", "http://localhost:8091/GasGift/");
            String url = serverUrl + "stake";
            AjaxResult ajaxResult = restTemplate.postForObject(url, params, AjaxResult.class);
            return ajaxResult;
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("质押失败");
        }
    }

    @PostMapping("/withdraw")
    @ResponseBody
    @ApiOperation(value = "withdraw", notes = "解除质押")
    public AjaxResult withdraw(@RequestBody GasParamsLite params) {
        try{
            String serverUrl = config.getConfig("GAS2_API", "http://localhost:8091/GasGift/");
            String url = serverUrl + "withdraw";
            AjaxResult ajaxResult = restTemplate.postForObject(url, params, AjaxResult.class);
            return ajaxResult;
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("解除质押失败");
        }
    }

    @PostMapping("/getGasAmount")
    @ResponseBody
    @ApiOperation(value = "getGasAmount", notes = "获取待领取gas余额")
    public AjaxResult getGasAmount(@RequestBody GasParamsLite params) {
        try{
            String serverUrl = config.getConfig("GAS2_API", "http://localhost:8091/GasGift/");
            String url = serverUrl + "getGasAmount";
            AjaxResult ajaxResult = restTemplate.postForObject(url, params, AjaxResult.class);
            return ajaxResult;
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("获取待领取gas余额失败");
        }
    }

    // 查询master里面，所有用户及最新操作时间
    @PostMapping("/getAllRemainGas")
    @ResponseBody
    @ApiOperation(value = "getAllRemainGas", notes = "获取所有待领取gas余额")
    public String getAllRemainGas(){
        try{
            String serverUrl = config.getConfig("GAS2_API", "http://localhost:8091/GasGift/");
            String url = serverUrl + "getAllRemainGas";
            String ajaxResult = restTemplate.getForObject(url, String.class);
            return ajaxResult;
        } catch (Exception e) {
            e.printStackTrace();
            return "获取所有待领取gas余额失败";
        }
    }



    @PostMapping("/withdrawGasByAddress")
    @ResponseBody
    @ApiOperation(value = "withdrawGasByAddress", notes = "根据地址领取gas")
    public AjaxResult withdrawGasByAddress(@RequestBody GasParamsLite params) {
        try{
            String serverUrl = config.getConfig("GAS2_API", "http://localhost:8091/GasGift/");
            String url = serverUrl + "withdrawGasByAddress";
            AjaxResult ajaxResult = restTemplate.postForObject(url, params, AjaxResult.class);
            return ajaxResult;
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("根据地址领取gas失败");
        }
    }


    @PostMapping("/gasLog")
    @ResponseBody
    @ApiOperation(value = "gasLog", notes = "gas领取记录")
    public AjaxResult gasLog(@RequestBody GasParamsLite params) {
        try{
            String serverUrl = config.getConfig("GAS2_API", "http://localhost:8091/GasGift/");
            String url = serverUrl + "gasLog";
            AjaxResult ajaxResult = restTemplate.postForObject(url, params, AjaxResult.class);
            return ajaxResult;
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("gas领取记录失败");
        }
    }

    // 质押记录，只检索最近50条
    @PostMapping("/stakeLog")
    @ResponseBody
    @ApiOperation(value = "stakeLog", notes = "质押记录")
    public AjaxResult stakeLog(@RequestBody GasParamsLite params) {
        try{
            String serverUrl = config.getConfig("GAS2_API", "http://localhost:8091/GasGift/");
            String url = serverUrl + "stakeLog";
            AjaxResult ajaxResult = restTemplate.postForObject(url, params, AjaxResult.class);
            return ajaxResult;
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("质押记录失败");
        }
    }
}
