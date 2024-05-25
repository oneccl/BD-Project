package com.cc.ccserverside.service;

import com.cc.ccserverside.pojo.LogInfo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/25
 * Time: 13:52
 * Description:
 */
public interface CtgMSInfoService {

    List<String> findCities();

    List<String> findCategories();

    List<LogInfo> findByCityAndCtg(String city, String ctg);

}
