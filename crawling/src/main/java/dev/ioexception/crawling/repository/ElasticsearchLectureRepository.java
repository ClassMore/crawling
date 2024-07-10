package dev.ioexception.crawling.repository;

import dev.ioexception.crawling.entity.LectureDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticsearchLectureRepository extends ElasticsearchRepository<LectureDocument, Long> {
    List<LectureDocument> findAllByTitle(String title);
}
