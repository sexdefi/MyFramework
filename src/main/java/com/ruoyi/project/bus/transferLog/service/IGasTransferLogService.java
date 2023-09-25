package com.ruoyi.project.bus.transferLog.service;

import java.util.List;
import com.ruoyi.project.bus.transferLog.domain.GasTransferLog;

/**
 * Gas领取空投记录Service接口
 * 
 * @author ruoyi
 * @date 2023-09-25
 */
public interface IGasTransferLogService 
{
    /**
     * 查询Gas领取空投记录
     * 
     * @param id Gas领取空投记录主键
     * @return Gas领取空投记录
     */
    public GasTransferLog selectGasTransferLogById(Long id);

    /**
     * 查询Gas领取空投记录列表
     * 
     * @param gasTransferLog Gas领取空投记录
     * @return Gas领取空投记录集合
     */
    public List<GasTransferLog> selectGasTransferLogList(GasTransferLog gasTransferLog);

    /**
     * 新增Gas领取空投记录
     * 
     * @param gasTransferLog Gas领取空投记录
     * @return 结果
     */
    public int insertGasTransferLog(GasTransferLog gasTransferLog);

    /**
     * 修改Gas领取空投记录
     * 
     * @param gasTransferLog Gas领取空投记录
     * @return 结果
     */
    public int updateGasTransferLog(GasTransferLog gasTransferLog);

    /**
     * 批量删除Gas领取空投记录
     * 
     * @param ids 需要删除的Gas领取空投记录主键集合
     * @return 结果
     */
    public int deleteGasTransferLogByIds(String ids);

    /**
     * 删除Gas领取空投记录信息
     * 
     * @param id Gas领取空投记录主键
     * @return 结果
     */
    public int deleteGasTransferLogById(Long id);

    public List<GasTransferLog> selectGasTransferLogList50(GasTransferLog gasTransferLog);
}
