package com.scglab.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scglab.common.adapter.LabelItem;
import com.scglab.common.adapter.LabelRenderer;
import com.scglab.common.listadapter.FlexAdapter;
import com.scglab.common.listadapter.RendererFactory;
import com.scglab.common.widget.CircleImageView;
import com.scglab.common.widget.NetworkImageView;
import com.scglab.common.widget.RoundImageView;

/**
 * Created by shj on 2017. 9. 21..
 */
public class RoundImageViewActivity extends AppCompatActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_round_image_view);

		RoundImageView roundImageView = (RoundImageView) findViewById(R.id.imgTest);
		roundImageView.setImageUrl("http://www.fnordware.com/superpng/pnggrad8rgb.png");

		CircleImageView circleImageView = (CircleImageView) findViewById(R.id.imgTest2);
		circleImageView.setImageUrl("http://www.fnordware.com/superpng/pnggrad8rgb.png");
	}
}
