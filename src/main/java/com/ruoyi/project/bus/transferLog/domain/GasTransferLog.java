package com.ruoyi.project.bus.transferLog.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * Gas领取空投记录对象 gas_transfer_log
 * 
 * @author ruoyi
 * @date 2023-09-25
 */
public class GasTransferLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 地址 */
    @Excel(name = "地址")
    private String userAddr;

    /** 转账金额 */
    @Excel(name = "转账金额")
    private String amount;

    /** 交易哈希 */
    @Excel(name = "交易哈希")
    private String txhash;

    /** 操作时间 */
    @Excel(name = "操作时间")
    private String optime;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setUserAddr(String userAddr)
    {
        this.userAddr = userAddr;
    }

    public String getUserAddr()
    {
        return userAddr;
    }
    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getAmount()
    {
        return amount;
    }
    public void setTxhash(String txhash)
    {
        this.txhash = txhash;
    }

    public String getTxhash()
    {
        return txhash;
    }
    public void setOptime(String optime)
    {
        this.optime = optime;
    }

    public String getOptime()
    {
        return optime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userAddr", getUserAddr())
            .append("amount", getAmount())
            .append("txhash", getTxhash())
            .append("optime", getOptime())
            .append("remark", getRemark())
            .toString();
    }
}
