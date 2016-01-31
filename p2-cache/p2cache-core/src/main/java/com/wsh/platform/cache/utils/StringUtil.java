package com.wsh.platform.cache.utils;


public class StringUtil {
	public static Boolean isNullOrEmpty(Object o){
		if(o == null)
			return true;
		return o.toString().equals("");
	}
}
