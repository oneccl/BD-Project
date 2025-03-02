package com.cc.ccserverside.dao;

import com.cc.ccserverside.pojo.LogInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/25
 * Time: 13:47
 * Description:
 */
@Repository
public interface CtgMSInfoMapper {

    List<String> queryCities();

    List<String> queryCategories();

    List<LogInfo> queryByCityAndCtg(String city, String ctg);

}
