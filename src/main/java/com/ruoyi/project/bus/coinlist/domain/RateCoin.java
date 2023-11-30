package com.ruoyi.project.bus.coinlist.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 有费率的币对象 rate_coin
 * 
 * @author ruoyi
 * @date 2023-11-30
 */
public class RateCoin extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 币地址 */
    @Excel(name = "币地址")
    private String coinName;

    /** 钱包地址 */
    @Excel(name = "钱包地址")
    private String tokenAddress;

    /** 数据状态:1，有效，0，无效 */
    @Excel(name = "数据状态:1，有效，0，无效")
    private Integer dataStatus;

    /** 链 */
    @Excel(name = "链")
    private String chainType;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setCoinName(String coinName)
    {
        this.coinName = coinName;
    }

    public String getCoinName()
    {
        return coinName;
    }
    public void setTokenAddress(String tokenAddress)
    {
        this.tokenAddress = tokenAddress;
    }

    public String getTokenAddress()
    {
        return tokenAddress;
    }
    public void setDataStatus(Integer dataStatus)
    {
        this.dataStatus = dataStatus;
    }

    public Integer getDataStatus()
    {
        return dataStatus;
    }
    public void setChainType(String chainType)
    {
        this.chainType = chainType;
    }

    public String getChainType()
    {
        return chainType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("coinName", getCoinName())
            .append("tokenAddress", getTokenAddress())
            .append("dataStatus", getDataStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("chainType", getChainType())
            .toString();
    }
}
