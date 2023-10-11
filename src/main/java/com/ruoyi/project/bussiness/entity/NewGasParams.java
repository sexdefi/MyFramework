package com.ruoyi.project.bussiness.entity;

import lombok.Data;

@Data
public class NewGasParams extends GasParams{
    public String chainId;
    public String token;
}
