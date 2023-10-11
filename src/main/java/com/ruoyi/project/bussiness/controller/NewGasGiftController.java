package com.ruoyi.project.bussiness.controller;

import com.ruoyi.common.utils.CacheUtils;
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
import com.ruoyi.project.bussiness.entity.*;
import com.ruoyi.project.bussiness.service.GasGiftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/GasGift")
@ApiOperation(value = "GasGift", notes = "个人领gas")
@Api(value = "GasGift", tags = {"GasGift"})
@DataSource(value = DataSourceType.MASTER)
public class NewGasGiftController {
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

    public static final String nonce = "419";


    @PostMapping("/stake")
    @ResponseBody
    @ApiOperation(value = "stake", notes = "质押")
    public AjaxResult stake(@RequestBody NewGasParams params) {
        String address = params.getAddress();
        String sign = params.getSign();
        String hash = params.getHash();
        String chainId = params.getChainId();
        String token = params.getToken();
        if(chainId == null || chainId.isEmpty()) {
            chainId = "198";
        }
        // 关键参数校验
        if (address == null || sign == null || hash == null || address.isEmpty() || sign.isEmpty() || hash.isEmpty() || token == null || token.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        // 如果关键参数为空，也返回false
        if (!signCheck(address, sign)) {
            return AjaxResult.error("签名错误");
        }

        // 如果他的状态是stake或gas，则直接返回
        GasOperateLog last = new GasOperateLog();
        last.setUserAddr(address);
        last.setToken(token);
        last.setChainid(chainId);
        GasOperateLog log1 = gasOperateLogService.selectGasOperateLast(last);
        if (log1 != null && (log1.getType().equals("stake") || log1.getType().equals("gas"))) {
            return AjaxResult.success("质押成功");
        }

        String stakeAmount = getBaseAmount(token);

        // 校验hash是否存在
        // 开启新线程，调用getStakeUser方法，获取链上数据，如果成功，则返回true，否则返回false。传递参数有：address、hash，如果成功则插入gas_operate_log表
        String finalChainId = chainId;
        new Thread(() -> {
            // 等待5秒，等待链上数据确认
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                boolean stakeUser = gasService.getStakeUser(address, token);
                if (stakeUser) {
                    // 如果成功，则记录质押记录，插入gas_operate_log表
                    GasOperateLog log = new GasOperateLog();
                    log.setUserAddr(address);
                    log.setAmount(stakeAmount);
                    log.setType("stake");
                    log.setOptime(String.valueOf(new Date().getTime() / 1000));
                    log.setRemark(hash);
                    log.setToken(token);
                    log.setChainid(finalChainId);
                    gasOperateLogService.insertGasOperateLog(log);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return AjaxResult.success("质押成功，等待链上数据确认");
    }

    private String getBaseAmount(String token) {
        // 判断token是否等于BRC或X10000，如果是，则返回对应的质押数量

        String tokenKey = "stakeAmount_" + token; // 例如：stakeAmount_BRC，stakeAmount_X10000
        String stakeAmount = config.getConfig(tokenKey, "998877");
        return stakeAmount;
    }


    @PostMapping("/withdraw")
    @ResponseBody
    @ApiOperation(value = "withdraw", notes = "解除质押")
    public AjaxResult withdraw(@RequestBody NewGasParamsLite params) {
        // 校验关键数据是否存在或为空
        String address = params.getAddress();
        String sign = params.getSign();
        String token = params.getToken();
        String chainId = params.getChainId();
        if(chainId == null || chainId.isEmpty()) {
            chainId = "198";
        }
        if (address == null || sign == null || address.isEmpty() || sign.isEmpty() || token == null || token.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        // 校验sign是否通过
        if (!signCheck(address, sign)) {
            return AjaxResult.error("签名错误");
        }
        String stakeAmount = getBaseAmount(token);
        // 设置缓存为0
        addBalanceCache(address, token, "0");
        // 此处没有判断是否已经质押了，因为提取就是个标志位，不需要判断是否已经质押，否则会出现链上数据和链下不一致的情况
        // 检测gas是否残留，或者如果不领取就接触质押，则gas清零
        GasOperateLog log = new GasOperateLog();
        log.setUserAddr(address);
        log.setType("withdraw");
        log.setOptime(String.valueOf(new Date().getTime() / 1000));
        log.setAmount(stakeAmount);
        log.setToken(token);
        log.setChainid(chainId);
        int i = gasOperateLogService.insertGasOperateLog(log);
        if (i > 0) {
            return AjaxResult.success("解除质押成功");
        } else {
            return AjaxResult.error("解除质押失败");
        }
    }

    @PostMapping("/getGasAmount")
    @ResponseBody
    @ApiOperation(value = "getGasAmount", notes = "获取待领取gas余额")
    public AjaxResult getGasAmount(@RequestBody NewGasParamsLite params) {
        String token = params.getToken();
        String address = params.getAddress();
        String chainId = params.getChainId();
        if(chainId == null || chainId.isEmpty()) {
            chainId = "198";
        }
        // 检查地址合法性
        if (address == null || address.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        String balanceCache = getBalanceCache(params.getAddress(), token);
        if (balanceCache != null) {
            return AjaxResult.success("获取成功", balanceCache);
        }
        String gasAmount = _getGasAmount(params, false);
        addBalanceCache(params.getAddress(), token, gasAmount);
        return AjaxResult.success("获取成功", gasAmount);
    }


    // 查询master里面，所有用户及最新操作时间
    @PostMapping("/getAllRemainGas")
    @ResponseBody
    @ApiOperation(value = "getAllRemainGas", notes = "获取所有待领取gas余额")
    public String getAllRemainGas(String tokenName) {
        // 查询所有用户地址，及最新操作时间
        List<String> addresses = gasOperateLogService.selectAllUser();
        // 循环调用_getGasAmount方法，获取gas余额
        BigDecimal total = new BigDecimal(0);
        StringBuffer sb = new StringBuffer();
        for (String address : addresses) {
            NewGasParamsLite params = new NewGasParamsLite();
            params.setAddress(address);
            params.setToken(tokenName);
            String gasAmount = _getGasAmount(params, false);
            if (gasAmount == null || gasAmount.isEmpty()) {
                gasAmount = "0";
            }
            total = total.add(new BigDecimal(gasAmount));
            sb.append(address).append(" ").append(gasAmount).append("\n");
        }

        sb.append("total:").append(total.toString());
        // 拼装stringbuffer，返回，按行换行

        return sb.toString();
    }


    public String _getGasAmount(@RequestBody NewGasParamsLite params, boolean isCheck) {
        // 从数据库中读取gas余额
        // 检索上次的操作，进行区别处理。存入缓存中。缓存有效期为10分钟，如果不存在，则从数据库中读取
        String address = params.getAddress();
        if (address == null || address.isEmpty()) {
            return "0";
        }
        if (!addressCheck(address)) {
            return "0";
        }
        // 开启时间，如果当前时间小于开启时间，则返回0
        long openTime = config.getConfig("GAS_GIFT_OPEN_TIME", 1696327200l);
        if (new Date().getTime() / 1000 < openTime) {
            return "0";
        }
        String token = params.getToken();
        if (token == null || token.isEmpty()) {
            return "0";
        }

        GasOperateLog log = new GasOperateLog();
        log.setUserAddr(address);
        log.setToken(token);
        GasOperateLog gasOperateLog = gasOperateLogService.selectGasOperateLast(log);
        if (gasOperateLog == null) {
            return "0";
        }
        String lastOperation = gasOperateLog.getType();
        String lastTime = gasOperateLog.getOptime();
        // 异常处理
        if (lastOperation == null || lastTime == null || lastOperation.isEmpty() || lastTime.isEmpty()) {
            return "0";
        }
        Long lastTimeLong = 0l;
        try {
            lastTimeLong = Long.valueOf(lastTime);
            // 如果间隔小于1小时，则返回错误
            if (isCheck)
                if (System.currentTimeMillis() / 1000 - lastTimeLong < 3600) {
                    return "0";
                }
        } catch (Exception e) {
            lastTimeLong = 99999999999l;
        }
        // if (isCheck)
        {
            if (lastOperation.equals("withdraw")) {
                return "0";
            }
            if (!lastOperation.equals("stake") && !lastOperation.equals("gas")) {
                return "0";
            }
        }

        String gasAmount = gasService.getGasAmount(address, token, lastTimeLong);
        return gasAmount;
    }


    @PostMapping("/withdrawGasByAddress")
    @ResponseBody
    @ApiOperation(value = "withdrawGasByAddress", notes = "根据地址领取gas")
    public AjaxResult withdrawGasByAddress(@RequestBody NewGasParamsLite params) {
        String address = params.getAddress();
        String sign = params.getSign();
        String tokenName = params.getToken();
        String chainId = params.getChainId();
        if (address == null || sign == null || address.isEmpty() || sign.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        if(chainId == null || chainId.isEmpty()) {
            chainId = "198";
        }

        // 校验sign是否通过
        if (!signCheck(address, sign)) {
            return AjaxResult.error("签名错误");
        }

        // 移除缓存
        String gasAmount = _getGasAmount(params, true);
        if (gasAmount == null || gasAmount.isEmpty() || gasAmount.equals("0")) {
            return AjaxResult.error("可提取gas余额不足，或者提取间隔小于1小时");
        }
        // 设置缓存为0
        addBalanceCache(address, tokenName, "0");
        // 生成订单号，并将提现记录插入提现表
        String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
        GasOperateLog gasOperateLog1 = new GasOperateLog();
        gasOperateLog1.setRemark(orderNo);
        gasOperateLog1.setUserAddr(address);
        gasOperateLog1.setOptime(String.valueOf(System.currentTimeMillis() / 1000));
        gasOperateLog1.setType("gas");
        gasOperateLog1.setAmount(gasAmount);
        gasOperateLog1.setToken(tokenName);
        gasOperateLog1.setChainid(chainId);
        int i = gasOperateLogService.insertGasOperateLog(gasOperateLog1);
        if (i != 1) {
            return AjaxResult.error("提取失败");
        }
        // 插入提现记录表
        boolean b = insertOrder(address, gasAmount, orderNo);
        // 启动线程来执行提现操作，防止阻塞，并返回结果
        new Thread(() -> {
            try {
                Thread.sleep(100);
                processGas(address, gasAmount, orderNo, tokenName);
            } catch (InterruptedException e) {
//                e.printStackTrace();
                System.out.println("提现失败:" + e.getMessage());
            }
        }).start();

        System.out.println("提交提现请求成功");
        if (!b) {
            return AjaxResult.error("提取失败");
        } else {
            return AjaxResult.success("提取成功，请等待区块链确认，订单号为：" + orderNo);
        }
    }


    @PostMapping("/gasLog")
    @ResponseBody
    @ApiOperation(value = "gasLog", notes = "gas领取记录")
    public AjaxResult gasLog(@RequestBody NewGasParamsLite params) {
        String address = params.getAddress();
        String tokenName = params.getToken();
        if (address == null || address.isEmpty() || tokenName == null || tokenName.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        if (!addressCheck(address)) {
            return AjaxResult.error("地址错误");
        }
        // 查询质押和提取记录
        GasOperateLog log = new GasOperateLog();
        log.setUserAddr(address);
        log.setToken(tokenName);
        log.setType("gas");
        List<GasOperateLog> gasOperateLogs = gasOperateLogService.selectGasWithdrawLogList50(log);

        List<TransferLogVO> transferLogVOS = new ArrayList<>();
        for (GasOperateLog gasOperateLog : gasOperateLogs) {
            TransferLogVO tf = new TransferLogVO();
            tf.setAmount(gasOperateLog.getAmount());
            tf.setOptime(gasOperateLog.getOptime());
            tf.setHash(gasOperateLog.getRemark());
            transferLogVOS.add(tf);
        }
        return AjaxResult.success(transferLogVOS);
    }

    // 质押记录，只检索最近50条
    @PostMapping("/stakeLog")
    @ResponseBody
    @ApiOperation(value = "stakeLog", notes = "质押记录")
    public AjaxResult stakeLog(@RequestBody NewGasParamsLite params) {
        String address = params.getAddress();
        String tokenName = params.getToken();
        if (address == null || address.isEmpty() || tokenName == null || tokenName.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        if (!addressCheck(address)) {
            return AjaxResult.error("地址错误");
        }

        // 查询质押和提取记录
        GasOperateLog log = new GasOperateLog();
        log.setUserAddr(address);
        log.setToken(tokenName);
        List<GasOperateLog> gasOperateLogs = gasOperateLogService.selectGasOperateLogList50(log);

        List<OperateLogVO> operateLogVOS = new ArrayList<>();
        for (GasOperateLog gasOperateLog : gasOperateLogs) {
            OperateLogVO operateLogVO = new OperateLogVO();
            operateLogVO.setAmount(gasOperateLog.getAmount());
            operateLogVO.setOptime(gasOperateLog.getOptime());
            String type = gasOperateLog.getType();
            if (type.equals("stake")) {
                operateLogVO.setOptype("质押BRC");
            } else if (type.equals("withdraw")) {
                operateLogVO.setOptype("提取BRC");
            } else {
//                operateLogVO.setOptype("领取GAS");
                continue;
            }
            operateLogVOS.add(operateLogVO);
        }
        return AjaxResult.success(operateLogVOS);
    }

    public String processGas(String address, String amount, String orderNo, String tokenName) {
        System.out.println("开始提取gas");
        GasWithdrawLog withdrawLog = new GasWithdrawLog();
        withdrawLog.setUserAddr(address);
        withdrawLog.setTxhash("new");
        withdrawLog.setRemark(orderNo);
        List<GasWithdrawLog> gasWithdrawLogs = gasWithdrawLogService.selectGasWithdrawLogList(withdrawLog);
        if (gasWithdrawLogs.size() == 0) {
            return "未发现订单";
        }
        GasWithdrawLog withdrawLog2 = gasWithdrawLogs.get(0);
        withdrawLog2.setTxhash("processing");
        int i = gasWithdrawLogService.updateGasWithdrawLog(withdrawLog2);
        if (i != 1) {
            return "提取过程异常";
        }
        // 查询质押合约，看这人是否还在质押，若不在质押，则不允许提取
        boolean b = gasService.getStakeUser(address, tokenName);
        if (!b) {
            withdrawLog2.setTxhash("失败，未质押");
            gasWithdrawLogService.updateGasWithdrawLog(withdrawLog2);
            return "未质押";
        }

        // 发送转账请求
        String hash = gasService.ethTransfer(address, new BigDecimal(amount));
        // 发送转账请求
        if (hash == null || hash.isEmpty()) {
            hash = "提取失败";
        }
        // 记录转账记录，插入gas_transfer_log表
        GasTransferLog gasTransferLog = new GasTransferLog();
        gasTransferLog.setUserAddr(address);
        gasTransferLog.setAmount(amount);
        gasTransferLog.setTxhash(hash);
        gasTransferLog.setOptime(String.valueOf(System.currentTimeMillis() / 1000));
        gasTransferLog.setRemark(orderNo);

        int j = gasTransferLogService.insertGasTransferLog(gasTransferLog);
        if (j != 1) {
            gasTransferLogService.insertGasTransferLog(gasTransferLog);
        }
        return hash;
    }


    public boolean signCheck(String useraddr, String sign) {
        return true;
//        try {
//            String message = useraddr + nonce;
//
//            boolean validate = SignValiditor.validate(sign, message, useraddr.toLowerCase());
//            return validate;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return false;
//        }
    }


    public boolean addressCheck(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        if (address.length() != 42) {
            return false;
        }
        if (!address.startsWith("0x")) {
            return false;
        }
        return true;
    }


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


    //TODO 缓存用户的余额
    //如果改造成按小时空投，就改下snapshot数据的，以及除非那块儿，有一些硬编码
    // 添加缓存
    public void addBalanceCache(String address, String tokenName, Object value) {
        CacheUtils.put("balance_" + tokenName, address, value);
    }


    public String getBalanceCache(String address, String tokenName) {
        try {
            Object o = CacheUtils.get("balance_" + tokenName, address);
            if (o == null) {
                return null;
            } else {
                return (String) o;
            }
        } catch (Exception e) {
            return "0";
        }
    }

}
