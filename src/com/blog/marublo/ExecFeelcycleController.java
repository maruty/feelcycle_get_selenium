package com.blog.marublo;

import java.io.BufferedWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class ExecFeelcycleController {

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException,
			IOException {

		// jsonファイルで取得予定のファイルのコントロール
		// String lessonJson = "";
		System.out.println("json情報の取得");
		/*
		 * try{ BufferedReader br = new BufferedReader(new FileReader(new
		 * File("/var/www/html/json/lesson.json")));
		 *
		 * lessonJson = br.readLine(); while(lessonJson != null){
		 * System.out.println(lessonJson);
		 *
		 * lessonJson = br.readLine(); }
		 *
		 * br.close(); }catch(FileNotFoundException e){ System.out.println(e);
		 * }catch(IOException e){ System.out.println(e); }
		 */
		Lesson lessonInfo = new Lesson();
		try {
			lessonInfo = JSON.decode(new FileReader(
					"/var/www/html/json/lesson.json"), Lesson.class);
					//"./lesson.json"), Lesson.class); //開発環境
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Lesson lessonInfo = JSON.decode(lessonJson, Lesson.class);
		System.out.println("==========================================");
		System.out.println("json内容");
		System.out.println("GYM:" + lessonInfo.getGym());
		System.out.println("LESSON_NAME:" + lessonInfo.getLessonName());
		System.out.println("LESSON_DATE:" + lessonInfo.getLessonDate());
		System.out.println("LESSON_TIME:" + lessonInfo.getLessonTime());
		System.out.println("LESSON_STATE:" + lessonInfo.getLessonState());
		System.out.println("USER_ID:" + lessonInfo.getUserId());
		System.out.println("USER_PASS:" + lessonInfo.getUserPass());
		System.out.println("==========================================");

		final String LESSON_NAME = lessonInfo.getLessonName();
		final String LESSON_DATE = lessonInfo.getLessonDate();
		final String LESSON_TIME = lessonInfo.getLessonTime();
		final String LESSON_STATE = lessonInfo.getLessonState();

		final String USER_ID = lessonInfo.getUserId();
		final String USER_PASS = lessonInfo.getUserPass();

		final String GYM = lessonInfo.getGym();

		WebDriver driver = new FirefoxDriver();

		//feelcycle パターン
		System.out.println("if手前");
		if(GYM == "1"){
			System.out.println("ロジック入った？1");
			// ログインフォームからスタート

			/*ログインセクション
			 * ログイン後2-3秒程度待たないとセッション格納されていないっぽい
			 */
			System.out.println("Feelcycle：Login");
			driver.get("https://www.feelcycle.com/feelcycle_reserve/mypage.php");

			driver.findElement(By.name("login_id")).sendKeys(USER_ID);
			driver.findElement(By.name("login_pass")).sendKeys(USER_PASS);

			driver.findElement(By.cssSelector(".submit_b")).click();
			System.out.println("Feelcycle：ログイン成功");
			//だから待つ
			Thread.sleep(3000);

			// 初回のみ後に使うjson用に店舗名　レッスンリストをパースしてjsonファイルとしてサーバーに置く
			driver.get("https://www.feelcycle.com/feelcycle_reserve/reserve.php");

			/*
			 * 店舗選択
			 */
			// selectタグを取得
			Select selectList = new Select(driver.findElement(By.name("tenpo")));
			// 選択する項目をテキストで指定
			selectList.selectByVisibleText(LESSON_STATE);

			// jsonファイルの作成 別に握りつぶしてもOKだからtry catchにする
			System.out.println("入力フォーム用json作成処理開始");
			try {
			  TenpoMapDto tenpoListDto = new TenpoMapDto();
			  List<WebElement> selectElement = selectList.getOptions();

			  // 店舗リスト

			  for (WebElement webElement : selectElement) {
			    ValueDto valueDto = new ValueDto(webElement.getText(),
			        webElement.getAttribute("value"));
			    tenpoListDto.setTenpoMap(valueDto);
			  }

			  // インストラクター
			  Select instList = new Select(driver.findElement(By.name("lesson")));

			  for (WebElement webElement : instList.getOptions()) {
			    ValueDto valueDto = new ValueDto(webElement.getText(),
			        webElement.getAttribute("value"));
			    tenpoListDto.setProgramMap(valueDto);
			  }

			  String jsonText = JSON.encode(tenpoListDto, true);

			  // jsonファイルの保存
			  // 開発環境
			  // BufferedWriter writer = new BufferedWriter(new
			  // FileWriter("lesson_master.json"));

			  // 本番

			  BufferedWriter writer = new BufferedWriter(new FileWriter(
			      "/var/www/html/json/lesson_master.json"));
			  writer.write(jsonText);
			  writer.close();

			} catch (Exception e) {
			  // 握りつぶす
			  // e.printStackTrace();
			}
			System.out.println("Feelcycle：座席予約");


			//実際の座席取得処理
			while (true) {
				driver.get("https://www.feelcycle.com/feelcycle_reserve/reserve.php");

				/*
				 * 店舗選択
				 */
				// selectタグを取得
				//Select selectList = new Select(driver.findElement(By.name("tenpo")));
				// 選択する項目をテキストで指定
				//selectList.selectByVisibleText(LESSON_STATE);

				// jsonファイルの作成 別に握りつぶしてもOKだからtry catchにする
				/*
				try {
					TenpoMapDto tenpoListDto = new TenpoMapDto();
					List<WebElement> selectElement = selectList.getOptions();
					*/
					// 店舗リスト
				/*
					for (WebElement webElement : selectElement) {
						ValueDto valueDto = new ValueDto(webElement.getText(),
								webElement.getAttribute("value"));
						tenpoListDto.setTenpoMap(valueDto);
					}
				*/
					// インストラクター
					//Select instList = new Select(driver.findElement(By.name("lesson")));
					/*
					for (WebElement webElement : instList.getOptions()) {
						ValueDto valueDto = new ValueDto(webElement.getText(),
								webElement.getAttribute("value"));
						tenpoListDto.setProgramMap(valueDto);
					}

					String jsonText = JSON.encode(tenpoListDto, true);
	*/
					// jsonファイルの保存
					// 開発環境
					// BufferedWriter writer = new BufferedWriter(new
					// FileWriter("lesson_master.json"));

					// 本番
				/*
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							"/var/www/html/json/lesson_master.json"));
					writer.write(jsonText);
					writer.close();

				} catch (Exception e) {
					// 握りつぶす
					// e.printStackTrace();
				}
				 */
				//Thread.sleep(13000);

				// 実際の予約連アタ処理
				/*
				 * 全ての当週のレッスン要素をWebElementに格納してレッスンDTOを作成する
				 */
				// day__bとday_の構成で作られているので取得方法は day__bで全部回した後 day_をまわす感じ
				// #day__b #title_week_b

				List<FeelcycleLessonDto> feelcycleLessonDtoList = new ArrayList<>();
				List<WebElement> firstDayElementList = new ArrayList<>();

				if(driver.findElements(By.cssSelector("#day__b >  #title_week_b")).size() == 0){
					System.out.println("要素が見つからない");
					System.exit(0);
				}

				// 1日目のDTO作成
				String getValueDay = driver.findElement(
						By.cssSelector("#day__b >  #title_week_b")).getText();
				firstDayElementList = driver.findElements(By
						.cssSelector("#day__b > a"));
				for (WebElement e : firstDayElementList) {
					// レッスン状態を確認する
					int unitPastCount = e
							.findElements(By.cssSelector(".unit_past")).size();
					int unitCount = e.findElements(By.cssSelector(".unit")).size();
					int unitReservedCount = e.findElements(
							By.cssSelector(".unit_reserved")).size();

					if (unitCount > 0 && unitPastCount == 0
							&& unitReservedCount == 0) {
						FeelcycleLessonDto feelcycleDto = new FeelcycleLessonDto();
						feelcycleDto
								.setLessonName(e.findElement(
										By.cssSelector(".unit > p:nth-child(2)"))
										.getText());
						feelcycleDto.setLessonDate(getValueDay);
						feelcycleDto.setLessonTime(e.findElement(
								By.cssSelector(".start")).getText());
						feelcycleDto.setLessonInstructor(e.findElement(
								By.cssSelector(".time")).getText());
						feelcycleDto.setIslessonStatus(true);
						feelcycleLessonDtoList.add(feelcycleDto);

						String perseTime = feelcycleDto.getLessonTime().substring(
								0, 5);
						if (getValueDay.equals(LESSON_DATE)
								&& perseTime.equals(LESSON_TIME)
								&& feelcycleDto.lessonName.equals(LESSON_NAME)) {
							getLesson(driver, e, feelcycleDto);
							// e.findElement(By.cssSelector(".unit")).click();
						}
					}
				}
				// 2日目以降のDTO
				firstDayElementList = new ArrayList<>();
				firstDayElementList = driver.findElements(By.cssSelector("#day_"));
				for (WebElement e : firstDayElementList) {
					getValueDay = e.findElement(
							By.cssSelector("#day_ >  #title_week")).getText();
					List<WebElement> aList = new ArrayList<>();
					aList = e.findElements(By.cssSelector("#day_ > a"));
					for (WebElement ae : aList) {
						// レッスン状態を確認する
						int unitPastCount = ae.findElements(
								By.cssSelector(".unit_past")).size();
						int unitCount = ae.findElements(By.cssSelector(".unit"))
								.size();
						int unitReservedCount = ae.findElements(
								By.cssSelector(".unit_reserved")).size();
						if (unitCount > 0 && unitPastCount == 0
								&& unitReservedCount == 0) {
							FeelcycleLessonDto feelcycleDto = new FeelcycleLessonDto();
							feelcycleDto.setLessonName(ae.findElement(
									By.cssSelector(".unit > p:nth-child(2)"))
									.getText());
							feelcycleDto.setLessonDate(getValueDay);
							feelcycleDto.setLessonTime(ae.findElement(
									By.cssSelector(".start")).getText());
							feelcycleDto.setLessonInstructor(ae.findElement(
									By.cssSelector(".time")).getText());
							feelcycleDto.setIslessonStatus(true);
							feelcycleLessonDtoList.add(feelcycleDto);

							String perseTime = feelcycleDto.getLessonTime()
									.substring(0, 5);
							if (getValueDay.equals(LESSON_DATE)
									&& perseTime.equals(LESSON_TIME)
									&& feelcycleDto.lessonName.equals(LESSON_NAME)) {
								getLesson(driver, ae, feelcycleDto);
								// e.findElement(By.cssSelector(".unit")).click();
							}
						}
					}
				}
				Calendar calendar = Calendar.getInstance();
				// System.out.println(calendar.getTime().toString());
				System.out.println(calendar.getTime().toString() + ": 満席状態なので再度取得");
				Thread.sleep(1000);
			}

		} else {
			System.out.println("gym1の分岐に入らなかった");
		}
		//b-monsterの場合
		if(GYM == "2"){
			System.out.println("b-monster:開始");
		}
	}

	public static void getLesson(WebDriver driver, WebElement element,
			FeelcycleLessonDto fc) throws InterruptedException {

		element.findElement(By.cssSelector(".unit")).click();
		// driver.findElements(element.findElement(By.cssSelector(".unit"))).click();
		// 座席のページに入る
		int sheetCount = driver.findElements(By.cssSelector(".seat_map > div"))
				.size();
		// 0番目と1番目はインストラクターと入り口だからそれ以降でぶんまわす
		if (sheetCount > 2) {
			int sheetCountNumber = 1;
			for (int i = 2; i < sheetCount; i++) {
				String countString = "";
				if (sheetCountNumber < 10) {
					countString = "0" + sheetCountNumber;
				} else {
					countString = String.valueOf(sheetCountNumber);
				}
				int emptySheet = driver.findElements(
						By.cssSelector(".no" + countString + " > .set")).size();
				// setが1個でもオブジェクトがあるととられている
				if (emptySheet < 1) {
					// .no01 > a:nth-child(1)
					driver.findElement(
							By.cssSelector(".no" + countString + " > a"))
							.click();

					// driver.switchTo().defaultContent();
					int ticketCount = driver.findElements(
							By.cssSelector("#TB_iframeContent")).size();

					if (ticketCount > 0) {
						driver.switchTo().frame("TB_iframeContent");
						Thread.sleep(2000);
						driver.findElement(
								By.cssSelector(".ticket > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(1) > span:nth-child(2)"))
								.click();
						Thread.sleep(1000);
					}

					// div.coment:nth-child(9) > table:nth-child(1) >
					// tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(10) >
					// a:nth-child(1)
					// 決定ボタン
					driver.findElement(
							By.cssSelector("div.coment:nth-child(9) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(10) > a:nth-child(1)"))
							.click();
					Thread.sleep(1000);
					// 取得完了

					Calendar calendar = Calendar.getInstance();
					System.out.println(calendar.getTime().toString()
							+ ": feelcycle:取得完了");
					// System.out.println("feelcycle:取得完了");
					driver.quit();
					System.exit(0);
				}
				sheetCountNumber++;
			}
		}
		System.out.println("feelcycle:座席画面までいきましたが処理完了が出来ませんでした。再度取得して下さい");
		driver.quit();
		System.exit(0);
	}

}
