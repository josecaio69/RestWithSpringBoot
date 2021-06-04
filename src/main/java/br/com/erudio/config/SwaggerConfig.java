package br.com.erudio.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("br.com.erudio"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		// TODO Auto-generated method stub
		return new ApiInfo("RESTful API With Spring Boot",
				"API praticando com Spring Boot", 
				"VersaoUm",
				"URL Termos de servico da API", 
				new Contact("Jose Caio", "https://www.linkedin.com/in/jos%C3%A9-caio-de-ara%C3%BAjo-cordeiro-a47a2b1a2/", "jccaioaraujo@gmail.com"), 
				"Licence da API", 
				"URL dos termos",
				Collections.emptyList());
	}
}
