package com.scglab.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SampleActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		((TextView) findViewById(R.id.txtConsole)).setText(Tester.say() + Tester.sayHo());

//		ListItem
	}
}
