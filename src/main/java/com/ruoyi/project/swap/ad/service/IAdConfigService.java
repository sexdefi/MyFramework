package com.ruoyi.project.swap.ad.service;

import java.util.List;
import com.ruoyi.project.swap.ad.domain.AdConfig;

/**
 * 广告BannerService接口
 * 
 * @author ruoyi
 * @date 2023-11-07
 */
public interface IAdConfigService 
{
    /**
     * 查询广告Banner
     * 
     * @param id 广告Banner主键
     * @return 广告Banner
     */
    public AdConfig selectAdConfigById(Long id);

    /**
     * 查询广告Banner列表
     * 
     * @param adConfig 广告Banner
     * @return 广告Banner集合
     */
    public List<AdConfig> selectAdConfigList(AdConfig adConfig);

    /**
     * 新增广告Banner
     * 
     * @param adConfig 广告Banner
     * @return 结果
     */
    public int insertAdConfig(AdConfig adConfig);

    /**
     * 修改广告Banner
     * 
     * @param adConfig 广告Banner
     * @return 结果
     */
    public int updateAdConfig(AdConfig adConfig);

    /**
     * 批量删除广告Banner
     * 
     * @param ids 需要删除的广告Banner主键集合
     * @return 结果
     */
    public int deleteAdConfigByIds(String ids);

    /**
     * 删除广告Banner信息
     * 
     * @param id 广告Banner主键
     * @return 结果
     */
    public int deleteAdConfigById(Long id);
}
