package com.cc.ccserverside.controller;

import com.cc.ccserverside.service.CtgScInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/25
 * Time: 11:45
 * Description:
 */
@CrossOrigin
@RestController
@RequestMapping("CtgSc")
public class CtgScInfoController {

    @Autowired
    private CtgScInfoService ctgScInfoService;

    // 实时统计每个城市的每个种类(category)的评分(score)排名前10

    @RequestMapping("getCities")
    public Object getCities(){
        return ctgScInfoService.findCities();
    }

    @RequestMapping("getCategories")
    public Object getCategories(){
        return ctgScInfoService.findCategories();
    }

    @RequestMapping("getByCityAndCtg")
    public Object getByCityAndCtg(String city,String ctg){
        return ctgScInfoService.findByCityAndCtg(city,ctg);
    }

}
