package com.ruoyi.project.swap.ad.controller;

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
import com.ruoyi.project.swap.ad.domain.AdConfig;
import com.ruoyi.project.swap.ad.service.IAdConfigService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * 广告BannerController
 * 
 * @author ruoyi
 * @date 2023-11-07
 */
@Controller
@RequestMapping("/ad/ad")
public class AdConfigController extends BaseController
{
    private String prefix = "ad/ad";

    @Autowired
    private IAdConfigService adConfigService;

    @RequiresPermissions("ad:ad:view")
    @GetMapping()
    public String ad()
    {
        return prefix + "/ad";
    }

    /**
     * 查询广告Banner列表
     */
    @RequiresPermissions("ad:ad:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AdConfig adConfig)
    {
        startPage();
        List<AdConfig> list = adConfigService.selectAdConfigList(adConfig);
        return getDataTable(list);
    }

    /**
     * 导出广告Banner列表
     */
    @RequiresPermissions("ad:ad:export")
    @Log(title = "广告Banner", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AdConfig adConfig)
    {
        List<AdConfig> list = adConfigService.selectAdConfigList(adConfig);
        ExcelUtil<AdConfig> util = new ExcelUtil<AdConfig>(AdConfig.class);
        return util.exportExcel(list, "广告Banner数据");
    }

    /**
     * 新增广告Banner
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存广告Banner
     */
    @RequiresPermissions("ad:ad:add")
    @Log(title = "广告Banner", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(AdConfig adConfig)
    {
        return toAjax(adConfigService.insertAdConfig(adConfig));
    }

    /**
     * 修改广告Banner
     */
    @RequiresPermissions("ad:ad:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        AdConfig adConfig = adConfigService.selectAdConfigById(id);
        mmap.put("adConfig", adConfig);
        return prefix + "/edit";
    }

    /**
     * 修改保存广告Banner
     */
    @RequiresPermissions("ad:ad:edit")
    @Log(title = "广告Banner", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AdConfig adConfig)
    {
        return toAjax(adConfigService.updateAdConfig(adConfig));
    }

    /**
     * 删除广告Banner
     */
    @RequiresPermissions("ad:ad:remove")
    @Log(title = "广告Banner", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(adConfigService.deleteAdConfigByIds(ids));
    }
}
