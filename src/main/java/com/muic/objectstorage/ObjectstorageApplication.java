package com.muic.objectstorage;

import com.muic.objectstorage.Property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import javax.servlet.Filter;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class ObjectstorageApplication {

	public static void main(String[] args) {
		SpringApplication.run(ObjectstorageApplication.class, args);
	}

	@Bean
	public Filter filter(){
		ShallowEtagHeaderFilter filter = new ShallowEtagHeaderFilter();
		return filter;
	}
}
