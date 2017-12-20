package com.scglab.common.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

/**
 * Created by sh on 2016. 3. 28..
 */
public class InputHelper {

	public static boolean isEmpty(TextView textView) {
		return null == textView || null == textView.getText() || 0 == textView.getText().length();
	}

	public static boolean isPassword(final String input) {
		int count = 2;
		if (Pattern.matches("^(?=.*[a-z]+).{8,16}$", input)) count--;
		if (Pattern.matches("^(?=.*[A-Z]+).{8,16}$", input)) count--;
		if (Pattern.matches("^(?=.*[0-9]+).{8,16}$", input)) count--;
		if (Pattern.matches("^(?=.*[!@#$%^*+=-]).{8,16}$", input)) count--;
		return count <= 0;
	}

	public static void keyboardHide(Activity activity) {
		View view = activity.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static void keyboardHide(Context context, View v){
		if(context != null){
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	public static void keyboardShow(Activity activity, EditText editText) {
		if (editText.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
		}
	}
}