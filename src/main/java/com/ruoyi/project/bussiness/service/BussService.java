package com.ruoyi.project.bussiness.service;


import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import com.ruoyi.project.bussiness.common.BusConfigService;
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
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tttt")
@DataSource(value = DataSourceType.SLAVE)
//只需要在需要切换数据源的方法上使用该注解即可
public class BussService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BusConfigService config;

    public String sql1 = "select c.from_addr as from_addr,sum(c.gas * c.gas_price) as gas from (select from_addr,gas,gas_price from account a left join transaction_info b on a.address = b.from_addr where a.balance >= %d and `TIMESTAMP`> %d and `TIMESTAMP` <= %d) c GROUP BY c.from_addr;";

    // 按照当前日期的前一天的时间段来查询
    public List getGasLastDay() {

        Date date = DateUtils.addDays(new Date(), -1);
        System.out.println(date.getTime());
        Long start = date.getTime() / 1000;
        Long end = new Date().getTime() / 1000;

        return getSepcGasLastDay(start,end);
    }

    // 指定时间段查询
    public List getSepcGasLastDay(Long start,Long end) {
        Integer amount = config.getConfig("OVER_AMOUNT",500);
        String sql2 = String.format(sql1,amount,start,end);
        System.out.println(sql2);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql2);
        if (maps == null) {
            maps = new ArrayList<>();
        }
        return maps;
    }


    @GetMapping("/getGasListByDay")
    @ResponseBody
    @ApiOperation(value = "getGasLastDay", notes = "getGasLastDay")
    public List getGasLastDayRestful(@ApiParam(name = "start", value = "start", required = true) Long start,
                                     @ApiParam(name = "end", value = "end", required = true) Long end) {
        return getSepcGasLastDay(start,end);
    }

    @GetMapping("/getGasLastDay")
    @ResponseBody
    @ApiOperation(value = "getGasLastDay", notes = "getGasLastDay")
    public List getGasLastDayRestful() {
        return getGasLastDay();
    }

    @GetMapping("/giveGasLastDay")
    @ResponseBody
    @ApiOperation(value = "giveGasLastDay", notes = "getGasLastDay")
    public boolean giveGasLastDay() {
        List gasLastDay = getGasLastDay();
        try {
            getGasToList(gasLastDay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean getGasToList(List<Map<String, Object>> maps) {

        String url = config.getConfig("RPC_URL","http://127.0.0.1:8545");
        String privateKey = config.getConfig("PRIVATE_KEY","0x46279b753d1397d9ff7a3df97501c4fa4316312620a32a00c2551b81b8be7326");
        Web3j web3j = Web3j.build(new HttpService(url));

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
        Credentials credentials = Credentials.create(privateKey);
//        System.out.println("from_addr_list:" + from_addr_list);
//        System.out.println("gas_list:" + gas_list);
        if(from_addr_list.size() != gas_list.size()){
            System.out.println("from_addr_list.size() != gas_list.size()");
            return false;
        }
        if(from_addr_list.size() == 0){
            System.out.println("from_addr_list.size() == 0");
            return false;
        }
        try {
            for (int i = 0; i < from_addr_list.size(); i++) {
                String addr = from_addr_list.get(i);
                BigDecimal gas = gas_list.get(i);
                boolean res = airdropToAddr(web3j, credentials, addr, gas);
                if (!res) {
                    System.out.println("airdropToAddr" + addr + " error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 单个地址转账，批量请使用空投合约
    @Log(title = "空投Gas", businessType = BusinessType.OTHER)

    public boolean airdropToAddr(Web3j web3j, Credentials credentials, String addr, BigDecimal gas) {
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
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    // 调用airdrop空投合约来进行转账
    public boolean airdrop(Web3j web3j,Credentials credentials,List addrs,List gas){

        return true;
    }


}
