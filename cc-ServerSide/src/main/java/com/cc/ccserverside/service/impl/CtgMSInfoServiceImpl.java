package com.cc.ccserverside.service.impl;

import com.cc.ccserverside.dao.CtgMSInfoMapper;
import com.cc.ccserverside.pojo.LogInfo;
import com.cc.ccserverside.service.CtgMSInfoService;
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
 * Time: 13:53
 * Description:
 */
@Service
public class CtgMSInfoServiceImpl implements CtgMSInfoService {

    @Autowired
    private CtgMSInfoMapper ctgMSInfoMapper;
    private final Map<String,Object> map3 = new HashMap<>();  // 暂存数据

    @Override
    public List<String> findCities() {
        List<String> list;
        try {
            list = ctgMSInfoMapper.queryCities();
        } catch (Exception e) {
            return (List<String>) map3.get("cities");
        }
        if (!list.isEmpty()){
            list.remove("中华人民共和国");
            map3.remove("cities");
            map3.put("cities",list);
            return list;
        }
        return (List<String>) map3.get("cities");
    }

    @Override
    public List<String> findCategories() {
        List<String> list;
        try {
            list = ctgMSInfoMapper.queryCategories();
        } catch (Exception e) {
            return (List<String>) map3.get("ctgs");
        }
        if (!list.isEmpty()){
            map3.remove("ctgs");
            map3.put("ctgs",list);
            return list;
        }
        return (List<String>) map3.get("ctgs");
    }

    @Override
    public List<LogInfo> findByCityAndCtg(String city, String ctg) {
        List<LogInfo> list;
        try {
            list = ctgMSInfoMapper.queryByCityAndCtg(city, ctg);
        } catch (Exception e) {
            return (List<LogInfo>) map3.get("ctgMS");
        }
        if (!list.isEmpty()){
            map3.remove("ctgMS");
            map3.put("ctgMS",list);
            return list;
        }
        return (List<LogInfo>) map3.get("ctgMS");
    }

}
