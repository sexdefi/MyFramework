package com.ruoyi.project.bus.operate.service;

import java.util.List;
import com.ruoyi.project.bus.operate.domain.GasOperateLog;

/**
 * gas领取操作记录表Service接口
 * 
 * @author ruoyi
 * @date 2023-09-25
 */
public interface IGasOperateLogService 
{
    /**
     * 查询gas领取操作记录表
     * 
     * @param id gas领取操作记录表主键
     * @return gas领取操作记录表
     */
    public GasOperateLog selectGasOperateLogById(Long id);

    /**
     * 查询gas领取操作记录表列表
     * 
     * @param gasOperateLog gas领取操作记录表
     * @return gas领取操作记录表集合
     */
    public List<GasOperateLog> selectGasOperateLogList(GasOperateLog gasOperateLog);

    /**
     * 新增gas领取操作记录表
     * 
     * @param gasOperateLog gas领取操作记录表
     * @return 结果
     */
    public int insertGasOperateLog(GasOperateLog gasOperateLog);

    /**
     * 修改gas领取操作记录表
     * 
     * @param gasOperateLog gas领取操作记录表
     * @return 结果
     */
    public int updateGasOperateLog(GasOperateLog gasOperateLog);

    /**
     * 批量删除gas领取操作记录表
     * 
     * @param ids 需要删除的gas领取操作记录表主键集合
     * @return 结果
     */
    public int deleteGasOperateLogByIds(String ids);

    /**
     * 删除gas领取操作记录表信息
     * 
     * @param id gas领取操作记录表主键
     * @return 结果
     */
    public int deleteGasOperateLogById(Long id);


    public GasOperateLog selectGasOperateLast(GasOperateLog gasOperateLog);

    public List<GasOperateLog> selectGasOperateLogList50(GasOperateLog gasOperateLog);

}
