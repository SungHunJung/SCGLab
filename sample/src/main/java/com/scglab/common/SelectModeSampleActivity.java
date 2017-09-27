package com.scglab.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.scglab.common.adapter.LabelItem;
import com.scglab.common.adapter.SelectModeRenderer;
import com.scglab.common.listadapter.FlexAdapter;
import com.scglab.common.listadapter.OnItemClickEventHandler;
import com.scglab.common.listadapter.RendererFactory;

import java.util.List;

/**
 * Created by shj on 2017. 9. 21..
 */
public class SelectModeSampleActivity extends AppCompatActivity {

	private FlexAdapter flexAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		//rendererFactory
		RendererFactory rendererFactory = new RendererFactory();
		rendererFactory.put(SelectModeRenderer.class, R.layout.renderer_select_mode);

		//adapter
		flexAdapter = new FlexAdapter(rendererFactory);
		flexAdapter.setOnItemClickEventHandler(onItemClickEventHandler);

		//recyclerView
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(flexAdapter);

		//add items
		for (int index = 0; index < 20; index++) {
			flexAdapter.addItem(new LabelItem(String.valueOf(index)));
		}
	}

	private Toast lastToast = null;

	private void showToast(String message) {
		if (null != lastToast) lastToast.cancel();

		lastToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		lastToast.show();
	}

	private final OnItemClickEventHandler onItemClickEventHandler = new OnItemClickEventHandler() {

		@Override
		public void onItemClick(Object item) {
			showToast("click : " + item.toString());

			if (flexAdapter.isSelectMode()) flexAdapter.toggleSelectItem(item);
		}

		@Override
		public void onItemLongClick(Object item) {
			boolean mode = !flexAdapter.isSelectMode();
			List<Object> selectedItemList = flexAdapter.getSelectedItemList();
			flexAdapter.setSelectMode(mode, true);

			showToast("SelectMode : " + mode + " / " + selectedItemList.size() + " items selected");
		}

		@Override
		public void onChildViewClick(Object item, int viewId) {
			showToast("child click : " + item.toString() + "/" + viewId);

			if (viewId == R.id.cbSelect && flexAdapter.isSelectMode()) flexAdapter.toggleSelectItem(item);
		}

		@Override
		public void onChildViewLongClick(Object item, int viewId) {
			showToast("child long click : " + item.toString() + "/" + viewId);
		}
	};
}
