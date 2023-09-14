package com.ruoyi.project.bus.token.service.impl;

import java.util.List;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.bus.token.mapper.AccountTokenMapper;
import com.ruoyi.project.bus.token.domain.AccountToken;
import com.ruoyi.project.bus.token.service.IAccountTokenService;
import com.ruoyi.common.utils.text.Convert;

/**
 * 代币余额表Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-09-14
 */
@Service
@DataSource(value = DataSourceType.SLAVE)
public class AccountTokenServiceImpl implements IAccountTokenService 
{
    @Autowired
    private AccountTokenMapper accountTokenMapper;

    /**
     * 查询代币余额表
     * 
     * @param id 代币余额表主键
     * @return 代币余额表
     */
    @Override
    public AccountToken selectAccountTokenById(Long id)
    {
        return accountTokenMapper.selectAccountTokenById(id);
    }

    /**
     * 查询代币余额表列表
     * 
     * @param accountToken 代币余额表
     * @return 代币余额表
     */
    @Override
    public List<AccountToken> selectAccountTokenList(AccountToken accountToken)
    {
        return accountTokenMapper.selectAccountTokenList(accountToken);
    }

    /**
     * 新增代币余额表
     * 
     * @param accountToken 代币余额表
     * @return 结果
     */
    @Override
    public int insertAccountToken(AccountToken accountToken)
    {
        return accountTokenMapper.insertAccountToken(accountToken);
    }

    /**
     * 修改代币余额表
     * 
     * @param accountToken 代币余额表
     * @return 结果
     */
    @Override
    public int updateAccountToken(AccountToken accountToken)
    {
        return accountTokenMapper.updateAccountToken(accountToken);
    }

    /**
     * 批量删除代币余额表
     * 
     * @param ids 需要删除的代币余额表主键
     * @return 结果
     */
    @Override
    public int deleteAccountTokenByIds(String ids)
    {
        return accountTokenMapper.deleteAccountTokenByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除代币余额表信息
     * 
     * @param id 代币余额表主键
     * @return 结果
     */
    @Override
    public int deleteAccountTokenById(Long id)
    {
        return accountTokenMapper.deleteAccountTokenById(id);
    }
}
