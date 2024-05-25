package com.cc.ccserverside.service;

import com.cc.ccserverside.pojo.LogInfo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/25
 * Time: 9:31
 * Description:
 */
public interface LogInfoService {

    List<String> findCities();

    List<LogInfo> findScoreByCity(String city);

}
