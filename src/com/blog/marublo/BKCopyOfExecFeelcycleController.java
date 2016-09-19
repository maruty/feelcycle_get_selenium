package com.blog.marublo;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class BKCopyOfExecFeelcycleController {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		final String LESSON_NAME = "BSW Hit 6";
		final String LESSON_DATE = "9/18(日)";
		final String LESSON_TIME = "17:00";
		final String LESSON_STATE = "池袋（IKB）";

		final String USER_ID = "yanagisawa.trade@gmail.com";
		final String USER_PASS = "yutaka467";


		WebDriver driver = new FirefoxDriver();
		//ログインフォームからスタート
		System.out.println("Feelcycle：Login");
		driver.get("https://www.feelcycle.com/feelcycle_reserve/mypage.php");

		driver.findElement(By.name("login_id")).sendKeys(USER_ID);
		driver.findElement(By.name("login_pass")).sendKeys(USER_PASS);

		driver.findElement(By.cssSelector(".submit_b")).click();
		System.out.println("Feelcycle：ログイン成功");

		System.out.println("Feelcycle：座席予約");
		driver.get("https://www.feelcycle.com/feelcycle_reserve/reserve.php");

		/*店舗選択
		 *
		 */
		//selectタグを取得
		Select selectList = new Select(driver.findElement(By.name("tenpo")));
		//選択する項目をテキストで指定
		selectList.selectByVisibleText(LESSON_STATE);

		Thread.sleep(3000);

		//実際の予約連アタ処理
		/*
		 * 全ての当週のレッスン要素をWebElementに格納してレッスンDTOを作成する
		 */
		//day__bとday_の構成で作られているので取得方法は day__bで全部回した後 day_をまわす感じ
		//#day__b #title_week_b
		String firstDay = driver.findElement(By.cssSelector("#day__b >  #title_week_b")).getText();

		System.out.println(firstDay);

		while(true){
			List <FeelcycleLessonDto> feelcycleLessonDtoList = new ArrayList<>();
			List<WebElement> firstDayElementList = new ArrayList<>();
			firstDayElementList = driver.findElements(By.cssSelector("#day__b > a"));
			
			//1日目のDTO作成
			for(WebElement e : firstDayElementList){
				//レッスン状態を確認する
				int unitPastCount = e.findElements(By.cssSelector(".unit_past")).size();
				int unitCount = e.findElements(By.cssSelector(".unit")).size();
				int unitReservedCount = e.findElements(By.cssSelector(".unit_reserved")).size();
				FeelcycleLessonDto feelcycleDto = new FeelcycleLessonDto();
				if(unitCount > 0 && unitPastCount == 0 && unitReservedCount == 0){
					feelcycleDto.setLessonName(e.findElement(By.cssSelector(".unit > p:nth-child(2)")).getText());
					feelcycleDto.setLessonDate(firstDay);
					feelcycleDto.setLessonTime(e.findElement(By.cssSelector(".start")).getText());
					feelcycleDto.setLessonInstructor(e.findElement(By.cssSelector(".time")).getText());
					feelcycleDto.setIslessonStatus(true);
					
					
					//条件に合致している場合クリックする
					String perseTime =  feelcycleDto.getLessonTime().substring(0, 5);

					if(firstDay.equals(LESSON_DATE) && perseTime.equals(LESSON_TIME) && feelcycleDto.lessonName.equals(LESSON_NAME)){
						e.findElement(By.cssSelector(".unit")).click();

						//座席のページに入る
						int sheetCount = driver.findElements(By.cssSelector(".seat_map > div")).size();
						//0番目と1番目はインストラクターと入り口だからそれ以降でぶんまわす
						if(sheetCount > 2){
							for(int i=2; i < sheetCount; i++){
								int sheetCountNumber = 1;
								String countString = "";
								if(sheetCountNumber < 10){
									countString = "0" + sheetCountNumber;
								}else{
									countString = String.valueOf(sheetCountNumber);
								}
								int emptySheet = driver.findElements(By.cssSelector(".no" + countString + " > .set" )).size();
								//setが1個でもオブジェクトがあるととられている
								if(emptySheet < 1){
									//.no01 > a:nth-child(1)
									driver.findElement(By.cssSelector(".no" + countString + " > a" )).click();

									//div.coment:nth-child(9) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(10) > a:nth-child(1)
									//決定ボタン
									driver.findElement(By.cssSelector("div.coment:nth-child(9) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(10) > a:nth-child(1)")).click();
									Thread.sleep(3000);
									//取得完了
									System.out.println("取得完了");
									System.exit(0);
								}
								sheetCountNumber++;
							}
						}
					}
				}
				//作られたdtoと逐次比較
				System.out.println("処理確認");

			}

			System.out.println("処理確認");
		}

	}

}


