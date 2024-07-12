package dev.ioexception.crawling.page.site;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.chrome.ChromeOptions;
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
public class YbmCrawling{

	private final LectureRepository lectureRepository;
	private final LectureTagRepository lectureTagRepository;
	private final TagRepository tagRepository;
	private final UploadImage uploadImage;

	public void getSaleLecture() throws IOException, InterruptedException {
		// webDriver 옵션 설정
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setHeadless(true);
		chromeOptions.addArguments("--lang=ko");
		chromeOptions.addArguments("--no-sandbox");
		chromeOptions.addArguments("--disable-dev-shm-usage");
		chromeOptions.addArguments("--disable-gpu");
		chromeOptions.addArguments("headless");
		chromeOptions.setCapability("ignoreProtectedModeSettings", true);

		// 토익, 영어, 일본어, 중국어 강의 가져오는 리스트페이지
		List<String> topicLists = new ArrayList<>();
		topicLists.add("https://toeic.ybmclass.com/toeic/toeic_list_v2.asp");
		topicLists.add("https://eng.ybmclass.com/eng/eng_list_v2.asp");
		topicLists.add("https://japan.ybmclass.com/japan/japan_list_v2.asp");
		topicLists.add("https://china.ybmclass.com/china/china_list_v2.asp");

		// 각 과목을 반복문을 통해 돈다.
		for (String topicList : topicLists) {
			String url = topicList;

			// /usr/local/bin 폴더안의 chromedirver 파일을 가져온다.
			WebDriver driver = new ChromeDriver();
			// url을 chromedriver로 실행한다.
			driver.get(url);
			Thread.sleep(1000);

			// 단과, 패키지, 환급패스 탭부분 선택하기
			WebElement classElement = driver.findElement(By.cssSelector("#content > div.tabmenu.tab-3 > ul"));
			List<WebElement> classes = classElement.findElements(By.cssSelector("li"));
			int classcount = classes.size();

			// 카테고리 단과, 패키지를 하나를 선택해서 클릭한다.

			for(int k = 1; k < classcount; k++){
				// 카테고리 단과, 패키지를 하나를 선택해서 클릭한다.
				String category = "#content > div.tabmenu.tab-3 > ul > li:nth-child(" + k + ")";
				driver.findElement(By.cssSelector(category)).click();
				Thread.sleep(1000);

				// 과목 체크박스를 가져온다.
				// ul 태그 선택
				WebElement ulElement = driver.findElement(By.cssSelector("#lecture_list"));
				// ul 태그 아래의 모든 li 태그 선택
				List<WebElement> types = ulElement.findElements(By.cssSelector("li"));
				int typeCount = types.size();


				for (int i = 1; i <= typeCount; i++){

					// 토익, 토스, 오픽 체크박스 클릭
					String subject = "#lecture_list > li:nth-child(" + i + ")";
					driver.findElement(By.cssSelector(subject)).click();

					// 선택된 체크박스 요소들 가져오기
					WebElement selectedCheckbox = driver.findElement(By.cssSelector("input[name='S_Parent_Category']:checked"));

					// 첫 번째 체크박스의 텍스트 출력
					WebElement labelElement = selectedCheckbox.findElement(By.xpath("../label"));
					String labelText = labelElement.getText();
					System.out.println("선택된 체크박스 텍스트: " + labelText);

					// 태그 저장
					Tag tag = tagRepository.findByName(labelText)
						.orElseGet(() -> tagRepository.save(Tag.builder().name(labelText).build()));

					Thread.sleep(1500);
					// 전체 강의 선택
					WebElement selection = driver.findElement(By.cssSelector("#level_list > li:nth-child(1)"));
					selection.click();
					Thread.sleep(1000);
					// 검색 버튼 클릭
					driver.findElement(By.cssSelector("#frm > div.lectureSearchBox > div > div > div.search-ip > div.bt > a.btn_m.btn_type2.searchBtn")).click();
					Thread.sleep(1000);

					try{
						// 마지막 페이지의 번호를 가져온다.
						WebElement nextPageLink = driver.findElement(By.cssSelector("a.next"));
						String onclickValue = nextPageLink.getAttribute("onclick");

						// 'goPage' 메서드의 인자에서 숫자 부분을 추출한다.
						String pageNumber = onclickValue.replaceAll("[^0-9]", "");
						int lastPageNum = Integer.parseInt(pageNumber);


						for (int j = 1; j <= lastPageNum; j++){
							String page = "goPage('" + j + "')";
							((JavascriptExecutor) driver).executeScript(page);
							Thread.sleep(1000);

							// ul 안에 있는 모든 li 태그(각각의 강의) 가져오기
							List<WebElement> liElements = driver.findElements(By.cssSelector("#lectureBody > ul > li"));


							// 각 강의에 대한 데이터 추출
							for (WebElement li : liElements) {
								if(getTitle(li).contains("Set") || getTitle(li).contains("TEST")){
									continue;
								}

								Lecture lecture = Lecture.builder()
									.lectureId(getLectureId(li))
									.title(getTitle(li))
									.instructor(getInstructor(li))
									.companyName("YBM")
									.ordinaryPrice(getPrice(li))
									.salePrice(getSalePrice(li))
									.salePercent(getSalePercent(li))
									.siteLink("https://toeic.ybmclass.com/toeic/toeic_main.asp")
									.imageLink(getImage(li))
									.date(LocalDate.now())
									.build();

								lectureRepository.save(lecture);

								// 강의 태그 중간테이블을 저장한다.
								LectureTag lectureTag = new LectureTag();
								lectureTag.setTag(tag);
								lectureTag.setLecture(lecture);
								lectureTagRepository.save(lectureTag);
							}
						}

					} catch (Exception e){
						// 강의 페이지의 목록이 없을 경우 예외가 발생해서 처리하였다.
						continue;
					}

					Thread.sleep(1000);
				}
			}

		}
	}
	private String getInstructor(WebElement li) {
		// 강의 infobox 가져오기
		WebElement infobox = li.findElement(By.cssSelector("div.infobox"));
		// 강사 이름을 가져온다.
		String instructorFull = infobox.findElement(By.cssSelector("div.txt-2 > ul > li:nth-child(1)")).getText();

		String instructor = instructorFull.substring(3);

		// "|" 문자로 문자열 분할
		String[] parts = instructor.split("\\|");

		// "|" 이후의 내용을 없애려면 첫 번째 요소 선택
		String result = parts[0].trim();

		if(result.isEmpty()){
			result = "강사없음";
		}

		return result;
	}
	private String getImage(WebElement li) throws IOException {
		// 드라이버에서 이미지 선택 (현재 LI 태그 내에서 선택)
		WebElement lectureContainer = li.findElement(By.cssSelector("div.ibox > a > span > img"));
		// 이미지 태그의 src 속성값을 가져옵니다.
		String srcValue = lectureContainer.getAttribute("src");

		return uploadImage.uploadFromUrlToS3(srcValue, "ybm", getLectureId(li));
	}
	public  String getTitle(WebElement li){
		// 강의 infobox 가져오기
		WebElement infobox = li.findElement(By.cssSelector("div.infobox"));
		// 강의 제목을 가져온다.

        return infobox.findElement(By.cssSelector("div.txt-1 > p > a")).getText();
	}
	public  String getLectureId(WebElement li){
		// 강의 pricebox 선택하기
		WebElement pricebox = li.findElement(By.cssSelector("div.price > div.scroll-box > ul"));
		// 강의 id 가져오기
		String lecture_id = pricebox.findElement(By.cssSelector("li:nth-child(1) > span > input")).getAttribute("value");
		int indexOfPipe = lecture_id.indexOf('|');

		return "ybm" + lecture_id.substring(0, indexOfPipe);
	}
	public int getPrice(WebElement li){
		// 강의 pricebox 선택하기
		WebElement pricebox = li.findElement(By.cssSelector("div.price > div.scroll-box > ul"));
		// 강의 원가 가져오기
		String price = pricebox.findElement(By.cssSelector("li:nth-child(1) > span > span")).getText();
		String numericPart = price.replaceAll("[^0-9]", "");

		return Integer.parseInt(numericPart);
	}
	public int getSalePrice(WebElement li){
		// 강의 pricebox 선택하기
		WebElement pricebox = li.findElement(By.cssSelector("div.price > div.scroll-box > ul"));

		String sale_price = "0";
		int sale_price_int = 0;

		// 강의 할인가 가져오기
		List<WebElement> salePriceElements = pricebox.findElements(By.cssSelector("li:nth-child(2) > span.stxt-1 > span.stxt-ch"));
		if (!salePriceElements.isEmpty()) {
			sale_price = salePriceElements.get(0).getText();
			String numericPart = sale_price.replaceAll("[^0-9]", "");
			sale_price_int = Integer.parseInt(numericPart);
		} else {
			sale_price_int = getPrice(li);
		}

		return sale_price_int;
	}
	public  String getSalePercent(WebElement li){
		// 강의 pricebox 선택하기
		WebElement pricebox = li.findElement(By.cssSelector("div.price > div.scroll-box > ul"));

		// 세일에 대한 정보
		String sale_percent = "-1";

		List<WebElement> salePercentElements = pricebox.findElements(By.cssSelector("li:nth-child(2) > span > span.sale"));
		if (!salePercentElements.isEmpty()) {
			sale_percent = salePercentElements.get(0).getText();
			sale_percent = sale_percent.substring(0, 3);
		}

		return sale_percent;
	}
}
