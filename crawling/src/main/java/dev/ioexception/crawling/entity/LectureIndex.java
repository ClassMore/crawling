package dev.ioexception.crawling.entity;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Document(indexName = "lecture")
@Getter
@Setter
public class LectureIndex {

	@Id
	private Long id;
	@Field(type = FieldType.Text, name = "lectureId")
	private String lectureId;
	@Field(type = FieldType.Text, name = "title")
	private String title;
	@Field(type = FieldType.Text, name = "instructor")
	private String instructor;
	@Field(type = FieldType.Text, name = "companyName")
	private String companyName;
	@Field(type = FieldType.Integer, name = "ordinaryPrice")
	private int ordinaryPrice;
	@Field(type = FieldType.Integer, name = "salePrice")
	private int salePrice;
	@Field(type = FieldType.Text, name = "salePercent")
	private String salePercent;
	@Field(type = FieldType.Text, name = "imageLink")
	private String imageLink;

	public LectureIndex() {}

	@Builder
	public LectureIndex(Long id, String lectureId, String title, String instructor, String companyName,
		int ordinaryPrice,
		int salePrice, String salePercent, String imageLink) {
		this.id = id;
		this.lectureId = lectureId;
		this.title = title;
		this.instructor = instructor;
		this.companyName = companyName;
		this.ordinaryPrice = ordinaryPrice;
		this.salePrice = salePrice;
		this.salePercent = salePercent;
		this.imageLink = imageLink;
	}
}
