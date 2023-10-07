package com.ruoyi.project.bus.operate.service.impl;

import java.util.List;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.bus.operate.mapper.GasOperateLogMapper;
import com.ruoyi.project.bus.operate.domain.GasOperateLog;
import com.ruoyi.project.bus.operate.service.IGasOperateLogService;
import com.ruoyi.common.utils.text.Convert;

/**
 * gas领取操作记录表Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-09-25
 */
@Service
@DataSource(value = DataSourceType.MASTER)
public class GasOperateLogServiceImpl implements IGasOperateLogService 
{
    @Autowired
    private GasOperateLogMapper gasOperateLogMapper;

    /**
     * 查询gas领取操作记录表
     * 
     * @param id gas领取操作记录表主键
     * @return gas领取操作记录表
     */
    @Override
    public GasOperateLog selectGasOperateLogById(Long id)
    {
        return gasOperateLogMapper.selectGasOperateLogById(id);
    }

    /**
     * 查询gas领取操作记录表列表
     * 
     * @param gasOperateLog gas领取操作记录表
     * @return gas领取操作记录表
     */
    @Override
    public List<GasOperateLog> selectGasOperateLogList(GasOperateLog gasOperateLog)
    {
        return gasOperateLogMapper.selectGasOperateLogList(gasOperateLog);
    }

    @Override
    public GasOperateLog selectGasOperateLast(GasOperateLog gasOperateLog)
    {
        return gasOperateLogMapper.selectGasOperateLast(gasOperateLog);
    }

    @Override
    public List<GasOperateLog> selectGasOperateLogList50(GasOperateLog gasOperateLog) {
        return gasOperateLogMapper.selectGasOperateLogList50(gasOperateLog);
    }

    @Override
    public List<String> selectAllUser() {
        return gasOperateLogMapper.selectAllUser();
    }

    /**
     * 新增gas领取操作记录表
     * 
     * @param gasOperateLog gas领取操作记录表
     * @return 结果
     */
    @Override
    public int insertGasOperateLog(GasOperateLog gasOperateLog)
    {
        return gasOperateLogMapper.insertGasOperateLog(gasOperateLog);
    }

    /**
     * 修改gas领取操作记录表
     * 
     * @param gasOperateLog gas领取操作记录表
     * @return 结果
     */
    @Override
    public int updateGasOperateLog(GasOperateLog gasOperateLog)
    {
        return gasOperateLogMapper.updateGasOperateLog(gasOperateLog);
    }

    /**
     * 批量删除gas领取操作记录表
     * 
     * @param ids 需要删除的gas领取操作记录表主键
     * @return 结果
     */
    @Override
    public int deleteGasOperateLogByIds(String ids)
    {
        return gasOperateLogMapper.deleteGasOperateLogByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除gas领取操作记录表信息
     * 
     * @param id gas领取操作记录表主键
     * @return 结果
     */
    @Override
    public int deleteGasOperateLogById(Long id)
    {
        return gasOperateLogMapper.deleteGasOperateLogById(id);
    }
}
