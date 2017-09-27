package com.scglab.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scglab.common.adapter.UrlItem;
import com.scglab.common.adapter.ImageRenderer;
import com.scglab.common.adapter.LabelItem;
import com.scglab.common.adapter.LabelRenderer;
import com.scglab.common.listadapter.ListAdapter;
import com.scglab.common.listadapter.RendererFactory;

/**
 * Created by shj on 2017. 9. 21..
 */
public class MultipleTypeSampleActivity extends AppCompatActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		//rendererFactory
		RendererFactory rendererFactory = new RendererFactory();
		rendererFactory.put(LabelRenderer.class, R.layout.renderer_label);
		rendererFactory.put(ImageRenderer.class, R.layout.renderer_image);

		//adapter
		ListAdapter listAdapter = new ListAdapter(rendererFactory);

		//recyclerView
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(listAdapter);

		//add items
		for (int index = 0; index < 20; index++) {
			listAdapter.addItem(new LabelItem(String.valueOf(index)));
		}
		listAdapter.addItem(5, new UrlItem("https://www.android.com/static/2016/img/logo-android-green_2x.png"));
		listAdapter.addItem(10, new UrlItem("https://developer.android.com/images/brand/Android_Robot_200.png"));
		listAdapter.addItem(15, new UrlItem("https://play.google.com/intl/en_us/badges/images/play-logo-2x.png"));
	}
}
