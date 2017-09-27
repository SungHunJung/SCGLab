package com.scglab.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.scglab.common.adapter.FilterItem;
import com.scglab.common.adapter.FilterRenderer;
import com.scglab.common.listadapter.ListAdapter;
import com.scglab.common.listadapter.RendererFactory;

/**
 * Created by shj on 2017. 9. 21..
 */
public class FilterSampleActivity extends AppCompatActivity {

	private ListAdapter listAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_list);

		initList();
		initFilter();
	}

	private void initList() {
		//rendererFactory
		RendererFactory rendererFactory = new RendererFactory();
		rendererFactory.put(FilterRenderer.class, R.layout.renderer_label);

		//adapter
		listAdapter = new ListAdapter(rendererFactory);

		//recyclerView
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(listAdapter);

		//add items
		for (int index = 0; index < 50; index++) {
			listAdapter.addItem(new FilterItem(String.valueOf(index)));
		}
	}

	private void initFilter() {
		EditText editText = (EditText) findViewById(R.id.txtFilter);
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				listAdapter.getFilter().filter(s);
			}
		});
	}
}
