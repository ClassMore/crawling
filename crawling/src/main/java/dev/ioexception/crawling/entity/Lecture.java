package dev.ioexception.crawling.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
@Builder
public class Lecture {

    @Id
    private String id;
    private String image;
    private String salePercent;
    private String title;
    private String url;
    private String instructor;
    private String price;
    private String salePrice;

    public Lecture() {

    }

    public Lecture(String id, String image, String salePercent, String title, String url, String instructor, String price,
                   String salePrice) {
        this.id = id;
        this.image = image;
        this.salePercent = salePercent;
        this.title = title;
        this.url = url;
        this.instructor = instructor;
        this.price = price;
        this.salePrice = salePrice;
    }
}
