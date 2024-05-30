//package dev.ioexception.crawling.service;
//
//import static java.util.Collections.singletonMap;
//import static org.opensearch.action.support.WriteRequest.RefreshPolicy.IMMEDIATE;
//
//import java.io.IOException;
//import lombok.RequiredArgsConstructor;
//import org.opensearch.action.index.IndexRequest;
//import org.opensearch.action.index.IndexResponse;
//import org.opensearch.client.RequestOptions;
//import org.opensearch.client.RestHighLevelClient;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class OpensearchService {
//
//    private final RestHighLevelClient highLevelClient;
//
//    private void OpensearchRequest() throws IOException {
//        IndexRequest request = new IndexRequest("spring-data")
//                .id(randomID())
//                .source(singletonMap("feature", "high-level-rest-client"))
//                .setRefreshPolicy(IMMEDIATE);
//
//        IndexResponse response = highLevelClient.index(request, RequestOptions.DEFAULT);
//    }
//
//    private String randomID() {
//
//
//    }
//}
