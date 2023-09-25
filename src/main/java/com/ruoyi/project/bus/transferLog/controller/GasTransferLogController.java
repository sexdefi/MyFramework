package com.ruoyi.project.bus.transferLog.controller;

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
import com.ruoyi.project.bus.transferLog.domain.GasTransferLog;
import com.ruoyi.project.bus.transferLog.service.IGasTransferLogService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * Gas领取空投记录Controller
 * 
 * @author ruoyi
 * @date 2023-09-25
 */
@Controller
@RequestMapping("/gas/transferLog")
public class GasTransferLogController extends BaseController
{
    private String prefix = "gas/transferLog";

    @Autowired
    private IGasTransferLogService gasTransferLogService;

    @RequiresPermissions("gas:transferLog:view")
    @GetMapping()
    public String transferLog()
    {
        return prefix + "/transferLog";
    }

    /**
     * 查询Gas领取空投记录列表
     */
    @RequiresPermissions("gas:transferLog:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(GasTransferLog gasTransferLog)
    {
        startPage();
        List<GasTransferLog> list = gasTransferLogService.selectGasTransferLogList(gasTransferLog);
        return getDataTable(list);
    }

    /**
     * 导出Gas领取空投记录列表
     */
    @RequiresPermissions("gas:transferLog:export")
    @Log(title = "Gas领取空投记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(GasTransferLog gasTransferLog)
    {
        List<GasTransferLog> list = gasTransferLogService.selectGasTransferLogList(gasTransferLog);
        ExcelUtil<GasTransferLog> util = new ExcelUtil<GasTransferLog>(GasTransferLog.class);
        return util.exportExcel(list, "Gas领取空投记录数据");
    }

    /**
     * 新增Gas领取空投记录
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存Gas领取空投记录
     */
    @RequiresPermissions("gas:transferLog:add")
    @Log(title = "Gas领取空投记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(GasTransferLog gasTransferLog)
    {
        return toAjax(gasTransferLogService.insertGasTransferLog(gasTransferLog));
    }

    /**
     * 修改Gas领取空投记录
     */
    @RequiresPermissions("gas:transferLog:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        GasTransferLog gasTransferLog = gasTransferLogService.selectGasTransferLogById(id);
        mmap.put("gasTransferLog", gasTransferLog);
        return prefix + "/edit";
    }

    /**
     * 修改保存Gas领取空投记录
     */
    @RequiresPermissions("gas:transferLog:edit")
    @Log(title = "Gas领取空投记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(GasTransferLog gasTransferLog)
    {
        return toAjax(gasTransferLogService.updateGasTransferLog(gasTransferLog));
    }

    /**
     * 删除Gas领取空投记录
     */
    @RequiresPermissions("gas:transferLog:remove")
    @Log(title = "Gas领取空投记录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(gasTransferLogService.deleteGasTransferLogByIds(ids));
    }
}
