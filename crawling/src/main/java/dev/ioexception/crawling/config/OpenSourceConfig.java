package dev.ioexception.crawling.config;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.support.HttpHeaders;

@Configuration
public class OpenSourceConfig extends AbstractOpenSearchConfiguration {

	@Override
	@Bean
	public RestHighLevelClient opensearchClient() {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("some-header", "on every request");

		final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
			.connectedTo("https://search-classmoa-opensearch-7x7bdmp7y5oslybzbjbcaebuiy.ap-northeast-2.es.amazonaws.com:9200")
			.usingSsl()
			.withProxy("localhost:8888")
			.withPathPrefix("ola")
			.withConnectTimeout(Duration.ofSeconds(5))
			.withSocketTimeout(Duration.ofSeconds(3))
			.withBasicAuth("classmoa", "#3Classmoa")
			.withClientConfigurer(clientConfigurer -> {
				// ...
				return clientConfigurer;
			})
			.build();

		return RestClients.create(clientConfiguration).rest();
	}
}
