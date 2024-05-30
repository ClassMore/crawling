package dev.ioexception.crawling.page.site;

import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.entity.LectureTag;
import dev.ioexception.crawling.entity.Tag;
import dev.ioexception.crawling.page.UploadImage;
import dev.ioexception.crawling.repository.LectureRepository;
import dev.ioexception.crawling.repository.LectureTagRepository;
import dev.ioexception.crawling.repository.TagRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GoormCrawling {
    private final LectureRepository lectureRepository;
    private final TagRepository tagRepository;
    private final LectureTagRepository lectureTagRepository;
    private final UploadImage uploadImage;


    public void getSaleLecture() throws IOException {
        Document document = Jsoup.connect("https://edu.goorm.io/category/programming").get();

        for(int j = 2; j <= 17; j++) {
            String tagUrl = document.select("#app > section > div > div._1HxzYw > div > div > div > div > div > div.collapse.show > a:nth-child(" + j + ")").attr("href");
            String tagName = document.select("#app > section > div > div._1HxzYw > div > div > div > div > div > div.collapse.show > a:nth-child(" + j + ") > div._3WcH5Y").text();
            String lectureCount = document.select("#app > section > div > div._1HxzYw > div > div > div > div > div > div.collapse.show > a:nth-child(" + j + ") > div:nth-child(2)").text();
            int lastPage = (int) (Math.ceil(Double.parseDouble(lectureCount) / 20));

            // 태그 저장
            Tag tag = tagRepository.findByName(tagName)
                .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));


            for (int i = 1; i <= lastPage; i++) {
                document = Jsoup.connect("https://edu.goorm.io" + tagUrl + "?page=" + i + "&sort=accuracy").get();
                Elements contents = document.select("#app > section > div > div.ebXc38 > div > div > div.override-v4 > div > div > div > div._2hZilU");

                for (Element content : contents) {
                    Lecture lecture = Lecture.builder()
                        .lectureId(getId(content))
                        .imageLink(getImage(content))
                        .salePercent(getSalePercent(content))
                        .title(getTitle(content))
                        .companyName("goorm")
                        .siteLink(getUrl(content))
                        .instructor(getInstructor(content))
                        .ordinaryPrice(getPrice(content))
                        .salePrice(getSalePrice(content))
                        .date(LocalDate.now())
                        .build();

                    lectureRepository.save(lecture);

                    // 강의 태그 중간 테이블 저장
                    LectureTag lectureTag = new LectureTag();
                    lectureTag.setTag(tag);
                    lectureTag.setLecture(lecture);

                    lectureTagRepository.save(lectureTag);
                }

                log.info("Crawling goorm page: " + i);
            }
        }
    }

    private String getId(Element content) {
        String url = content.select("a._1xnzzp._1MfH_h").attr("href");
        String[] parts = url.split("/");

        return "goorm"+parts[2];
    }

    private String getImage(Element content) throws IOException {
        String imageUrl = content.select("div._31ylS5 > img").attr("data-src");

        return uploadImage.uploadFromUrlToS3(imageUrl, "goorm", getId(content));
    }

    private String getSalePercent(Element content) throws IOException {
        String salePercent = "";
        Elements tagAs = content.select("div._3kC1O1");

        for (Element tagA : tagAs) {
            int size = tagA.childNodeSize();
            if (size == 1) {
                salePercent = "-1";
                break;
            } else {
                salePercent = tagA.select("span.c8BRmM span").text();
                break;
            }
        }

        return salePercent;
    }

    private int getPrice(Element content) throws IOException {
        int originPrice = 0;
        Elements tagAs = content.select("div._3kC1O1");

        for (Element tagA : tagAs) {
            int size = tagA.childNodeSize();
            if (size == 1) {
                if(tagA.select("span._1zPZlD").text().equals("무료")) {
                    originPrice = 0;
                    break;
                } else {
                    originPrice = Integer.parseInt(tagA.select("span._1zPZlD").text().replaceAll("[^0-9]",""));
                    break;
                }
            } else {
                originPrice = Integer.parseInt(tagA.select("span._1TRM7z").text().replaceAll("[^0-9]",""));
                break;
            }
        }

        return originPrice;
    }

    private int getSalePrice(Element content) throws IOException {
        int salePrice = 0;
        Elements tagAs = content.select("div._3kC1O1");

        for (Element tagA : tagAs) {
            if(tagA.select("span._1zPZlD").text().equals("무료")) {
                salePrice = 0;
                break;
            } else {
                salePrice = Integer.parseInt(tagA.select("span._1zPZlD").text().replaceAll("[^0-9]",""));
                break;
            }
        }

        return salePrice;
    }

    private String getTitle(Element content) { return content.select("div._3pJh0l.mt-2.card-body").text(); }

    private String getUrl(Element content) { return content.select("a._1xnzzp").attr("abs:href"); }

    private String getInstructor(Element content) { return content.select("div._3WBYZo > a._2q_4L7").text(); }
}