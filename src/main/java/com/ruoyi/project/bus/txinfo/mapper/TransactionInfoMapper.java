package com.ruoyi.project.bus.txinfo.mapper;

import java.util.List;
import com.ruoyi.project.bus.txinfo.domain.TransactionInfo;

/**
 * 交易记录Mapper接口
 * 
 * @author ruoyi
 * @date 2023-09-14
 */
public interface TransactionInfoMapper 
{
    /**
     * 查询交易记录
     * 
     * @param thash 交易记录主键
     * @return 交易记录
     */
    public TransactionInfo selectTransactionInfoByThash(String thash);

    /**
     * 查询交易记录列表
     * 
     * @param transactionInfo 交易记录
     * @return 交易记录集合
     */
    public List<TransactionInfo> selectTransactionInfoList(TransactionInfo transactionInfo);

    /**
     * 新增交易记录
     * 
     * @param transactionInfo 交易记录
     * @return 结果
     */
    public int insertTransactionInfo(TransactionInfo transactionInfo);

    /**
     * 修改交易记录
     * 
     * @param transactionInfo 交易记录
     * @return 结果
     */
    public int updateTransactionInfo(TransactionInfo transactionInfo);

    /**
     * 删除交易记录
     * 
     * @param thash 交易记录主键
     * @return 结果
     */
    public int deleteTransactionInfoByThash(String thash);

    /**
     * 批量删除交易记录
     * 
     * @param thashs 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTransactionInfoByThashs(String[] thashs);
}
