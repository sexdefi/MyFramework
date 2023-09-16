package com.ruoyi.project.bussiness.service;

import com.ruoyi.common.utils.CacheUtils;
import com.ruoyi.framework.web.service.CacheService;
import com.ruoyi.project.bus.blacklist.domain.Blacklist;
import com.ruoyi.project.bus.blacklist.service.IBlacklistService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Service
public class BlackService {

    @Autowired
    CacheService cache;

    @Autowired
    IBlacklistService blacklistService;

    public static final String NAME = "blacklist";

    public boolean isBlacklist(String address) {
        Object o = cache.getCacheValue(NAME, address);
        if (o == null) {
            return false;
        }
        return true;
    }

    @GetMapping("/blacklist/refresh")
    @ResponseBody
    @ApiOperation(value = "刷新黑名单缓存")
    public void initBlacklist() {
        // 如果缓存存在，则删除
        System.out.println("清除黑名单缓存");
        cache.clearCacheName(NAME);

        Blacklist bl = new Blacklist();
        bl.setEnable(1l);

        List<Blacklist> blacklists = blacklistService.selectBlacklistList(bl);

        blacklists.forEach(blacklist -> {
            CacheUtils.put("blacklist", blacklist.getAddr(), 1);
        });
        System.out.println("黑名单缓存初始化完成, 共" + blacklists.size() + "条记录");
    }


}
