package com.ruoyi.project.bus.operate.controller;

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
import com.ruoyi.project.bus.operate.domain.GasOperateLog;
import com.ruoyi.project.bus.operate.service.IGasOperateLogService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * gas领取操作记录表Controller
 * 
 * @author ruoyi
 * @date 2023-09-25
 */
@Controller
@RequestMapping("/gas/operate")
public class GasOperateLogController extends BaseController
{
    private String prefix = "gas/operate";

    @Autowired
    private IGasOperateLogService gasOperateLogService;

    @RequiresPermissions("gas:operate:view")
    @GetMapping()
    public String operate()
    {
        return prefix + "/operate";
    }

    /**
     * 查询gas领取操作记录表列表
     */
    @RequiresPermissions("gas:operate:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(GasOperateLog gasOperateLog)
    {
        startPage();
        List<GasOperateLog> list = gasOperateLogService.selectGasOperateLogList(gasOperateLog);
        return getDataTable(list);
    }

    /**
     * 导出gas领取操作记录表列表
     */
    @RequiresPermissions("gas:operate:export")
    @Log(title = "gas领取操作记录表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(GasOperateLog gasOperateLog)
    {
        List<GasOperateLog> list = gasOperateLogService.selectGasOperateLogList(gasOperateLog);
        ExcelUtil<GasOperateLog> util = new ExcelUtil<GasOperateLog>(GasOperateLog.class);
        return util.exportExcel(list, "gas领取操作记录表数据");
    }

    /**
     * 新增gas领取操作记录表
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存gas领取操作记录表
     */
    @RequiresPermissions("gas:operate:add")
    @Log(title = "gas领取操作记录表", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(GasOperateLog gasOperateLog)
    {
        return toAjax(gasOperateLogService.insertGasOperateLog(gasOperateLog));
    }

    /**
     * 修改gas领取操作记录表
     */
    @RequiresPermissions("gas:operate:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        GasOperateLog gasOperateLog = gasOperateLogService.selectGasOperateLogById(id);
        mmap.put("gasOperateLog", gasOperateLog);
        return prefix + "/edit";
    }

    /**
     * 修改保存gas领取操作记录表
     */
    @RequiresPermissions("gas:operate:edit")
    @Log(title = "gas领取操作记录表", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(GasOperateLog gasOperateLog)
    {
        return toAjax(gasOperateLogService.updateGasOperateLog(gasOperateLog));
    }

    /**
     * 删除gas领取操作记录表
     */
    @RequiresPermissions("gas:operate:remove")
    @Log(title = "gas领取操作记录表", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(gasOperateLogService.deleteGasOperateLogByIds(ids));
    }
}
