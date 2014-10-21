package com.thepegeekapps.easyplanner.parser;

import android.text.TextUtils;

import com.thepegeekapps.easyplanner.api.ApiData;

public class ParserFactory {
	
	public static ApiParser getParser(String command, String method) {
		if (TextUtils.isEmpty(command)) {
			return null;
		} else if (ApiData.AUTHENTICATE.equalsIgnoreCase(command)) {
			return new LoginParser();
		} else if (ApiData.USER.equalsIgnoreCase(command)) {
			return new LoginParser();
		} else if (ApiData.CLASSES.equalsIgnoreCase(command)) {
			if (ApiData.GET.equalsIgnoreCase(method)) {
				return new ClasListParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.ACTIVITY.equalsIgnoreCase(command)) {
			if (ApiData.GET.equalsIgnoreCase(method)) {
				return new ActivityListParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.RESOURCE.equalsIgnoreCase(command)) {
			if (ApiData.GET.equalsIgnoreCase(method)) {
				return new ResourceListParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.HOMEWORK.equalsIgnoreCase(command)) {
			if (ApiData.GET.equalsIgnoreCase(method)) {
				return new HomeworkListParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.TASK.equalsIgnoreCase(command)) {
			if (ApiData.GET.equalsIgnoreCase(method)) {
				return new TaskListParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.DATA.equalsIgnoreCase(command)) {
			if (ApiData.GET.equalsIgnoreCase(method)) {
				return new DayDataParser();
			} else {
				return new SimpleParser();
			}
		} else {
			return null;
		}
	}

}
