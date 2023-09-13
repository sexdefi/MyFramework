package com.ruoyi.project.bus.Profit.mapper;

import java.util.List;
import com.ruoyi.project.bus.Profit.domain.ProfitLog;

/**
 * 当天交易数据Mapper接口
 * 
 * @author ruoyi
 * @date 2023-09-13
 */
public interface ProfitLogMapper 
{
    /**
     * 查询当天交易数据
     * 
     * @param id 当天交易数据主键
     * @return 当天交易数据
     */
    public ProfitLog selectProfitLogById(Long id);

    /**
     * 查询当天交易数据列表
     * 
     * @param profitLog 当天交易数据
     * @return 当天交易数据集合
     */
    public List<ProfitLog> selectProfitLogList(ProfitLog profitLog);

    /**
     * 新增当天交易数据
     * 
     * @param profitLog 当天交易数据
     * @return 结果
     */
    public int insertProfitLog(ProfitLog profitLog);

    /**
     * 修改当天交易数据
     * 
     * @param profitLog 当天交易数据
     * @return 结果
     */
    public int updateProfitLog(ProfitLog profitLog);

    /**
     * 删除当天交易数据
     * 
     * @param id 当天交易数据主键
     * @return 结果
     */
    public int deleteProfitLogById(Long id);

    /**
     * 批量删除当天交易数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProfitLogByIds(String[] ids);
}
