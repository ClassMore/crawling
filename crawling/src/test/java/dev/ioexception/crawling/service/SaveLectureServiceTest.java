package dev.ioexception.crawling.service;

import static org.junit.jupiter.api.Assertions.*;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;

public class SaveLectureServiceTest {

	public static void main(String[] args) {
		try {
			// OpenSearch 클라이언트 생성
			OpenSearchClient client = SaveLectureService.create();

			// 간단한 인덱스 생성 요청
			CreateIndexRequest request = new CreateIndexRequest("my_index");
			CreateIndexResponse createIndexResponse = client.indices().create(request);

			// 인덱스 생성 응답 확인
			if (createIndexResponse.isAcknowledged()) {
				System.out.println("Index created successfully.");
			} else {
				System.out.println("Failed to create index.");
			}

			// 클라이언트 종료
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
