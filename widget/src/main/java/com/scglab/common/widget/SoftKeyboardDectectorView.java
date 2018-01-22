package com.scglab.common.widget;

/**
 * Created by gusalnim on 2016-05-26.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class SoftKeyboardDectectorView extends View {

	private boolean mShownKeyboard;
	private OnShownKeyboardListener mOnShownSoftKeyboard;
	private OnHiddenKeyboardListener onHiddenSoftKeyboard;

	public SoftKeyboardDectectorView(Context context) {
		this(context, null);
	}

	public SoftKeyboardDectectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Activity activity = (Activity)getContext();
		Rect rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top;
		int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
		int diffHeight = (screenHeight - statusBarHeight) - h;
		if (diffHeight > 100 && !mShownKeyboard) { // 모든 키보드는 100px보다 크다고 가정
			mShownKeyboard = true;
			onShownSoftKeyboard();
		} else if (diffHeight < 100 && mShownKeyboard) {
			mShownKeyboard = false;
			onHiddenSoftKeyboard();
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public void onHiddenSoftKeyboard() {
		if (onHiddenSoftKeyboard != null)
			onHiddenSoftKeyboard.onHiddenSoftKeyboard();
	}

	public void onShownSoftKeyboard() {
		if (mOnShownSoftKeyboard != null)
			mOnShownSoftKeyboard.onShowSoftKeyboard();
	}

	public void setOnShownKeyboard(OnShownKeyboardListener listener) {
		mOnShownSoftKeyboard = listener;
	}

	public void setOnHiddenKeyboard(OnHiddenKeyboardListener listener) {
		onHiddenSoftKeyboard = listener;
	}

	public interface OnShownKeyboardListener {
		void onShowSoftKeyboard();
	}

	public interface OnHiddenKeyboardListener {
		void onHiddenSoftKeyboard();
	}
}
