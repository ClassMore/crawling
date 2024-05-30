package dev.ioexception.crawling.repository.query;

import dev.ioexception.crawling.entity.LectureTag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureTagRepositoryCustom {
    List<LectureTag> getLectureTags(String lectureId);
}
