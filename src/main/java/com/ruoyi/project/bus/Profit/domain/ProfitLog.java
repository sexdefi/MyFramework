package com.ruoyi.project.bus.Profit.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 当天交易数据对象 profit_log
 * 
 * @author ruoyi
 * @date 2023-09-13
 */
public class ProfitLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 日期 */
    @Excel(name = "日期")
    private String clearDate;

    /** 交易笔数 */
    @Excel(name = "交易笔数")
    private String txCount;

    /** 总交易手续费 */
    @Excel(name = "总交易手续费")
    private String totalFee;

    /** 交易费返还 */
    @Excel(name = "交易费返还")
    private String feeReturn;

    /** 成功交易的手续费 */
    @Excel(name = "成功交易的手续费")
    private String successFee;

    /** 交易失败的手续费 */
    @Excel(name = "交易失败的手续费")
    private String failFee;

    /** 利润 */
    @Excel(name = "利润")
    private String profit;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setClearDate(String clearDate)
    {
        this.clearDate = clearDate;
    }

    public String getClearDate()
    {
        return clearDate;
    }
    public void setTxCount(String txCount)
    {
        this.txCount = txCount;
    }

    public String getTxCount()
    {
        return txCount;
    }
    public void setTotalFee(String totalFee)
    {
        this.totalFee = totalFee;
    }

    public String getTotalFee()
    {
        return totalFee;
    }
    public void setFeeReturn(String feeReturn)
    {
        this.feeReturn = feeReturn;
    }

    public String getFeeReturn()
    {
        return feeReturn;
    }
    public void setSuccessFee(String successFee)
    {
        this.successFee = successFee;
    }

    public String getSuccessFee()
    {
        return successFee;
    }
    public void setFailFee(String failFee)
    {
        this.failFee = failFee;
    }

    public String getFailFee()
    {
        return failFee;
    }
    public void setProfit(String profit)
    {
        this.profit = profit;
    }

    public String getProfit()
    {
        return profit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("clearDate", getClearDate())
            .append("txCount", getTxCount())
            .append("totalFee", getTotalFee())
            .append("feeReturn", getFeeReturn())
            .append("successFee", getSuccessFee())
            .append("failFee", getFailFee())
            .append("profit", getProfit())
            .append("remark", getRemark())
            .toString();
    }
}
