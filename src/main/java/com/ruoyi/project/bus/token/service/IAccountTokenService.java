package com.ruoyi.project.bus.token.service;

import java.util.List;
import com.ruoyi.project.bus.token.domain.AccountToken;

/**
 * 代币余额表Service接口
 * 
 * @author ruoyi
 * @date 2023-09-14
 */
public interface IAccountTokenService 
{
    /**
     * 查询代币余额表
     * 
     * @param id 代币余额表主键
     * @return 代币余额表
     */
    public AccountToken selectAccountTokenById(Long id);

    /**
     * 查询代币余额表列表
     * 
     * @param accountToken 代币余额表
     * @return 代币余额表集合
     */
    public List<AccountToken> selectAccountTokenList(AccountToken accountToken);

    /**
     * 新增代币余额表
     * 
     * @param accountToken 代币余额表
     * @return 结果
     */
    public int insertAccountToken(AccountToken accountToken);

    /**
     * 修改代币余额表
     * 
     * @param accountToken 代币余额表
     * @return 结果
     */
    public int updateAccountToken(AccountToken accountToken);

    /**
     * 批量删除代币余额表
     * 
     * @param ids 需要删除的代币余额表主键集合
     * @return 结果
     */
    public int deleteAccountTokenByIds(String ids);

    /**
     * 删除代币余额表信息
     * 
     * @param id 代币余额表主键
     * @return 结果
     */
    public int deleteAccountTokenById(Long id);
}
