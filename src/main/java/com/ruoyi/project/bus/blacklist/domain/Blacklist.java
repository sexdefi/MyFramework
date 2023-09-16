package com.ruoyi.project.bus.blacklist.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 黑名单对象 blacklist
 * 
 * @author ruoyi
 * @date 2023-09-16
 */
public class Blacklist extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增ID */
    private Long id;

    /** 黑名单地址 */
    @Excel(name = "黑名单地址")
    private String addr;

    /** 是否可用 */
    @Excel(name = "是否可用")
    private Long enable;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setAddr(String addr)
    {
        this.addr = addr;
    }

    public String getAddr()
    {
        return addr;
    }
    public void setEnable(Long enable)
    {
        this.enable = enable;
    }

    public Long getEnable()
    {
        return enable;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("addr", getAddr())
            .append("remark", getRemark())
            .append("enable", getEnable())
            .toString();
    }
}
