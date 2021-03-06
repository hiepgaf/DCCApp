package com.example.dcc.helpers.mysql;

import android.os.AsyncTask;
import android.util.Log;

import com.example.dcc.helpers.ActionItem;
import com.example.dcc.helpers.EDaily;
import com.example.dcc.helpers.News;
import com.example.dcc.helpers.ObjectStorage;
import com.example.dcc.helpers.User;
import com.example.dcc.helpers.hacks.DCCArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

//TODO:Move json parsing to the json object by passing in the JSONObject.
/**
 * This class manages the communication between the application and sql queries.
 * Primarily parses database information from JSon objects into Lists of items.
 * Created by Harmon on 5/17/13.
 */
public class MySQLQuery {

    /**
     * Returns an input stream from the designated URL that contains the contents of the request.
     * @param url
     * @return
     */
    private synchronized static InputStream getInputStream(String url){
        try {
            return new GetInputStreamTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url).get();
        } catch (InterruptedException e) {
            Log.e("dcc.MySQLQuery", e.getMessage());
        } catch (ExecutionException e) {
            Log.e("dcc.MySQLQuery", e.getMessage());
        }
        return null;
    }

    public synchronized static List<News> getNews(String url){
        JSONArray jNews = (JSONArray)getArray(url);
        List<News> newsList = new ArrayList<News>();

        for(int i = 0; i < jNews.length(); i++){
            try {
                JSONObject temp = jNews.getJSONObject(i);
                News n = new News();

                int authorID = Integer.parseInt(temp.getString("post_author"));
                User u;
                if(ObjectStorage.hasUser(authorID)){
                    u = ObjectStorage.getUser(authorID);
                }else{
                    u = convertUser (((JSONArray)getArray("/DCC/getUserById.php?id="+authorID)).getJSONObject(0));
                    ObjectStorage.setUser(authorID, u);
                }
                n.setPublisher(u);
                n.setPubdate(temp.getString("post_date"));
                n.setTitle(temp.getString("post_title"));
                n.setText(temp.getString("post_content"));
                newsList.add(n);
            } catch (JSONException e) {
                Log.e("dcc.MySQLQuery", e.getMessage());
            }
        }

        //Need to reverse the list for correct view
        Collections.reverse(newsList);

        return newsList;
    }

    private synchronized static User convertUser(JSONObject jUser){
        try{
            User user = new User();
            user.setID(Integer.parseInt(jUser.getString("ID")));
            user.setName(jUser.getString("display_name"));
            user.setEmail(jUser.getString("user_email"));
            user.setHandle(jUser.getString("user_login"));
            user.setProject(jUser.getString("project"));
            user.setProject2(jUser.getString("project2"));
            return user;
        } catch (JSONException e) {
            Log.e("dcc.MySQLQuery", e.getMessage());
        }
        return null;
    }

    public synchronized static User validateUser(String url){

        JSONObject jUser;
        jUser = (JSONObject)getArray(url);

        User user = ObjectStorage.getUser();

        try{
            Log.e("test", jUser.getString("display_name"));
            user.setID(Integer.parseInt(jUser.getString("ID")));
            user.setName(jUser.getString("display_name"));
            user.setEmail(jUser.getString("user_email"));
            user.setHandle(jUser.getString("user_login"));
            user.setProject(jUser.getString("project"));
            user.setProject2(jUser.getString("project2"));

            StringBuilder sb = new StringBuilder();
            InputStream in = getInputStream("/DCC/getGravitar.php");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"iso-8859-1"),8);
            String s;
            while((s = reader.readLine())!=null){
                sb.append(s);
            }

            user.setImageURL(sb.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e("dcc.MySQLQuery", e.getMessage());
        } catch (IOException e) {
            Log.e("dcc.MySQLQuery", e.getMessage());
        } catch (JSONException e) {
            Log.e("dcc.MySQLQuery", e.getMessage());
        }

        return null;
    }

    private synchronized static Object getArray(String url){
        String result = "";
        try{
            StringBuilder sb = new StringBuilder();
            InputStream is = getInputStream(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            sb.append(reader.readLine()).append("\n");
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
            is.close();

            result=sb.toString();
            //Log.e("getarray", result);
            return new JSONObject(result);
        } catch (UnsupportedEncodingException e) {
            Log.e("dcc.MySQLQuery", e.getMessage());
        } catch (IOException e) {
            Log.e("dcc.MySQLQuery", e.getMessage());
        } catch (JSONException e) {
            try {
                return new JSONArray(result);
            } catch (JSONException e1) {
                Log.e("dcc.MySQLQuery", e.getMessage());
            }
        }
        return null;
    }

    public synchronized static List<User> getAllMembers(String url){
        List<User> lMembers = new ArrayList<User>();
        JSONArray jMembers = (JSONArray)getArray(url);

        for(int i = 0 ; i < jMembers.length(); i++){
            try {
                JSONObject jMember = jMembers.getJSONObject(i);
                int uid = Integer.parseInt(jMember.getString("ID"));
                User m;
                if(!ObjectStorage.hasUser(uid)){
                    m = convertUser(jMember);
                    ObjectStorage.setUser(uid, m);
                }else{
                    m = ObjectStorage.getUser(uid);
                }
                lMembers.add(m);
            } catch (JSONException e) {
                Log.e("dcc.MySQLQuery", e.getMessage());
            }
        }
        return lMembers;
    }

    public static List<ActionItem> getActionItems(String s) {
        List<ActionItem> actionItems = new ArrayList<ActionItem>();
        JSONArray jActionItems = (JSONArray)getArray(s);
        for(int i = 0; i < jActionItems.length(); i++){
            try {
                JSONObject jActionItem = jActionItems.getJSONObject(i);
                ActionItem item = new ActionItem();
                item.setAid(Integer.parseInt(jActionItem.getString("id")));
                item.setDescription(jActionItem.getString("description"));
                item.setTag(jActionItem.getString("tag"));
                item.setBody(jActionItem.getString("body"));
                item.setSubject(jActionItem.getString("subject"));
                item.setDate(jActionItem.getString("date"));
                item.setTime(jActionItem.getString("time"));
                item.setStatus(Integer.parseInt(jActionItem.getString("status")));
                actionItems.add(item);
            } catch (JSONException e) {
                Log.e("dcc.MySQLQuery", e.getMessage());
            }
        }
        return actionItems;
    }

    public static DCCArrayList<EDaily> getEdailys(String url) {
        DCCArrayList<EDaily> edailys = new DCCArrayList<EDaily>();
        JSONArray jEDailys = (JSONArray)getArray(url);
        for(int i = 0; i < jEDailys.length(); i++){
            try {
                JSONObject jEdaily = jEDailys.getJSONObject(i);
                EDaily edaily = new EDaily();

                edaily.setFirstname(jEdaily.getString("first"));
                edaily.setLastname(jEdaily.getString("last"));
                edaily.setProj(jEdaily.getString("project"));

                edaily.setHours(jEdaily.getInt("hours"));
                edaily.setBody(jEdaily.getString("body"));

                edaily.setUsr_id(jEdaily.getInt("ID"));


                edaily.setDate(jEdaily.getString("date"));
                edaily.setSubmitted(jEdaily.getString("submitted"));
                edaily.setColor(jEdaily.getString("color"));
                edaily.setGrade(jEdaily.getInt("grade"));

                Log.e("getEdailys", jEDailys.toString());

                edailys.add(edaily);

            } catch (JSONException e) {
                Log.e("getEdailys---", e.getMessage());
            }
        }
        return edailys;
    }

    public static void removeActionItem(String url, ActionItem ai){
        try{
            InputStream is = getInputStream(url+ai.getAid());
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
        sb.append(reader.readLine()).append("\n");
        String line;
        while ((line = reader.readLine()) != null) sb.append(line).append("\n");
        is.close();

        Log.e("getarray", sb.toString());
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
