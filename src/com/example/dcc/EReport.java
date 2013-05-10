package com.example.dcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Submission interface for eReports
 * 
 * @author Chris Crowell <crowelch@mail.uc.edu>
 */

public class EReport extends Activity implements OnClickListener {

	private String url = "http://storage.virtualdiscoverycenter.net/projectmorpheus/dcc/save.php";
	private String popupText = "Reports can not be submitted until sudent info is set!\n\nFill this out now?";

	private EditText Sunday;
	private EditText Monday;
	private EditText Tuesday;
	private EditText Wednesday;
	private EditText Thursday;
	private EditText Friday;
	private EditText Saturday;
	
	private Button SendButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ereport);

		Sunday = (EditText) findViewById(R.id.sunday);
		Monday = (EditText) findViewById(R.id.monday);
		Tuesday = (EditText) findViewById(R.id.tuesday);
		Wednesday = (EditText) findViewById(R.id.wednesday);
		Thursday = (EditText) findViewById(R.id.thursday);
		Friday = (EditText) findViewById(R.id.friday);
		Saturday = (EditText) findViewById(R.id.saturday);

		SendButton = (Button) findViewById(R.id.sendEreportButton);
		SendButton.setOnClickListener(this);

		File f = new File(Environment.getExternalStorageDirectory()
				+ "/enotebook/InternalStorage.txt");
		if (!f.exists()) {
			setDefaultsPopup();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Intent intent = new Intent(this, SetDefaults.class);
			startActivity(intent);
			return true;
		}
		return false;
	}

	/* 
	 * When the send button is pressed, get the student ID
	 * from InternalSorage.txt and send the file to the server
	 */
	public void onClick(View v) {
		File f = new File(Environment.getExternalStorageDirectory()
				+ "/enotebook/InternalStorage.txt");
		if (f.exists()) {
			String studentID = "";
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(
						"sdcard/eNotebook/InternalStorage.txt")));
				br.readLine(); // Skip Title
				studentID = br.readLine(); // Get Student ID#
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Uploader uploader = new Uploader();
			uploader.execute(url, studentID);
			finish();
		} else {
			setDefaultsPopup();
		}
	}

	private String getEditText(EditText edittext) {
		return edittext.getText().toString();
	}

	/*
	 * Creates a popup dialog using alertbuilder to ask the user to fill out
	 * their information in SetDefaults.txt if InternalStorage.txt does not exit
	 */
	private void setDefaultsPopup() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View popup = factory.inflate(R.layout.default_popup, null);
		TextView textview = (TextView) popup
				.findViewById(R.id.defaultPopupText);
		textview.setText(popupText);
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder
				.setCancelable(true)
				.setTitle("Student Info")
				.setView(popup)
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				})
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(EReport.this,
										SetDefaults.class);
								startActivity(intent);
							}
						});

		AlertDialog alertdialog = alertBuilder.create();
		alertdialog.show();
	}

	public class Uploader extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			/* Uplaod to server */
			try {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("id", params[1]));
				nameValuePairs.add(new BasicNameValuePair("type", "ereport"));
				nameValuePairs.add(new BasicNameValuePair("sunday",
						getEditText(Sunday)));
				nameValuePairs.add(new BasicNameValuePair("monday",
						getEditText(Monday)));
				nameValuePairs.add(new BasicNameValuePair("tuesday",
						getEditText(Tuesday)));
				nameValuePairs.add(new BasicNameValuePair("wednesday",
						getEditText(Wednesday)));
				nameValuePairs.add(new BasicNameValuePair("thursday",
						getEditText(Thursday)));
				nameValuePairs.add(new BasicNameValuePair("friday",
						getEditText(Friday)));
				nameValuePairs.add(new BasicNameValuePair("saturday",
						getEditText(Saturday)));
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(params[0]);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				response.getEntity();
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection " + e.toString());
			}

			/* Save to local file */

			/* Write to local file */
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("MMddyy", Locale.US);
				BufferedReader br = new BufferedReader(new FileReader(new File(
						"sdcard/eNotebook/InternalStorage.txt")));
				br.readLine(); // Skip Title
				String studentID = br.readLine(); // Get Student ID#
				br.close();
				String filename = "/enotebook/ereports/" + studentID + "_"
						+ sdf.format(new Date()) + "_ereport.txt";
				FileWriter internal = new FileWriter(
						Environment.getExternalStorageDirectory() + filename);
				internal.append(getEditText(Sunday) + "\n"
						+ getEditText(Monday) + "\n" + getEditText(Tuesday)
						+ "\n" + getEditText(Wednesday) + "\n"
						+ getEditText(Thursday) + "\n" + getEditText(Friday)
						+ "\n" + getEditText(Saturday));
				internal.flush();
				internal.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			/* Update complete toast */
			Toast.makeText(getApplicationContext(), "Report Sent!",
					Toast.LENGTH_SHORT).show();
		}

	}

}