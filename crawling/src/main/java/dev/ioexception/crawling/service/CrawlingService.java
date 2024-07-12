package dev.ioexception.crawling.service;

import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.page.site.ArtandStudyCrawling;
import dev.ioexception.crawling.page.site.ClassUCrawling;
import dev.ioexception.crawling.page.site.GoormCrawling;
import dev.ioexception.crawling.page.site.InflearnCrawling;
import dev.ioexception.crawling.page.site.MegaCrawling;
import dev.ioexception.crawling.page.site.YbmCrawling;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CrawlingService {
    private final MegaCrawling megaCrawling;
    private final GoormCrawling goormCrawling;
    private final YbmCrawling ybmCrawling;
    private final ArtandStudyCrawling artandStudyCrawling;
    private final ClassUCrawling classUCrawling;
    private final InflearnCrawling inflearnCrawling;

    public void getMega() throws IOException {
        megaCrawling.getSaleLecture();
    }

    public void getYbm() throws IOException, InterruptedException {

        ybmCrawling.getSaleLecture();
    }

    public void getArtandStudy() throws IOException, InterruptedException {

        artandStudyCrawling.getSaleLecture();
    }

    public void getGoorm() throws IOException {

        goormCrawling.getSaleLecture();
    }

    public void getClassu() throws IOException {
        classUCrawling.process();
    }
  
    public void getInflearn() {
        inflearnCrawling.getLecture();
    }
}
