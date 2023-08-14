package com.ruoyi.project.bussiness.service;


import com.ruoyi.common.utils.LogUtils;
import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
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
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        return getSepcGasLastDay(longs[0],longs[1]);
    }

    @GetMapping("/giveGasListByDay")
    @ResponseBody
    @ApiOperation(value = "giveGasListByDay", notes = "按照时间段空投GAS")
    public List giveGasListByDay(@ApiParam(name = "start", value = "start", required = true) String start,
                                  @ApiParam(name = "end", value = "end", required = true) String end) {
        List r = new ArrayList();
        r.add("空投失败");
        try {
            Long[] longs = getTimeStampByDayRestful(start, end);
            List sepcGasLastDay = getSepcGasLastDay(longs[0], longs[1]);
            String res = airdropGasForList(sepcGasLastDay,longs[0],longs[1]);
            if (res.equals("空投成功")) {
                return sepcGasLastDay;
            } else {
                return r;
            }
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
            String res = airdropGasForList(list,start,end);
            if (res.equals("空投成功")) {
                return true;
            } else {
                LogUtils.ERROR_LOG.error("空投失败，失败原因：" + res);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public String sql1 = "select c.from_addr as from_addr,sum(c.gas * c.gas_price) as gas from (select from_addr,gas,gas_price from account a left join transaction_info b on a.address = b.from_addr where a.balance >= %d and `TIMESTAMP`> %d and `TIMESTAMP` <= %d) c GROUP BY c.from_addr;";

    // 指定时间段查询
    public List getSepcGasLastDay(Long start,Long end) {
        Integer amount = config.getConfig("OVER_AMOUNT",500);
        if (amount == null) {
            amount = 500;
        }
        if(start >= end){
            return new ArrayList<>();
        }
        if (start == null) {
            start = DateUtils.addDays(new Date(), -1).getTime() / 1000;
        }
        if (end == null) {
            end = new Date().getTime() / 1000;
        }

        String sql2 = String.format(sql1,amount,start,end);
        System.out.println(sql2);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql2);
        if (maps == null) {
            maps = new ArrayList<>();
        }
        return maps;
    }


    public String airdropGasForList(List<Map<String, Object>> maps,Long start,Long end) {

        String url = config.getConfig("RPC_URL","http://127.0.0.1:8545");
        String privateKey = config.getConfig("PRIVATE_KEY","0x46279b753d1397d9ff7a3df97501c4fa4316312620a32a00c2551b81b8be7326");
        Web3j web3j = null;
        Credentials credentials = null;
        try {
            web3j = Web3j.build(new HttpService(url));
            EthBlockNumber send = web3j.ethBlockNumber().send();
            System.out.println("send:" + send.getBlockNumber());
            credentials = Credentials.create(privateKey);
            System.out.println("credentials:" + credentials.getAddress());
        }catch (Exception e){
            System.out.println("web3j = Web3j.build(new HttpService(url));");
            return "Web3未空或者私钥不正确";
        }

        if(maps == null || maps.size() == 0){
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

        if(from_addr_list.size() != gas_list.size()){
            System.out.println("from_addr_list.size() != gas_list.size()");
            return "空投列表异常，数量不匹配";
        }
        if(from_addr_list.size() == 0){
            System.out.println("from_addr_list.size() == 0");
            return "空投列表为空";
        }
        if(web3j == null || credentials == null){
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
            if(balance.compareTo(totalGas.toBigInteger()) < 0){
                System.out.println("账户余额不足，无法空投");
                return "账户余额不足，无法空投";
            }
        }catch (Exception e){
            System.out.println("查询余额时候，异常！");
            return "查询余额时候，异常！";
        }

        int count = 0;
        try {
            for (int i = 0; i < from_addr_list.size(); i++) {
                String addr = from_addr_list.get(i);
                BigDecimal gas = gas_list.get(i);
                String batch = Hash.sha3String(start + "-" + end).substring(2, 8);
                // 判断是否已经空投过了,如果已经空投过了，就不再空投。为了防止定时任务重复空投
                if(airdropDto.isExist(addr,batch)){
                    System.out.println("addr:"+addr+" batch:"+batch+" 已经空投过了");
                    count++;
                    continue;
                }

                String hash = airdropToAddr(web3j, credentials, addr, gas);
                if (hash.equals("false")) {
                    System.out.println("airdropToAddr" + addr + " error");
                }
                // 保存到数据库当中
                // 将gas从BigDecimal转为String类型，不用科学记数法
                String gasStr = gas.toPlainString();
                // adDate为当前时间用yyyy-MM-dd格式化后的字符串
                String adDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                // 使用start、end计算批次，用于查询，hash以后取前8位
                airdropDto.SaveAirdropResultToDb(addr, gasStr, adDate, hash, start, end,batch);
            }
            // 如果count等于from_addr_list.size()，说明所有的地址都已经空投过了，返回false
            boolean r = count != from_addr_list.size();
            if(r) {
                System.out.println("空投成功");
                return "空投成功";
            }else{
                System.out.println("所有地址都已经空投过了");
                return "所有地址都已经空投过了";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "空投过程异常";
        }

    }

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
            if(transactionReceipt != null){
                System.out.println("Transaction "
                        + transactionReceipt.getTransactionHash());
                return transactionReceipt.getTransactionHash();
            }else{
                return "false";
            }
        } catch (Exception e) {
            return "false";
        }
    }

    // 调用airdrop空投合约来进行转账
    public boolean airdrop(Web3j web3j,Credentials credentials,List addrs,List gas){

        return true;
    }


}
