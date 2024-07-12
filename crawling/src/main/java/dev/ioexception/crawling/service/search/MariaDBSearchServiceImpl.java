package dev.ioexception.crawling.service.search;

import dev.ioexception.crawling.dto.SearchedLectureResponse;
import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.repository.LectureRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MariaDBSearchServiceImpl implements SearchService{
    private final LectureRepository lectureRepository;

    @Override
    public List<SearchedLectureResponse> search(String q) {
        List<Lecture> lectures = lectureRepository.findAllByTitleContainingAndDate(q, LocalDate.of(2024, 5, 15));

        if (lectures.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return lectures.stream()
                .map(lecture -> new SearchedLectureResponse().toDto(lecture))
                .collect(Collectors.toList());
    }
}
