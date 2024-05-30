package dev.ioexception.crawling.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLecture is a Querydsl query type for Lecture
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLecture extends EntityPathBase<Lecture> {

    private static final long serialVersionUID = -172704792L;

    public static final QLecture lecture = new QLecture("lecture");

    public final StringPath companyName = createString("companyName");

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageLink = createString("imageLink");

    public final StringPath instructor = createString("instructor");

    public final StringPath lectureId = createString("lectureId");

    public final ListPath<LectureTag, QLectureTag> lectureTags = this.<LectureTag, QLectureTag>createList("lectureTags", LectureTag.class, QLectureTag.class, PathInits.DIRECT2);

    public final NumberPath<Integer> ordinaryPrice = createNumber("ordinaryPrice", Integer.class);

    public final StringPath salePercent = createString("salePercent");

    public final NumberPath<Integer> salePrice = createNumber("salePrice", Integer.class);

    public final StringPath siteLink = createString("siteLink");

    public final StringPath title = createString("title");

    public QLecture(String variable) {
        super(Lecture.class, forVariable(variable));
    }

    public QLecture(Path<? extends Lecture> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLecture(PathMetadata metadata) {
        super(Lecture.class, metadata);
    }

}

