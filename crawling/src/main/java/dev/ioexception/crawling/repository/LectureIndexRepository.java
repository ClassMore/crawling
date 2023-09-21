package dev.ioexception.crawling.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import dev.ioexception.crawling.entity.LectureIndex;

public interface LectureIndexRepository extends ElasticsearchRepository<LectureIndex, String> {
	List<LectureIndex> findByTitleAndInstructor(String title, String instructor);
}
