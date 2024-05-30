package dev.ioexception.crawling.repository.query;

import dev.ioexception.crawling.entity.LectureTag;
import dev.ioexception.crawling.entity.QLecture;
import dev.ioexception.crawling.entity.QLectureTag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class LectureTagRepositoryCustomImpl extends QuerydslRepositorySupport implements LectureTagRepositoryCustom {

    public LectureTagRepositoryCustomImpl() {
        super(LectureTag.class);
    }

    public List<LectureTag> getLectureTags(String lectureId) {
        QLecture lecture = QLecture.lecture;
        QLectureTag lectureTag = QLectureTag.lectureTag;

        return from(lectureTag)
                .leftJoin(lectureTag.lecture, lecture)
                .fetchJoin()
                .where(lectureTag.lecture.lectureId.eq(lectureId)
                        .and(lecture.date.eq(LocalDate.now())))
                .fetch();
    }
}
