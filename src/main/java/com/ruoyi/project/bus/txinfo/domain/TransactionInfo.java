package com.ruoyi.project.bus.txinfo.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 交易记录对象 transaction_info
 * 
 * @author ruoyi
 * @date 2023-09-14
 */
public class TransactionInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 交易哈希 */
    private String thash;

    /**  */
    private String actualData;

    /** 区块哈希 */
    private String blockHash;

    /** 区块号 */
    @Excel(name = "区块号")
    private Long blockNumber;

    /** 合约地址 */
    @Excel(name = "合约地址")
    private String contractAddress;

    /** 创建者 */
    private String creates;

    /** 费率 */
    private Long feePercent;

    /** From */
    @Excel(name = "From")
    private String fromAddr;

    /** gasInput */
    private String gas;

    /** gas价格 */
    @Excel(name = "gas价格")
    private String gasPrice;

    /** gas消耗 */
    @Excel(name = "gas消耗")
    private String gasUsed;

    /**  */
    private String input;

    /**  */
    private String metaAddress;

    /**  */
    private Long nonce;

    /** 时间戳 */
    @Excel(name = "时间戳")
    private Long timestamp;

    /** To */
    @Excel(name = "To")
    private String toAddr;

    /**  */
    private Long transactionIndex;

    /** 交易状态 */
    @Excel(name = "交易状态")
    private String txstatus;

    /**  */
    @Excel(name = "")
    private String value;

    public void setThash(String thash)
    {
        this.thash = thash;
    }

    public String getThash()
    {
        return thash;
    }
    public void setActualData(String actualData)
    {
        this.actualData = actualData;
    }

    public String getActualData()
    {
        return actualData;
    }
    public void setBlockHash(String blockHash)
    {
        this.blockHash = blockHash;
    }

    public String getBlockHash()
    {
        return blockHash;
    }
    public void setBlockNumber(Long blockNumber)
    {
        this.blockNumber = blockNumber;
    }

    public Long getBlockNumber()
    {
        return blockNumber;
    }
    public void setContractAddress(String contractAddress)
    {
        this.contractAddress = contractAddress;
    }

    public String getContractAddress()
    {
        return contractAddress;
    }
    public void setCreates(String creates)
    {
        this.creates = creates;
    }

    public String getCreates()
    {
        return creates;
    }
    public void setFeePercent(Long feePercent)
    {
        this.feePercent = feePercent;
    }

    public Long getFeePercent()
    {
        return feePercent;
    }
    public void setFromAddr(String fromAddr)
    {
        this.fromAddr = fromAddr;
    }

    public String getFromAddr()
    {
        return fromAddr;
    }
    public void setGas(String gas)
    {
        this.gas = gas;
    }

    public String getGas()
    {
        return gas;
    }
    public void setGasPrice(String gasPrice)
    {
        this.gasPrice = gasPrice;
    }

    public String getGasPrice()
    {
        return gasPrice;
    }
    public void setGasUsed(String gasUsed)
    {
        this.gasUsed = gasUsed;
    }

    public String getGasUsed()
    {
        return gasUsed;
    }
    public void setInput(String input)
    {
        this.input = input;
    }

    public String getInput()
    {
        return input;
    }
    public void setMetaAddress(String metaAddress)
    {
        this.metaAddress = metaAddress;
    }

    public String getMetaAddress()
    {
        return metaAddress;
    }
    public void setNonce(Long nonce)
    {
        this.nonce = nonce;
    }

    public Long getNonce()
    {
        return nonce;
    }
    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }

    public Long getTimestamp()
    {
        return timestamp;
    }
    public void setToAddr(String toAddr)
    {
        this.toAddr = toAddr;
    }

    public String getToAddr()
    {
        return toAddr;
    }
    public void setTransactionIndex(Long transactionIndex)
    {
        this.transactionIndex = transactionIndex;
    }

    public Long getTransactionIndex()
    {
        return transactionIndex;
    }
    public void setTxstatus(String txstatus)
    {
        this.txstatus = txstatus;
    }

    public String getTxstatus()
    {
        return txstatus;
    }
    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("thash", getThash())
            .append("actualData", getActualData())
            .append("blockHash", getBlockHash())
            .append("blockNumber", getBlockNumber())
            .append("contractAddress", getContractAddress())
            .append("creates", getCreates())
            .append("feePercent", getFeePercent())
            .append("fromAddr", getFromAddr())
            .append("gas", getGas())
            .append("gasPrice", getGasPrice())
            .append("gasUsed", getGasUsed())
            .append("input", getInput())
            .append("metaAddress", getMetaAddress())
            .append("nonce", getNonce())
            .append("timestamp", getTimestamp())
            .append("toAddr", getToAddr())
            .append("transactionIndex", getTransactionIndex())
            .append("txstatus", getTxstatus())
            .append("value", getValue())
            .toString();
    }
}
