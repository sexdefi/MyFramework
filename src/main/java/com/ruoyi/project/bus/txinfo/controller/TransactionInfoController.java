package com.ruoyi.project.bus.txinfo.controller;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.utils.DateUtils;
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
import com.ruoyi.project.bus.txinfo.domain.TransactionInfo;
import com.ruoyi.project.bus.txinfo.service.ITransactionInfoService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * 交易记录Controller
 * 
 * @author ruoyi
 * @date 2023-09-14
 */
@Controller
@RequestMapping("/token/txinfo")
public class TransactionInfoController extends BaseController
{
    private String prefix = "token/txinfo";

    @Autowired
    private ITransactionInfoService transactionInfoService;

    @RequiresPermissions("token:txinfo:view")
    @GetMapping()
    public String txinfo()
    {
        return prefix + "/txinfo";
    }

    /**
     * 查询交易记录列表
     */
    @RequiresPermissions("token:txinfo:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(TransactionInfo transactionInfo)
    {
        startPage();

        Map<String, Object> params = transactionInfo.getParams();
        // 如果beginTimeStamp和endTimeStamp都不为空，则将其减去8小时后，转换为时间戳
        if (params.get("beginTimestamp") != null && params.get("endTimestamp") != null && !params.get("beginTimestamp").toString().equals("") && !params.get("endTimestamp").toString().equals("")) {
            String begin = params.get("beginTimestamp").toString();
            String end = params.get("endTimestamp").toString();

            long beginTimeStamp = DateUtils.parseDate(begin).getTime() / 1000;
            long endTimeStamp = DateUtils.parseDate(end).getTime() / 1000;

//            params.put("beginTimestamp", beginTimeStamp - 8 * 60 * 60 );
//            params.put("endTimestamp", endTimeStamp - 8 * 60 * 60 );
// 生产环境需要减去8小时，开发环境不需要
            params.put("beginTimestamp", beginTimeStamp - 8 * 60 * 60 );
            params.put("endTimestamp", endTimeStamp - 8 * 60 * 60 );
        }
        // 如果transactionInfo中的txstatus不为空，则判断是否是"成功"，如果是，则将其转换为0x1，否则转换为0x0
        if (transactionInfo.getTxstatus() != null && !transactionInfo.getTxstatus().equals("")) {
            if (transactionInfo.getTxstatus().equals("成功")) {
                transactionInfo.setTxstatus("0x1");
            } else {
                transactionInfo.setTxstatus("0x0");
            }
        }
        // 将params中的参数设置到transactionInfo中
        transactionInfo.setParams(params);

        List<TransactionInfo> list = transactionInfoService.selectTransactionInfoList(transactionInfo);
        return getDataTable(list);
    }

    /**
     * 导出交易记录列表
     */
    @RequiresPermissions("token:txinfo:export")
    @Log(title = "交易记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(TransactionInfo transactionInfo)
    {
        Map<String, Object> params = transactionInfo.getParams();
        // 如果beginTimeStamp和endTimeStamp都不为空，则将其减去8小时后，转换为时间戳
        if (params.get("beginTimestamp") != null && params.get("endTimestamp") != null && !params.get("beginTimestamp").toString().equals("") && !params.get("endTimestamp").toString().equals("")) {
            String begin = params.get("beginTimestamp").toString();
            String end = params.get("endTimestamp").toString();

            long beginTimeStamp = DateUtils.parseDate(begin).getTime() / 1000;
            long endTimeStamp = DateUtils.parseDate(end).getTime() / 1000;
            params.put("beginTimestamp", beginTimeStamp);
            params.put("endTimestamp", endTimeStamp);
        }
        // 如果transactionInfo中的txstatus不为空，则判断是否是"成功"，如果是，则将其转换为0x1，否则转换为0x0
        if (transactionInfo.getTxstatus() != null && !transactionInfo.getTxstatus().equals("")) {
            if (transactionInfo.getTxstatus().equals("成功")) {
                transactionInfo.setTxstatus("0x1");
            } else {
                transactionInfo.setTxstatus("0x0");
            }
        }
        // 将params中的参数设置到transactionInfo中
        transactionInfo.setParams(params);

        List<TransactionInfo> list = transactionInfoService.selectTransactionInfoList(transactionInfo);
        ExcelUtil<TransactionInfo> util = new ExcelUtil<TransactionInfo>(TransactionInfo.class);
        return util.exportExcel(list, "交易记录数据");
    }

    /**
     * 新增交易记录
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存交易记录
     */
    @RequiresPermissions("token:txinfo:add")
    @Log(title = "交易记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(TransactionInfo transactionInfo)
    {
        return toAjax(transactionInfoService.insertTransactionInfo(transactionInfo));
    }

    /**
     * 修改交易记录
     */
    @RequiresPermissions("token:txinfo:edit")
    @GetMapping("/edit/{thash}")
    public String edit(@PathVariable("thash") String thash, ModelMap mmap)
    {
        TransactionInfo transactionInfo = transactionInfoService.selectTransactionInfoByThash(thash);
        mmap.put("transactionInfo", transactionInfo);
        return prefix + "/edit";
    }

    /**
     * 修改保存交易记录
     */
    @RequiresPermissions("token:txinfo:edit")
    @Log(title = "交易记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(TransactionInfo transactionInfo)
    {
        return toAjax(transactionInfoService.updateTransactionInfo(transactionInfo));
    }

    /**
     * 删除交易记录
     */
    @RequiresPermissions("token:txinfo:remove")
    @Log(title = "交易记录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(transactionInfoService.deleteTransactionInfoByThashs(ids));
    }
}
