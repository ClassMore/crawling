package dev.ioexception.crawling.repository;

import dev.ioexception.crawling.entity.LectureTag;
import dev.ioexception.crawling.repository.query.LectureTagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureTagRepository extends JpaRepository<LectureTag, Long>, LectureTagRepositoryCustom {
    List<LectureTag> findAllByLecture_LectureId(String lectureId);
}
