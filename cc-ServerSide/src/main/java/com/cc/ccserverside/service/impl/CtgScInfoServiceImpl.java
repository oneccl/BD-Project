package com.cc.ccserverside.service.impl;

import com.cc.ccserverside.dao.CtgScInfoMapper;
import com.cc.ccserverside.pojo.LogInfo;
import com.cc.ccserverside.service.CtgScInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/25
 * Time: 11:44
 * Description:
 */
@Service("ctgScInfoService")
public class CtgScInfoServiceImpl implements CtgScInfoService {

    @Autowired
    private CtgScInfoMapper ctgScInfoMapper;
    private final Map<String,Object> map2 = new HashMap<>();  // 暂存数据

    @Override
    public List<String> findCities() {
        List<String> list;
        try {
            list = ctgScInfoMapper.queryCities();
        } catch (Exception e) {
            return (List<String>) map2.get("cities");
        }
        if (!list.isEmpty()){
            list.remove("中华人民共和国");
            map2.remove("cities");
            map2.put("cities",list);
            return list;
        }
        return (List<String>) map2.get("cities");
    }

    @Override
    public List<String> findCategories() {
        List<String> list;
        try {
            list = ctgScInfoMapper.queryCategories();
        } catch (Exception e) {
            return (List<String>) map2.get("ctgs");
        }
        if (!list.isEmpty()){
            map2.remove("ctgs");
            map2.put("ctgs",list);
            return list;
        }
        return (List<String>) map2.get("ctgs");
    }

    @Override
    public List<LogInfo> findByCityAndCtg(String city, String ctg) {
        List<LogInfo> list;
        try {
            list = ctgScInfoMapper.queryByCityAndCtg(city, ctg);
        } catch (Exception e) {
            return (List<LogInfo>) map2.get("ctgSc");
        }
        if (!list.isEmpty()){
            map2.remove("ctgSc");
            map2.put("ctgSc",list);
            return list;
        }
        return (List<LogInfo>) map2.get("ctgSc");
    }

}
