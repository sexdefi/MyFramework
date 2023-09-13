package com.ruoyi.project.bus.airdrop.service.impl;

import java.util.List;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.bus.airdrop.mapper.AirdropMapper;
import com.ruoyi.project.bus.airdrop.domain.Airdrop;
import com.ruoyi.project.bus.airdrop.service.IAirdropService;
import com.ruoyi.common.utils.text.Convert;

/**
 * airdropService业务层处理
 * 
 * @author ruoyi
 * @date 2023-08-14
 */
@Service
@DataSource(value = DataSourceType.MASTER)
public class AirdropServiceImpl implements IAirdropService 
{
    @Autowired
    private AirdropMapper airdropMapper;

    /**
     * 查询airdrop
     * 
     * @param id airdrop主键
     * @return airdrop
     */
    @Override
    public Airdrop selectAirdropById(Long id)
    {
        return airdropMapper.selectAirdropById(id);
    }

    /**
     * 查询airdrop列表
     * 
     * @param airdrop airdrop
     * @return airdrop
     */
    @Override
    public List<Airdrop> selectAirdropList(Airdrop airdrop)
    {
        return airdropMapper.selectAirdropList(airdrop);
    }

    /**
     * 新增airdrop
     * 
     * @param airdrop airdrop
     * @return 结果
     */
    @Override
    public int insertAirdrop(Airdrop airdrop)
    {
        return airdropMapper.insertAirdrop(airdrop);
    }

    /**
     * 修改airdrop
     * 
     * @param airdrop airdrop
     * @return 结果
     */
    @Override
    public int updateAirdrop(Airdrop airdrop)
    {
        return airdropMapper.updateAirdrop(airdrop);
    }

    /**
     * 批量删除airdrop
     * 
     * @param ids 需要删除的airdrop主键
     * @return 结果
     */
    @Override
    public int deleteAirdropByIds(String ids)
    {
        return airdropMapper.deleteAirdropByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除airdrop信息
     * 
     * @param id airdrop主键
     * @return 结果
     */
    @Override
    public int deleteAirdropById(Long id)
    {
        return airdropMapper.deleteAirdropById(id);
    }
}
