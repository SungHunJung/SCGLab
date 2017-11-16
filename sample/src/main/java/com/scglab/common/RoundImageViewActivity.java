package com.scglab.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.scglab.common.widget.NetworkImageView;

/**
 * Created by shj on 2017. 9. 21..
 */
public class RoundImageViewActivity extends AppCompatActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_round_image_view);

		NetworkImageView roundImageView = (NetworkImageView) findViewById(R.id.imgTest2);
		roundImageView.setImageUrl("http://c.ymcdn.com/sites/www.ibpa-online.org/resource/resmgr/images/checklist/copyright-page-sample-5.png");
	}
}