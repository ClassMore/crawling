package dev.ioexception.crawling.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dev.ioexception.crawling.entity.LectureIndex;
import dev.ioexception.crawling.repository.LectureIndexRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class LectureSearchController {
	private final LectureIndexRepository lectureIndexRepository;

	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<LectureIndex> search(
		@RequestParam(value = "title", required = false, defaultValue = "") String title,
		@RequestParam(value = "instructor", required = false, defaultValue = "0.0") String instructor) {
		return lectureIndexRepository.findByTitleAndInstructor(title, instructor);
	}
}
