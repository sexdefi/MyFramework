package com.ruoyi.project.bus.airdrop.controller;

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
import com.ruoyi.project.bus.airdrop.domain.Airdrop;
import com.ruoyi.project.bus.airdrop.service.IAirdropService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * airdropController
 * 
 * @author ruoyi
 * @date 2023-08-14
 */
@Controller
@RequestMapping("/airdropLog/airdrop")
public class AirdropController extends BaseController
{
    private String prefix = "airdropLog/airdrop";

    @Autowired
    private IAirdropService airdropService;

    @RequiresPermissions("airdropLog:airdrop:view")
    @GetMapping()
    public String airdrop()
    {
        return prefix + "/airdrop";
    }

    /**
     * 查询airdrop列表
     */
    @RequiresPermissions("airdropLog:airdrop:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Airdrop airdrop)
    {
        startPage();
        List<Airdrop> list = airdropService.selectAirdropList(airdrop);
        return getDataTable(list);
    }

    /**
     * 导出airdrop列表
     */
    @RequiresPermissions("airdropLog:airdrop:export")
    @Log(title = "airdrop", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Airdrop airdrop)
    {
        List<Airdrop> list = airdropService.selectAirdropList(airdrop);
        ExcelUtil<Airdrop> util = new ExcelUtil<Airdrop>(Airdrop.class);
        return util.exportExcel(list, "airdrop数据");
    }

    /**
     * 新增airdrop
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存airdrop
     */
    @RequiresPermissions("airdropLog:airdrop:add")
    @Log(title = "airdrop", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Airdrop airdrop)
    {
        return toAjax(airdropService.insertAirdrop(airdrop));
    }

    /**
     * 修改airdrop
     */
    @RequiresPermissions("airdropLog:airdrop:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        Airdrop airdrop = airdropService.selectAirdropById(id);
        mmap.put("airdrop", airdrop);
        return prefix + "/edit";
    }

    /**
     * 修改保存airdrop
     */
    @RequiresPermissions("airdropLog:airdrop:edit")
    @Log(title = "airdrop", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Airdrop airdrop)
    {
        return toAjax(airdropService.updateAirdrop(airdrop));
    }

    /**
     * 删除airdrop
     */
    @RequiresPermissions("airdropLog:airdrop:remove")
    @Log(title = "airdrop", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(airdropService.deleteAirdropByIds(ids));
    }
}
