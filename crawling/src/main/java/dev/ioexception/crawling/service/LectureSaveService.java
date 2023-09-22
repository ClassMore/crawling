package dev.ioexception.crawling.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.ioexception.crawling.entity.LectureIndex;
import dev.ioexception.crawling.repository.LectureIndexRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureSaveService {
	private final LectureIndexRepository lectureIndexRepository;

	public void saveLectureBulk(List<LectureIndex> lectureIndexs) {
		lectureIndexRepository.saveAll(lectureIndexs);
	}
	public void saveLecture(LectureIndex lectureIndex) {
		lectureIndexRepository.save(lectureIndex);
	}
}
