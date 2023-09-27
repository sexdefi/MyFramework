package com.ruoyi.project.bus.order.controller;

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
import com.ruoyi.project.bus.order.domain.GasWithdrawLog;
import com.ruoyi.project.bus.order.service.IGasWithdrawLogService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * 提现记录Controller
 * 
 * @author ruoyi
 * @date 2023-09-27
 */
@Controller
@RequestMapping("/gas/order")
public class GasWithdrawLogController extends BaseController
{
    private String prefix = "gas/order";

    @Autowired
    private IGasWithdrawLogService gasWithdrawLogService;

    @RequiresPermissions("gas:order:view")
    @GetMapping()
    public String order()
    {
        return prefix + "/order";
    }

    /**
     * 查询提现记录列表
     */
    @RequiresPermissions("gas:order:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(GasWithdrawLog gasWithdrawLog)
    {
        startPage();
        List<GasWithdrawLog> list = gasWithdrawLogService.selectGasWithdrawLogList(gasWithdrawLog);
        return getDataTable(list);
    }

    /**
     * 导出提现记录列表
     */
    @RequiresPermissions("gas:order:export")
    @Log(title = "提现记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(GasWithdrawLog gasWithdrawLog)
    {
        List<GasWithdrawLog> list = gasWithdrawLogService.selectGasWithdrawLogList(gasWithdrawLog);
        ExcelUtil<GasWithdrawLog> util = new ExcelUtil<GasWithdrawLog>(GasWithdrawLog.class);
        return util.exportExcel(list, "提现记录数据");
    }

    /**
     * 新增提现记录
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存提现记录
     */
    @RequiresPermissions("gas:order:add")
    @Log(title = "提现记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(GasWithdrawLog gasWithdrawLog)
    {
        return toAjax(gasWithdrawLogService.insertGasWithdrawLog(gasWithdrawLog));
    }

    /**
     * 修改提现记录
     */
    @RequiresPermissions("gas:order:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        GasWithdrawLog gasWithdrawLog = gasWithdrawLogService.selectGasWithdrawLogById(id);
        mmap.put("gasWithdrawLog", gasWithdrawLog);
        return prefix + "/edit";
    }

    /**
     * 修改保存提现记录
     */
    @RequiresPermissions("gas:order:edit")
    @Log(title = "提现记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(GasWithdrawLog gasWithdrawLog)
    {
        return toAjax(gasWithdrawLogService.updateGasWithdrawLog(gasWithdrawLog));
    }

    /**
     * 删除提现记录
     */
    @RequiresPermissions("gas:order:remove")
    @Log(title = "提现记录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(gasWithdrawLogService.deleteGasWithdrawLogByIds(ids));
    }
}
