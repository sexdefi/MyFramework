package com.ruoyi.project.bussiness.mapper;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@DataSource(value = DataSourceType.MASTER)
@Service
public class AirdropDto {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public boolean SaveAirdropResultToDb(String address, String gas, String datestring, String txHash,Long start,Long end, String result) {
        try {
            String sql = "insert into airdrop(address,gas,nowday,txhash,start,end,result) values(?,?,?,?,?,?,?)";
            int update = jdbcTemplate.update(sql, address, gas, datestring, txHash,start,end, result);
            System.out.println("update:" + update);
            System.out.println("address:" + address);
            System.out.println("gas:" + gas);
            System.out.println("datestring:" + datestring);
            System.out.println("txHash:" + txHash);
            System.out.println("result:" + result);
            if (update == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isExist(String address,String result){
        try {
            String sql = "select count(*) from airdrop where address=? and result=?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, address,result);
            if (count == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAirdropped(String address,Long start){
        // 判断是否已经空投过，同一天如果空投过，就不再空投
        try {
            String sql = "select count(*) from airdrop where address=? and start>=? and end <= ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, address,start,start);
            if (count == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
