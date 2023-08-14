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
}
