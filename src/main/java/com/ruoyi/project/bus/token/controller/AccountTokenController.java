package com.ruoyi.project.bus.token.controller;

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
import com.ruoyi.project.bus.token.domain.AccountToken;
import com.ruoyi.project.bus.token.service.IAccountTokenService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * 代币余额表Controller
 * 
 * @author ruoyi
 * @date 2023-09-14
 */
@Controller
@RequestMapping("/token/token")
public class AccountTokenController extends BaseController
{
    private String prefix = "token/token";

    @Autowired
    private IAccountTokenService accountTokenService;

    @RequiresPermissions("token:token:view")
    @GetMapping()
    public String token()
    {
        return prefix + "/token";
    }

    /**
     * 查询代币余额表列表
     */
    @RequiresPermissions("token:token:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AccountToken accountToken)
    {
        startPage();
        List<AccountToken> list = accountTokenService.selectAccountTokenList(accountToken);
        return getDataTable(list);
    }

    /**
     * 导出代币余额表列表
     */
    @RequiresPermissions("token:token:export")
    @Log(title = "代币余额表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AccountToken accountToken)
    {
        List<AccountToken> list = accountTokenService.selectAccountTokenList(accountToken);
        ExcelUtil<AccountToken> util = new ExcelUtil<AccountToken>(AccountToken.class);
        return util.exportExcel(list, "代币余额表数据");
    }

    /**
     * 新增代币余额表
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存代币余额表
     */
    @RequiresPermissions("token:token:add")
    @Log(title = "代币余额表", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(AccountToken accountToken)
    {
        return toAjax(accountTokenService.insertAccountToken(accountToken));
    }

    /**
     * 修改代币余额表
     */
    @RequiresPermissions("token:token:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        AccountToken accountToken = accountTokenService.selectAccountTokenById(id);
        mmap.put("accountToken", accountToken);
        return prefix + "/edit";
    }

    /**
     * 修改保存代币余额表
     */
    @RequiresPermissions("token:token:edit")
    @Log(title = "代币余额表", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AccountToken accountToken)
    {
        return toAjax(accountTokenService.updateAccountToken(accountToken));
    }

    /**
     * 删除代币余额表
     */
    @RequiresPermissions("token:token:remove")
    @Log(title = "代币余额表", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(accountTokenService.deleteAccountTokenByIds(ids));
    }
}
