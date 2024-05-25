package com.cc.ccserverside;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/24
 * Time: 20:11
 * Description:
 */
@MapperScan("com.cc.ccserverside.dao")
@SpringBootApplication
public class ServerSideApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerSideApplication.class,args);
    }

}
