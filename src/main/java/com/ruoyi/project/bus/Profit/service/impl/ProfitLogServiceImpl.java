package com.ruoyi.project.bus.Profit.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.bus.Profit.mapper.ProfitLogMapper;
import com.ruoyi.project.bus.Profit.domain.ProfitLog;
import com.ruoyi.project.bus.Profit.service.IProfitLogService;
import com.ruoyi.common.utils.text.Convert;

/**
 * 当天交易数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-09-13
 */
@Service
public class ProfitLogServiceImpl implements IProfitLogService 
{
    @Autowired
    private ProfitLogMapper profitLogMapper;

    /**
     * 查询当天交易数据
     * 
     * @param id 当天交易数据主键
     * @return 当天交易数据
     */
    @Override
    public ProfitLog selectProfitLogById(Long id)
    {
        return profitLogMapper.selectProfitLogById(id);
    }

    /**
     * 查询当天交易数据列表
     * 
     * @param profitLog 当天交易数据
     * @return 当天交易数据
     */
    @Override
    public List<ProfitLog> selectProfitLogList(ProfitLog profitLog)
    {
        return profitLogMapper.selectProfitLogList(profitLog);
    }

    /**
     * 新增当天交易数据
     * 
     * @param profitLog 当天交易数据
     * @return 结果
     */
    @Override
    public int insertProfitLog(ProfitLog profitLog)
    {
        return profitLogMapper.insertProfitLog(profitLog);
    }

    /**
     * 修改当天交易数据
     * 
     * @param profitLog 当天交易数据
     * @return 结果
     */
    @Override
    public int updateProfitLog(ProfitLog profitLog)
    {
        return profitLogMapper.updateProfitLog(profitLog);
    }

    /**
     * 批量删除当天交易数据
     * 
     * @param ids 需要删除的当天交易数据主键
     * @return 结果
     */
    @Override
    public int deleteProfitLogByIds(String ids)
    {
        return profitLogMapper.deleteProfitLogByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除当天交易数据信息
     * 
     * @param id 当天交易数据主键
     * @return 结果
     */
    @Override
    public int deleteProfitLogById(Long id)
    {
        return profitLogMapper.deleteProfitLogById(id);
    }
}
