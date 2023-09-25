package com.ruoyi.project.bussiness.service;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.Utils.SignValiditor;
import com.ruoyi.project.Utils.Web3jUtils;
import com.ruoyi.project.bus.operate.domain.GasOperateLog;
import com.ruoyi.project.bus.operate.service.IGasOperateLogService;
import com.ruoyi.project.bus.transferLog.domain.GasTransferLog;
import com.ruoyi.project.bus.transferLog.service.IGasTransferLogService;
import com.ruoyi.project.bussiness.common.BusConfigService;
import com.ruoyi.project.bussiness.entity.GasParams;
import com.ruoyi.project.bussiness.entity.GasParamsLite;
import com.ruoyi.project.bussiness.entity.OperateLogVO;
import com.ruoyi.project.bussiness.entity.TransferLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Controller
@RequestMapping("/GasGift")
@ApiOperation(value = "GasGift", notes = "个人领gas")
@Api(value = "GasGift", tags = {"GasGift"})
@DataSource(value = DataSourceType.SLAVE)
public class GasGiftService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Web3jUtils web3jUtils;

    @Autowired
    BusConfigService config;

    @Autowired
    IGasTransferLogService gasTransferLogService;

    @Autowired
    IGasOperateLogService gasOperateLogService;

    public static final String nonce = "419";

    @PostMapping("/stake")
    @ResponseBody
    @ApiOperation(value = "stake", notes = "质押")
    public AjaxResult stake(@RequestBody GasParams params) {
        String address = params.getAddress();
        String sign = params.getSign();
        String hash = params.getHash();
        // 关键参数校验
        if (address == null || sign == null || hash == null || address.isEmpty() || sign.isEmpty() || hash.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        // 如果关键参数为空，也返回false
        if (!signCheck(address, sign)) {
            return AjaxResult.error("签名错误");
        }
        // 校验hash是否存在
        // 从链上读取hash，查看from和to是否争取，交易是否成功，method是否是质押方法
        if (!hashCheck(address, hash)) {
            return AjaxResult.error("hash有问题");
        }
        // 如果成功，则记录质押记录，插入gas_operate_log表
        GasOperateLog log = new GasOperateLog();
        log.setUserAddr(address);
        log.setAmount("100");
        log.setType("stake");
        log.setOptime(String.valueOf(new Date().getTime()));
        int i = gasOperateLogService.insertGasOperateLog(log);
        if (i > 0) {
            return AjaxResult.success("质押成功");
        } else {
            return AjaxResult.error("质押失败");
        }
    }

    @PostMapping("/withdraw")
    @ResponseBody
    @ApiOperation(value = "withdraw", notes = "解除质押")
    public AjaxResult withdraw(@RequestBody GasParamsLite params) {
        // 校验关键数据是否存在或为空
        String address = params.getAddress();
        String sign = params.getSign();
        if (address == null || sign == null || address.isEmpty() || sign.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        // 校验sign是否通过
        if (!signCheck(address, sign)) {
            return AjaxResult.error("签名错误");
        }
        // 检测gas是否残留，或者如果不领取就接触质押，则gas清零
        GasOperateLog log = new GasOperateLog();
        log.setUserAddr(address);
        log.setType("withdraw");
        log.setOptime(String.valueOf(new Date().getTime()));
        log.setAmount("0");
        int i = gasOperateLogService.insertGasOperateLog(log);
        if (i > 0) {
            return AjaxResult.success("解除质押成功");
        } else {
            return AjaxResult.error("解除质押失败");
        }
    }

    public String sumGasSQL =
            "SELECT\n" +
                    "from_addr,sum(gas_used * gas_price) AS gas\n" +
                    "FROM\n" +
                    "transaction_info\n" +
                    "WHERE \n" +
                    "from_addr = '%s' AND `TIMESTAMP` >= %d AND txstatus = '0x1'\n" +
                    "GROUP BY from_addr;";


    @PostMapping("/getGasAmount")
    @ResponseBody
    @ApiOperation(value = "getGasAmount", notes = "获取待领取gas余额")
    public AjaxResult getGasAmount(@RequestBody GasParamsLite params) {
        // 从数据库中读取gas余额
        // 检索上次的操作，进行区别处理。存入缓存中。缓存有效期为10分钟，如果不存在，则从数据库中读取
        String address = params.getAddress();
        if (address == null || address.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        // 如果address长度不是42位，则返回错误，或者不是0x开头，长度也不是40位，则返回错误
        if (address.length() != 42 || !address.startsWith("0x") || address.length() != 40) {
            return AjaxResult.error("地址格式错误");
        }
        GasOperateLog log = new GasOperateLog();
        log.setUserAddr(address);
        GasOperateLog gasOperateLog = gasOperateLogService.selectGasOperateLast(log);
        if (gasOperateLog == null) {
            return AjaxResult.error("未找到质押记录");
        }
        String lastOperation = gasOperateLog.getType();
        String lastTime = gasOperateLog.getOptime();
        // 异常处理
        if (lastOperation == null || lastTime == null || lastOperation.isEmpty() || lastTime.isEmpty()) {
            return AjaxResult.error("数据异常");
        }
        Long lastTimeLong = 0l;
        try {
            lastTimeLong = Long.valueOf(lastTime);
        } catch (Exception e) {
            lastTimeLong = 9999999999999l;
        }
        // 如果上次操作是stake或gas，则执行查询SQL，如果是withdraw，则返回0
        if (lastOperation.equals("stake") || lastOperation.equals("gas")) {
            // 查询所有的交易记录，统计gas
            String sql = String.format(sumGasSQL, address, lastTimeLong);
            List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
            BigDecimal totalGas = new BigDecimal(0);
            for (Map<String, Object> map : maps) {
                BigDecimal gas = (BigDecimal) map.get("gas");
                totalGas = totalGas.add(gas);
            }
            // 返回非科学计数法的值
            String plainString = totalGas.toPlainString();
            return AjaxResult.success(plainString);
        } else if (lastOperation.equals("withdraw")) {
            return AjaxResult.success(0);
        } else {
            return AjaxResult.success(0);
        }
    }

    @Transactional
    @PostMapping("/withdrawGasByAddress")
    @ResponseBody
    @ApiOperation(value = "withdrawGasByAddress", notes = "根据地址领取gas")
    public AjaxResult withdrawGasByAddress(@RequestBody GasParamsLite params) {
        String address = params.getAddress();
        String sign = params.getSign();
        if (address == null || sign == null || address.isEmpty() || sign.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        // 如果address长度不是42位，则返回错误，或者不是0x开头，长度也不是40位，则返回错误
        if (address.length() != 42 || !address.startsWith("0x") || address.length() != 40) {
            return AjaxResult.error("地址格式错误");
        }
        // 校验sign是否通过
        if (!signCheck(address, sign)) {
            return AjaxResult.error("签名错误");
        }

        // 校验address的最后一条是否是质押记录或提取Gas，如果不是，则返回错误
        GasOperateLog log = new GasOperateLog();
        log.setUserAddr(address);
        GasOperateLog gasOperateLog = gasOperateLogService.selectGasOperateLast(log);
        if (gasOperateLog == null) {
            return AjaxResult.error("未找到质押记录");
        }
        String lastOperation = gasOperateLog.getType();
        String lastTime = gasOperateLog.getOptime();
        // 异常处理
        if (lastOperation == null || lastTime == null || lastOperation.isEmpty() || lastTime.isEmpty()) {
            return AjaxResult.error("数据异常");
        }
        Long lastTimeLong = 0l;
        try {
            lastTimeLong = Long.valueOf(lastTime);
            // 如果间隔小于1小时，则返回错误
            if (System.currentTimeMillis() - lastTimeLong < 3600000) {
                return AjaxResult.error("提取Gas间隔不能小于1小时");
            }
        } catch (Exception e) {
            lastTimeLong = 9999999999999l;
        }
        if (lastOperation.equals("withdraw")) {
            return AjaxResult.error("未质押BRC");
        }
        if (!lastOperation.equals("stake") && !lastOperation.equals("gas")) {
            return AjaxResult.error("操作类型错误");
        }

        // 查询所有的交易记录，统计gas
        String sql = String.format(sumGasSQL, address, lastTimeLong);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        BigDecimal totalGas = new BigDecimal(0);
        for (Map<String, Object> map : maps) {
            BigDecimal gas = (BigDecimal) map.get("gas");
            totalGas = totalGas.add(gas);
        }
        // 如果gas为0，则返回错误
        if (totalGas.compareTo(new BigDecimal(0)) == 0) {
            return AjaxResult.error("没有可提取的Gas");
        }

        // 生成订单号，并将提现记录插入提现表
        String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
        GasOperateLog gasOperateLog1 = new GasOperateLog();
        gasOperateLog1.setRemark(orderNo);
        gasOperateLog1.setUserAddr(address);
        gasOperateLog1.setOptime(String.valueOf(System.currentTimeMillis()));
        gasOperateLog1.setType("withdraw");
        gasOperateLog1.setAmount(totalGas.toPlainString());
        int i = gasOperateLogService.insertGasOperateLog(gasOperateLog1);
        if (i != 1) {
            return AjaxResult.error("提取失败");
        }

        String hash = ethTransfer(getWeb3j(), address, totalGas);
        // 发送转账请求
        if (hash == null || hash.isEmpty()) {
            hash = "提取失败";
        }
        // 记录转账记录，插入gas_transfer_log表
        GasTransferLog gasTransferLog = new GasTransferLog();
        gasTransferLog.setUserAddr(address);
        gasTransferLog.setAmount(totalGas.toPlainString());
        gasTransferLog.setTxhash(hash);
        gasTransferLog.setOptime(String.valueOf(System.currentTimeMillis()));
        gasTransferLog.setRemark(orderNo);

        int j = gasTransferLogService.insertGasTransferLog(gasTransferLog);
        if (j != 1) {
            gasTransferLogService.insertGasTransferLog(gasTransferLog);
        }
        // 返回交易哈希
        return AjaxResult.success(hash);
    }

    // 质押记录，只检索最近50条
    @PostMapping("/stakeLog")
    @ResponseBody
    @ApiOperation(value = "stakeLog", notes = "质押记录")
    public AjaxResult stakeLog(@RequestBody GasParamsLite params) {
        String address = params.getAddress();
        if (address == null || address.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        // 如果address长度不是42位，则返回错误，或者不是0x开头，长度也不是40位，则返回错误
        if (address.length() != 42 || !address.startsWith("0x") || address.length() != 40) {
            return AjaxResult.error("地址格式错误");
        }

        // 查询质押和提取记录
        GasOperateLog log = new GasOperateLog();
        log.setUserAddr(address);
        List<GasOperateLog> gasOperateLogs = gasOperateLogService.selectGasOperateLogList50(log);

        List<OperateLogVO> operateLogVOS = new ArrayList<>();
        for (GasOperateLog gasOperateLog : gasOperateLogs) {
            OperateLogVO operateLogVO = new OperateLogVO();
            operateLogVO.setAmount(gasOperateLog.getAmount());
            operateLogVO.setOptime(gasOperateLog.getOptime());
            String type = gasOperateLog.getType();
            if(type.equals("stake")){
                operateLogVO.setOptype("质押BRC");
            }else if(type.equals("withdraw")){
                operateLogVO.setOptype("提取BRC");
            }else{
                operateLogVO.setOptype("领取GAS");
            }
            operateLogVOS.add(operateLogVO);
        }
        return AjaxResult.success(operateLogVOS);
    }

    // gas领取记录
    @PostMapping("/gasLog")
    @ResponseBody
    @ApiOperation(value = "gasLog", notes = "gas领取记录")
    public AjaxResult gasLog(@RequestBody GasParamsLite params) {
        String address = params.getAddress();
        if (address == null || address.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        // 如果address长度不是42位，则返回错误，或者不是0x开头，长度也不是40位，则返回错误
        if (address.length() != 42 || !address.startsWith("0x") || address.length() != 40) {
            return AjaxResult.error("地址格式错误");
        }

        // 查询质押和提取记录
        GasTransferLog log = new GasTransferLog();
        log.setUserAddr(address);
        List<GasTransferLog> gasTransferLogs = gasTransferLogService.selectGasTransferLogList50(log);

        List<TransferLogVO> transferLogVOS = new ArrayList<>();
        for (GasTransferLog gasTransferLog : gasTransferLogs) {
            TransferLogVO transferLogVO = new TransferLogVO();
            transferLogVO.setAmount(gasTransferLog.getAmount());
            transferLogVO.setOptime(gasTransferLog.getOptime());
            transferLogVO.setHash(gasTransferLog.getTxhash());
            transferLogVOS.add(transferLogVO);
        }
        return AjaxResult.success(transferLogVOS);
    }


    public boolean signCheck(String useraddr, String sign) {
        // return true;
        try {
            String message = useraddr + nonce;

            boolean validate = SignValiditor.validate(sign, message, useraddr.toLowerCase());
            return validate;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean hashCheck(String useraddr, String hash) {
        String contractAddr = config.getConfig("STAKE_CONTRACT", "0xbf1517A5C733ad7ed59AF36A281F37dB8b8210bA");
        String methodId = config.getConfig("STAKE_METHOD", "0x7d8e0c9f");
        // return true;
        try {
            Web3j web3j = getWeb3j();
            EthTransaction ethTransaction = web3j.ethGetTransactionByHash(hash).send();
            if (ethTransaction == null) {
                return false;
            }
            Transaction transaction = ethTransaction.getTransaction().get();
            String to = transaction.getTo();
            String input = transaction.getInput();
            if (!contractAddr.equalsIgnoreCase(to)) {
                return false;
            }
            String method = input.substring(0, 10);
            if (!methodId.equalsIgnoreCase(method)) {
                return false;
            }
            String data = input.substring(10);
            String[] split = data.split("000000000000000000000000");
            String from = split[1].substring(0, 40);
            String toAddr = split[2].substring(0, 40);
            if (!useraddr.equalsIgnoreCase(from) || !useraddr.equalsIgnoreCase(toAddr)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    Web3j w3j = null;

    // load web3j
    public Web3j getWeb3j() {
        // 采用单例模式
        if (w3j != null) {
            return w3j;
        }
        String url = config.getConfig("RPC", "https://rpc.bitchain.biz");
        String privateKey = config.getConfig("PRIVATE_KEY", "");
        int chainid = config.getConfig("CHAINID", 198);
        String contractAddress = config.getConfig("STAKE_CONTRACT", "0xbf1517A5C733ad7ed59AF36A281F37dB8b8210bA");
        Web3j web3j = null;
        Credentials credentials = null;
        try {
            web3j = Web3j.build(new HttpService(url));
            EthBlockNumber send = web3j.ethBlockNumber().send();
            System.out.println("send:" + send.getBlockNumber());
            credentials = Credentials.create(privateKey);
            System.out.println("credentials:" + credentials.getAddress());
            w3j = web3j;
        } catch (Exception e) {
            System.out.println("web3j = Web3j.build(new HttpService(url));");
            return null;
        }
        return web3j;
    }

    // transfer value to address
    public String ethTransfer(Web3j web3j, String fromAddr,BigDecimal amount){
        String privateKey = config.getConfig("PRIVATE_KEY", "");
        if(!privateKey.startsWith("0x")){
            privateKey = "0x" + privateKey;
        }
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(BigInteger.valueOf(1), BigInteger.valueOf(1_000_000_000L), BigInteger.valueOf(210000), fromAddr, amount.toBigInteger());
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        try {
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
            String transactionHash = ethSendTransaction.getTransactionHash();
            System.out.println("transactionHash:" + transactionHash);
            return transactionHash;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


}
