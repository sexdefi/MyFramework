package com.ruoyi.project.bus.transferLog.service.impl;

import java.util.List;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.bus.transferLog.mapper.GasTransferLogMapper;
import com.ruoyi.project.bus.transferLog.domain.GasTransferLog;
import com.ruoyi.project.bus.transferLog.service.IGasTransferLogService;
import com.ruoyi.common.utils.text.Convert;

/**
 * Gas领取空投记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-09-25
 */
@Service
@DataSource(value = DataSourceType.MASTER)
public class GasTransferLogServiceImpl implements IGasTransferLogService 
{
    @Autowired
    private GasTransferLogMapper gasTransferLogMapper;

    /**
     * 查询Gas领取空投记录
     * 
     * @param id Gas领取空投记录主键
     * @return Gas领取空投记录
     */
    @Override
    public GasTransferLog selectGasTransferLogById(Long id)
    {
        return gasTransferLogMapper.selectGasTransferLogById(id);
    }

    /**
     * 查询Gas领取空投记录列表
     * 
     * @param gasTransferLog Gas领取空投记录
     * @return Gas领取空投记录
     */
    @Override
    public List<GasTransferLog> selectGasTransferLogList(GasTransferLog gasTransferLog)
    {
        return gasTransferLogMapper.selectGasTransferLogList(gasTransferLog);
    }

    @Override
    public List<GasTransferLog> selectGasTransferLogList50(GasTransferLog gasTransferLog)
    {
        return gasTransferLogMapper.selectGasTransferLogList50(gasTransferLog);
    }
    /**
     * 新增Gas领取空投记录
     * 
     * @param gasTransferLog Gas领取空投记录
     * @return 结果
     */
    @Override
    public int insertGasTransferLog(GasTransferLog gasTransferLog)
    {
        return gasTransferLogMapper.insertGasTransferLog(gasTransferLog);
    }

    /**
     * 修改Gas领取空投记录
     * 
     * @param gasTransferLog Gas领取空投记录
     * @return 结果
     */
    @Override
    public int updateGasTransferLog(GasTransferLog gasTransferLog)
    {
        return gasTransferLogMapper.updateGasTransferLog(gasTransferLog);
    }

    /**
     * 批量删除Gas领取空投记录
     * 
     * @param ids 需要删除的Gas领取空投记录主键
     * @return 结果
     */
    @Override
    public int deleteGasTransferLogByIds(String ids)
    {
        return gasTransferLogMapper.deleteGasTransferLogByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除Gas领取空投记录信息
     * 
     * @param id Gas领取空投记录主键
     * @return 结果
     */
    @Override
    public int deleteGasTransferLogById(Long id)
    {
        return gasTransferLogMapper.deleteGasTransferLogById(id);
    }
}
