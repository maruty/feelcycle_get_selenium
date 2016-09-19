package com.blog.marublo;

public class FeelcycleLessonDto {
	//空席状況
	public boolean islessonStatus;
	//レッスン名
	public String lessonName;
	//レッスン日
	public String lessonDate;
	//レッスン時間
	public String lessonTime;
	//インストラクター名
	public String lessonInstructor;

	//コンストラクタ
	public FeelcycleLessonDto() {

	}


	/*ゲッターセッター*/

	public boolean isIslessonStatus() {
		return islessonStatus;
	}
	public String getLessonName() {
		return lessonName;
	}
	public String getLessonDate() {
		return lessonDate;
	}
	public String getLessonTime() {
		return lessonTime;
	}
	public String getLessonInstructor() {
		return lessonInstructor;
	}
	public void setIslessonStatus(boolean islessonStatus) {
		this.islessonStatus = islessonStatus;
	}
	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}
	public void setLessonDate(String lessonDate) {
		this.lessonDate = lessonDate;
	}
	public void setLessonTime(String lessonTime) {
		this.lessonTime = lessonTime;
	}
	public void setLessonInstructor(String lessonInstructor) {
		this.lessonInstructor = lessonInstructor;
	}
}
