package dev.ioexception.crawling.entity;

import static jakarta.persistence.FetchType.EAGER;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class LectureTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectagId;

    @ManyToOne
    @JoinColumn(name = "lecture_id", referencedColumnName = "lecture_id")
    private Lecture lecture;

    @ManyToOne
    @JoinColumn(name = "tagId")
    private Tag tag;

    public void setLecture(Lecture lecture){
        this.lecture = lecture;
        lecture.getLectureTags().add(this);
    }

    public void setTag(Tag tag){
        this.tag = tag;
        tag.getLectureTags().add(this);
    }

    public Long getTagId(Tag tag){
        this.tag = tag;
        return tag.getTagId();
    }
}