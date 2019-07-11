package com.blog.marublo;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import net.arnx.jsonic.JSON;

public class ExecFeelcycleController {

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException,
			IOException, BmonsterException {

		// jsonファイルで取得予定のファイルのコントロール
		// String lessonJson = "";
		System.out.println("json情報の取得");

		Lesson lessonInfo = new Lesson();
		
		try {
			//JenkinsInfo jenkinsInfo = new JenkinsInfo();
			lessonInfo = JSON.decode(new FileReader(
					"/var/www/html/json/lesson.json"), Lesson.class);
					//"./lesson.json"), Lesson.class); //開発環境
			//jenkinsInfo = JSON.decode(new FileReader(
					//"/var/www/html/json/jenkins.json"), JenkinsInfo.class);
					//"./jenkins.json"), JenkinsInfo.class); //開発環境
		    	//本番
		    	System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver");
		    	//開発環境
		    //System.setProperty("webdriver.gecko.driver", "/Applications/geckodriver");

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

		//final String LESSON_NAME = lessonInfo.getLessonName();
		final String LESSON_DATE = lessonInfo.getLessonDate();
		final String LESSON_TIME = lessonInfo.getLessonTime();
		final String LESSON_STATE = lessonInfo.getLessonState();
		final String USER_ID = lessonInfo.getUserId();
		final String USER_PASS = lessonInfo.getUserPass();
		final String GYM = lessonInfo.getGym();

		//chrome driver用
		//jenkins のビルドの　MAVEN_OPTISに-DargLine="-Dwebdriver.chrome.driver=/opt/chromedriver/chromedriver"
		//を設定している
		FirefoxOptions options = new FirefoxOptions();


		WebDriver driver = new FirefoxDriver(options);
		driver.manage().window().maximize();

		//feelcycle パターン
		if(GYM.equals("2")){
			//driver = new FirefoxDriver();
			System.out.println("b-monster:ログイン開始");
			
			driver.get("https://www.b-monster.jp/reserve/?studio_code=0001");

		    System.out.println("ログイン");

		    driver.manage().timeouts().implicitlyWait(1 ,TimeUnit.SECONDS);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("var v = document.getElementsByTagName('button');v[0].click();");

			//Thread.sleep(3000);
			//Thread.sleep(1000);

			driver.findElement(By.cssSelector("#your-id")).sendKeys(USER_ID);
			//js.executeScript("document.getElementById('your-id').value='"+USER_ID +"';");
			driver.manage().timeouts().implicitlyWait(1 ,TimeUnit.SECONDS);
			//js.executeScript("document.getElementById('your-password').value='"+USER_PASS+ "';");
			driver.findElement(By.cssSelector("#your-password")).sendKeys(USER_PASS);
			driver.manage().timeouts().implicitlyWait(1 ,TimeUnit.SECONDS);
			//js.executeScript("document.querySelector(\"#login-btn > span\").click();");
			driver.findElement(By.cssSelector("#login-btn")).click();
			driver.manage().timeouts().implicitlyWait(1 ,TimeUnit.SECONDS);


			//#login-user-name
			//ログインできたかチェック
			int loginCount = driver.findElements(By.cssSelector("#login-user-name")).size();
			if(loginCount < 1) {
				while(true){
					System.out.println("ログイン出来てなかったので再度ログインします");
					
					driver.get("https://www.b-monster.jp/reserve/?studio_code=0001");
					driver.navigate().refresh();
					driver.navigate().refresh();
					driver.navigate().refresh();
				    driver.manage().timeouts().implicitlyWait(2 ,TimeUnit.SECONDS);
					js.executeScript("var v = document.getElementsByTagName('button');v[0].click();");
					driver.manage().timeouts().implicitlyWait(2 ,TimeUnit.SECONDS);
					driver.findElement(By.cssSelector("#your-id")).sendKeys(USER_ID);
					//js.executeScript("document.getElementById('your-password').value='"+USER_PASS+ "';");
					driver.findElement(By.cssSelector("#your-password")).sendKeys(USER_PASS);
					driver.manage().timeouts().implicitlyWait(2 ,TimeUnit.SECONDS);
					//js.executeScript("document.querySelector(\"#login-btn > span\").click();");
					driver.findElement(By.cssSelector("#login-btn")).click();
					driver.manage().timeouts().implicitlyWait(2 ,TimeUnit.SECONDS);
					int loginCountLoop = driver.findElements(By.cssSelector("#login-user-name")).size();
					if(loginCountLoop > 0) {
						break;
					}
				}
			}


		    System.out.println("ログインボタンチェック");
			System.out.println("b-monster：ログイン成功");

			
			driver.manage().timeouts().implicitlyWait(2,TimeUnit.SECONDS);
			long startMilliBmon = System.currentTimeMillis();

			/*
			 *10分たったら強制的に終了
			 * */

			long loopMilli = System.currentTimeMillis();
			//1秒1000ミリ秒
			if(loopMilli - startMilliBmon > 600000 ) {
				//driver.quit();
				System.out.println("10分経過しても座席が空きません、いったん再処理を行います");
				getShellCall();
				System.exit(0);
			}

			//初回のみ一覧画面からの選択とする それ移行はURL再アタックで効率化を図る

			System.out.println("b-monster：予約画面スケジュール一覧");
			String bmonsterStudioUrl = "https://www.b-monster.jp/reserve/?studio_code=" + LESSON_STATE;

			driver.get(bmonsterStudioUrl);
			//System.out.println("30秒待つ");
			driver.manage().timeouts().implicitlyWait(1 ,TimeUnit.SECONDS);
			//日にちの合致を行なって対象のオブジェクトのみを集めに行く

			//int bmonLessonDayCountint = driver.findElements(By.cssSelector(".flex-no-wrap")).size();
			List<WebElement> lessonCountList = driver.findElements(By.cssSelector(".flex-no-wrap"));

			//List<WebElement>bmnonLessonList = null;

			int bmonLessonListCount = 100;

			int coutLessonDate = 0;

			for(WebElement e : lessonCountList) {
				String lessonDay = e.findElement(By.cssSelector(".smooth-text")).getText();
				System.out.println(lessonDay);

				if(lessonDay.equals(LESSON_DATE)) {
					bmonLessonListCount = coutLessonDate;
				}

				coutLessonDate++;

			}



			//for(int i=0; i < lessonCountList.size(); i++) {
				//String msg = "var box=document.getElementById('scroll-box'); var tags = box.getElementsByClassName('flex-no-wrap');  var leg =  tags[" +(i) + "].getElementsByClassName('column-header'); " +
				//		"var a; for(var i = 0; i < leg.length; i++){ a = leg[i].getElementsByTagName('h3')[0]}; return a.innerHTML;";
				//System.out.println(msg);
				//String bmonLessonDayMuch = (String)js.executeScript(msg);
				//#scroll-box > div > div:nth-child(1) > div > h3
				//#scroll-box > div > div:nth-child(1)
				//driver.findElement(By.cssSelector())

				/*
				if(bmonLessonDayMuch.equals(LESSON_DATE)){

					bmonLessonListCount = i;
					break;
				}
			}
			*/


			//System.out.println("bmnonLessonListの数は:" + bmnonLessonList.size());
			//合致する時間のレッスンをクリックするこの100が1週間のどこの要素と合致しているかをあわしている 2=火曜日みたいな感じ
			if(bmonLessonListCount != 100){
				//for(WebElement element : bmnonLessonList) {
					//時間帯の要素数を割り出す
					/*
					String msg =
							"var box=document.getElementById('scroll-box'); " +
							"var tags = box.getElementsByClassName('flex-no-wrap')[" + bmonLessonListCount + "];" +
							"var leg =  tags.getElementsByClassName('daily-panel'); " +
							"var low =  leg[0].getElementsByClassName('panel'); " +
							"return low.length";

					 */
					//Long hourCountElement = (Long) js.executeScript(msg);

					//該当の要素のの.flex-no-wrapを取得
					List <WebElement> hourList = lessonCountList.get(bmonLessonListCount+1).findElements(By.cssSelector("panel"));

					//Long hourCountElement = driver.findElements(By.cssSelector(".flex-no-wrap"));



					//System.out.println("時間の要素数：" + hourCountElement);
					//この要素数でまわす
					//String bmonTimeStr ="";

					for(WebElement e : hourList) {
						String time = e.findElement(By.cssSelector("tt-time")).getText();
						if(time.substring(0,5).equals(LESSON_TIME)){
							e.findElement(By.cssSelector("a:nth-child(1)")).click();
							System.out.println("タップ押された");
							break;
						}

					}




					/*
					for(int i=0; i < hourList.size(); i++){
						String msg1 =
								"var box=document.getElementById('scroll-box'); " +
								"var tags = box.getElementsByClassName('flex-no-wrap')[" + bmonLessonListCount + "];" +
								"var leg =  tags.getElementsByClassName('daily-panel'); " +
								"var low =  leg[0].getElementsByClassName('panel'); " +
								"var low2 = low[" + i + "].getElementsByClassName('tt-time')[0];" +
								"return low2.innerHTML";
						String hourStringNameHour = (String) js.executeScript(msg1);
						if(hourStringNameHour.substring(0, 5).equals(LESSON_TIME)){

							String msg2 =
									"var box=document.getElementById('scroll-box'); " +
									"var tags = box.getElementsByClassName('flex-no-wrap')[" + bmonLessonListCount + "];" +
									"var leg =  tags.getElementsByClassName('daily-panel'); " +
									"var low =  leg[0].getElementsByClassName('panel'); " +
									"var low2 = low[" + i + "].getElementsByTagName('a')[0];" +
									"low2.click();";
							js.executeScript(msg2);
							break;
						}

					}
					*/


				//}
			}else{
				System.out.println("エラーのはず");
				//driver.quit();
				System.exit(0);
			}

			ExecFeelcycleController.getCapture(driver,"status_move_list");

			//座席ページへの移動完了 waiting-list
			driver.manage().timeouts().implicitlyWait(2 ,TimeUnit.SECONDS);
			bmonsterStudioUrl = driver.getCurrentUrl();



			//満員だとキャン待ち画面になるのでチェック
			System.out.println("targetUrl:" + bmonsterStudioUrl);
			//System.out.println("キャン待ち状態か？人数：" + judgeMent1);
			//System.out.println("座席ページに移動したはず");

			boolean loopFlagBmonster = true;

			while(loopFlagBmonster){
				//String msg1 = "var val = document.getElementsByName('lesson_id'); return val[0].value;";
				//String hiddenCall = driver.findElement(By.name("lesson_id")).getAttribute("value");
				//String studioLessonURL = "https://www.b-monster.jp/reserve/punchbag?lesson_id=" + hiddenCall + "&studio_code=" + LESSON_STATE;
				driver.get(bmonsterStudioUrl);

				int judgeMent1 = driver.findElements(By.cssSelector(".waiting-list")).size();



				ExecFeelcycleController.getCapture(driver,"status_detail_list");

				if(judgeMent1 > 0){

					//ここまで来るということは座席空席なし
					Calendar calendar = Calendar.getInstance();
					// System.out.println(calendar.getTime().toString());
					System.out.println(calendar.getTime().toString() + ": 満席状態1なので再度取得");
					driver.manage().timeouts().implicitlyWait(1 ,TimeUnit.SECONDS);

				} else {
					js = (JavascriptExecutor) driver;

					//boolean firstLoopFlag = true;
					//座席
					int sheetMax = 0;
					switch (LESSON_STATE) {
						case "0001":
							sheetMax = 30;
							break;
						case "0002":
							sheetMax = 54;
							break;
						case "0003":
							sheetMax = 94;
							break;
						case "0006":
							sheetMax = 53;
							break;

						default:
							sheetMax = 30;
							break;
					}


					boolean firstFlagSheets = true;

					for(int i=1; i<=sheetMax; i++) {
						if(firstFlagSheets) {
							switch (LESSON_STATE) {
								case "0001":
									i = 7;
									break;

								case "0006":
									i = 15;
									break;

								default:
									break;
							}
						}
						if(LESSON_STATE.equals("0006") && i > 25 && i < 29) {
							i = 29;
						}
						if(LESSON_STATE.equals("0006") && i > 39 && i < 43) {
							i = 43;
						}

						int judgeMent = 0;


						//ExecFeelcycleController.getCapture(driver,"test1");
						System.out.println("バッグ選択ループへ");
						if(driver.findElements(By.cssSelector("#bag" + i)).size() > 0 ) {
							String msgJudge = "var a; var elm1 = document.getElementById(\"bag" + i + "\"); if(elm1.disabled){ a = false; }else{a = true;} return a;";
							System.out.println(msgJudge);
							boolean abmonJudgeMent = (boolean)js.executeScript(msgJudge);
							System.out.println("判定:" + abmonJudgeMent);
							if(abmonJudgeMent == true) {
								judgeMent = 1;

								String msg = "var bag = document.getElementById('bag" + i + "'); var count = 0;" +
										"if(!bag.disabled) {bag.click(); count = 1; } ;";
								js.executeScript(msg);

								Actions act = new Actions(driver);
								act.sendKeys(Keys.PAGE_DOWN);
							}
						}



						if(judgeMent == 1){
							System.out.println("座席bag=" + i + "をタップ");
							driver.manage().timeouts().implicitlyWait(1 ,TimeUnit.SECONDS);
							driver.findElement(By.cssSelector("#your-reservation > button.btn.btn-large.btn-gray.btn-orange > span")).click();
							System.out.println("最終確認前タップ");
							driver.manage().timeouts().implicitlyWait(1 ,TimeUnit.SECONDS);

							int checkLastButton = driver.findElements(By.xpath("/html/body/div[1]/div/div[2]/div/div[2]/button/span")).size();
							System.out.println("数字：" + checkLastButton);
							driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/div[2]/button/span")).click();

							System.out.println("最終確認タップ");

							driver.manage().timeouts().implicitlyWait(1 ,TimeUnit.SECONDS);

							if(driver.findElements(By.cssSelector("#main-container > div > section > h2")).size() > 0) {
								System.out.println("b-monster:取得完了");
								driver.quit();
							} else {
								System.out.println("b-monster:最終画面で取得NGになりました再度取得Qをいれます");
								getShellCall();

							}
							//driver.quit();
							System.exit(0);
						}
						firstFlagSheets = false;
					}

				}

				//ここまで来るということは座席空席なし
				//Calendar calendar = Calendar.getInstance();
				// System.out.println(calendar.getTime().toString());
				//System.out.println(calendar.getTime().toString() + ": 満席状態2なので再度取得ここだとおかしいので再度取得Qいれます");
				//driver.quit();
				//getShellCall();
				//System.exit(0);

			}
		}
	}

	public static void getShellCall() {
        BufferedReader br = null;
        // 起動するコマンド、引数でProcessBuilderを作る。
        ProcessBuilder pb = new ProcessBuilder("/root/tiritir_script/automationLessson.sh");
        // 実行するプロセスの標準エラー出力を標準出力に混ぜる。(標準エラー出力を標準入力から入力できるようになる)
        pb.redirectErrorStream(true);
        try {
            // プロセス起動
            Process process = pb.start();

            // 起動したプロセスの標準出力を取得して表示する。
            //   標準出力やエラー出力が必要なくても読んどかないとバッファがいっぱいになって
            //   プロセスが止まる(一時停止)してしまう場合がある。
            InputStream is = process.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            while(true) {
                String line = br.readLine();
                if(line == null) {
                    break;
                }
                System.out.println(line);
            }
            // プロセスの終了を待つ。
            int ret = process.waitFor();
            // 終了コードを表示
            System.out.println("ret = " + ret);
        } catch (IOException ex) {
        		System.out.println(ex);
            //Logger.getLogger(TestProcess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
           // Logger.getLogger(TestProcess.class.getName()).log(Level.SEVERE, null, ex);
        	System.out.println(ex);
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    //Logger.getLogger(TestProcess.class.getName()).log(Level.SEVERE, null, ex);
                		System.out.println(ex);
                }
            }
        }
	}



	public static void getCapture(WebDriver driver,String site) throws IOException{
		File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			String fileName = site + "_"  + ".png";
			String rootPath = "/var/www/html/log_images/";
			String URL = "http://133.242.226.123";
			FileUtils.copyFile(srcFile, new File(rootPath + fileName));
			System.out.println("file:" + "  " + URL + "/log_images/" + fileName);
	}

	public static void getLesson(WebDriver driver, WebElement element,
			FeelcycleLessonDto fc) throws InterruptedException, IOException {
		//#day_ > a:nth-child(2)
		//#day__b > a:nth-child(2)

		element.findElement(By.cssSelector(".unit")).click();
		driver.manage().timeouts().implicitlyWait(1 ,TimeUnit.SECONDS);
		//System.out.println("ここまできた3");

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
						driver.manage().timeouts().implicitlyWait(1 ,TimeUnit.SECONDS);
						driver.findElement(
								By.cssSelector(".ticket > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(1) > span:nth-child(2)"))
								.click();
						driver.manage().timeouts().implicitlyWait(1 ,TimeUnit.SECONDS);
					}



					// div.coment:nth-child(9) > table:nth-child(1) >
					// tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(10) >
					// a:nth-child(1)
					// 決定ボタン
					driver.findElement(
							By.xpath("/html/body/div/div/div[9]/table/tbody/tr/td[2]/a"))
							.click();

					driver.manage().timeouts().implicitlyWait(10 ,TimeUnit.SECONDS);
					Thread.sleep(10000);
					ExecFeelcycleController.getCapture(driver,"feelcycle_finish");

					if(driver.findElements(By.cssSelector("#form_back_dark > div:nth-child(4) > table > tbody > tr > td")).size() > 0) {
						String finishText = driver.findElement(By.xpath("/html/body/div/div/div[4]/table/tbody/tr/td/p")).getText();
						System.out.println("finishText:" + finishText);
						if(finishText.equals("下記の内容で予約確定いたしました。")) {
							// 取得完了
							Calendar calendar = Calendar.getInstance();
							System.out.println(calendar.getTime().toString()
									+ ": feelcycle:取得完了");
							System.out.println("feelcycle:取得完了");
							driver.quit();
							System.exit(0);
						} else {
							System.out.println("feelcycle:座席画面までいきましたが処理完了が出来ませんでした。再度取得JOBが起動しました1");
							driver.quit();
							//再取得実行
							getShellCall();
							System.exit(0);
						}
					} else {
						System.out.println("feelcycle:座席画面までいきましたが処理完了が出来ませんでした。再度取得JOBが起動しました2");
						driver.quit();
						//再取得実行
						getShellCall();
						System.exit(0);
					}



				}
				sheetCountNumber++;
			}
		}
		System.out.println("feelcycle:座席画面までいきましたが処理完了が出来ませんでした。再度取得JOBが起動しました");
		driver.quit();
		//再取得実行
		getShellCall();
		System.exit(0);
	}

}
