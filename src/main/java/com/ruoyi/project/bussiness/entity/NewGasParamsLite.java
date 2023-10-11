package com.ruoyi.project.bussiness.entity;

import lombok.Data;

@Data
public class NewGasParamsLite extends GasParamsLite{
    public String chainId;
    public String token;
}
