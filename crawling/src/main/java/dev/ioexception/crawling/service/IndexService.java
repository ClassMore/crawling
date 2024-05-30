package dev.ioexception.crawling.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.entity.LectureDocument;
import dev.ioexception.crawling.entity.LectureTag;
import dev.ioexception.crawling.repository.ElasticsearchLectureRepository;
import dev.ioexception.crawling.repository.LectureRepository;
import dev.ioexception.crawling.repository.LectureTagRepository;
import dev.ioexception.crawling.repository.TagRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestClient;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class IndexService {
    private final LectureRepository lectureRepository;
    private final TagRepository tagRepository;
    private final LectureTagRepository lectureTagRepository;
    private final ElasticsearchOperations operations;
    private final ElasticsearchLectureRepository elasticsearchLectureRepository;
    private final ElasticsearchClient elasticsearchClient;
    private final RestClient restClient;

    public void inputIndex() throws IOException {
        List<Lecture> lectures = lectureRepository.findAllByDate(LocalDate.now());

        for (Lecture lecture : lectures) {
            if (lecture.getImageLink().equals("no image")) {
                continue;
            }

            List<LectureTag> lectureTags = lectureTagRepository.getLectureTags(lecture.getLectureId());

            LectureTag lectureTag = lectureTags.get(0);
            Long tagId = lectureTag.getTagId(lectureTag.getTag());
            String tag = tagRepository.findById(tagId).orElseThrow().getName();

            LectureDocument lectureDocument = LectureDocument.builder()
                    .id(lecture.getId())
                    .title(lecture.getTitle())
                    .instructor(lecture.getInstructor())
                    .companyName(lecture.getCompanyName())
                    .ordinaryPrice(lecture.getOrdinaryPrice())
                    .salePrice(lecture.getSalePrice())
                    .salePercent(lecture.getSalePercent())
                    .siteLink(lecture.getSiteLink())
                    .imageLink(lecture.getImageLink())
                    .tag(tag)
                    .build();

            elasticsearchLectureRepository.save(lectureDocument);
        }
    }
}
