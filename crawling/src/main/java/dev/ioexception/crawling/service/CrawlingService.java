package dev.ioexception.crawling.service;

import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.page.site.GoormCrawling;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrawlingService {
    private final GoormCrawling goormCrawling;

    public List<Lecture> getGr() throws IOException {

        return goormCrawling.getSaleLecture();
    }
}
