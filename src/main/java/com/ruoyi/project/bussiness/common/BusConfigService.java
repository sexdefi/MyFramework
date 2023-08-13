package com.ruoyi.project.bussiness.common;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.project.system.config.service.IConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BusConfigService {
    @Autowired
    IConfigService configService;

    public String getConfig(String key, String defaultValues) {
        try {
            String value = configService.selectConfigByKey(key);
            if (StringUtils.isEmpty(value))
                return defaultValues;
            return value;
        } catch (Exception e) {
            return defaultValues;
        }
    }


    public int getConfig(String key, int defaultValues) {
        try {
            String value = configService.selectConfigByKey(key);
            if (StringUtils.isEmpty(value))
                return defaultValues;
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValues;
        }
    }

    public long getConfig(String key, long defaultValues) {
        try {
            String value = configService.selectConfigByKey(key);
            if (StringUtils.isEmpty(value))
                return defaultValues;
            return Long.parseLong(value);
        } catch (Exception e) {
            return defaultValues;
        }
    }

    public double getConfig(String key, double defaultValues) {
        try {
            String value = configService.selectConfigByKey(key);
            if (StringUtils.isEmpty(value))
                return defaultValues;
            return Double.parseDouble(value);
        } catch (Exception e) {
            return defaultValues;
        }
    }

    public Date getConfigDate(String key, String defaultValues){
        try {
            String value = configService.selectConfigByKey(key);
            if (StringUtils.isEmpty(value))
                value = defaultValues;
            return DateUtils.parseDate(value);
        } catch (Exception e) {
            return DateUtils.parseDate(defaultValues);
        }
    }

    public String[] getConfig(String key, String defaultValues,String sep) {
        try {
            String value = configService.selectConfigByKey(key);
            if (StringUtils.isEmpty(value))
                value = defaultValues;
            String[] split = value.split(sep);
            if(split == null || split.length == 0){
                return defaultValues.split(sep);
            }else{
                return value.split(sep);
            }
        } catch (Exception e) {
            return defaultValues.split(sep);
        }
    }
}
