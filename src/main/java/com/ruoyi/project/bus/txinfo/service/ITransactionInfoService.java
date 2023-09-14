package com.ruoyi.project.bus.txinfo.service;

import java.util.List;
import com.ruoyi.project.bus.txinfo.domain.TransactionInfo;

/**
 * 交易记录Service接口
 * 
 * @author ruoyi
 * @date 2023-09-14
 */
public interface ITransactionInfoService 
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
     * 批量删除交易记录
     * 
     * @param thashs 需要删除的交易记录主键集合
     * @return 结果
     */
    public int deleteTransactionInfoByThashs(String thashs);

    /**
     * 删除交易记录信息
     * 
     * @param thash 交易记录主键
     * @return 结果
     */
    public int deleteTransactionInfoByThash(String thash);
}
