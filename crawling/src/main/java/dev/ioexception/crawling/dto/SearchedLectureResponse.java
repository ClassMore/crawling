package dev.ioexception.crawling.dto;

import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.entity.LectureDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchedLectureResponse {
    String title;
    String instructor;
    String companyName;

    public SearchedLectureResponse toDto(Lecture lecture) {
        return new SearchedLectureResponse(lecture.getTitle(), lecture.getInstructor(), lecture.getCompanyName());
    }

    public SearchedLectureResponse toDto(LectureDocument lecture) {
        return new SearchedLectureResponse(lecture.getTitle(), lecture.getInstructor(), lecture.getCompanyName());
    }
}
