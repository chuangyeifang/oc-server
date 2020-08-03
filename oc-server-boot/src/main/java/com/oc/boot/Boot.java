package com.oc.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.oc.provider.context.SpringContext;

/**
 * 配置启动加载项
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月5日
 * @version v 1.0
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages="com.oc")
@MapperScan(basePackages="com.oc.mapper")
@ServletComponentScan
@Configuration
public class Boot{
	
	@Bean
	public SpringContext getAppContext() {
		return new SpringContext();
	}
}
