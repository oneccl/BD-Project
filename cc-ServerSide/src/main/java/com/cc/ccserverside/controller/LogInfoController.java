package com.cc.ccserverside.controller;

import com.cc.ccserverside.service.LogInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/25
 * Time: 9:58
 * Description:
 */
@CrossOrigin
@RestController
public class LogInfoController {

    @Autowired
    private LogInfoService logInfoService;

    // 实时统计每个城市店铺评分(score)排名前10的店铺信息(city、id、name、category、score)

    @RequestMapping("getCities")
    public Object getCities(){
        return logInfoService.findCities();
    }

    @RequestMapping("getScoreByCity")
    public Object getScoreByCity(String city){
        return logInfoService.findScoreByCity(city);
    }

}
