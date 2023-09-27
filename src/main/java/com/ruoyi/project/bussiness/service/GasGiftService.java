package com.ruoyi.project.bussiness.service;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import com.ruoyi.project.bussiness.common.BusConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@DataSource(value = DataSourceType.SLAVE)
public class GasGiftService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BusConfigService config;

    public String sumGasSQL =
            "SELECT from_addr,sum(gas_used * gas_price) AS gas FROM transaction_info WHERE  from_addr = '%s' AND `TIMESTAMP` >= %d AND txstatus = '0x1' GROUP BY from_addr;";
    public String sumGasSQL2 = "select * from transaction_info";

    @DataSource(value = DataSourceType.SLAVE)
    public String getGasAmount(String address, Long lastTimeLong) {
        String tokenAddress = config.getConfig("TOKEN_ADDRESS", "0x9599695608BE59a420d7b9A32f3AbFc362d88d36");
        Integer amount = config.getConfig("OVER_AMOUNT", 500);
        // 查询所有的交易记录，统计gas
        String sql = String.format(sumGasSQL, address, lastTimeLong);

        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sumGasSQL2);
        if(maps == null || maps.size() == 0){
            return "0";
        }
        BigDecimal totalGas = new BigDecimal(0);
        for (Map<String, Object> map : maps) {
            BigDecimal gas = (BigDecimal) map.get("gas");
            totalGas = totalGas.add(gas);
        }
        // 返回非科学计数法的值
        String plainString = totalGas.toPlainString();
        return plainString;

    }

}
