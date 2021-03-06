/**********************************************************************
 * Director's Command Center - This displays a list of options specifically
 * for director/management use
 *
 * @author Chris Crowell <crowelch@mail.uc.edu>
 * @version 1.0
 *
 * YATE Spring 2013
 *********************************************************************/

package com.example.dcc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dcc.admin.AdminActivity;
import com.example.dcc.fragment.AdminSearchFragment;
import com.example.dcc.fragment.CreateActionItemFrag;
import com.example.dcc.helpers.OnButtonSelectedListener;
import com.example.dcc.surveys.CreateSurvey;
import com.example.dcc.surveys.ManageSurvey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import utilities.StartDateDownloader;

public class LaunchActivityFrag extends Fragment implements OnClickListener {

    public static String startdate = "";
    public static String today;

    private OnButtonSelectedListener listener;

    Activity activity;

    private EditText passwordBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_launch_frag,
                container, false);
        activity = getActivity();

		/* Check if directory exists, if not create it */
		/* Call functions to get start and end dates */
        //setDefaults();

		/* Set up buttons */
        Button metaSearchButton = (Button) view.findViewById(R.id.meta_button);
        metaSearchButton.setOnClickListener(this);
        Button createSurveyButton = (Button) view.findViewById(R.id.survey_button);
        createSurveyButton.setOnClickListener(this);
        Button manageSurveyButton = (Button) view.findViewById(R.id.manage_survey_button);
        manageSurveyButton.setOnClickListener(this);
        Button adminButton = (Button) view.findViewById(R.id.admin_button);
        adminButton.setOnClickListener(this);
        Button createAI = (Button) view.findViewById(R.id.createaction);
        createAI.setOnClickListener(this);
        Button search = (Button) view.findViewById(R.id.meta_button);
        search.setOnClickListener(this);
        return view;
    }

    /*
     * Check if directory /dcc, and files startdate.txt and enddate.txt exist,
     * and create them if they don't. Set default start and end date search
     * parameters.
     */
    private void setDefaults() {
//        today = getToday();
//        startdate = setStartDate();
//        FileWriter filewriter = null;
//        String path = Environment.getExternalStorageDirectory().getPath();
//        File f = new File(path + "/dcc");
//        File checkKeyword = new File(f + "/keywords.txt");
//        if (!f.exists()) {
//            f.mkdir();
//        }
//        if (!checkKeyword.exists()) {
//            try {
//                checkKeyword.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            filewriter = new FileWriter(f + "/startdate.txt");
//            filewriter.append(startdate);
//            filewriter.flush();
//            filewriter.close();
//            filewriter = new FileWriter(f + "/enddate.txt");
//            filewriter.append(today);
//            filewriter.flush();
//            filewriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /* Get today's date and format it to YYYY-MM-DD */
    private String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(new Date());
    }

    /* Set the start date of the program by getting the data from the server */
    private String setStartDate() {
        String programStart = "";
        StartDateDownloader getDate = new StartDateDownloader();
        getDate.execute();
        try {
            programStart = getDate.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return programStart;
    }

    /*
     * Creates a popup dialog using alertbuilder to prevent access to
     * AdminActivitywithout the password
     */
    private void passwordPopup() {
        LayoutInflater factory = LayoutInflater.from(activity);
        final View popup = factory.inflate(R.layout.admin_password_popup, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder
                .setCancelable(true)
                .setTitle("Enter your Password:")
                .setView(popup)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Enter",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                passwordBox = (EditText) popup
                                        .findViewById(R.id.password_box);
                                if (isCorrect(passwordBox.getText().toString())) {
                                    Intent intent = new Intent(
                                            activity,
                                            AdminActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(activity,
                                            "Incorrect password",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

        AlertDialog alertdialog = alertBuilder.create();
        alertdialog.show();
    }

    /* Checks password */
    private boolean isCorrect(String password) {
        if (password.matches("bobbill")) {
            return true;
        }
        return false;
    }

    /* Make the buttons work */
    public void onClick(View v) {
        Intent intent = new Intent();
        FragmentManager manager = activity.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment newer;

        switch (v.getId()) {
            case R.id.meta_button:
                newer = new AdminSearchFragment();
                listener.launchFragment(newer);
                break;
            case R.id.admin_button:
                passwordPopup();
                break;
            case R.id.survey_button:
                intent.setClass(activity, CreateSurvey.class);
                startActivity(intent);
                break;
            case R.id.manage_survey_button:
                intent.setClass(activity, ManageSurvey.class);
                activity.finish();
                startActivity(intent);
                break;
            case R.id.createaction:
                createActionItems();
                break;
        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if(activity instanceof OnButtonSelectedListener){
            listener = (OnButtonSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString() +
                    "must implement MyListFragment.OnButtonSelectedListener");
        }
    }

    public void createActionItems(){
        Fragment newer = new CreateActionItemFrag();
        listener.launchFragment(newer);
    }
    public void onBackPressed() {
        activity.finish();
    }
}
