package com.ruoyi.project.swap.ad.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.swap.ad.mapper.AdConfigMapper;
import com.ruoyi.project.swap.ad.domain.AdConfig;
import com.ruoyi.project.swap.ad.service.IAdConfigService;
import com.ruoyi.common.utils.text.Convert;

/**
 * 广告BannerService业务层处理
 * 
 * @author ruoyi
 * @date 2023-11-07
 */
@Service
public class AdConfigServiceImpl implements IAdConfigService 
{
    @Autowired
    private AdConfigMapper adConfigMapper;

    /**
     * 查询广告Banner
     * 
     * @param id 广告Banner主键
     * @return 广告Banner
     */
    @Override
    public AdConfig selectAdConfigById(Long id)
    {
        return adConfigMapper.selectAdConfigById(id);
    }

    /**
     * 查询广告Banner列表
     * 
     * @param adConfig 广告Banner
     * @return 广告Banner
     */
    @Override
    public List<AdConfig> selectAdConfigList(AdConfig adConfig)
    {
        return adConfigMapper.selectAdConfigList(adConfig);
    }

    /**
     * 新增广告Banner
     * 
     * @param adConfig 广告Banner
     * @return 结果
     */
    @Override
    public int insertAdConfig(AdConfig adConfig)
    {
        adConfig.setCreateTime(DateUtils.getNowDate());
        return adConfigMapper.insertAdConfig(adConfig);
    }

    /**
     * 修改广告Banner
     * 
     * @param adConfig 广告Banner
     * @return 结果
     */
    @Override
    public int updateAdConfig(AdConfig adConfig)
    {
        adConfig.setUpdateTime(DateUtils.getNowDate());
        return adConfigMapper.updateAdConfig(adConfig);
    }

    /**
     * 批量删除广告Banner
     * 
     * @param ids 需要删除的广告Banner主键
     * @return 结果
     */
    @Override
    public int deleteAdConfigByIds(String ids)
    {
        return adConfigMapper.deleteAdConfigByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除广告Banner信息
     * 
     * @param id 广告Banner主键
     * @return 结果
     */
    @Override
    public int deleteAdConfigById(Long id)
    {
        return adConfigMapper.deleteAdConfigById(id);
    }
}
