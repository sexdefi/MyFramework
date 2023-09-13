package com.ruoyi.project.bus.batch.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.bus.batch.mapper.AirdropBatchLogMapper;
import com.ruoyi.project.bus.batch.domain.AirdropBatchLog;
import com.ruoyi.project.bus.batch.service.IAirdropBatchLogService;
import com.ruoyi.common.utils.text.Convert;

/**
 * 空投批次快照Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-09-13
 */
@Service
public class AirdropBatchLogServiceImpl implements IAirdropBatchLogService 
{
    @Autowired
    private AirdropBatchLogMapper airdropBatchLogMapper;

    /**
     * 查询空投批次快照
     * 
     * @param id 空投批次快照主键
     * @return 空投批次快照
     */
    @Override
    public AirdropBatchLog selectAirdropBatchLogById(Long id)
    {
        return airdropBatchLogMapper.selectAirdropBatchLogById(id);
    }

    /**
     * 查询空投批次快照列表
     * 
     * @param airdropBatchLog 空投批次快照
     * @return 空投批次快照
     */
    @Override
    public List<AirdropBatchLog> selectAirdropBatchLogList(AirdropBatchLog airdropBatchLog)
    {
        return airdropBatchLogMapper.selectAirdropBatchLogList(airdropBatchLog);
    }

    /**
     * 新增空投批次快照
     * 
     * @param airdropBatchLog 空投批次快照
     * @return 结果
     */
    @Override
    public int insertAirdropBatchLog(AirdropBatchLog airdropBatchLog)
    {
        return airdropBatchLogMapper.insertAirdropBatchLog(airdropBatchLog);
    }

    /**
     * 修改空投批次快照
     * 
     * @param airdropBatchLog 空投批次快照
     * @return 结果
     */
    @Override
    public int updateAirdropBatchLog(AirdropBatchLog airdropBatchLog)
    {
        return airdropBatchLogMapper.updateAirdropBatchLog(airdropBatchLog);
    }

    /**
     * 批量删除空投批次快照
     * 
     * @param ids 需要删除的空投批次快照主键
     * @return 结果
     */
    @Override
    public int deleteAirdropBatchLogByIds(String ids)
    {
        return airdropBatchLogMapper.deleteAirdropBatchLogByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除空投批次快照信息
     * 
     * @param id 空投批次快照主键
     * @return 结果
     */
    @Override
    public int deleteAirdropBatchLogById(Long id)
    {
        return airdropBatchLogMapper.deleteAirdropBatchLogById(id);
    }
}
