package com.example.dcc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Login extends Activity implements OnClickListener {

	//Create buttons Globally so they are available to all methods
	Button newsB;
	Button loginB;
	Button calB;
	Button mailB;
	Button photoB;
	Button reportB;
	Button actionB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		//These are the buttons on the left side of the screen.
		//The have been initialized in order.
		newsB = (Button) findViewById(R.id.button1);
		loginB = (Button) findViewById(R.id.button2);
		calB = (Button) findViewById(R.id.button3);
		mailB = (Button) findViewById(R.id.button4);
		photoB = (Button) findViewById(R.id.button5);
		reportB = (Button) findViewById(R.id.button6);
		actionB = (Button) findViewById(R.id.button7);

		//Here the listener for each button that allows actions is set.
		newsB.setOnClickListener(this);
		loginB.setOnClickListener(this);
		calB.setOnClickListener(this);
		mailB.setOnClickListener(this);
		photoB.setOnClickListener(this);
		reportB.setOnClickListener(this);
		actionB.setOnClickListener(this);
	}

	public void onClick(View v) {
		//this switch listens for any and all click actions in the app
		//each case is a button in the menu.
		switch (v.getId()) {
		case R.id.button1:
			startActivity(new Intent(Login.this, MainActivity.class));
			break;
		case R.id.button2:
			startActivity(new Intent(Login.this, MainActivity.class));
			break;
		case R.id.button3:
			startActivity(new Intent(Login.this, MainActivity.class));
			break;
		case R.id.button4:
			startActivity(new Intent(Login.this, MainActivity.class));
			break;
		case R.id.button5:
			startActivity(new Intent(Login.this, MainActivity.class));
			break;
		case R.id.button6:
			startActivity(new Intent(Login.this, MainActivity.class));
			break;
		case R.id.button7:
			startActivity(new Intent(Login.this, MainActivity.class));
			break;
		}
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * newsB loginB calB mailB photoB reportB actionB
	 */

}