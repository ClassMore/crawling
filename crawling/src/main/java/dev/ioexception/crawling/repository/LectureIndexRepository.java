package dev.ioexception.crawling.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.ioexception.crawling.entity.LectureIndex;

@Repository
public interface LectureIndexRepository extends CrudRepository<LectureIndex, Long> {
	List<LectureIndex> findByTitle(String title);
}
