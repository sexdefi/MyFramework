package com.ruoyi.project.bus.blacklist.controller;

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
import com.ruoyi.project.bus.blacklist.domain.Blacklist;
import com.ruoyi.project.bus.blacklist.service.IBlacklistService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * 黑名单Controller
 * 
 * @author ruoyi
 * @date 2023-09-16
 */
@Controller
@RequestMapping("/bus/blacklist")
public class BlacklistController extends BaseController
{
    private String prefix = "bus/blacklist";

    @Autowired
    private IBlacklistService blacklistService;

    @RequiresPermissions("bus:blacklist:view")
    @GetMapping()
    public String blacklist()
    {
        return prefix + "/blacklist";
    }

    /**
     * 查询黑名单列表
     */
    @RequiresPermissions("bus:blacklist:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Blacklist blacklist)
    {
        startPage();
        List<Blacklist> list = blacklistService.selectBlacklistList(blacklist);
        return getDataTable(list);
    }

    /**
     * 导出黑名单列表
     */
    @RequiresPermissions("bus:blacklist:export")
    @Log(title = "黑名单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Blacklist blacklist)
    {
        List<Blacklist> list = blacklistService.selectBlacklistList(blacklist);
        ExcelUtil<Blacklist> util = new ExcelUtil<Blacklist>(Blacklist.class);
        return util.exportExcel(list, "黑名单数据");
    }

    /**
     * 新增黑名单
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存黑名单
     */
    @RequiresPermissions("bus:blacklist:add")
    @Log(title = "黑名单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Blacklist blacklist)
    {
        return toAjax(blacklistService.insertBlacklist(blacklist));
    }

    /**
     * 修改黑名单
     */
    @RequiresPermissions("bus:blacklist:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        Blacklist blacklist = blacklistService.selectBlacklistById(id);
        mmap.put("blacklist", blacklist);
        return prefix + "/edit";
    }

    /**
     * 修改保存黑名单
     */
    @RequiresPermissions("bus:blacklist:edit")
    @Log(title = "黑名单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Blacklist blacklist)
    {
        return toAjax(blacklistService.updateBlacklist(blacklist));
    }

    /**
     * 删除黑名单
     */
    @RequiresPermissions("bus:blacklist:remove")
    @Log(title = "黑名单", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(blacklistService.deleteBlacklistByIds(ids));
    }
}
