package dev.ioexception.crawling.service.search;

import dev.ioexception.crawling.dto.SearchedLectureResponse;
import java.util.List;

public interface SearchService {
    List<SearchedLectureResponse> search(String q);
}
