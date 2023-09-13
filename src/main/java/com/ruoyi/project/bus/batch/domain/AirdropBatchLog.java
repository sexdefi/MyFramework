package com.ruoyi.project.bus.batch.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 空投批次快照对象 airdrop_batch_log
 * 
 * @author ruoyi
 * @date 2023-09-13
 */
public class AirdropBatchLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 批次号 */
    @Excel(name = "批次号")
    private String batchNo;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 快照token地址 */
    @Excel(name = "快照token地址")
    private String tokenAddress;

    /** 快照token数量 */
    @Excel(name = "快照token数量")
    private String tokenAmount;

    /** 空投数量 */
    @Excel(name = "空投数量")
    private String amount;

    /** 空投时间 */
    @Excel(name = "空投时间")
    private String airdropTime;

    /** 快照时间 */
    @Excel(name = "快照时间")
    private String snapshotTime;

    /** 空投状态（0：未空投，1：空投中，2：空投成功，3：空投失败） */
    @Excel(name = "空投状态", readConverterExp = "0=：未空投，1：空投中，2：空投成功，3：空投失败")
    private String adStatus;

    /** 空投hash */
    @Excel(name = "空投hash")
    private String airdropHash;

    /** 空投ID */
    @Excel(name = "空投ID")
    private String batchIndex;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo()
    {
        return batchNo;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }
    public void setTokenAddress(String tokenAddress)
    {
        this.tokenAddress = tokenAddress;
    }

    public String getTokenAddress()
    {
        return tokenAddress;
    }
    public void setTokenAmount(String tokenAmount)
    {
        this.tokenAmount = tokenAmount;
    }

    public String getTokenAmount()
    {
        return tokenAmount;
    }
    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getAmount()
    {
        return amount;
    }
    public void setAirdropTime(String airdropTime)
    {
        this.airdropTime = airdropTime;
    }

    public String getAirdropTime()
    {
        return airdropTime;
    }
    public void setSnapshotTime(String snapshotTime)
    {
        this.snapshotTime = snapshotTime;
    }

    public String getSnapshotTime()
    {
        return snapshotTime;
    }
    public void setAdStatus(String adStatus)
    {
        this.adStatus = adStatus;
    }

    public String getAdStatus()
    {
        return adStatus;
    }
    public void setAirdropHash(String airdropHash)
    {
        this.airdropHash = airdropHash;
    }

    public String getAirdropHash()
    {
        return airdropHash;
    }
    public void setBatchIndex(String batchIndex)
    {
        this.batchIndex = batchIndex;
    }

    public String getBatchIndex()
    {
        return batchIndex;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("batchNo", getBatchNo())
            .append("address", getAddress())
            .append("tokenAddress", getTokenAddress())
            .append("tokenAmount", getTokenAmount())
            .append("amount", getAmount())
            .append("airdropTime", getAirdropTime())
            .append("snapshotTime", getSnapshotTime())
            .append("adStatus", getAdStatus())
            .append("airdropHash", getAirdropHash())
            .append("batchIndex", getBatchIndex())
            .append("remark", getRemark())
            .toString();
    }
}
