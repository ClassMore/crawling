package dev.ioexception.crawling.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLectureTag is a Querydsl query type for LectureTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLectureTag extends EntityPathBase<LectureTag> {

    private static final long serialVersionUID = 322445970L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLectureTag lectureTag = new QLectureTag("lectureTag");

    public final NumberPath<Long> lectagId = createNumber("lectagId", Long.class);

    public final QLecture lecture;

    public final QTag tag;

    public QLectureTag(String variable) {
        this(LectureTag.class, forVariable(variable), INITS);
    }

    public QLectureTag(Path<? extends LectureTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLectureTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLectureTag(PathMetadata metadata, PathInits inits) {
        this(LectureTag.class, metadata, inits);
    }

    public QLectureTag(Class<? extends LectureTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lecture = inits.isInitialized("lecture") ? new QLecture(forProperty("lecture")) : null;
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag")) : null;
    }

}

