package dev.ioexception.crawling.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import dev.ioexception.crawling.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
	Optional<Lecture> findLectureByLectureId(String lectureId);
	Optional<Lecture> findByLectureIdAndDate(String lectureId, LocalDate now);
	List<Lecture> findAllByTitleContainingAndDate(String title, LocalDate localDate);

	List<Lecture> findAllByDate(LocalDate localDate);
}
