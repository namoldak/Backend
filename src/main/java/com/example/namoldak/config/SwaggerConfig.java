//package com.example.namoldak.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//// 기능 : Swagger 사용에 필요한 설정
//@Configuration
//@EnableSwagger2
//@EnableAsync
//@EnableWebMvc
//public class SwaggerConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
//
//    @Bean
//    public Docket swagger() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build()
//                .apiInfo(apiInfo())
//                .useDefaultResponseMessages(false);
//    }
//
//    //http://localhost:8080/swagger-ui/index.html#/ 스웨거 주소 url 입니다.
//    private ApiInfo apiInfo() {
//        ApiInfo apiInfo =
//                new ApiInfo("나몰닭 API", "나만 모른닭 API 명세서 입니다", "ver1.0", "https://github.com/namoldak/Backend2", "contact", "노션", "https://www.notion.so/ad96dfad0856455c922e9d0f756a7f60");
//        return apiInfo;
//    }
//}