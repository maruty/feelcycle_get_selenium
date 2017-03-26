package com.blog.marublo;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.arnx.jsonic.JSON;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.ClickAction;
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
					//"/var/www/html/json/lesson.json"), Lesson.class);
					"./lesson.json"), Lesson.class); //開発環境
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
		driver.manage().window().maximize();
		//driver.manage().window().setSize(new Dimension(width, height));

		//feelcycle パターン
		if(GYM.equals("1")){
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
			//System.out.println("gym1の分岐に入らなかった");
		}
		//b-monsterの場合
		if(GYM.equals("2")){
			System.out.println("b-monster:ログイン開始");
			driver.get("https://www.b-monster.jp/reserve/?studio_code=0001");
			Thread.sleep(2000);
			//driver.findElement(By.cssSelector("#g-console > li:nth-child(1) > button")).click();
			//button.btn
			//driver.manage().window().maximize();
			//driver.get("https://www.b-monster.jp/");
			//driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);//画面表示まで10秒待つ
			//Actions clicker = new Actions(driver);
		    //clicker.sendKeys(Keys.PAGE_DOWN);
		    Thread.sleep(1000);

			// ウィンドウ切り替え

			//int debugCount = driver.findElements(By.cssSelector("#g-header")).size();


            //List<WebElement> iframeElements = driver.findElements(By.tagName("iframe"));
            //System.out.println("The total number of iframes are " + iframeElements.size());
			//JavascriptExecutor js = (JavascriptExecutor) driver;
			//js.executeScript("document.getElementsByClassName('btn').click();");
		    System.out.println("ログイン");
		    //int width = 320;
		    //int height = 480;
		    //driver.manage().window().setSize(new Dimension(width, height));


			Actions clicker = new Actions(driver);
		    clicker.sendKeys(Keys.PAGE_DOWN);

		    Thread.sleep(3000);

			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("var v = document.getElementsByTagName('button');v[0].click();");
			Thread.sleep(3000);
			//js.executeScript("var v = document.getElementsByTagName('button');v[1].click();");

			Thread.sleep(3000);
			js.executeScript("document.getElementById('your-id').value='"+USER_ID +"';");
			Thread.sleep(3000);
			js.executeScript("document.getElementById('your-password').value='"+USER_PASS+ "';");
			//int debugCount = driver.findElements(By.cssSelector("#gnav > div > div > div.gnav-btns > button")).size();
			//System.out.println("数:"+ debugCount + "個");
			//driver.findElement(By.cssSelector("#g-console > button")).click();
			Thread.sleep(3000);
			js.executeScript("document.querySelector(\"#login-btn > span\").click();");
			Thread.sleep(3000);
		    try{
			    File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				String fileName = "test.png";
				String rootPath = "/var/www/html/log_images/";
				String URL = "http://133.242.235.62";
				FileUtils.copyFile(srcFile, new File(rootPath + fileName));
				System.out.println("file:" + "  " + URL + "/log_images/" + fileName);

		    }catch(Exception e){

		    }



			//driver.findElement(By.xpath("//*[@id='g-console']/li[1]/button")).click();
			Thread.sleep(4000);
			System.out.println("ログインモーダルチェック");
			//System.out.println(driver.findElements(By.cssSelector("#login-modal")).size() + "個");
			//ログイン画面
			//driver.findElement(By.cssSelector("#your-id")).sendKeys(USER_ID);
			//driver.findElement(By.name("#your-password")).sendKeys(USER_PASS);

			//driver.findElement(By.cssSelector("#login-btn")).click();
			System.out.println("b-monster：ログイン成功");
			//画面の切り替わりとクッキー関係のため待つ
			Thread.sleep(3000);

			while(true){
				//予約画面への遷移
				System.out.println("b-monster：予約画面の店舗選択");
				driver.get("https://www.b-monster.jp/reserve/list");
				int bmonTenpoCount = driver.findElements(By.cssSelector("#main-container > div.block-body > div")).size();
				for(int i=0; i < bmonTenpoCount; i++){
					String tenpoText = driver.findElement(By.cssSelector("#main-container > div.block-body > div:nth-child("
																			+ (i+1) + ") > a > div > div > div > h3")).getText();
					if(tenpoText.equals(LESSON_STATE)){
						driver.findElement(By.cssSelector("#main-container > div.block-body > div:nth-child("
								+ (i+1) + ") > a")).click();
						break;
					}
				}
				System.out.println("b-monster：予約画面スケジュール一覧");

				//日にちの合致を行なって対象のオブジェクトのみを集めに行く
				int bmonLessonDayCount = driver.findElements(By.cssSelector(
						"#scroll-box > div.grid > div")).size();

				System.out.println("bmonLessonDayCount：" + bmonLessonDayCount);


				List<WebElement>bmnonLessonList = null;
				for(int i=0; i < bmonLessonDayCount; i++) {
					String bmonLessonDayMuch = driver.findElement(By.cssSelector(
							"#scroll-box > div.grid > div:nth-child(" + (i+1) + ") > div > h3"
							)).getText();
					//日単位の合致したリストを取得してBreak
					System.out.println(bmonLessonDayMuch);
					System.out.println(LESSON_DATE);

					if(bmonLessonDayMuch.equals(LESSON_DATE)){
						bmnonLessonList = driver.findElements(By.cssSelector(
								"#scroll-box > div.grid > div:nth-child(" +  (i+1) +") > ul:nth-child(2) > li"
								));
						break;
					}
				}


				//System.out.println("bmnonLessonListの数は:" + bmnonLessonList.size());

				//合致する時間のレッスンをクリックする
				if(bmnonLessonList != null){
					for(WebElement element : bmnonLessonList) {


						//System.out.println(element.findElements(By.cssSelector("a > .panel-content > p:nth-child(1)")).size() + "個");
						String bmonTimeStr = element.findElement(By.cssSelector("a:nth-child(1) > .panel-content >  p:nth-child(1)")).getText();
						//System.out.println(bmonTimeStr.substring(0, 5));
						//System.out.println("LESSON_TIME:" + LESSON_TIME);
						if(bmonTimeStr.substring(0, 5).equals(LESSON_TIME)){
							element.findElement(By.cssSelector("a:nth-child(1)")).click();
							break;
						}
					}
				}

				//座席ページへの移動完了 waiting-list
				int waitingCount = driver.findElements(By.cssSelector(".waiting-list")).size();
				if(waitingCount > 0){
					Thread.sleep(1000);
					//席が満席だった場合頭からループをやり直す
					Calendar calendar = Calendar.getInstance();
					System.out.println(calendar.getTime().toString() + ": 満席状態なので再度取得");
					continue;
				}

				//空いてるサンドバッグ選択
				int bagStart = 7;
				int bagEnd = 30;
				for(int i=bagStart; i < bagEnd+1; i++){
					if(driver.findElement(By.cssSelector("#bag" + i)).isEnabled()){
						driver.findElement(By.cssSelector(".bag" + i + "> .bag-point")).click();
						driver.findElement(By.cssSelector("#your-reservation > span > button")).click();

						//予約確認ページ
						driver.findElement(By.cssSelector("#main-container > div.form-action > button")).click();
						int endMsgCount = driver.findElements(By.cssSelector("#main-container > div > section > h2")).size();
						if(endMsgCount > 0){
							String endMsg = driver.findElement(By.cssSelector("#main-container > div > section > h2")).getText();
							if(endMsg.equals("予約が完了いたしました。")){
								driver.quit();
								System.out.println("b-monster:取得完了");
								System.exit(0);
							}
						}
						Calendar calendar = Calendar.getInstance();
						System.out.println(calendar.getTime().toString() + ": 満席状態なので再度取得");
						continue;
					}
				}
			}
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
