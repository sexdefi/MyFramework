package com.ruoyi.project.bus.coinlist.mapper;

import java.util.List;
import com.ruoyi.project.bus.coinlist.domain.RateCoin;

/**
 * 有费率的币Mapper接口
 * 
 * @author ruoyi
 * @date 2023-11-30
 */
public interface RateCoinMapper 
{
    /**
     * 查询有费率的币
     * 
     * @param id 有费率的币主键
     * @return 有费率的币
     */
    public RateCoin selectRateCoinById(Long id);

    /**
     * 查询有费率的币列表
     * 
     * @param rateCoin 有费率的币
     * @return 有费率的币集合
     */
    public List<RateCoin> selectRateCoinList(RateCoin rateCoin);

    /**
     * 新增有费率的币
     * 
     * @param rateCoin 有费率的币
     * @return 结果
     */
    public int insertRateCoin(RateCoin rateCoin);

    /**
     * 修改有费率的币
     * 
     * @param rateCoin 有费率的币
     * @return 结果
     */
    public int updateRateCoin(RateCoin rateCoin);

    /**
     * 删除有费率的币
     * 
     * @param id 有费率的币主键
     * @return 结果
     */
    public int deleteRateCoinById(Long id);

    /**
     * 批量删除有费率的币
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRateCoinByIds(String[] ids);
}
