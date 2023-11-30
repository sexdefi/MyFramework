package com.ruoyi.project.bus.coinlist.controller;

import java.util.List;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.project.bus.coinlist.domain.RateCoin;
import com.ruoyi.project.bus.coinlist.service.IRateCoinService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * 有费率的币Controller
 * 
 * @author ruoyi
 * @date 2023-11-30
 */
@Controller
@RequestMapping("/swap/coinlist")
public class RateCoinController extends BaseController
{
    private String prefix = "swap/coinlist";

    @Autowired
    private IRateCoinService rateCoinService;

    @RequiresPermissions("swap:coinlist:view")
    @GetMapping()
    public String coinlist()
    {
        return prefix + "/coinlist";
    }

    /**
     * 查询有费率的币列表
     */
    @RequiresPermissions("swap:coinlist:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(RateCoin rateCoin)
    {
        startPage();
        List<RateCoin> list = rateCoinService.selectRateCoinList(rateCoin);
        return getDataTable(list);
    }

    /**
     * 导出有费率的币列表
     */
    @RequiresPermissions("swap:coinlist:export")
    @Log(title = "有费率的币", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(RateCoin rateCoin)
    {
        List<RateCoin> list = rateCoinService.selectRateCoinList(rateCoin);
        ExcelUtil<RateCoin> util = new ExcelUtil<RateCoin>(RateCoin.class);
        return util.exportExcel(list, "有费率的币数据");
    }

    /**
     * 新增有费率的币
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存有费率的币
     */
    @RequiresPermissions("swap:coinlist:add")
    @Log(title = "有费率的币", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(RateCoin rateCoin)
    {
        return toAjax(rateCoinService.insertRateCoin(rateCoin));
    }

    /**
     * 修改有费率的币
     */
    @RequiresPermissions("swap:coinlist:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        RateCoin rateCoin = rateCoinService.selectRateCoinById(id);
        mmap.put("rateCoin", rateCoin);
        return prefix + "/edit";
    }

    /**
     * 修改保存有费率的币
     */
    @RequiresPermissions("swap:coinlist:edit")
    @Log(title = "有费率的币", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(RateCoin rateCoin)
    {
        return toAjax(rateCoinService.updateRateCoin(rateCoin));
    }

    /**
     * 删除有费率的币
     */
    @RequiresPermissions("swap:coinlist:remove")
    @Log(title = "有费率的币", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(rateCoinService.deleteRateCoinByIds(ids));
    }
}
