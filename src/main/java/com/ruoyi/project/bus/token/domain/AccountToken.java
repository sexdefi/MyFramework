package com.ruoyi.project.bus.token.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 代币余额表对象 account_token
 * 
 * @author ruoyi
 * @date 2023-09-14
 */
public class AccountToken extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 代币数量 */
    @Excel(name = "代币数量")
    private String balance;

    /** 创建时间 */
    private Date fCreateAt;

    /**  */
    private Long erc1155id;

    /** 代币地址 */
    @Excel(name = "代币地址")
    private String tokenAddress;

    /** 代币名称 */
    @Excel(name = "代币名称")
    private String tokenName;

    /** 代币类型 */
    private Long tokenType;

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
    public void setBalance(String balance)
    {
        this.balance = balance;
    }

    public String getBalance()
    {
        return balance;
    }
    public void setfCreateAt(Date fCreateAt)
    {
        this.fCreateAt = fCreateAt;
    }

    public Date getfCreateAt()
    {
        return fCreateAt;
    }
    public void setErc1155id(Long erc1155id)
    {
        this.erc1155id = erc1155id;
    }

    public Long getErc1155id()
    {
        return erc1155id;
    }
    public void setTokenAddress(String tokenAddress)
    {
        this.tokenAddress = tokenAddress;
    }

    public String getTokenAddress()
    {
        return tokenAddress;
    }
    public void setTokenName(String tokenName)
    {
        this.tokenName = tokenName;
    }

    public String getTokenName()
    {
        return tokenName;
    }
    public void setTokenType(Long tokenType)
    {
        this.tokenType = tokenType;
    }

    public Long getTokenType()
    {
        return tokenType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("address", getAddress())
            .append("balance", getBalance())
            .append("fCreateAt", getfCreateAt())
            .append("erc1155id", getErc1155id())
            .append("tokenAddress", getTokenAddress())
            .append("tokenName", getTokenName())
            .append("tokenType", getTokenType())
            .toString();
    }
}
