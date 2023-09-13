package com.ruoyi.project.bus.batch.controller;

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
import com.ruoyi.project.bus.batch.domain.AirdropBatchLog;
import com.ruoyi.project.bus.batch.service.IAirdropBatchLogService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * 空投批次快照Controller
 * 
 * @author ruoyi
 * @date 2023-09-13
 */
@Controller
@RequestMapping("/batch/batch")
public class AirdropBatchLogController extends BaseController
{
    private String prefix = "batch/batch";

    @Autowired
    private IAirdropBatchLogService airdropBatchLogService;

    @RequiresPermissions("batch:batch:view")
    @GetMapping()
    public String batch()
    {
        return prefix + "/batch";
    }

    /**
     * 查询空投批次快照列表
     */
    @RequiresPermissions("batch:batch:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AirdropBatchLog airdropBatchLog)
    {
        startPage();
        List<AirdropBatchLog> list = airdropBatchLogService.selectAirdropBatchLogList(airdropBatchLog);
        return getDataTable(list);
    }

    /**
     * 导出空投批次快照列表
     */
    @RequiresPermissions("batch:batch:export")
    @Log(title = "空投批次快照", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AirdropBatchLog airdropBatchLog)
    {
        List<AirdropBatchLog> list = airdropBatchLogService.selectAirdropBatchLogList(airdropBatchLog);
        ExcelUtil<AirdropBatchLog> util = new ExcelUtil<AirdropBatchLog>(AirdropBatchLog.class);
        return util.exportExcel(list, "空投批次快照数据");
    }

    /**
     * 新增空投批次快照
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存空投批次快照
     */
    @RequiresPermissions("batch:batch:add")
    @Log(title = "空投批次快照", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(AirdropBatchLog airdropBatchLog)
    {
        return toAjax(airdropBatchLogService.insertAirdropBatchLog(airdropBatchLog));
    }

    /**
     * 修改空投批次快照
     */
    @RequiresPermissions("batch:batch:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        AirdropBatchLog airdropBatchLog = airdropBatchLogService.selectAirdropBatchLogById(id);
        mmap.put("airdropBatchLog", airdropBatchLog);
        return prefix + "/edit";
    }

    /**
     * 修改保存空投批次快照
     */
    @RequiresPermissions("batch:batch:edit")
    @Log(title = "空投批次快照", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AirdropBatchLog airdropBatchLog)
    {
        return toAjax(airdropBatchLogService.updateAirdropBatchLog(airdropBatchLog));
    }

    /**
     * 删除空投批次快照
     */
    @RequiresPermissions("batch:batch:remove")
    @Log(title = "空投批次快照", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(airdropBatchLogService.deleteAirdropBatchLogByIds(ids));
    }
}
