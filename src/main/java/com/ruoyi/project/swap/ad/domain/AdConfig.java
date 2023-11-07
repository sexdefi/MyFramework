package com.ruoyi.project.swap.ad.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 广告Banner对象 ad_config
 * 
 * @author ruoyi
 * @date 2023-11-07
 */
public class AdConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 描述 */
    @Excel(name = "描述")
    private String des;

    /** 跳转链接 */
    @Excel(name = "跳转链接")
    private String jumpUrl;

    /** 图像地址 */
    @Excel(name = "图像地址")
    private String image;

    /** 分类 */
    @Excel(name = "分类")
    private String classification;

    /** 排序 */
    @Excel(name = "排序")
    private Long weights;

    /** 数据状态 */
    @Excel(name = "数据状态")
    private Long dataStatus;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }
    public void setDes(String des)
    {
        this.des = des;
    }

    public String getDes()
    {
        return des;
    }
    public void setJumpUrl(String jumpUrl)
    {
        this.jumpUrl = jumpUrl;
    }

    public String getJumpUrl()
    {
        return jumpUrl;
    }
    public void setImage(String image)
    {
        this.image = image;
    }

    public String getImage()
    {
        return image;
    }
    public void setClassification(String classification)
    {
        this.classification = classification;
    }

    public String getClassification()
    {
        return classification;
    }
    public void setWeights(Long weights)
    {
        this.weights = weights;
    }

    public Long getWeights()
    {
        return weights;
    }
    public void setDataStatus(Long dataStatus)
    {
        this.dataStatus = dataStatus;
    }

    public Long getDataStatus()
    {
        return dataStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("des", getDes())
            .append("jumpUrl", getJumpUrl())
            .append("image", getImage())
            .append("classification", getClassification())
            .append("weights", getWeights())
            .append("dataStatus", getDataStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
