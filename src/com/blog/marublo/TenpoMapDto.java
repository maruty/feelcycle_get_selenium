package com.blog.marublo;

import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.Map;

public class TenpoMapDto {

	public Map<String, String> tenpoMap = new LinkedHashMap<>();
	public Map<String, String> programMap = new LinkedHashMap<>();
	public Map<String, String> tenpoTimeMap = new LinkedHashMap<>();

	public TenpoMapDto() {
		tenpoTimeMap.put("07:00", "07:45");
		tenpoTimeMap.put("07:30", "08:15");
		tenpoTimeMap.put("08:00", "08:45");
		tenpoTimeMap.put("08:30", "09:15");
		tenpoTimeMap.put("09:00", "09:45");
		tenpoTimeMap.put("09:30", "10:15");
		tenpoTimeMap.put("10:00", "10:45");
		tenpoTimeMap.put("10:30", "11:15");
		tenpoTimeMap.put("11:00", "11:45");
		tenpoTimeMap.put("11:30", "12:15");
		tenpoTimeMap.put("12:00", "12:45");
		tenpoTimeMap.put("12:30", "13:15");
		tenpoTimeMap.put("13:00", "13:45");
		tenpoTimeMap.put("13:30", "14:15");
		tenpoTimeMap.put("14:00", "14:45");
		tenpoTimeMap.put("14:30", "15:15");
		tenpoTimeMap.put("15:00", "15:45");
		tenpoTimeMap.put("15:30", "16:15");
		tenpoTimeMap.put("16:00", "16:45");
		tenpoTimeMap.put("16:30", "17:15");
		tenpoTimeMap.put("17:00", "17:45");
		tenpoTimeMap.put("17:30", "18:15");
		tenpoTimeMap.put("18:00", "18:45");
		tenpoTimeMap.put("18:30", "19:15");
		tenpoTimeMap.put("19:00", "19:45");
		tenpoTimeMap.put("19:30", "20:15");
		tenpoTimeMap.put("20:00", "20:45");
		tenpoTimeMap.put("20:30", "21:15");
		tenpoTimeMap.put("21:00", "21:45");
		tenpoTimeMap.put("21:30", "22:15");
	}

	public void setTenpoMap(ValueDto valueDto){
		tenpoMap.put(valueDto.name, valueDto.value);
	}

	public void setProgramMap(ValueDto valueDto){
		programMap.put(valueDto.name, valueDto.value);
	}

}
