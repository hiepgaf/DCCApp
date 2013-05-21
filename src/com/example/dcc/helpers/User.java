package com.example.dcc.helpers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.os.AsyncTask;
import com.example.dcc.helpers.mysql.GetInputStreamTask;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import com.example.dcc.R;
import com.example.dcc.fragment.MemberDetailFragment;
import com.example.dcc.helpers.hacks.DCCCookieStore;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

public class User implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = -5285360029686080283L;
    private static final String LOG = "User Object";
    private String handle,email,name,cookies,project,project2;
    public int id;
    private Bitmap image;
    private String imageURL;
    private String phone;
    private List<Friend> friends;
    private int ID;


    public User(){
        this.name = "";
        this.cookies = "";
        this.image = null;
        this.friends = new ArrayList<Friend>();
        int ID = 0;
    }

    public void setImageURL(String url){
        this.imageURL = url;
    }

    public void displayData(){
        Log.i(LOG, name);
        Log.i(LOG, handle);
        Log.i(LOG, cookies);
    }

    public String getCookies(){
        return cookies;
    }

    public void setCookie(String cookie){
        this.cookies = cookie;
    }
    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURL(Links link){
        String base = "/members/"+handle+"/";
        String url = "";
        switch(link){
            case PROFILE:
                url = "/intern/"; break;
            case MESSAGES:
                url = base+"messages/"; break;
            case MEDIA:
                url = base+"media/"; break;
            case FRIENDS:
                url = base+"friends/"; break;
            case GROUPS:
                url = base+"groups/"; break;
            case MEMBERS:
                url = "/members/"; break;
            case SETTINGS:
                url = "/settings/"; break;
            default:
                url = "/intern/"; break;
        }
        Log.e("Link", url);
        return url;
    }

    public Bitmap getImage() {
        if(image==null){
            setImage(imageURL);
        }
        return image;
    }

    public void setImage(String img) {

        try{
            String uri = "/DCC/getUserGravitar.php?email=" + email;

            StringBuilder sb = new StringBuilder();
            InputStream in = new GetInputStreamTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri).get();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"iso-8859-1"),8);

            String s;
            while((s = reader.readLine())!=null){
                sb.append(s);
            }
            setImageURL(sb.toString());
            in.close();

            new GetImageTask().execute().get();

        }catch(Exception e){
            Log.e("EH", e.getLocalizedMessage());
        }

    }

    public void addFriend(Friend f) {
        friends.add(f);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getProject2() {
        return project2;
    }

    public void setProject2(String project2) {
        this.project2 = project2;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n"+name);
        sb.append("<br/>"+project);
        return sb.toString();
    }

    public void launchWindow(Activity activity) {
        MemberDetailFragment detailFrag = new MemberDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("member", this);
        detailFrag.setArguments(bundle);

        FragmentManager manager = activity.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment old = ObjectStorage.getFragment(R.id.fragmentcontainerright);
        ObjectStorage.setFragment(R.id.fragmentcontainerright, detailFrag);
        transaction.replace(R.id.fragmentcontainerright, detailFrag);

        transaction.commit();
    }

    public class GetImageTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            try{
                URL url = new URL(imageURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                image = BitmapFactory.decodeStream(conn.getInputStream());
                conn.disconnect();
            }catch(Exception e){
                return null;
            }
            return null;
        }
    }
}
