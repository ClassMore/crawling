package dev.ioexception.crawling.page.site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.repository.LectureRepository;

@Component
public class GoormCrawling {
    private final LectureRepository lectureRepository;

    public GoormCrawling(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    public List<Lecture> getSaleLecture() throws IOException {
        List<Lecture> lectureList = new ArrayList<>();
        Document document = Jsoup.connect("https://edu.goorm.io/category/programming?page=1&sort=relevant").get();
        Elements page = document.select("a.YjsAK2 div:last-child");

        int lastPage = (int) (Math.ceil(Double.parseDouble(page.select("div").text()) / 20));

        for (int i = 1; i <= lastPage; i++) {
            document = Jsoup.connect("https://edu.goorm.io/category/programming?page=" + i + "&sort=relevant").get();
            Elements contents = document.select(
                    "#app section.TkhYW1 div._3B-tMe div.ebXc38 div.container-lg div.override-v4 div.lbawCm div.Aj2j_L");

            List<String> originPrice = new ArrayList<>();
            List<String> salePrice = new ArrayList<>();
            List<String> salePercent = new ArrayList<>();

            Elements tagAs = contents.select("div._3kC1O1");

            for (Element tagA : tagAs) {
                int size = tagA.childNodeSize();
                if (size == 1) {
                    originPrice.add(tagA.select("span._1zPZlD").text());
                    salePercent.add("-1");
                    salePrice.add("-1");
                } else {
                    originPrice.add(tagA.select("span._1TRM7z").text());
                    salePrice.add(tagA.select("span._1zPZlD").text());
                    salePercent.add(tagA.select("span.c8BRmM span").text());
                }
            }

            int idx = 0;
            for (Element content : contents) {
                for (int j = 0; j < getInstructors(content).size(); j++) {
                    Lecture lecture = Lecture.builder()
                            .id(getId(document).get(j))
                            .image(getImages(content).get(j))
                            .salePercent(salePercent.get(idx))
                            .title(getTitles(content).get(j))
                            .url(getUrls(content).get(j))
                            .instructor(getInstructors(content).get(j))
                            .price(originPrice.get(idx))
                            .salePrice(salePrice.get(idx))
                            .build();
                    idx++;
                    lectureList.add(lecture);
                    System.out.println(i);
                    System.out.println(lecture.getTitle());
                    //lectureRepository.save(lecture);
                }
            }
        }

        return lectureList;
    }

    private List<String> getId(Element content) {
        Elements elements = content.select("#app > section > div > div.ebXc38 > div:nth-child(1) > div > div.override-v4 > div > div > div > div > a");

        List<String> ids = new ArrayList<>();
        for(Element element : elements){
            String url = element.attr("href");
            String[] parts = url.split("/");
            String id = parts[2];
            ids.add(id);
        }
        return ids;
    }

    private List<String> getImages(Element content) {
        Elements imageElements = content.select("div._31ylS5 img");
        List<String> images = new ArrayList<>();

        for (Element element : imageElements) {
            String imageUrl = element.attr("data-src");
            images.add(imageUrl);
        }
        return images;
    }

    private List<String> getSalePercents(Element content) {
        return content.select("div._3kC1O1 span.c8BRmM").eachText();
    }

    private List<String> getTitles(Element content) {
        return content.select("div._3pJh0l div._14ksIk").eachText();

    }

    private List<String> getUrls(Element content) {

        Elements urlElements = content.select("a._1xnzzp");
        List<String> urls = new ArrayList<>();

        for (Element element : urlElements) {
            String imageUrl = element.attr("abs:href");
            urls.add(imageUrl);
        }
        return urls;
    }

    private List<String> getInstructors(Element content) {
        return content.select("div._3WBYZo a._2q_4L7").eachText();
    }

    private List<String> getPrices(Element content) { //3)할인 할 경우 : 원가
        return content.select("div._3kC1O1 span._1TRM7z").eachText();
    }

    private List<String> getSalePrices(Element content) { //1)무료 2)할인 안할 경우 : 원가 3)할인 할 경우 : 할인가
        return content.select("div._3kC1O1 span._1zPZlD").eachText();
    }
}