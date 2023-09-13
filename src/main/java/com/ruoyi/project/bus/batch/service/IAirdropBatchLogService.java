package com.ruoyi.project.bus.batch.service;

import java.util.List;
import com.ruoyi.project.bus.batch.domain.AirdropBatchLog;

/**
 * 空投批次快照Service接口
 * 
 * @author ruoyi
 * @date 2023-09-13
 */
public interface IAirdropBatchLogService 
{
    /**
     * 查询空投批次快照
     * 
     * @param id 空投批次快照主键
     * @return 空投批次快照
     */
    public AirdropBatchLog selectAirdropBatchLogById(Long id);

    /**
     * 查询空投批次快照列表
     * 
     * @param airdropBatchLog 空投批次快照
     * @return 空投批次快照集合
     */
    public List<AirdropBatchLog> selectAirdropBatchLogList(AirdropBatchLog airdropBatchLog);

    /**
     * 新增空投批次快照
     * 
     * @param airdropBatchLog 空投批次快照
     * @return 结果
     */
    public int insertAirdropBatchLog(AirdropBatchLog airdropBatchLog);

    /**
     * 修改空投批次快照
     * 
     * @param airdropBatchLog 空投批次快照
     * @return 结果
     */
    public int updateAirdropBatchLog(AirdropBatchLog airdropBatchLog);

    /**
     * 批量删除空投批次快照
     * 
     * @param ids 需要删除的空投批次快照主键集合
     * @return 结果
     */
    public int deleteAirdropBatchLogByIds(String ids);

    /**
     * 删除空投批次快照信息
     * 
     * @param id 空投批次快照主键
     * @return 结果
     */
    public int deleteAirdropBatchLogById(Long id);
}
