// package dev.ioexception.crawling.service;
//
// import java.util.List;
//
// import org.springframework.stereotype.Service;
//
// import dev.ioexception.crawling.entity.LectureIndex;
// import dev.ioexception.crawling.repository.LectureIndexRepository;
// import lombok.RequiredArgsConstructor;
//
// @Service
// @RequiredArgsConstructor
// public class SearchLecture {
// 	private final LectureIndexRepository lectureIndexRepository;
//
// 	public List<LectureIndex> findByTitle(String title){
// 		return lectureIndexRepository.findByTitle(title);
// 	}
// 	public List<LectureIndex> findByInstructor(String instructor){
// 		return lectureIndexRepository.findByInstructor(instructor);
// 	}
// 	public List<LectureIndex> findByCompanyName(String companyname){
// 		return lectureIndexRepository.findByCompanyName(companyname);
// 	}
// }
