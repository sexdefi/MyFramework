package com.ruoyi.project.bus.txinfo.service.impl;

import java.util.List;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.bus.txinfo.mapper.TransactionInfoMapper;
import com.ruoyi.project.bus.txinfo.domain.TransactionInfo;
import com.ruoyi.project.bus.txinfo.service.ITransactionInfoService;
import com.ruoyi.common.utils.text.Convert;

/**
 * 交易记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-09-14
 */
@Service
@DataSource(value = DataSourceType.SLAVE)
public class TransactionInfoServiceImpl implements ITransactionInfoService 
{
    @Autowired
    private TransactionInfoMapper transactionInfoMapper;

    /**
     * 查询交易记录
     * 
     * @param thash 交易记录主键
     * @return 交易记录
     */
    @Override
    public TransactionInfo selectTransactionInfoByThash(String thash)
    {
        return transactionInfoMapper.selectTransactionInfoByThash(thash);
    }

    /**
     * 查询交易记录列表
     * 
     * @param transactionInfo 交易记录
     * @return 交易记录
     */
    @Override
    public List<TransactionInfo> selectTransactionInfoList(TransactionInfo transactionInfo)
    {
        return transactionInfoMapper.selectTransactionInfoList(transactionInfo);
    }

    /**
     * 新增交易记录
     * 
     * @param transactionInfo 交易记录
     * @return 结果
     */
    @Override
    public int insertTransactionInfo(TransactionInfo transactionInfo)
    {
        return transactionInfoMapper.insertTransactionInfo(transactionInfo);
    }

    /**
     * 修改交易记录
     * 
     * @param transactionInfo 交易记录
     * @return 结果
     */
    @Override
    public int updateTransactionInfo(TransactionInfo transactionInfo)
    {
        return transactionInfoMapper.updateTransactionInfo(transactionInfo);
    }

    /**
     * 批量删除交易记录
     * 
     * @param thashs 需要删除的交易记录主键
     * @return 结果
     */
    @Override
    public int deleteTransactionInfoByThashs(String thashs)
    {
        return transactionInfoMapper.deleteTransactionInfoByThashs(Convert.toStrArray(thashs));
    }

    /**
     * 删除交易记录信息
     * 
     * @param thash 交易记录主键
     * @return 结果
     */
    @Override
    public int deleteTransactionInfoByThash(String thash)
    {
        return transactionInfoMapper.deleteTransactionInfoByThash(thash);
    }
}
