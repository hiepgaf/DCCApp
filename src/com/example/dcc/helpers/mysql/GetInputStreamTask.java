package com.example.dcc.helpers.mysql;

import android.os.AsyncTask;

import com.example.dcc.helpers.ObjectStorage;
import com.example.dcc.helpers.User;
import com.example.dcc.helpers.hacks.DCCCookieSpecFactory;
import com.example.dcc.helpers.hacks.DCCCookieStore;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

/**
 * This is used to get a basic input stream for posting to a URI.
 *
 * This class is unique because it passes the wordpress cookie back to the server
 * to authenticate the user.
 *
 * Created by Harmon on 5/17/13.
 */
public class GetInputStreamTask extends AsyncTask<String, Void, InputStream>{

    //URL of the main HOST
    private static final String HOST = "www.virtualdiscoverycenter.net";


    @Override
    public InputStream doInBackground(String... url) {
        try{
            User user = ObjectStorage.getUser();

            //Get HTTP Client
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url[0]);

            //Mod cookies to allow for lower standards
            client.setCookieStore(new DCCCookieStore());
            client.getCookieSpecs().register("easy", getCookieSpec());
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");

            //Set Cookies
            httpPost.setHeader("Cookie", user.getCookies());

            //Execute the post and get the returned content(JSON file)
            return client.execute(new HttpHost(HOST), httpPost).getEntity().getContent();

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Method used to bypass some cookie security features.
     *
     * @return
     */
    private static CookieSpecFactory getCookieSpec() {
        return new DCCCookieSpecFactory();
    }
}