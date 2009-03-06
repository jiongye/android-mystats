package com.sanbit.android.mystats;

import java.util.Calendar;

public class Helper {
	
	public static String timeToString(Integer seconds) {
		String time = " (";
		int hours = seconds / 3600;
		int mins = (seconds % 3600) / 60;
		int sec = seconds % 60;
		time += hours + "h " + mins + "m " + sec + "s)";
		return time;
	}
	
	public static String timeAgo(Long timestamp){
		Long now = Calendar.getInstance().getTime().getTime();
		Long seconds = (now - timestamp) / 1000;
		
		String time = " (";
		Long day = seconds / 86400;
		Long hours = (seconds % 86400) / 3600;
		//Long mins = (seconds % 3600) / 60;
		//Long sec = seconds % 60;
		time += day + "d " + hours + "h " + " ago)";
		return time;
	}
	
	public static String timeStampToString(Long timestamp){
		
		Long seconds = timestamp / 1000;
		
		String time = "";
		Long day = seconds / 86400;
		Long hours = (seconds % 86400) / 3600;
		time += day + "d " + hours + "h";
		return time;
	}
	
	public static String[] phoneType(){
		String[] phoneTypes = new String[]{"Home", "Mobile", "Work", "Work Fax", "Home Fax", "Pager", "Other"};
		
		return phoneTypes;
	}
	
	public static String[] contactMethodKind(){
		String[] kind = new String[]{"Email", "Postal"};
		
		return kind;
	}
	
	public static String[] contactMethodType(Integer type){
		String[] emailTypes = new String[]{"Home", "Work", "Primary", "Other"};
		String[] postalTypes = new String[]{"Postal", "Home", "Work", "Other"};
		
		if (type == 1){
			return emailTypes;
		}else {
			return postalTypes;
		}
	}

}


