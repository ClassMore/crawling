package dev.ioexception.crawling.page.site;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.entity.LectureTag;
import dev.ioexception.crawling.entity.Tag;
import dev.ioexception.crawling.page.UploadImage;
import dev.ioexception.crawling.repository.LectureRepository;
import dev.ioexception.crawling.repository.LectureTagRepository;
import dev.ioexception.crawling.repository.TagRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class ClassUCrawling {
	private WebDriver categoryDriver;
	private final String CLASSU_URL = "https://www.classu.co.kr/new/category?categoryId=";

	private final LectureRepository lectureRepository;
	private final TagRepository tagRepository;
	private final LectureTagRepository lectureTagRepository;
	private final UploadImage uploadImage;

	public void process() throws IOException {
		System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
		// System.setProperty("webdriver.chrome.driver", "C:\\Users\\user\\Desktop\\chromedriver-win64\\chromedriver.exe");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-popup-blocking");       //팝업안띄움
		options.addArguments("headless");                       //브라우저 안띄움
		options.addArguments("--disable-gpu");            //gpu 비활성화
		options.addArguments("--start-maximized");
		options.addArguments("--lang=ko");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--disable-gpu");

		//        options.addArguments("--blink-settings=imagesEnabled=false"); //이미지 다운 안받음

		categoryDriver = new ChromeDriver(options);
		Dimension dim = new Dimension(1920, 1280);
		categoryDriver.manage().window().setSize(dim);
		try {
			categoryMove();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void categoryMove() throws InterruptedException, IOException {
		categoryDriver.get(CLASSU_URL);
		Thread.sleep(1000);
		List<WebElement> elements = categoryDriver.findElements(
			By.cssSelector("#left-side > div > div > div > div.sc-b08f523c-3.cGhORZ > button > nav > ul > li"));

		List<String> categories = new ArrayList<>();
		for (WebElement element : elements) {
			categories.add(element.getAttribute("class"));
		}

		for (String category : categories) {
			List<String> subcategories = new ArrayList<>();
			String url = CLASSU_URL + category;
			Optional<Tag> tag = tagRepository.findByName(category);
			if (tag.isEmpty())
				tag = Optional.of(tagRepository.save(new Tag(category)));

			categoryDriver.get(url);
			Thread.sleep(1000);
			List<WebElement> subElements = categoryDriver.findElements(
				By.cssSelector("#right-side > div > div > section > ul:nth-child(2) > li"));

			for (WebElement webElement : subElements) {
				subcategories.add(webElement.getText());
			}

			for (String sub : subcategories) {
				categoryDriver.get(url + "&subCategoryId=" + sub);
				Thread.sleep(1000);
				Long last_height = (Long)((JavascriptExecutor)categoryDriver).executeScript(
					"return document.body.scrollHeight");

				while (true) {
					new Actions(categoryDriver).sendKeys(Keys.END).perform();
					Thread.sleep(1500);
					Long new_height = (Long)((JavascriptExecutor)categoryDriver).executeScript(
						"return document.body.scrollHeight");
					if (Objects.equals(last_height, new_height))
						break;
					last_height = new_height;
				}

				getData(sub, tag.get());
				Thread.sleep(1000);
			}
		}
	}

	private void getData(String sub, Tag tag) throws InterruptedException, IOException {
		List<Lecture> lectureList = new ArrayList<>();
		Optional<Tag> subtag = tagRepository.findByName(sub);
		if (subtag.isEmpty())
			subtag = Optional.of(tagRepository.save(new Tag(sub)));
		Thread.sleep(1000);
		List<WebElement> elements = categoryDriver
			.findElements(
				By.cssSelector("#right-side > div > div > div.sc-7debd690-6.jUbDlK > ul.sc-b6a60c73-0.lkDVWE > li"));

		List<WebElement> price = categoryDriver.findElements(
			By.cssSelector("#right-side > div > div > div.sc-7debd690-6.jUbDlK > ul.sc-d12dc573-0.beuvJr > li")
		);

		for (int i = 0; i < elements.size(); i++) {
			Lecture lecture = Lecture.builder()
				.lectureId(getId(elements.get(i)))
				.title(getTitle(elements.get(i)))
				.imageLink(getImage(price.get(i)))
				.instructor(getInstructor(elements.get(i)))
				.siteLink(getUrl(elements.get(i)))
				.salePrice(getSalePrice(price.get(i)))
				.ordinaryPrice(getPrice(price.get(i)))
				.companyName("classU")
				.date(LocalDate.now())
				.salePercent(getSalePercent(price.get(i)))
				.build();

			lectureList.add(lecture);
		}

		for (Lecture lecture : lectureList) {
			Lecture lec = lectureRepository
				.findLectureByLectureId(lecture.getLectureId())
				.orElseGet(() -> lectureRepository.save(lecture));
			LectureTag lectureTag1 = new LectureTag();
			lectureTag1.setLecture(lec);
			lectureTag1.setTag(tag);
			lectureTagRepository.save(lectureTag1);

			LectureTag lectureTag2 = new LectureTag();
			lectureTag2.setLecture(lec);
			lectureTag2.setTag(subtag.get());
			lectureTagRepository.save(lectureTag2);
		}

	}

	private int getSalePrice(WebElement webElement) {
		String str = webElement.findElement(By.cssSelector("span.sc-96b9a5ad-10.iNxKfN"))
			.getAttribute("innerText").replaceAll(",", "");
		return Integer.parseInt(str.substring(2, str.length() - 1));
	}

	private String getSalePercent(WebElement webElement) {
		List<WebElement> result = webElement.findElements(By.cssSelector("div.sc-96b9a5ad-8.bgSGYO > span"));
		if (result.size() == 1)
			return "-1";
		return webElement.findElement(By.cssSelector("span.sc-96b9a5ad-9.ktbZpF")).getAttribute("innerHTML");
	}

	private Integer getPrice(WebElement webElement) {
		List<WebElement> spans = webElement.findElements(By.cssSelector("div.sc-96b9a5ad-8.bgSGYO > span"));
		String str = webElement.findElement(By.cssSelector("span.sc-96b9a5ad-10.iNxKfN"))
			.getAttribute("innerText").replaceAll(",", "");
		int price = Integer.parseInt(str.substring(2, str.length() - 1));
		if (spans.size() == 1)
			return price;
		int percentage = Integer.parseInt(getSalePercent(webElement)
			.substring(0, getSalePercent(webElement).length() - 1));
		return (int)(price / (1 - (double)percentage / 100));
	}

	private String getId(WebElement element) {
		return "ClassU" + element.getAttribute("title");
	}

	private String getTitle(WebElement element) {
		return element.findElement(By.tagName("p")).getAttribute("innerHTML");
	}

	private String getUrl(WebElement element) {
		return "https://www.classu.co.kr/class/classDetail/" + element.getAttribute("title");
	}

	private String getInstructor(WebElement element) {
		return element.findElement(By.tagName("span")).getAttribute("innerHTML");
	}

	private String getImage(WebElement element) throws IOException {
		return uploadImage
			.uploadFromUrlToS3(element.findElement(By.cssSelector("div.sc-62320715-1.jgQDxN"))
					.getAttribute("src"),
				"classU", getId(element));
	}
}
