package dev.ioexception.crawling.page.site;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dev.ioexception.crawling.entity.LectureTag;
import dev.ioexception.crawling.entity.Tag;
import dev.ioexception.crawling.page.UploadImage;
import dev.ioexception.crawling.repository.LectureTagRepository;
import dev.ioexception.crawling.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.repository.LectureRepository;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MegaCrawling {
    private final LectureRepository lectureRepository;
    private final TagRepository tagRepository;
    private final LectureTagRepository lectureTagRepository;
    private final UploadImage uploadImage;

    public void getSaleLecture() throws IOException {
        Document document = Jsoup.connect("https://megastudyacademy.co.kr/lecture_list").get();

        String page = document.select("body > div.lecture > div > div.lecture_listView > div.paging > ul > li > a.last").attr("href").replaceAll("[^0-9]","");
        int lastPage = Integer.parseInt(page);

        for (int i = 1; i <= lastPage; i++) {
            document = Jsoup.connect("https://megastudyacademy.co.kr/lecture_list?&page=" + i).get();
            Elements contents = document.select("body > div.lecture > div > div.lecture_listView > ul > li");

            for (Element content : contents) {

                Lecture lecture = Lecture.builder()
                    .lectureId(getId(content))
                    .imageLink(getImage(content))
                    .salePercent(getSalePercent(content))
                    .title(getTitle(content))
                    .companyName("mega")
                    .siteLink(getUrl(content))
                    .instructor(getInstructor(content))
                    .ordinaryPrice(getPrice(content))
                    .salePrice(getSalePrice(content))
                    .date(LocalDate.now())
                    .build();
                lectureRepository.save(lecture);


                // 태그 저장
                Tag tag = tagRepository.findByName(getTagName(content))
                    .orElseGet(() -> tagRepository.save(Tag.builder().name(getTagName(content)).build()));

                // 강의 태그 중간 테이블 저장
                LectureTag lectureTag = new LectureTag();
                lectureTag.setLecture(lecture);
                lectureTag.setTag(tag);
                lectureTagRepository.save(lectureTag);
            }

            log.info("Crawling mega page: " + i);
        }
    }

    private String getId(Element content) {
        String url = content.select("a:nth-child(1)").attr("href");
        String[] parts = url.split("/");
        return "mega"+parts[2];
    }

    private String getImage(Element content) throws IOException {
        String imageUrl = content.select("a > div:nth-child(1) > img").attr("src");
        String image = uploadImage.uploadFromUrlToS3(imageUrl, "mega", getId(content));

        return image;
    }

    private String getSalePercent(Element content) throws IOException {
        String salePercent ="";
        Elements tagAs = content.select("a > div.lc_price");

        for (Element tagA : tagAs) {
            int size = tagA.childNodeSize();
            if (size == 3) {
                salePercent = "-1";
                break;
            } else {
                salePercent = tagA.select("h2 span").text();
                break;
            }
        }

        return salePercent;
    }

    private int getPrice(Element content) throws IOException {
        int originPrice = 0;
        Elements tagAs = content.select("a > div.lc_price");

        for (Element tagA : tagAs) {
            int size = tagA.childNodeSize();

            if (size == 3) {
                if(tagA.select("h2").text().equals("무료강좌")) {
                    originPrice = 0;
                    break;
                } else {
                    originPrice = Integer.parseInt(tagA.select("h2").text().replaceAll("[^0-9]",""));
                    break;
                }
            } else {
                originPrice = Integer.parseInt(tagA.select("h4").text().replaceAll("[^0-9]",""));
                break;
            }
        }

        return originPrice;
    }

    private int getSalePrice(Element content) throws IOException {
        String salePrice = ""; int saleprice = 0;
        Elements tagAs = content.select("a > div.lc_price");

        for (Element tagA : tagAs) {
            salePrice = tagA.select("h2").text();
            if(salePrice.contains("%")) {
                saleprice = Integer.parseInt((salePrice.substring(salePrice.indexOf("%")+2)).replaceAll("[^0-9]",""));
            } else if(salePrice.equals("무료강좌")) {
                saleprice = 0;
            } else {
                saleprice = Integer.parseInt(salePrice.replaceAll("[^0-9]",""));
            }
        }

        return saleprice;
    }

    private String getTagName(Element content) { return content.select("a > h5").text(); }

    private String getTitle(Element content) { return content.select("a > p").text(); }

    private String getUrl(Element content) { return content.select("a:nth-child(1)").attr("abs:href"); }

    private String getInstructor(Element content) { return content.select("a > h3").text(); }
}