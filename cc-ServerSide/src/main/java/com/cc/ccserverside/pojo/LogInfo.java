package com.cc.ccserverside.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/25
 * Time: 9:24
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogInfo {

    private String city;
    private String id;
    private String name;
    private String category;
    private Double score;
    private Long month_sales;

}
