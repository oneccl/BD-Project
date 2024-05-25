package com.cc.ccserverside.controller;

import com.cc.ccserverside.service.CtgMSInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/25
 * Time: 13:55
 * Description:
 */
@CrossOrigin
@RestController
@RequestMapping("CtgMS")
public class CtgMSInfoController {

    @Autowired
    private CtgMSInfoService ctgMSInfoService;

    // 业务：实时统计每个城市的每个种类(category)的销售量(month_sales)排名前10

    @RequestMapping("getCities")
    public Object getCities(){
        return ctgMSInfoService.findCities();
    }

    @RequestMapping("getCategories")
    public Object getCategories(){
        return ctgMSInfoService.findCategories();
    }

    @RequestMapping("getByCityAndCtg")
    public Object getByCityAndCtg(String city,String ctg){
        return ctgMSInfoService.findByCityAndCtg(city,ctg);
    }

}
