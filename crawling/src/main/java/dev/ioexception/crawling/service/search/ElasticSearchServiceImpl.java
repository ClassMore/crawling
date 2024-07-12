package dev.ioexception.crawling.service.search;

import dev.ioexception.crawling.dto.SearchedLectureResponse;
import dev.ioexception.crawling.entity.LectureDocument;
import dev.ioexception.crawling.repository.ElasticsearchLectureRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticSearchServiceImpl implements SearchService{
    private final ElasticsearchLectureRepository elasticsearchLectureRepository;

    @Override
    public List<SearchedLectureResponse> search(String q) {
        List<LectureDocument> lectures = elasticsearchLectureRepository.findAllByTitle(q);

        if (lectures == null) {
            throw new IllegalArgumentException();
        }

        return lectures.stream()
                .map(lecture -> new SearchedLectureResponse().toDto(lecture))
                .collect(Collectors.toList());
    }
}
