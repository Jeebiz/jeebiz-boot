/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.demo.setup.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.springfox.spring.boot.Swagger2WebMvcProperties;
import springfox.documentation.annotations.ApiIgnore;

@Configuration
@ConditionalOnProperty(prefix = Swagger2WebMvcProperties.PREFIX, value = "enabled", havingValue = "true")
public class WebMvcSwagger2Configuration {

	@Controller
	class HomepageController {
	 
		@ApiIgnore
		@GetMapping("/")
		public String index() {
			return "forward:/doc.html";
		}
		
	}
	
}
