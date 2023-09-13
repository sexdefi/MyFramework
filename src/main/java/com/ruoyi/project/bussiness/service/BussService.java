package com.ruoyi.project.bussiness.service;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import com.ruoyi.project.bus.Profit.service.IProfitLogService;
import com.ruoyi.project.bus.airdrop.service.IAirdropService;
import com.ruoyi.project.bus.batch.service.IAirdropBatchLogService;
import com.ruoyi.project.bussiness.common.BusConfigService;
import com.ruoyi.project.bussiness.mapper.AirdropDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;

import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.ContractGasProvider;

import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/gasfee")
@ApiOperation(value = "GasFee", notes = "空投GAS")
@Api(value = "GasFee", tags = {"GasFee"})
@DataSource(value = DataSourceType.SLAVE)
//只需要在需要切换数据源的方法上使用该注解即可
public class BussService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BusConfigService config;

    @Autowired
    AirdropDto airdropDto;

    @GetMapping("/getTimeStampByDay")
    @ResponseBody
    @ApiOperation(value = "getTimeStampByDay", notes = "计算时间戳")
    public Long[] getTimeStampByDayRestful(@ApiParam(name = "start", value = "start", required = true) String start,
                                           @ApiParam(name = "end", value = "end", required = true) String end) throws ParseException {
        // start = "2019-01-01 00:00:00";
        Long startTimestamp = DateUtils.parseDate(start, "yyyy-MM-dd HH:mm:ss").getTime() / 1000;
        Long endTimestamp = DateUtils.parseDate(end, "yyyy-MM-dd HH:mm:ss").getTime() / 1000;
        return new Long[]{startTimestamp, endTimestamp};
    }

    @GetMapping("/getGasListByDay")
    @ResponseBody
    @ApiOperation(value = "getGasLastDayRestful", notes = "GET指定时间段的GAS列表")
    public List getGasLastDayRestful(@ApiParam(name = "start", value = "start", required = true) String start,
                                     @ApiParam(name = "end", value = "end", required = true) String end) throws ParseException {
        Long[] longs = getTimeStampByDayRestful(start, end);
        return getSepcGasLastDay(longs[0], longs[1]);
    }

    @GetMapping("/giveGasListByDay")
    @ResponseBody
    @ApiOperation(value = "giveGasListByDay", notes = "按照时间段空投GAS")
    public List giveGasListByDay(@ApiParam(name = "start", value = "start", required = true) String start,
                                 @ApiParam(name = "end", value = "end", required = true) String end,
                                 @ApiParam(name= "address", value = "address", required = false) String addr) throws ParseException {
        List r = new ArrayList();
        r.add("空投失败");
        try {
            Long[] longs = getTimeStampByDayRestful(start, end);
            List sepcGasLastDay = getSepcGasLastDay(longs[0], longs[1]);
            String res = airdropGasForList(sepcGasLastDay, longs[0], longs[1]);
            if (res.startsWith("success")) {
                return sepcGasLastDay;
            } else {
                r.add(res);
                return r;
            }
//            if (res.equals("空投成功")) {
//                return sepcGasLastDay;
//            } else {
//                return r;
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return r;
        }
    }

    @GetMapping("/getGasLastDay")
    @ResponseBody
    @ApiOperation(value = "getGasLastDay", notes = "按照当前日期的前一天的时间段来查询")
    public List getGasLastDayRestful() {
        Date date = DateUtils.addDays(new Date(), -1);
        System.out.println(date.getTime());
        Long start = date.getTime() / 1000;
        Long end = new Date().getTime() / 1000;
        List list = getSepcGasLastDay(start, end);
        return list;
    }

    @GetMapping("/giveGasLastDay")
    @ResponseBody
    @ApiOperation(value = "giveGasLastDay", notes = "按照当前日期的前一天的时间段来空投")
    public boolean giveGasLastDay() {
        try {
            Date date = DateUtils.addDays(new Date(), -1);
            System.out.println(date.getTime());
            Long start = date.getTime() / 1000;
            Long end = new Date().getTime() / 1000;
            List list = getSepcGasLastDay(start, end);
            String res = airdropGasForList(list, start, end);

            if (res.startsWith("success")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // public String sql1 = "select c.from_addr as from_addr,sum(c.gas_used * c.gas_price) as gas from (select from_addr,gas_used,gas_price from account a left join transaction_info b on a.address = b.from_addr where a.balance >= %d and `TIMESTAMP`> %d and `TIMESTAMP` <= %d) c GROUP BY c.from_addr;";
//    public String sql1 =
//            "select c.from_addr as from_addr,sum(c.gas_used * c.gas_price) as gas from (select from_addr,gas_used,gas_price from account_token a left join transaction_info b on a.address = b.from_addr where a.token_address = '%s' && a.balance >= %d and `TIMESTAMP`> %d and `TIMESTAMP` <= %d) c GROUP BY c.from_addr;";
//
    public String sql1 = "SELECT  c.from_addr AS from_addr, sum( c.gas_used * c.gas_price ) AS gas  FROM  ( SELECT  from_addr,  gas_used,  gas_price  FROM  account_token a  LEFT JOIN transaction_info b ON a.address = b.from_addr   WHERE   a.token_address = '%s' && convert(a.balance,signed) >= %d && b.txstatus = '0x1'   AND `TIMESTAMP` > %d    AND `TIMESTAMP` <= %d  ) c  GROUP BY  c.from_addr";

    public String sqlfail = "select * from (SELECT * FROM `txchain_scan_sh`.`transaction_info` WHERE `txstatus` = '0x0' AND `input` LIKE '%418d1873a4440c6e0a16d45dcd328a214f697dd5%' AND `input` LIKE '%f89c8c3cf0d39745f9f691fc2839572ddc00e02f%' ORDER BY `timestamp` asc ) a JOIN token_transfer b on a.thash = b.tx_hash;";

    // 指定时间段查询
    public List getSepcGasLastDay(Long start, Long end) {
        String tokenAddress = config.getConfig("TOKEN_ADDRESS", "0x9599695608BE59a420d7b9A32f3AbFc362d88d36");
        Integer amount = config.getConfig("OVER_AMOUNT", 500);
        if (amount == null) {
            amount = 500;
        }
        if (start >= end) {
            return new ArrayList<>();
        }
        if (start == null) {
            start = DateUtils.addDays(new Date(), -1).getTime() / 1000;
        }
        if (end == null) {
            end = new Date().getTime() / 1000;
        }
        String sqlconfig = config.getConfig("SQL", sql1);
        String sql2 = String.format(sqlconfig,tokenAddress, amount, start, end);
        System.out.println(sql2);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql2);
        if (maps == null) {
            maps = new ArrayList<>();
        }
        return maps;
    }


    public String airdropGasForList(List<Map<String, Object>> maps, Long start, Long end) throws IOException {
        if (maps == null || maps.size() == 0) {
            return "空投失败，没有符合条件的地址";
        }

        String url = config.getConfig("RPC", "https://rpc.bitchain.biz");
        String privateKey = config.getConfig("PRIVATE_KEY", "0x3a42de4ce6a82ad59012b3629f860a5781ff64bd99a992398f138dece323f01d");
        int chainid = config.getConfig("CHAINID", 2023);
        String contractAddress = config.getConfig("AIRDROP_ADDRESS", "0xbf1517A5C733ad7ed59AF36A281F37dB8b8210bA");
        Web3j web3j = null;
        Credentials credentials = null;
        Airdrop airdrop = null;
        try {
            web3j = Web3j.build(new HttpService(url));
            EthBlockNumber send = web3j.ethBlockNumber().send();
            System.out.println("send:" + send.getBlockNumber());
            credentials = Credentials.create(privateKey);
            System.out.println("credentials:" + credentials.getAddress());
            airdrop = _loadAirdrop(web3j, chainid, privateKey, contractAddress);
            System.out.println("airdrop:" + airdrop.getContractAddress());
        } catch (Exception e) {
            System.out.println("web3j = Web3j.build(new HttpService(url));");
            return "Web3未空或者私钥不正确";
        }

        if (maps == null || maps.size() == 0) {
            System.out.println("maps == null || maps.size() == 0");
            return "空投列表为空";
        }

        // 定义两个list，存放所有的from_addr和gas
        List<String> from_addr_list = new ArrayList<>();
        List<BigDecimal> gas_list = new ArrayList<BigDecimal>();

        // 遍历maps，取出from_addr和gas，放入list中
        maps.forEach(map -> {
            // 参数校验
            if (map.get("from_addr") != null && map.get("gas") != null) {
                from_addr_list.add(map.get("from_addr").toString());

                BigDecimal bigGas = new BigDecimal(map.get("gas").toString());
                gas_list.add(bigGas);
            }
        });

        if (from_addr_list.size() != gas_list.size()) {
            System.out.println("from_addr_list.size() != gas_list.size()");
            return "空投列表异常，数量不匹配";
        }
        if (from_addr_list.size() == 0) {
            System.out.println("from_addr_list.size() == 0");
            return "空投列表为空";
        }
        if (web3j == null || credentials == null) {
            System.out.println("web3j == null || credentials == null");
            return "Web3j或者私钥不正确";
        }
        // 将gas累加，查询余额，如果余额小于gas累加，就不空投，返回false
        BigDecimal totalGas = new BigDecimal(0);
        for (BigDecimal gas : gas_list) {
            totalGas = totalGas.add(gas);
        }
        try {
            BigInteger balance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();
            if (balance.compareTo(totalGas.toBigInteger()) < 0) {
                System.out.println("账户余额不足，无法空投");
                return "账户余额不足，无法空投";
            }
        } catch (Exception e) {
            System.out.println("查询余额时候，异常！");
            return "查询余额时候，异常！";
        }

        // 定义一个计数器，记录空投成功的数量
        // 将gas_list里面的数据类型从BigDecimal转换成BigInteger
        BigInteger totalGasBig = BigInteger.ZERO;
        List<BigInteger> gas_list_big = new ArrayList<>();
        for (BigDecimal gas : gas_list) {
            gas_list_big.add(gas.toBigInteger());
            totalGasBig = totalGasBig.add(gas.toBigInteger());
        }
        // 多加一点gas防止四舍五入
        totalGasBig = totalGasBig.add(new BigInteger("100000000000"));

        String contractAirdrop = config.getConfig("CONTRACT_AIRDROP_SWITCH", "true");
        boolean contractAirdropSwitch = Boolean.parseBoolean(contractAirdrop);
        String hash = "";
        if (contractAirdropSwitch) {
            try {
                // for (int i = 0; i < from_addr_list.size(); i++) { // 先取50条
                int _nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.PENDING).send().getTransactionCount().intValue();
                String _gasPrice = "1";
                int _gasLimit = 6000000;
                String _to = contractAddress;
                String _value = totalGasBig.toString();
                String _data = airdrop.multiTransfer_OST(from_addr_list, gas_list_big).encodeFunctionCall().substring(2);
                RawTransaction rawTransaction = newRawTx(_nonce, _gasPrice, _gasLimit, _to, _value, _data);
                byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainid, credentials);
                String hexValue = Numeric.toHexString(signedMessage);
                EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
                if (ethSendTransaction.hasError()) {
                    System.out.println("空投失败");
                    return "空投失败";
                }
                hash = ethSendTransaction.getTransactionHash();
                System.out.println("hash:" + hash);

                String batch = Hash.sha3String(start + "-" + end).substring(2, 8);
                // 判断是否已经空投过了,如果已经空投过了，就不再空投。为了防止定时任务重复空投
                if (airdropDto.isExist(from_addr_list.get(0), batch)) {
                    System.out.println("addr:" + from_addr_list.get(0) + " batch:" + batch + " 已经空投过了");
                }

                // 保存到数据库当中
                // 将gas从BigDecimal转为String类型，不用科学记数法
                String gasStr = totalGasBig.toString();
                // adDate为当前时间用yyyy-MM-dd格式化后的字符串
                String adDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                // 使用start、end计算批次，用于查询，hash以后取前8位
                airdropDto.SaveAirdropResultToDb(from_addr_list.get(0), gasStr, adDate, hash, start, end, batch);
                return "success," + hash;
            } catch (Exception ex) {
                System.out.println("ex:" + ex);
                return "空投失败";
            }
        } else {
            int count = 0;
            try {
                for (int i = 0; i < from_addr_list.size(); i++) {
                    String addr = from_addr_list.get(i);
                    BigDecimal gas = gas_list.get(i);
                    String batch = Hash.sha3String(start + "-" + end).substring(2, 8);
                    // 判断是否已经空投过了,如果已经空投过了，就不再空投。为了防止定时任务重复空投
                    if (airdropDto.isExist(addr, batch)) {
                        System.out.println("addr:" + addr + " batch:" + batch + " 已经空投过了");
                        count++;
                        continue;
                    }

                    hash = airdropToAddr(web3j, credentials, addr, gas);

                    if (hash == null || hash.equals("false")) {
                        System.out.println("airdropToAddr" + addr + " error");
                    }
                    System.out.println("hash:" + hash);

                    // 保存到数据库当中
                    // 将gas从BigDecimal转为String类型，不用科学记数法
                    String gasStr = gas.toPlainString();
                    // adDate为当前时间用yyyy-MM-dd格式化后的字符串
                    String adDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    // 使用start、end计算批次，用于查询，hash以后取前8位
                    airdropDto.SaveAirdropResultToDb(addr, gasStr, adDate, hash, start, end, batch);
                }
                // 如果count等于from_addr_list.size()，说明所有的地址都已经空投过了，返回false
                boolean r = count != from_addr_list.size();
                if (r) {
                    System.out.println("空投成功");
                    return "空投成功";
                } else {
                    System.out.println("所有地址都已经空投过了");
                    return "所有地址都已经空投过了";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "空投过程异常";
            }
        }

    }
//        try {
//            for (int i = 0; i < from_addr_list.size(); i++) {
//                String addr = from_addr_list.get(i);
//                BigDecimal gas = gas_list.get(i);
//                String batch = Hash.sha3String(start + "-" + end).substring(2, 8);
//                // 判断是否已经空投过了,如果已经空投过了，就不再空投。为了防止定时任务重复空投
//                if(airdropDto.isExist(addr,batch)){
//                    System.out.println("addr:"+addr+" batch:"+batch+" 已经空投过了");
//                    count++;
//                    continue;
//                }
//
//                String hash = airdropToAddr(web3j, credentials,addr, gas);
//
//                if (hash == null || hash.equals("false")) {
//                    System.out.println("airdropToAddr" + addr + " error");
//                }
//                System.out.println("hash:" + hash);
//
//                // 保存到数据库当中
//                // 将gas从BigDecimal转为String类型，不用科学记数法
//                String gasStr = gas.toPlainString();
//                // adDate为当前时间用yyyy-MM-dd格式化后的字符串
//                String adDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//
//                // 使用start、end计算批次，用于查询，hash以后取前8位
//                airdropDto.SaveAirdropResultToDb(addr, gasStr, adDate, hash, start, end,batch);
//            }
//            // 如果count等于from_addr_list.size()，说明所有的地址都已经空投过了，返回false
//            boolean r = count != from_addr_list.size();
//            if(r) {
//                System.out.println("空投成功");
//                return "空投成功";
//            }else{
//                System.out.println("所有地址都已经空投过了");
//                return "所有地址都已经空投过了";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "空投过程异常";
//        }
//}

    // 单个地址转账，批量请使用空投合约
    @Log(title = "定时任务", businessType = BusinessType.OTHER)
    public String airdropToAddr(Web3j web3j, Credentials credentials, String addr, BigDecimal gas) {
        TransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt = Transfer.sendFunds(
                            web3j, credentials,
                            addr,  // you can put any address here
                            gas, Convert.Unit.WEI)  // 1 wei = 10^-18 Ether
                    .send();
            if (transactionReceipt != null) {
                System.out.println("Transaction "
                        + transactionReceipt.getTransactionHash());
                return transactionReceipt.getTransactionHash();
            } else {
                return "false";
            }
        } catch (Exception e) {
            return "false";
        }
    }


    @Log(title = "定时任务", businessType = BusinessType.OTHER)
    public String airdropToAddrEIP155(Web3j web3j, Credentials credentials, Integer chainid, BigInteger nonce, String addr, BigDecimal gas) {
        // 满足EIP155，转账一定数量的主币
        try {
            EthGasPrice ethGasPrice = web3j.ethGasPrice().send();

            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                    nonce,
                    ethGasPrice.getGasPrice().multiply(BigInteger.valueOf(2)),
                    BigInteger.valueOf(210000),
                    addr,
                    gas.toBigInteger()
            );
            byte[] bytes = TransactionEncoder.signMessage(rawTransaction, chainid, credentials);
            String hexValue = Numeric.toHexString(bytes);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
            if (ethSendTransaction != null) {
                System.out.println("Transaction "
                        + ethSendTransaction.getTransactionHash());
                return ethSendTransaction.getTransactionHash();
            } else {
                return "false";
            }
        } catch (Exception e) {
            return "false";
        }
    }

    Airdrop _airdrop = null;

    private Airdrop _loadAirdrop(Web3j web3j, int chainid, String privatekey, String contract_address) {
        if (_airdrop == null) {
            Credentials credentials = Credentials.create(privatekey);
            ContractGasProvider contractGasProvider = new StaticGasProvider(BigInteger.valueOf(1000000000), BigInteger.valueOf(2100000));
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials, chainid); //EIP-155
            return Airdrop.load(contract_address, web3j, transactionManager, contractGasProvider);
        }
        return _airdrop;
    }

    public static RawTransaction newRawTx(int _nonce, String _gasPrice, int _gasLimit, String _to, String _value, String _data) {
        BigInteger nonce = new BigInteger(String.valueOf(_nonce));
        BigInteger gasLimit = new BigInteger(String.valueOf(_gasLimit));
        BigInteger gasPrice = Convert.toWei(_gasPrice, Convert.Unit.GWEI).toBigInteger();
        BigInteger value = Convert.toWei(_value, Convert.Unit.WEI).toBigInteger();

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, _to, value, _data);
        return rawTransaction;
    }
}
