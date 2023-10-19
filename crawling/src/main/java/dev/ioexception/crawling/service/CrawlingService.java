package dev.ioexception.crawling.service;

import dev.ioexception.crawling.page.site.ArtandStudyCrawling;
import dev.ioexception.crawling.page.site.GoormCrawling;
import dev.ioexception.crawling.page.site.InflearnCrawling;
import dev.ioexception.crawling.page.site.MegaCrawling;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CrawlingService {
    private final MegaCrawling megaCrawling;
    private final GoormCrawling goormCrawling;
    private final ArtandStudyCrawling artandStudyCrawling;
    private final InflearnCrawling inflearnCrawling;

    public void getMega() throws IOException {
        megaCrawling.getSaleLecture();
    }


    public void getArtandStudy() throws IOException, InterruptedException {

        artandStudyCrawling.getSaleLecture();
    }

    public void getGoorm() throws IOException {

        goormCrawling.getSaleLecture();
    }

  
    public void getInflearn() {
        inflearnCrawling.getLecture();
    }
}
