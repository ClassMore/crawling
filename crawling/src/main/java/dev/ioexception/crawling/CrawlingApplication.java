package dev.ioexception.crawling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;

@SpringBootApplication(exclude = {ElasticsearchDataAutoConfiguration.class})
public class CrawlingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlingApplication.class, args);
    }

}
