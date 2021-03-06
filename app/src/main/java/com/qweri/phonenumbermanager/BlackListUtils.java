package com.qweri.phonenumbermanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BlackListUtils {
	public static final String SP_BLOCK_NUMBER = "black_list_number";
	public static final String SPLIT = ",";
	
	public static final String SP_BLOCK_UNKNOW = "block_unknow";
	public static final String SP_BLOCK_ALL = "block_all";
	public static final String SP_RETURN_VOICE = "return_voice";
	public static final String SP_AUTO_BLOCK = "is_auto_block";

	private static void setBlockNumber(Context context, String number) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		sharedPreferences.edit().putString(SP_BLOCK_NUMBER, number).commit();
	}

	public static String getBlackListNumbers(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPreferences.getString(SP_BLOCK_NUMBER, "");
	}
	
	public static void resetNumber(Context context) {
		setBlockNumber(context, "");
	}
	public static void addNumber(Context context, String number) {
		String blackNumbers = getBlackListNumbers(context);
		setBlockNumber(context, blackNumbers+SPLIT+number);
//		Log.d("tttttt", number+","+getBlackListNumbers(context));
	}
	
	public static void deleteNumber(Context context, String number){
		
		String blackNumbers = getBlackListNumbers(context);
		Log.d("test", blackNumbers+"----"+number);
		int startIndex = blackNumbers.indexOf(number);
		if(startIndex == -1) {
			return;
		}
		int endIndex = -1;
		for(int i = startIndex;i<blackNumbers.length();i++ ) {
			if((blackNumbers.charAt(i)+"").equals(SPLIT)){
				endIndex = i;
				break;
			}
		}
		String result = blackNumbers.substring(0, startIndex);
		if(endIndex != -1){
			result += blackNumbers.substring(endIndex+1);
		}
		Log.d("test", result);
		setBlockNumber(context, result);
	}

	public static boolean isNumberInBlackList(Context context, String number) {
		if (number == null) {
			return false;
		}
		String blackListNames = getBlackListNumbers(context);
		if(blackListNames != null && !"".equals(blackListNames) && blackListNames.contains(number)){
			return true;
		}
		return false;
	}
	
	public static void setReturnVoice(Context context, String number) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().putString(SP_RETURN_VOICE, number).commit();
	}
	
	public static String getReturnVoice(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(SP_RETURN_VOICE, "");
	}
	
	public static void setInterceptAllNumber(Context context, boolean result){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SP_BLOCK_ALL, result).commit();
	}
	
	public static boolean isInterceptAllNumber(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getBoolean(SP_BLOCK_ALL, false);
	}
	
	public static void setInterceptUnknow(Context context, boolean result) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SP_BLOCK_UNKNOW, result).commit();
	}
	public static boolean isInterceptUnknow(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getBoolean(SP_BLOCK_UNKNOW, false);
	}
	
	public static void setAutoBlockOpen(Context context, boolean result) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SP_AUTO_BLOCK, result).commit();
	}
	public static boolean isAutoBlockOpen(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getBoolean(SP_AUTO_BLOCK, false);
	}
}
