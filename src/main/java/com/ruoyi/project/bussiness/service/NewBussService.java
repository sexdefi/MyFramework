package com.ruoyi.project.bussiness.service;


import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import com.ruoyi.project.bus.Profit.service.IProfitLogService;
import com.ruoyi.project.bus.airdrop.domain.Airdrop;
import com.ruoyi.project.bus.airdrop.service.IAirdropService;
import com.ruoyi.project.bus.batch.domain.AirdropBatchLog;
import com.ruoyi.project.bus.batch.service.IAirdropBatchLogService;
import com.ruoyi.project.bussiness.common.BusConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/newbuss")
@ApiOperation(value = "GasFee", notes = "New空投GAS")
@Api(value = "GasFee", tags = {"GasFee"})
@DataSource(value = DataSourceType.SLAVE)
//只需要在需要切换数据源的方法上使用该注解即可
public class NewBussService {

    // 数据库
    @Autowired
    JdbcTemplate jdbcTemplate;

    // 缓存
    @Autowired
    BusConfigService config;

    @Autowired
    AirdropContractService airdropContractService;

    @Autowired
    IAirdropBatchLogService airdropBatchLogService;

    @Autowired
    IProfitLogService profitLogService;

    @Autowired
    IAirdropService airdropService;

//    @GetMapping("/getTimeStampByDay")
//    @ResponseBody
//    @ApiOperation(value = "getTimeStampByDay", notes = "计算时间戳")
//    public Long[] getTimeStampByDayRestful(@ApiParam(name = "start", value = "start", required = true) String start,
//                                           @ApiParam(name = "end", value = "end", required = true) String end) throws ParseException {
//        // 如果start和end是yyyyMMdd的话，就转化为yyyy-MM-dd HH:mm:ss
//        if (start.length() == 8) {
//            start = start.substring(0, 4) + "-" + start.substring(4, 6) + "-" + start.substring(6, 8) + " 00:00:05";
//        }
//        if (end.length() == 8) {
//            end = end.substring(0, 4) + "-" + end.substring(4, 6) + "-" + end.substring(6, 8) + " 00:00:05";
//        }
//        // 如果start和end是yyyy-MM-dd的话，就转化为yyyy-MM-dd HH:mm:ss
//        if (start.length() == 10) {
//            start = start + " 00:00:05";
//        }
//        if (end.length() == 10) {
//            end = end + " 00:00:05";
//        }
//        Long startTimestamp = DateUtils.parseDate(start, "yyyy-MM-dd HH:mm:ss").getTime() / 1000;
//        Long endTimestamp = DateUtils.parseDate(end, "yyyy-MM-dd HH:mm:ss").getTime() / 1000;
//        return new Long[]{startTimestamp, endTimestamp};
//    }
//

    // 定时任务 1小时执行一次，取出当前时间，计算0分5秒到一小时之内的时间戳范围
    @GetMapping("/generateGasListLastHour")
    @ResponseBody
    @ApiOperation(value = "generateGasListLastHour", notes = "生成一个小时内空投批次数据，并保存到数据库中")
    public boolean generateBatchLastHour() {
        long[] period = getTimestampLastHour();
        boolean res = generateGasListByPeriod(period[0], period[1]);
        return res;
    }

    // 空投批次数据
    @GetMapping("/airdropGasLastHour")
    @ResponseBody
    @ApiOperation(value = "airdropGasLastHour", notes = "空投一个小时内的批次数据")
    public String giveGasLastHour() {
        long[] period = getTimestampLastHour();
        String res = airdropGasListByPeriod(period[0], period[1]);
        return res;
    }

    public long[] getTimestampLastHour() {
        // 当前时间的小时的0分5秒
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 5);
        Date start = calendar.getTime();

        // 当前时间的小时的1小时前的0分5秒
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        Date end = calendar.getTime();

        long startTimestamp = start.getTime() / 1000;
        long endTimestamp = end.getTime() / 1000;
        return new long[]{startTimestamp, endTimestamp};
    }

    // 生成当前时间段内空投批次数据，并保存到数据库中,
    private boolean generateGasListByPeriod(long start, long end) {
        // 避免重新生成
        String batchNo = getBatchNo(start, end);
        AirdropBatchLog a = new AirdropBatchLog();
        a.setBatchNo(batchNo);
        List airdropBatchLogList = airdropBatchLogService.selectAirdropBatchLogList(a);
        if (airdropBatchLogList.size() > 0) {
            System.out.println("已经生成过了，不需要重新生成，batchNo=" + batchNo);
            return false;
        }
        // 查询start和end之间的空投批次数据
        List<Map<String, Object>> sepcGasList = getSepcGas(start, end);
        // 遍历sepcGasList，按照180个一组，生成batchID，批量保存到数据库airdrop_batch_log表中
        for (int i = 0; i < sepcGasList.size(); i += 180) {
            List<Map<String, Object>> subList = sepcGasList.subList(i, i + 180 > sepcGasList.size() ? sepcGasList.size() : i + 180);
            String batchId = batchNo + i;
            // 删除空格
            batchId = batchId.replaceAll(" ", "");
            for (Map<String, Object> item : subList) {
                AirdropBatchLog airdropBatchLog = new AirdropBatchLog();
                airdropBatchLog.setBatchNo(batchNo);
                airdropBatchLog.setAddress((String) item.get("from_addr"));
                airdropBatchLog.setAmount((String) item.get("gas"));
                // 从config里面获取token_address
                airdropBatchLog.setTokenAddress("");
                // 从数据库中查询token余额
                airdropBatchLog.setTokenAmount("");
                airdropBatchLog.setAirdropTime("");
                airdropBatchLog.setBatchIndex(batchId);
                // snapshot_time，保存当前日期的yyyy-MM-dd HH:mm:ss格式字符串
                airdropBatchLog.setSnapshotTime(DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss"));
                airdropBatchLog.setAdStatus("0");
                airdropBatchLog.setAirdropHash("");

                try {
                    airdropBatchLogService.insertAirdropBatchLog(airdropBatchLog);
                } catch (Exception e) {
                    System.out.println("保存空投数据失败:" + e.getMessage());
                }
            }
        }
        return true;
    }

    // 空投批次数据
    private String airdropGasListByPeriod(long start, long end) {
        // 查询start和end之间的空投批次数据，找到未空投的批次，进行空投，即ad_status=0的批次
        // 计算批次号
        String batchNo = getBatchNo(start, end);

        String sql = "select batch_index from airdrop_batch_log where batch_no='" + batchNo + "' and ad_status='0'";
        List<Map<String, Object>> batchIndexList = jdbcTemplate.queryForList(sql);
        if (batchIndexList.size() == 0) {
            System.out.println("没有需要空投的批次，batchNo=" + batchNo);
            return "没有需要空投的批次，batchNo=" + batchNo;
        }
        // 遍历batchIndexList，查找数据库中，batch_index=当前batch_index的数据，进行空投
        for (Map<String, Object> item : batchIndexList) {
            String batchIndex = (String) item.get("batch_index");
            // 查询数据库中，batch_index=当前batch_index的数据
            AirdropBatchLog a = new AirdropBatchLog();
            a.setBatchNo(batchNo);
            a.setBatchIndex(batchIndex);
            a.setAdStatus("0");
            List<AirdropBatchLog> airdropBatchLogList = airdropBatchLogService.selectAirdropBatchLogList(a);
            // 调用空投接口，进行空投
            String res = airdropGasForListBatch(airdropBatchLogList);
            if (res.startsWith("success")) {
                // 更新数据库中的ad_status=1
                for (AirdropBatchLog airdropBatchLog : airdropBatchLogList) {
                    airdropBatchLog.setAdStatus("1");
                    airdropBatchLogService.updateAirdropBatchLog(airdropBatchLog);
                }
            }
        }

        return "";
    }

    // 空投主逻辑
    private String airdropGasForListBatch(List<AirdropBatchLog> maps) {
        if (maps == null || maps.size() == 0) {
            return "空投失败，没有符合条件的地址";
        }
        // 更新数据库中的ad_status=2，表示正在空投
        AirdropBatchLog airdropBatchLog = new AirdropBatchLog();
        airdropBatchLog.setBatchNo(maps.get(0).getBatchNo());
        airdropBatchLog.setAdStatus("2");
        airdropBatchLogService.updateAirdropBatchLog(airdropBatchLog);

        String batchNo = maps.get(0).getBatchNo();
        String batchIndex = maps.get(0).getBatchIndex();
        // 如果batchNo中包含_，则start和end分别取batchNo的前半部分和后半部分，否则start和end都取batchNo
        String start = batchNo;
        String end = batchNo;
        if (batchNo.contains("_")) {
            start = batchNo.split("_")[0];
            end = batchNo.split("_")[1];
        }

        String url = config.getConfig("RPC", "https://rpc.bitchain.biz");
        String privateKey = config.getConfig("PRIVATE_KEY", "0x3a42de4ce6a82ad59012b3629f860a5781ff64bd99a992398f138dece323f01d");
        int chainid = config.getConfig("CHAINID", 2023);
        String contractAddress = config.getConfig("AIRDROP_ADDRESS", "0xbf1517A5C733ad7ed59AF36A281F37dB8b8210bA");
        Web3j web3j = null;
        Credentials credentials = null;
        AirdropContract airdrop = null;
        try {
            web3j = Web3j.build(new HttpService(url));
            EthBlockNumber send = web3j.ethBlockNumber().send();
            System.out.println("send:" + send.getBlockNumber());
            credentials = Credentials.create(privateKey);
            System.out.println("credentials:" + credentials.getAddress());
            airdrop = airdropContractService._loadAirdrop(web3j, chainid, privateKey, contractAddress);
            System.out.println("airdrop:" + airdrop.getContractAddress());
        } catch (Exception e) {
            System.out.println("web3j = Web3j.build(new HttpService(url));");
            return "Web3未空或者私钥不正确";
        }

        if (maps == null || maps.size() == 0) {
            System.out.println("maps == null || maps.size() == 0");
            return "空投列表为空";
        }

        // 遍历maps，取出每个AirdropBatchLog，分别写入from_addr_list和gas_list，然后调用空投接口
        // 定义两个list，存放所有的from_addr和gas
        List<String> from_addr_list = new ArrayList<>();
        List<BigDecimal> gas_list = new ArrayList<BigDecimal>();

        maps.forEach(map -> {
            // 参数校验
            if (map.getAddress() != null && map.getAmount() != null) {
                from_addr_list.add(map.getAddress());
                BigDecimal bigGas = new BigDecimal(map.getAmount());
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

        try {
            int _nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.PENDING).send().getTransactionCount().intValue();
            String _gasPrice = "1";
            int _gasLimit = 6000000;
            String _to = contractAddress;
            String _value = totalGasBig.toString();
            String _data = airdrop.multiTransfer_OST(from_addr_list, gas_list_big).encodeFunctionCall().substring(2);
            RawTransaction rawTransaction = airdropContractService.newRawTx(_nonce, _gasPrice, _gasLimit, _to, _value, _data);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainid, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
            if (ethSendTransaction.hasError()) {
                System.out.println("空投失败");
                return "空投失败";
            }
            hash = ethSendTransaction.getTransactionHash();
            System.out.println("hash:" + hash);
            if (!hash.startsWith("0x")) {
                System.out.println("失败成功，hash不正确");
                return "空投失败";
            }

            // 保存到数据库当中
            // 将gas从BigDecimal转为String类型，不用科学记数法
            String gasStr = totalGasBig.toString();
            // adDate为当前时间用yyyy-MM-dd格式化后的字符串
            String adDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            // 更新数据库airdrop表和airdrop_batch_log表
            AirdropBatchLog log = new AirdropBatchLog();
            log.setBatchIndex(batchIndex);
            log.setAdStatus("1");
            log.setAirdropTime(adDate);
            log.setAirdropHash(hash);
            airdropBatchLogService.updateAirdropBatchLog(log); // 更新airdrop_batch_log表

            // 更新airdrop表，里面包括统计数据
            Airdrop adrop = new Airdrop();
            adrop.setAddress(batchIndex);
            adrop.setGas(gasStr);
            adrop.setNowday(adDate);
            adrop.setTxhash(hash);
            adrop.setResult("success");
            adrop.setStart(start);
            adrop.setEnd(end);
            airdropService.insertAirdrop(adrop); // 更新airdrop表

            return "success," + hash;
        } catch (Exception ex) {
            System.out.println("ex:" + ex);
            return "空投失败";
        }
    }

//    public String sqlfail = "select * from (SELECT * FROM `txchain_scan_sh`.`transaction_info` WHERE `txstatus` = '0x0' AND `input` LIKE '%418d1873a4440c6e0a16d45dcd328a214f697dd5%' AND `input` LIKE '%f89c8c3cf0d39745f9f691fc2839572ddc00e02f%' ORDER BY `timestamp` asc ) a JOIN token_transfer b on a.thash = b.tx_hash;";
    // 指定时间段查询get空投列表
    public List getSepcGas(Long start, Long end) {
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
        String sqlconfig = "SELECT  c.from_addr AS from_addr, sum( c.gas_used * c.gas_price ) AS gas  FROM  ( SELECT  from_addr,  gas_used,  gas_price  FROM  account_token a  LEFT JOIN transaction_info b ON a.address = b.from_addr   WHERE   a.token_address = '%s' && convert(a.balance,signed) >= %d && b.txstatus = '0x1'   AND `TIMESTAMP` > %d    AND `TIMESTAMP` <= %d  ) c  GROUP BY  c.from_addr";
        String sql2 = String.format(sqlconfig, tokenAddress, amount, start, end);
        System.out.println(sql2);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql2);
        if (maps == null) {
            maps = new ArrayList<>();
        }
        return maps;
    }

    // 时间戳过来，返回批次号，格式为yyyyMMddHH，如果跨小时，就拼接上end的yyyyMMddHH，中间用_分隔
    public String getBatchNo(long start, long end) {
        // 就用start时间戳的yyyyMMddHH字符串表示
        // start是秒级时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        String startStr = sdf.format(new Date(start * 1000));

        // 计算start和end之间相差的小时数，如果大于1小时，就拼接上end的_yyyyMMddHH字符串
        // end是秒级时间戳
        String endStr = sdf.format(new Date(end * 1000));
        if (!startStr.equals(endStr)) {
            startStr = startStr + "_" + endStr;
        }
        return startStr;
    }
}
