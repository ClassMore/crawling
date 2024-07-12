package dev.ioexception.crawling.entity;

import jakarta.persistence.Id;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Builder
@Setting(replicas = 0)
@Document(indexName = "lecture")
@AllArgsConstructor
@NoArgsConstructor
public class LectureDocument {
    @Id
    private String id;
    private String title;
    private String instructor;
    private String companyName;
    private int ordinaryPrice;
    private int salePrice;
    private String salePercent;
    private String siteLink;
    private String imageLink;
    private List<String> tag;
}
