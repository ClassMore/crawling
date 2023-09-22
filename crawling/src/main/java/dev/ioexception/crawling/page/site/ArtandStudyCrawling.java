package dev.ioexception.crawling.page.site;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.entity.LectureIndex;
import dev.ioexception.crawling.entity.LectureTag;
import dev.ioexception.crawling.entity.Tag;
import dev.ioexception.crawling.page.UploadImage;
import dev.ioexception.crawling.repository.LectureIndexRepository;
import dev.ioexception.crawling.repository.LectureRepository;
import dev.ioexception.crawling.repository.LectureTagRepository;
import dev.ioexception.crawling.repository.TagRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class ArtandStudyCrawling {

    private final LectureRepository lectureRepository;
    private final LectureTagRepository lectureTagRepository;
    private final TagRepository tagRepository;
    private final UploadImage uploadImage;
    private final LectureIndexRepository lectureIndexRepository;

    public void getSaleLecture() throws IOException {
        String[][] sub_category = {{},
                {"", "철학입문", "서양고대철학", "근대현대철학", "동양철학", "정신분석/심리학", "윤리학", "정치철학"},
                {"", "시", "소설", "문학일반", "문예창작"},
                {"", "미술", "미학", "문화", "음악", "영화", "사진", "건축"},
                {"", "고전", "역사", "과학", "종교/신화", "사회/경제", "교양인문학"}
        };

        // 강의 카테고리 생성
        for (int i = 1; i <= 4; i++) {
            Document document = Jsoup.connect("https://www.artnstudy.com/n_lecture/LecList.asp?LessonPart=" + i + "00")
                    .get();
            // 각 카테고리 별 세부 카테고리
            int categorylen = document.select("#lnb > ul > a").size();

            for (int j = 1; j <= categorylen; j++) {
                document = Jsoup.connect("https://www.artnstudy.com/n_lecture/LecList.asp?LessonPart=" + i + "0" + j)
                        .get();

                // 태그를 넣어준다.
                // 먼저 태그가 이미 존재하는지 확인하고, 존재한다면 해당 태그를 가져옴
                String sub_cat_name = sub_category[i][j];
                Tag tag = tagRepository.findByName(sub_category[i][j])
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(sub_cat_name).build()));

                Elements contents = document.select(
                        "#content > div.wrap > div > div > ul > li:nth-child(4) > ul > a");

                saveLecture(tag, contents);
            }
        }
    }

    private void saveLecture(Tag tag, Elements contents) throws IOException {
        for (Element content : contents) {
            Lecture lecture = Lecture.builder()
                    .lectureId(getLectureId(content))
                    .title(getTitle(content))
                    .instructor(getInstructor(content))
                    .companyName("ArtandStudy")
                    .ordinaryPrice(getPrice(content))
                    .salePrice(getSalePrice(content))
                    .salePercent(getSalePercent(content))
                    .siteLink(getUrl(content))
                    .imageLink(getImage(content))
                    .date(LocalDate.now())
                    .build();

            LectureIndex lectureIndex = LectureIndex.builder()
                    .lectureId(getLectureId(content))
                    .title(getTitle(content))
                    .instructor(getInstructor(content))
                    .companyName("ArtandStudy")
                    .ordinaryPrice(getPrice(content))
                    .salePrice(getSalePrice(content))
                    .salePercent(getSalePercent(content))
                    .imageLink(getImage(content))
                    .build();

            lectureIndexRepository.save(lectureIndex);
            lectureRepository.save(lecture);

            // 강의 태그 중간테이블을 저장한다.
            LectureTag lectureTag = new LectureTag();
            lectureTag.setTag(tag);
            lectureTag.setLecture(lecture);
            lectureTagRepository.save(lectureTag);
        }
    }

    private String getLectureId(Element content) {
        String classUrl = content.attr("href");

        // 정규 표현식 패턴
        String regex = "LessonIdx=([^&]+)";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(classUrl);

        String lessonIdx = "";
        if (matcher.find()) {
            lessonIdx = matcher.group(1);
        }
        return "artstudy" + lessonIdx;

    }

    private int getSalePrice(Element content) {
        Elements elements = content.select("li > ul > li:nth-child(2) > ul > li.viewsale > span");

        if (elements.isEmpty() || elements.text().contains("강좌추가")) {
            return getPrice(content);
        }

        return Integer.parseInt(elements.text().replaceAll("[^0-9]", ""));
    }

    private String getImage(Element content) throws IOException {
        // 이미지를 가져온다.
        Element imgElement = content.selectFirst("li > ul > li:nth-child(1) > div");
        // Style 속성값을 가져온다.
        String styleAttribute = imgElement.attr("style");
        // style 속성에서 URL 부분을 추출합니다.
        String imageUrl = styleAttribute.replaceAll(".*?url\\(([^)]+)\\).*", "$1");
        imageUrl = imageUrl.replaceAll("'", "");

        // 마지막 강의의 이미지가 없어서 정적으로 넣어줌.
        if (imageUrl.equals("/image/photo_b/sub/njPark003_2.jpg")) {
            imageUrl = "/image/photo_b/sub/njPark006_2.jpg";
        }
        imageUrl = "https://www.artnstudy.com" + imageUrl;

        return uploadImage.uploadFromUrlToS3(imageUrl, "artandstudy", getLectureId(content));
    }

    private String getUrl(Element content) {
        // a태그의 href 속성을 가져온다.
        String classUrl = content.attr("href");
        return "https://www.artnstudy.com" + classUrl;
    }

    private int getPrice(Element content) {
        String price = content.select("li > ul > li:last-child > ul > li:nth-child(2) > span").text();
        // if(price.equals("0원")){
        // 	price = "무료";
        // }
        String numericPart = price.replaceAll("[^0-9]", "");

        return Integer.parseInt(numericPart);
    }

    private String getTitle(Element content) {

        return content.select("li > ul > li:last-child > ul > li:nth-child(1) > ul > li").text();
    }

    private String getInstructor(Element content) {
        String instructor = content.select("li > ul > li:last-child > ul > li:nth-child(2)").text();
        instructor = instructor.substring(0, 3); // inline 으로 바꾸는게 좋지않을까요

        return instructor;
    }

    private String getSalePercent(Element content) {
        int price = getPrice(content);
        int salePrice = getSalePrice(content);

        if (salePrice == price) {
            return "-1";
        }
        double discountPercentage = ((double) (price - salePrice) / price) * 100.0;
        int salePercent = (int) Math.round(discountPercentage);

        return salePercent + "%";
    }
}
