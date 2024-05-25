package com.cc.ccserverside.service.impl;

import com.cc.ccserverside.dao.LogInfoMapper;
import com.cc.ccserverside.pojo.LogInfo;
import com.cc.ccserverside.service.LogInfoService;
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
 * Time: 9:34
 * Description:
 */
@Service("logInfoService")
public class LogInfoServiceImpl implements LogInfoService {

    @Autowired
    private LogInfoMapper logInfoMapper;
    private final Map<String,Object> map1 = new HashMap<>();  // 暂存数据

    @Override
    public List<String> findCities() {
        List<String> list;
        try {
            list = logInfoMapper.queryCities();
        } catch (Exception e) {
            return (List<String>) map1.get("cities");
        }
        if (!list.isEmpty()){
            list.remove("中华人民共和国");
            map1.remove("cities");
            map1.put("cities",list);
            return list;
        }
        return (List<String>) map1.get("cities");
    }

    @Override
    public List<LogInfo> findScoreByCity(String city) {
        List<LogInfo> list;
        try {
            list = logInfoMapper.queryScoreByCity(city);
        } catch (Exception e) {
            return (List<LogInfo>) map1.get("citySc");
        }
        if (!list.isEmpty()){
            map1.remove("citySc");
            map1.put("citySc",list);
            return list;
        }
        return (List<LogInfo>) map1.get("citySc");
    }

}
