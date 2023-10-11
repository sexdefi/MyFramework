package com.ruoyi.project.bus.operate.mapper;

import java.util.List;
import com.ruoyi.project.bus.operate.domain.GasOperateLog;

/**
 * gas领取操作记录表Mapper接口
 * 
 * @author ruoyi
 * @date 2023-10-11
 */
public interface GasOperateLogMapper 
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
     * 删除gas领取操作记录表
     * 
     * @param id gas领取操作记录表主键
     * @return 结果
     */
    public int deleteGasOperateLogById(Long id);

    /**
     * 批量删除gas领取操作记录表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGasOperateLogByIds(String[] ids);

    public GasOperateLog selectGasOperateLast(GasOperateLog gasOperateLog);

    List<GasOperateLog> selectGasOperateLogList50(GasOperateLog gasOperateLog);

    List<String> selectAllUser();

    //selectGasWithdrawLogList50
    List<GasOperateLog> selectGasWithdrawLogList50(GasOperateLog gasOperateLog);
}
