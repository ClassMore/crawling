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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
        List<Lecture> lectures = lectureRepository.findAllByDate(LocalDate.of(2024, 5, 15));
        List<LectureDocument> documents = new ArrayList<>();
        log.info("size : {}", lectures.size());

        for (Lecture lecture : lectures) {

            LectureDocument lectureDocument = LectureDocument.builder()
                    .id(lecture.getLectureId())
                    .title(lecture.getTitle())
                    .instructor(lecture.getInstructor())
                    .companyName(lecture.getCompanyName())
                    .ordinaryPrice(lecture.getOrdinaryPrice())
                    .salePrice(lecture.getSalePrice())
                    .salePercent(lecture.getSalePercent())
                    .siteLink(lecture.getSiteLink())
                    .imageLink(lecture.getImageLink())
                    .tag(getTagNamesByLectureId(lecture.getLectureId()))
                    .build();

            documents.add(lectureDocument);
        }

        elasticsearchLectureRepository.saveAll(documents);
    }

    public List<String> getTagNamesByLectureId(String lectureId) {
        List<LectureTag> lectureTags = lectureTagRepository.getLectureTagsWithTagByLectureId(lectureId, LocalDate.of(2024,5,15));

        return lectureTags.stream()
                .map(lt -> lt.getTag().getName())
                .collect(Collectors.toList());
    }
}
