package com.oc.startup;

import org.springframework.boot.SpringApplication;

import com.oc.boot.Boot;

/**
 * 程序入口
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月5日
 * @version v 1.0
 */
public class Startup {

    public static void main( String[] args ) {
        SpringApplication.run(Boot.class, args);
    }
}
