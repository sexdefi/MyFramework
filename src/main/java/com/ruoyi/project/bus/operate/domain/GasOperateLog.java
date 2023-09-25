package com.ruoyi.project.bus.operate.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * gas领取操作记录表对象 gas_operate_log
 * 
 * @author ruoyi
 * @date 2023-09-25
 */
public class GasOperateLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 地址 */
    @Excel(name = "地址")
    private String userAddr;

    /** 操作类型 */
    @Excel(name = "操作类型")
    private String type;

    /** 金额 */
    @Excel(name = "金额")
    private String amount;

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
    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }
    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getAmount()
    {
        return amount;
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
            .append("type", getType())
            .append("amount", getAmount())
            .append("optime", getOptime())
            .append("remark", getRemark())
            .toString();
    }
}
