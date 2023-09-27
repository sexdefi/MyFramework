package com.ruoyi.project.bus.order.service.impl;

import java.util.List;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.bus.order.mapper.GasWithdrawLogMapper;
import com.ruoyi.project.bus.order.domain.GasWithdrawLog;
import com.ruoyi.project.bus.order.service.IGasWithdrawLogService;
import com.ruoyi.common.utils.text.Convert;

/**
 * 提现记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-09-27
 */
@Service
@DataSource(value = DataSourceType.MASTER)
public class GasWithdrawLogServiceImpl implements IGasWithdrawLogService
{
    @Autowired
    private GasWithdrawLogMapper gasWithdrawLogMapper;

    /**
     * 查询提现记录
     * 
     * @param id 提现记录主键
     * @return 提现记录
     */
    @Override
    public GasWithdrawLog selectGasWithdrawLogById(Long id)
    {
        return gasWithdrawLogMapper.selectGasWithdrawLogById(id);
    }

    /**
     * 查询提现记录列表
     * 
     * @param gasWithdrawLog 提现记录
     * @return 提现记录
     */
    @Override
    public List<GasWithdrawLog> selectGasWithdrawLogList(GasWithdrawLog gasWithdrawLog)
    {
        return gasWithdrawLogMapper.selectGasWithdrawLogList(gasWithdrawLog);
    }

    /**
     * 新增提现记录
     * 
     * @param gasWithdrawLog 提现记录
     * @return 结果
     */
    @Override
    public int insertGasWithdrawLog(GasWithdrawLog gasWithdrawLog)
    {
        return gasWithdrawLogMapper.insertGasWithdrawLog(gasWithdrawLog);
    }

    /**
     * 修改提现记录
     * 
     * @param gasWithdrawLog 提现记录
     * @return 结果
     */
    @Override
    public int updateGasWithdrawLog(GasWithdrawLog gasWithdrawLog)
    {
        return gasWithdrawLogMapper.updateGasWithdrawLog(gasWithdrawLog);
    }

    /**
     * 批量删除提现记录
     * 
     * @param ids 需要删除的提现记录主键
     * @return 结果
     */
    @Override
    public int deleteGasWithdrawLogByIds(String ids)
    {
        return gasWithdrawLogMapper.deleteGasWithdrawLogByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除提现记录信息
     * 
     * @param id 提现记录主键
     * @return 结果
     */
    @Override
    public int deleteGasWithdrawLogById(Long id)
    {
        return gasWithdrawLogMapper.deleteGasWithdrawLogById(id);
    }
}
