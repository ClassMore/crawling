package dev.ioexception.crawling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;

// Elasticdl 자동으로 검색되는 것을 막는다.
@SpringBootApplication(exclude = {ElasticsearchDataAutoConfiguration.class})
public class CrawlingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlingApplication.class, args);
    }

}
