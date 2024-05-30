package dev.ioexception.crawling.entity;

import jakarta.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Tag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    private String name;

    // orphanRemoval = true는 Tag 엔티티에서 삭제된 LectureTag 엔티티도 실제로 데이터베이스에서 삭제되도록 설정합니다.
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureTag> lectureTags = new ArrayList<>();

    @Builder
    public Tag(String name) {
        this.name = name;
    }
}