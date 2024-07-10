package dev.ioexception.crawling.repository;

import dev.ioexception.crawling.entity.LectureTag;
import dev.ioexception.crawling.repository.query.LectureTagRepositoryCustom;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureTagRepository extends JpaRepository<LectureTag, Long>, LectureTagRepositoryCustom {
    //    List<LectureTag> findAllByLecture_LectureId(String lectureId);
    @EntityGraph(attributePaths = {"tag", "lecture"})
    @Query("SELECT lt FROM LectureTag lt WHERE lt.lecture.lectureId = :lectureId AND lt.lecture.date = :localDate")
    List<LectureTag> getLectureTagsWithTagByLectureId(@Param("lectureId") String lectureId,
                                                      @Param("localDate") LocalDate localDate);

}

