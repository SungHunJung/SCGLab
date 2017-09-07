package com.scglab.common.listadapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * Created by sh on 2016. 1. 5..
 */
public abstract class SearchViewHolder<T extends ListItem> extends ItemViewHolder<T> implements TextWatcher {

	private EditText txtInput;

	public SearchViewHolder(View view, EditText editText) {
		super(view);
		txtInput = editText;
		txtInput.addTextChangedListener(this);
	}

	public final ListAdapter<T> getListAdapter() {
		return listAdapter;
	}

	public final EditText getTxtInput() {
		return txtInput;
	}

	public abstract void onFilter(Editable s);

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		onFilter(s);
	}
}
