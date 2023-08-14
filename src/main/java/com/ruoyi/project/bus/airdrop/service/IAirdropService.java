package com.ruoyi.project.bus.airdrop.service;

import java.util.List;
import com.ruoyi.project.bus.airdrop.domain.Airdrop;

/**
 * airdropService接口
 * 
 * @author ruoyi
 * @date 2023-08-14
 */
public interface IAirdropService 
{
    /**
     * 查询airdrop
     * 
     * @param id airdrop主键
     * @return airdrop
     */
    public Airdrop selectAirdropById(Long id);

    /**
     * 查询airdrop列表
     * 
     * @param airdrop airdrop
     * @return airdrop集合
     */
    public List<Airdrop> selectAirdropList(Airdrop airdrop);

    /**
     * 新增airdrop
     * 
     * @param airdrop airdrop
     * @return 结果
     */
    public int insertAirdrop(Airdrop airdrop);

    /**
     * 修改airdrop
     * 
     * @param airdrop airdrop
     * @return 结果
     */
    public int updateAirdrop(Airdrop airdrop);

    /**
     * 批量删除airdrop
     * 
     * @param ids 需要删除的airdrop主键集合
     * @return 结果
     */
    public int deleteAirdropByIds(String ids);

    /**
     * 删除airdrop信息
     * 
     * @param id airdrop主键
     * @return 结果
     */
    public int deleteAirdropById(Long id);
}
