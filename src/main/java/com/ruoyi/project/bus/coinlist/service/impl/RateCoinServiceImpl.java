package com.ruoyi.project.bus.coinlist.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.bus.coinlist.mapper.RateCoinMapper;
import com.ruoyi.project.bus.coinlist.domain.RateCoin;
import com.ruoyi.project.bus.coinlist.service.IRateCoinService;
import com.ruoyi.common.utils.text.Convert;

/**
 * 有费率的币Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-11-30
 */
@Service
public class RateCoinServiceImpl implements IRateCoinService 
{
    @Autowired
    private RateCoinMapper rateCoinMapper;

    /**
     * 查询有费率的币
     * 
     * @param id 有费率的币主键
     * @return 有费率的币
     */
    @Override
    public RateCoin selectRateCoinById(Long id)
    {
        return rateCoinMapper.selectRateCoinById(id);
    }

    /**
     * 查询有费率的币列表
     * 
     * @param rateCoin 有费率的币
     * @return 有费率的币
     */
    @Override
    public List<RateCoin> selectRateCoinList(RateCoin rateCoin)
    {
        return rateCoinMapper.selectRateCoinList(rateCoin);
    }

    /**
     * 新增有费率的币
     * 
     * @param rateCoin 有费率的币
     * @return 结果
     */
    @Override
    public int insertRateCoin(RateCoin rateCoin)
    {
        rateCoin.setCreateTime(DateUtils.getNowDate());
        return rateCoinMapper.insertRateCoin(rateCoin);
    }

    /**
     * 修改有费率的币
     * 
     * @param rateCoin 有费率的币
     * @return 结果
     */
    @Override
    public int updateRateCoin(RateCoin rateCoin)
    {
        rateCoin.setUpdateTime(DateUtils.getNowDate());
        return rateCoinMapper.updateRateCoin(rateCoin);
    }

    /**
     * 批量删除有费率的币
     * 
     * @param ids 需要删除的有费率的币主键
     * @return 结果
     */
    @Override
    public int deleteRateCoinByIds(String ids)
    {
        return rateCoinMapper.deleteRateCoinByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除有费率的币信息
     * 
     * @param id 有费率的币主键
     * @return 结果
     */
    @Override
    public int deleteRateCoinById(Long id)
    {
        return rateCoinMapper.deleteRateCoinById(id);
    }
}
