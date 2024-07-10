package dev.ioexception.crawling.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lecture_id")
    private String lectureId;
    private String title;
    private String instructor;
    private String companyName;
    private int ordinaryPrice;
    private int salePrice;
    private String salePercent;
    private String imageLink;
    private String siteLink;
    private LocalDate date;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureTag> lectureTags = new ArrayList<>();

    @Builder
    public Lecture(String lectureId, String title, String instructor, String companyName, int ordinaryPrice, int salePrice, String salePercent, String imageLink, String siteLink, LocalDate date) {
        this.lectureId = lectureId;
        this.title = title;
        this.instructor = instructor;
        this.companyName = companyName;
        this.ordinaryPrice = ordinaryPrice;
        this.salePrice = salePrice;
        this.salePercent = salePercent;
        this.imageLink = imageLink;
        this.siteLink = siteLink;
        this.date = date;
    }
}