package com.ruoyi.project.bus.Profit.controller;

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
import com.ruoyi.project.bus.Profit.domain.ProfitLog;
import com.ruoyi.project.bus.Profit.service.IProfitLogService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * 当天交易数据Controller
 * 
 * @author ruoyi
 * @date 2023-09-13
 */
@Controller
@RequestMapping("/Profit/Profit")
public class ProfitLogController extends BaseController
{
    private String prefix = "Profit/Profit";

    @Autowired
    private IProfitLogService profitLogService;

    @RequiresPermissions("Profit:Profit:view")
    @GetMapping()
    public String Profit()
    {
        return prefix + "/Profit";
    }

    /**
     * 查询当天交易数据列表
     */
    @RequiresPermissions("Profit:Profit:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ProfitLog profitLog)
    {
        startPage();
        List<ProfitLog> list = profitLogService.selectProfitLogList(profitLog);
        return getDataTable(list);
    }

    /**
     * 导出当天交易数据列表
     */
    @RequiresPermissions("Profit:Profit:export")
    @Log(title = "当天交易数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ProfitLog profitLog)
    {
        List<ProfitLog> list = profitLogService.selectProfitLogList(profitLog);
        ExcelUtil<ProfitLog> util = new ExcelUtil<ProfitLog>(ProfitLog.class);
        return util.exportExcel(list, "当天交易数据数据");
    }

    /**
     * 新增当天交易数据
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存当天交易数据
     */
    @RequiresPermissions("Profit:Profit:add")
    @Log(title = "当天交易数据", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ProfitLog profitLog)
    {
        return toAjax(profitLogService.insertProfitLog(profitLog));
    }

    /**
     * 修改当天交易数据
     */
    @RequiresPermissions("Profit:Profit:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        ProfitLog profitLog = profitLogService.selectProfitLogById(id);
        mmap.put("profitLog", profitLog);
        return prefix + "/edit";
    }

    /**
     * 修改保存当天交易数据
     */
    @RequiresPermissions("Profit:Profit:edit")
    @Log(title = "当天交易数据", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ProfitLog profitLog)
    {
        return toAjax(profitLogService.updateProfitLog(profitLog));
    }

    /**
     * 删除当天交易数据
     */
    @RequiresPermissions("Profit:Profit:remove")
    @Log(title = "当天交易数据", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(profitLogService.deleteProfitLogByIds(ids));
    }
}
