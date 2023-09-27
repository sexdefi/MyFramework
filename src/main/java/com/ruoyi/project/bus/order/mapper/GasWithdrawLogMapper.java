package com.ruoyi.project.bus.order.mapper;

import java.util.List;
import com.ruoyi.project.bus.order.domain.GasWithdrawLog;

/**
 * 提现记录Mapper接口
 * 
 * @author ruoyi
 * @date 2023-09-27
 */
public interface GasWithdrawLogMapper 
{
    /**
     * 查询提现记录
     * 
     * @param id 提现记录主键
     * @return 提现记录
     */
    public GasWithdrawLog selectGasWithdrawLogById(Long id);

    /**
     * 查询提现记录列表
     * 
     * @param gasWithdrawLog 提现记录
     * @return 提现记录集合
     */
    public List<GasWithdrawLog> selectGasWithdrawLogList(GasWithdrawLog gasWithdrawLog);

    /**
     * 新增提现记录
     * 
     * @param gasWithdrawLog 提现记录
     * @return 结果
     */
    public int insertGasWithdrawLog(GasWithdrawLog gasWithdrawLog);

    /**
     * 修改提现记录
     * 
     * @param gasWithdrawLog 提现记录
     * @return 结果
     */
    public int updateGasWithdrawLog(GasWithdrawLog gasWithdrawLog);

    /**
     * 删除提现记录
     * 
     * @param id 提现记录主键
     * @return 结果
     */
    public int deleteGasWithdrawLogById(Long id);

    /**
     * 批量删除提现记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGasWithdrawLogByIds(String[] ids);
}
