package dev.ioexception.crawling.controller;

import dev.ioexception.crawling.service.CrawlingService;
import dev.ioexception.crawling.service.IndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class CrawlingController {
    private final CrawlingService crawlingService;
    private final IndexService indexService;

    @GetMapping("/crawling")
    public void crawling() throws IOException, InterruptedException {
//        crawlingService.getMega();
//        crawlingService.getGoorm();
//        crawlingService.getArtandStudy();
        crawlingService.getInflearn();
//        indexService.inputIndex();
        // crawlingService.getClassu();
        // crawlingService.getYbm();
    }
}

