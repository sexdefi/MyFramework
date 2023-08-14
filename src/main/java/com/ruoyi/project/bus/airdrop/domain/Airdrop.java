package com.ruoyi.project.bus.airdrop.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * airdrop对象 airdrop
 * 
 * @author ruoyi
 * @date 2023-08-14
 */
public class Airdrop extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 返还GAS */
    @Excel(name = "返还GAS")
    private String gas;

    /** 操作日期 */
    @Excel(name = "操作日期")
    private String nowday;

    /** 交易哈希 */
    @Excel(name = "交易哈希")
    private String txhash;

    /** 批次 */
    @Excel(name = "批次")
    private String result;

    /** 开始 */
    @Excel(name = "开始")
    private String start;

    /** 结束 */
    @Excel(name = "结束")
    private String end;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }
    public void setGas(String gas)
    {
        this.gas = gas;
    }

    public String getGas()
    {
        return gas;
    }
    public void setNowday(String nowday)
    {
        this.nowday = nowday;
    }

    public String getNowday()
    {
        return nowday;
    }
    public void setTxhash(String txhash)
    {
        this.txhash = txhash;
    }

    public String getTxhash()
    {
        return txhash;
    }
    public void setResult(String result)
    {
        this.result = result;
    }

    public String getResult()
    {
        return result;
    }
    public void setStart(String start)
    {
        this.start = start;
    }

    public String getStart()
    {
        return start;
    }
    public void setEnd(String end)
    {
        this.end = end;
    }

    public String getEnd()
    {
        return end;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("address", getAddress())
            .append("gas", getGas())
            .append("nowday", getNowday())
            .append("txhash", getTxhash())
            .append("result", getResult())
            .append("start", getStart())
            .append("end", getEnd())
            .toString();
    }
}
