package com.ruoyi.project.bussiness.service;


import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import com.ruoyi.project.bus.Profit.service.IProfitLogService;
import com.ruoyi.project.bus.airdrop.service.IAirdropService;
import com.ruoyi.project.bus.batch.domain.AirdropBatchLog;
import com.ruoyi.project.bus.batch.service.IAirdropBatchLogService;
import com.ruoyi.project.bussiness.common.BusConfigService;
import com.ruoyi.project.bussiness.mapper.AirdropDto;
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
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/newgasfee")
@ApiOperation(value = "GasFee", notes = "空投GAS")
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

    // 合约
    @Autowired
    AirdropDto airdropDto;

    @Autowired
    IAirdropBatchLogService airdropBatchLogService;

    @Autowired
    IProfitLogService profitLogService;

    @Autowired
    IAirdropService airdropService;

    @GetMapping("/getTimeStampByDay")
    @ResponseBody
    @ApiOperation(value = "getTimeStampByDay", notes = "计算时间戳")
    public Long[] getTimeStampByDayRestful(@ApiParam(name = "start", value = "start", required = true) String start,
                                           @ApiParam(name = "end", value = "end", required = true) String end) throws ParseException {
        // 如果start和end是yyyyMMdd的话，就转化为yyyy-MM-dd HH:mm:ss
        if (start.length() == 8) {
            start = start.substring(0, 4) + "-" + start.substring(4, 6) + "-" + start.substring(6, 8) + " 00:00:05";
        }
        if (end.length() == 8) {
            end = end.substring(0, 4) + "-" + end.substring(4, 6) + "-" + end.substring(6, 8) + " 00:00:05";
        }
        // 如果start和end是yyyy-MM-dd的话，就转化为yyyy-MM-dd HH:mm:ss
        if (start.length() == 10) {
            start = start + " 00:00:05";
        }
        if (end.length() == 10) {
            end = end + " 00:00:05";
        }
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

    // 生成当前时间段内空投批次数据，并保存到数据库中
    @GetMapping("/generateGasListByDay")
    @ResponseBody
    @ApiOperation(value = "generateGasListByDay", notes = "生成当前时间段内空投批次数据，并保存到数据库中")
    public boolean generateGasListByDay(@ApiParam(name = "start", value = "start", required = true) String start,
                                     @ApiParam(name = "end", value = "end", required = true) String end) throws ParseException {
        // 避免重新生成
        String batchIdTest = start + "_" + end + "_0";
        // 删除空格
        batchIdTest = batchIdTest.replaceAll(" ", "");
        AirdropBatchLog a = new AirdropBatchLog();
        a.setBatchNo(batchIdTest);
        List airdropBatchLogList = airdropBatchLogService.selectAirdropBatchLogList(a);
        if (airdropBatchLogList.size() > 0) {
            System.out.println("已经生成过了，不需要重新生成，batchIdTest=" + batchIdTest);
            return false;
        }

        // 查询start和end之间的空投批次数据
        Long[] longs = getTimeStampByDayRestful(start, end);
        List<Map<String, Object>> sepcGasList = getSepcGasLastDay(longs[0], longs[1]);
        // 保存到数据库中
        // 遍历sepcGasList，按照180个一组，生成batchID，批量保存到数据库airdrop_batch_log表中
        for (int i = 0; i < sepcGasList.size(); i += 180) {
            List<Map<String, Object>> subList = sepcGasList.subList(i, i + 180 > sepcGasList.size() ? sepcGasList.size() : i + 180);
            String batchId = start + "_" + end + "_" + i;
            // 删除空格
            batchId = batchId.replaceAll(" ", "");
            for (Map<String, Object> item : subList) {
                AirdropBatchLog airdropBatchLog = new AirdropBatchLog();
                airdropBatchLog.setBatchNo(batchId);
                airdropBatchLog.setAddress((String) item.get("from_addr"));
                airdropBatchLog.setAmount((String)item.get("gas"));
                // 从config里面获取token_address
                airdropBatchLog.setTokenAddress("");
                // 从数据库中查询token余额
                airdropBatchLog.setTokenAmount("");
                airdropBatchLog.setAirdropTime("");

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


    @GetMapping("/airdropGasListByDay")
    @ResponseBody
    @ApiOperation(value = "airdropGasListByDay", notes = "按照时间段空投GAS")
    public String airdropGasListByDay(@ApiParam(name = "start", value = "start", required = true) String start,
                                      @ApiParam(name = "end", value = "end", required = true) String end) throws ParseException {
        // 查询start和end之间的空投批次数据，找到未空投的批次，进行空投，即ad_status=0的批次
        // 计算批次号
        String batchIdTest = start + "_" + end + "_";
        // 删除空格
        batchIdTest = batchIdTest.replaceAll(" ", "");

        AirdropBatchLog a = new AirdropBatchLog();
        a.setBatchNo(batchIdTest);
        List airdropBatchLogList = airdropBatchLogService.selectAirdropBatchLogList(a);

        return "";
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

//    @GetMapping("/snapshotGasLastDay")
//    @ResponseBody
//    @ApiOperation(value = "snapshotGasLastDay", notes = "按照当前日期的前一天的时间段来快照，保存到数据库中")
//    public boolean snapshotGasLastDay() {
//        Date date = DateUtils.addDays(new Date(), -1);
//        System.out.println(date.getTime());
//        Long start = date.getTime() / 1000;
//        Long end = new Date().getTime() / 1000;
//        List list = getSepcGasLastDay(start, end);
//        return saveGasList(list);
//    }

    //将快照和空投批次写入到数据库中。
    //表格字段有：自增id，批次号，用户地址，token地址，token数量，花费gas数量，空投时间，快照时间，空投状态（0：未空投，1：空投中，2：空投成功，3：空投失败），空投hash
    //批次号生成规则：当前时间yyyyMMdd+4位顺序号
    // 建立表的SQL语句：
    // CREATE TABLE `airdrop_batch_log` (
    //  `id` int(11) NOT NULL AUTO_INCREMENT,
    //  `batch_no` varchar(20) DEFAULT NULL COMMENT '批次号',
    //  `address` varchar(100) DEFAULT NULL COMMENT '地址',
    //  `token_address` varchar(100) DEFAULT NULL COMMENT '快照token地址',
    //  `token_amount` varchar(255) DEFAULT NULL COMMENT '快照token数量',
    //  `amount` varchar(255) DEFAULT NULL COMMENT '空投数量',
    //  `airdrop_time` varchar(100) DEFAULT NULL COMMENT '空投时间',
    //  `snapshot_time` varchar(100) DEFAULT NULL COMMENT '快照时间',
    //  `ad_status` varchar(10) DEFAULT NULL COMMENT '空投状态（0：未空投，1：空投中，2：空投成功，3：空投失败）',
    //  `airdrop_hash` varchar(100) DEFAULT NULL COMMENT '空投hash',
    //  PRIMARY KEY (`id`)
    //) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='空投批次快照表';

    // 当天交易数据表，字段包括：自增id，日期(yyyyMMdd)，交易笔数，总交易手续费，交易费返还，成功交易的手续费，交易失败的手续费，利润，备注
    // 建表语句：
    // CREATE TABLE `profit_log` (
    //  `id` int(11) NOT NULL AUTO_INCREMENT,
    //  `clear_date` varchar(20) DEFAULT NULL COMMENT '日期',
    //  `tx_count` varchar(20) DEFAULT NULL COMMENT '交易笔数',
    //  `total_fee` varchar(20) DEFAULT NULL COMMENT '总交易手续费',
    //  `fee_return` varchar(20) DEFAULT NULL COMMENT '交易费返还',
    //  `success_fee` varchar(20) DEFAULT NULL COMMENT '成功交易的手续费',
    //  `fail_fee` varchar(20) DEFAULT NULL COMMENT '交易失败的手续费',
    //  `profit` varchar(20) DEFAULT NULL COMMENT '利润',
    //  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
    //  PRIMARY KEY (`id`)
    //) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='当天交易数据表';

//
//
//    private boolean saveGasList(List list) {
//        // 遍历list，每150条生成一个批次号，然后写入数据库
//        int size = list.size();
//        int count = size / 150;
//        int mod = size % 150;
//        if (mod > 0) {
//            count++;
//        }
//        String batchPrefix = "";
//        try {
//            // 获取今天的yyyyMMdd
//            batchPrefix = com.ruoyi.common.utils.DateUtils.dateTimeNow("yyyyMMdd");
//        }catch (Exception e){
//            return false;
//        }
//
//        for (int i = 0; i < count; i++) {
//            String batchNo =  batchPrefix + String.format("%04d", i + 1);
//            List subList = list.subList(i * 150, (i + 1) * 150 > size ? size : (i + 1) * 150);
//            for (int j = 0; j < subList.size(); j++) {
//                Map map = (Map) subList.get(j);
//                String address = (String) map.get("from_addr");
//                Double gas = (Double) map.get("gas");
//                BigDecimal amount = new BigDecimal(gas);
//                // 空投时间的0点
//                Date airdropTime = DateUtils.truncate(new Date(), Calendar.DATE);
//                // 前一天
//                Date gasStartTime = DateUtils.addDays(new Date(), -1);
//                Date gasSnapshotTime = new Date();
//                try {
//                    String sql = "insert into airdrop_batch_log(batch_no,address,amount,airdrop_time,gas_start_time,gas_snapshot_time,gstatus,tx_hash) values(?,?,?,?,?,?,?,?)";
//                    jdbcTemplate.update(sql, batchNo, address, amount, airdropTime, gasStartTime, gasSnapshotTime, 0, "");
//                } catch (Exception e) {
//                    System.out.println("保存失败：：：：批次号：" + batchNo + "，地址：" + address + "，空投数量：" + amount + "，空投时间：" + airdropTime.getTime() + "，Gas开始时间：" + gasStartTime.getTime() + "，Gas快照时间：" + gasSnapshotTime.getTime() + "，空投状态：0");
//                }
//            }
//        }
//        return true;
//    }




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

    // 查询规定时间段内，失败的交易的列表，并join account_token表，查询出对应的token_amount.

    // 查询规定时间内，总的交易手续费

    // 查询规定时间内，指定token的fail交易哈希列表

    // 快照机制，查询指定时间的token余额，进行保存。


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
