package dev.ioexception.crawling.repository;

import dev.ioexception.crawling.entity.Tag;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    @Query("SELECT t FROM Tag t WHERE t.tagId IN :tagIds")
    List<Tag> findByIdIn(@Param("tagIds") List<Long> tagIds);
}
