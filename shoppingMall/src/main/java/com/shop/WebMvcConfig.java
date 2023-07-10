package com.shop;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

    	registry.addResourceHandler("/p_img/**")
				.addResourceLocations("file:///c:/Repository/shop/product/");
    	registry.addResourceHandler("/**")
    			.addResourceLocations("classpath:/static/");
    }

}
