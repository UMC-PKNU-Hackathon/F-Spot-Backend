package com.example.spot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String API_NAME = "SPOT API";
    private static final String API_VERSION = "0.0.1";
    private static final String API_DESCRIPTION = "스팟 API 명세서";


    /**
     * Swagger를 사용하기 위한 핵심 객체(Docket) 생성
     *
     * apiInfo: 현재 등록하려 하는 API 명세 정보
     * apis:  api 스펙이 작성되어 있는 패키지(Controller) 지정
     * paths: 패키지 내, 특정 경로를 추가 세팅할 경우 사용 (PathSelectors.any() = 패키지 내, 전체 경로에 대해 문서화 진행)

     * @return Docket
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName(API_VERSION)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.spot"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }


    /**
     * API 문서 정보 설정
     *
     * title: API 이름
     * version: API 명세 버전
     * description: API description
     *
     * @return ApiInfo
     */
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(API_NAME)
                .version(API_VERSION)
                .description(API_DESCRIPTION)
                .build();

    }
}