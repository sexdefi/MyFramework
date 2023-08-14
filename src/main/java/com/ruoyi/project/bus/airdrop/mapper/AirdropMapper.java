package com.ruoyi.project.bus.airdrop.mapper;

import java.util.List;
import com.ruoyi.project.bus.airdrop.domain.Airdrop;

/**
 * airdropMapper接口
 * 
 * @author ruoyi
 * @date 2023-08-14
 */
public interface AirdropMapper 
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
     * 删除airdrop
     * 
     * @param id airdrop主键
     * @return 结果
     */
    public int deleteAirdropById(Long id);

    /**
     * 批量删除airdrop
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAirdropByIds(String[] ids);
}
