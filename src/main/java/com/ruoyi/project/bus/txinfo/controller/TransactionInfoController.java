package com.ruoyi.project.bus.txinfo.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ruoyi.common.utils.DateUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
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
public class TransactionInfoController extends BaseController {
    private String prefix = "token/txinfo";

    @Autowired
    private ITransactionInfoService transactionInfoService;

    @RequiresPermissions("token:txinfo:view")
    @GetMapping()
    public String txinfo() {
        return prefix + "/txinfo";
    }

    /**
     * 查询交易记录列表
     */
    @RequiresPermissions("token:txinfo:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(TransactionInfo transactionInfo) {
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
            params.put("beginTimestamp", beginTimeStamp - 8 * 60 * 60);
            params.put("endTimestamp", endTimeStamp - 8 * 60 * 60);
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

    @GetMapping("/analysis")
    @ApiOperation("交易记录分析")
    @ResponseBody
    public String getAnalysis(@RequestParam(required = true) String from_day,@ApiParam(format = "yyyy-MM-dd")  @RequestParam(required = true) String to_day, @RequestParam Boolean isSuccess) {

        TransactionInfo info = new TransactionInfo();
        if(isSuccess == null){
            isSuccess = true;
        }
        if(from_day == null || to_day == null){
            return "参数错误";
        }
        // 如果from_day或to_day是4为数字，则将其转换为2023-09-14格式
        if (from_day.length() == 4) {
            from_day = "2023" + "-" + from_day.substring(0, 2) + "-" + from_day.substring(2, 4);
        }
        if (to_day.length() == 4) {
            to_day = "2023" + "-" + to_day.substring(0, 2) + "-" + to_day.substring(2, 4);
        }
        // 如果from_day或to_day是8为数字，则将其转换为2023-09-14格式
        if (from_day.length() == 8) {
            from_day = from_day.substring(0, 4) + "-" + from_day.substring(4, 6) + "-" + from_day.substring(6, 8);
        }
        if (to_day.length() == 8) {
            to_day = to_day.substring(0, 4) + "-" + to_day.substring(4, 6) + "-" + to_day.substring(6, 8);
        }

        long beginTimeStamp = DateUtils.parseDate(from_day).getTime() / 1000;
        long endTimeStamp = DateUtils.parseDate(to_day).getTime() / 1000;

        Map<String, Object> params = new HashMap<>();
        params.put("beginTimestamp", beginTimeStamp - 8 * 60 * 60);
        params.put("endTimestamp", endTimeStamp - 8 * 60 * 60);

        if (isSuccess) {
            info.setTxstatus("0x1");
        } else {
            info.setTxstatus("0x0");
        }
        // 将params中的参数设置到transactionInfo中
        info.setParams(params);
        List<TransactionInfo> list = transactionInfoService.selectTransactionInfoList(info);
        if(list.size() == 0){
            return "没有数据";
        }
        HashMap<String, BigDecimal> newMap = new HashMap<>();
        BigDecimal total = new BigDecimal(0);
        for (TransactionInfo tx : list) {
            String from = tx.getFromAddr();
            BigDecimal gasMul = new BigDecimal(tx.getGasPrice()).multiply(new BigDecimal(tx.getGasUsed()));
            if (!newMap.containsKey(from)) {
                newMap.put(from, gasMul);
            } else {
                BigDecimal temp2 = newMap.get(from);
                BigDecimal after = temp2.add(gasMul);
                newMap.put(from, after);
            }
            total = total.add(gasMul);
        }
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : newMap.entrySet()) {
            String key = entry.getKey();
            BigDecimal value = entry.getValue();
            result.add(key + "," + value);
        }
        StringBuffer sb = new StringBuffer();
        sb.append(isSuccess ? "成功" : "失败").append("交易记录分析").append(" 共").append(list.size()).append("条").append("\n");
        sb.append("独立地址数:").append(newMap.size()).append("\n");
        sb.append("总共花费gas:").append(total).append(" ").append(total.divide(new BigDecimal(1000000000000000000L), 6, BigDecimal.ROUND_HALF_UP)).append("BTC").append("\n");

        for (String s : result) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

    // 计算交易记录的总数
//    @RequiresPermissions("token:txinfo:list")
    @PostMapping("/analysis")
    @ResponseBody
    public TableDataInfo analysis(TransactionInfo transactionInfo) {
        Map<String, Object> params = transactionInfo.getParams();
        // 如果beginTimeStamp和endTimeStamp都不为空，则将其减去8小时后，转换为时间戳
        if (params.get("beginTimestamp") != null && params.get("endTimestamp") != null && !params.get("beginTimestamp").toString().equals("") && !params.get("endTimestamp").toString().equals("")) {
            String begin = params.get("beginTimestamp").toString();
            String end = params.get("endTimestamp").toString();

            long beginTimeStamp = DateUtils.parseDate(begin).getTime() / 1000;
            long endTimeStamp = DateUtils.parseDate(end).getTime() / 1000;

            params.put("beginTimestamp", beginTimeStamp - 8 * 60 * 60);
            params.put("endTimestamp", endTimeStamp - 8 * 60 * 60);
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
        // 计算TransactionInfo中From相同的Gas总消耗
        // 新结果集
        HashMap<String, TransactionInfo> newMap = new HashMap<>();

        for (TransactionInfo tx : list) {
            BigDecimal gasTotal = new BigDecimal(0);
            String from = tx.getFromAddr();
            TransactionInfo temp = new TransactionInfo();
            // 如果from相同，则将gas累加
            if (!newMap.containsKey(from)) {
                temp.setFromAddr(from);
                BigDecimal gasMul = new BigDecimal(tx.getGasPrice()).multiply(new BigDecimal(tx.getGasUsed()));
                temp.setGasUsed(gasMul.toString());
                temp.setBlockHash("0");
                temp.setBlockNumber(0l);
                temp.setTxstatus(tx.getTxstatus());
                temp.setGas("0");
                temp.setGasPrice("0");
                temp.setCreates(tx.getCreates());
                temp.setActualData("");
                temp.setTimestamp(0l);
                temp.setToAddr("");
                temp.setActualData("");
                temp.setFeePercent(0l);
                temp.setContractAddress("");
                temp.setMetaAddress("");
                temp.setNonce(0l);
                temp.setTransactionIndex(0l);
                temp.setValue("0");
                newMap.put(from, temp);
            } else {
                temp = newMap.get(from);
                BigDecimal newGasMul = new BigDecimal(tx.getGasPrice()).multiply(new BigDecimal(tx.getGasUsed()));
                BigDecimal totalGasMul = new BigDecimal(temp.getGasUsed()).add(newGasMul);
                temp.setGasUsed(totalGasMul.toString());
            }
        }
        List<TransactionInfo> newList = newMap.values().stream().collect(Collectors.toList());
        return getDataTable(newList);
    }

    /**
     * 导出交易记录列表
     */
    @RequiresPermissions("token:txinfo:export")
    @Log(title = "交易记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(TransactionInfo transactionInfo) {

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
            params.put("beginTimestamp", beginTimeStamp - 8 * 60 * 60);
            params.put("endTimestamp", endTimeStamp - 8 * 60 * 60);
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
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存交易记录
     */
    @RequiresPermissions("token:txinfo:add")
    @Log(title = "交易记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(TransactionInfo transactionInfo) {
        return toAjax(transactionInfoService.insertTransactionInfo(transactionInfo));
    }

    /**
     * 修改交易记录
     */
    @RequiresPermissions("token:txinfo:edit")
    @GetMapping("/edit/{thash}")
    public String edit(@PathVariable("thash") String thash, ModelMap mmap) {
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
    public AjaxResult editSave(TransactionInfo transactionInfo) {
        return toAjax(transactionInfoService.updateTransactionInfo(transactionInfo));
    }

    /**
     * 删除交易记录
     */
    @RequiresPermissions("token:txinfo:remove")
    @Log(title = "交易记录", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(transactionInfoService.deleteTransactionInfoByThashs(ids));
    }
}
