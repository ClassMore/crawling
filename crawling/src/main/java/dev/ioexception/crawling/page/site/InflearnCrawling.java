package dev.ioexception.crawling.page.site;

import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.entity.LectureTag;
import dev.ioexception.crawling.entity.Tag;
import dev.ioexception.crawling.page.UploadImage;
import dev.ioexception.crawling.repository.LectureRepository;
import dev.ioexception.crawling.repository.LectureTagRepository;
import dev.ioexception.crawling.repository.TagRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InflearnCrawling {
    private final LectureTagRepository lectureTagRepository;
    private final LectureRepository lectureRepository;
    private final TagRepository tagRepository;
    private final UploadImage uploadImage;

    private static final String SITE_NAME = "inflearn";
    private static final Pattern PRICE_PATTERN = Pattern.compile("[,\\₩]");

    @Transactional
    public void getLecture() {
        Document document = handleIOException(
                () -> Jsoup.connect("https://www.inflearn.com/courses?types=ONLINE").get()
        );

        if (document == null) {
            return;
        }

//        Element page = document.select("div.mantine-ktc6ma div.mantine-o8b522 button.mantine-z1qa8g button:last-child").first();
//        System.out.println(page);
        int lastPage = 1;
        ConcurrentLinkedQueue<Lecture> lectures = new ConcurrentLinkedQueue<>();


        ExecutorService executorService = Executors.newFixedThreadPool(1);

        for (int currentPage = 1; currentPage <= lastPage; currentPage++) {
            final int pageToCrawl = currentPage;

            int finalCurrentPage = currentPage;
            executorService.submit(() -> {
                ArrayList<Lecture> lectureArrayList = crawlPage(pageToCrawl);

                if (lectureArrayList != null) {
                    lectures.addAll(lectureArrayList);
                } else {
                    log.error("{} page fail", finalCurrentPage);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        lectureRepository.saveAll(lectures);

        try {
            if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    public ArrayList<Lecture> crawlPage(int page) {
        ArrayList<Lecture> lectureArrayList = new ArrayList<>();
        Document documentPage = handleIOException(
                () -> Jsoup.connect("https://www.inflearn.com/courses?types=ONLINE&page_number=" + page).get()
        );

        log.info("Crawling inflearn page: " + page);

        if (documentPage == null) {
            return null;
        }

        Elements contents = documentPage.select("ul.mantine-1avyp1d li.mantine-1avyp1d");
        System.out.println(contents);

        for (Element content : contents) {
            Lecture lecture = saveLecture(content);
            System.out.println(lecture);

            if (lecture != null) {
                lectureArrayList.add(saveLecture(content));
            }
        }

        return lectureArrayList;
    }

    public Lecture saveLecture(Element content) {
        String lectureId = getCourseNumber(content);
//        String imageLink = handleIOException(
//                () -> uploadImage.uploadFromUrlToS3(getImage(content), SITE_NAME, lectureId));

        if (getOriginPrice(content) == -2) {
            return null;
        }

        log.info("lecture title: {}", getTitle(content));

        Lecture lecture = Lecture.builder()
                .lectureId(lectureId)
                .imageLink("image link")
                .salePercent(getSalePercent(content))
                .title(getTitle(content))
                .siteLink(getUrl(content))
                .instructor(getInstructor(content))
                .ordinaryPrice(getOriginPrice(content))
                .salePrice(getBeforeDiscount(content))
                .companyName(SITE_NAME)
                .date(LocalDate.now())
                .build();

        StringTokenizer tags = new StringTokenizer(getTag(content).replaceAll("·", ","), ",");

        while (tags.hasMoreTokens()) {
            String tag = tags.nextToken().trim();
            Tag tagId = existTag(tag);

            try {
                saveLecTag(lecture, tagId);
            } catch (Exception e) {
                log.warn("Error", e);
                throw e;
            }
        }

        return lecture;
    }

    public Tag existTag(String tag) {
        return tagRepository.findByName(tag)
                .orElseGet(() -> tagRepository.save(Tag.builder().name(tag).build()));
    }

    public void saveLecTag(Lecture lecture, Tag tag) {
        LectureTag lectureTag = new LectureTag();
        lectureTag.setLecture(lecture);
        lectureTag.setTag(tag);

        lectureTagRepository.save(lectureTag);
    }

    private String getCourseNumber(Element content) {
        log.info("content: {}", content.text());
        return SITE_NAME + content.select("div.mantine-1w8yksd img").attr("abs:src").split("/")[4];
    }

    private String getImage(Element content) {

        return content.select("article.mantine-n8y7xk div.mantine-1w8yksd img").attr("abs:src");
    }

    private String getSalePercent(Element content) {
        return content.select("div.mantine-1n7ftt8 p.mantine-1eaww3o").text();
    }

    private String getTitle(Element content) {
        return content.select("article.mantine-n8y7xk div.mantine-5t8g7z div.mantine-5n4x4z p.mantine-169r75g").text();
    }

    private String getUrl(Element content) {
        return content.select("a").attr("abs:href");
    }

    private String getInstructor(Element content) {
        return content.select("article.mantine-n8y7xk div.mantine-5t8g7z div.mantine-5n4x4z p.mantine-17j39m6").text();
    }

    private int getOriginPrice(Element content) {
        String price = PRICE_PATTERN.matcher(content.select("div.mantine-1n7ftt8 p.mantine-nu4660").text()).replaceAll("").trim();
        
        if (price.equals("무료")) {
            return 0;
        }
        
        if (price.equals("미설정")) {
            return -2;
        }
        
        return Integer.parseInt(price);
    }

    private int getBeforeDiscount(Element content) {
        String salePrice = content.select("div.mantine-1n7ftt8 p.mantine-1jss0zj").text();
        if (salePrice.isBlank()) {

            return getOriginPrice(content);
        }

        return Integer.parseInt(PRICE_PATTERN.matcher(salePrice).replaceAll("").trim());
    }


    private String getTag(Element content) {
        return content.select("div.mantine-1d86z2b div.mantine-1i71bpy p.mantine-10boh4h").text();
    }

    private <T> T handleIOException(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            log.error("Exception occurred", e);

            return null;
        }
    }
}
