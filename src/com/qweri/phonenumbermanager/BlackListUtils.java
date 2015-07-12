package com.qweri.phonenumbermanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BlackListUtils {
	public static final String SP_BLOCK_NUMBER = "black_list_number";
	public static final String SPLIT = "+";

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
		int startIndex = blackNumbers.indexOf(number) - 1;
		String result = blackNumbers.substring(0, startIndex)+blackNumbers.substring(startIndex+12);
		setBlockNumber(context, result);
	}

	public static boolean isNumberInBlackList(Context context, String number) {
		String blackListNames = getBlackListNumbers(context);
		if(blackListNames.contains(number)){
			return true;
		}
		return false;
	}
}
