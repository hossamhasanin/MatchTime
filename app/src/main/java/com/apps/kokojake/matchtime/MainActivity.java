package com.apps.kokojake.matchtime;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<MatchList> matchList;
    ListView matchlst;
    MatchAdapter matchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // MatchList class to fill the data to olaces
        matchList = new ArrayList<MatchList>();
        // Generating this adapter
        matchlst = (ListView) findViewById(R.id.matchlst);
        matchAdapter = new MatchAdapter(matchList);
        matchlst.setAdapter(matchAdapter);
        new MatchTask().execute();
    }


    Document doc;
    Elements lis;
    class MatchTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //lis.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            // Add data to list
            // Scrap data from yallakora website to display it
                try {
                    HttpURLConnection urlc = (HttpURLConnection)
                            (new URL("http://clients3.google.com/generate_204")
                                    .openConnection());
                    urlc.setRequestProperty("User-Agent", "Android");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    if (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0) {
                        doc = Jsoup.connect("http://www.yallakora.com/ar/Matches").userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21").get();
                        lis = doc.select("div.MatchListItems").select("li.MatchClipData");
                        return "connected";
                    } else {
                        return "noconnection";
                    }
                    //return lis;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String resalt) {
            super.onPostExecute(resalt);

            if (resalt == "connected") {
                for (Element li : lis) {
                    String match_time = li.select("div.MatchCase").select("span.MatchTime").text();
                    String team1 = li.select("div.TeamOne").select("a").select("span.TeamName").text();
                    String imgTeam1 = li.select("div.TeamOne").select("a").select("img.TeamLogoSmall").attr("onerror");
                    String team2 = li.select("div.TeamTwo").select("a").select("span.TeamName").text();
                    String imgTeam2 = li.select("div.TeamTwo").select("a").select("img.TeamLogoSmall").attr("onerror");
                    String clears2 = imgTeam2.replace("this.onerror=null;this.src=", "").replace("'", "").replace(";", "").replace(" ", "%20");
                    String clears1 = imgTeam1.replace("this.onerror=null;this.src=", "").replace("'", "").replace(";", "").replace(" ", "%20");
                    System.out.println(clears2);
                    matchList.add(new MatchList("Access", team1, team2, match_time, clears1, clears2));
                }
            } else {
                matchList.add(new MatchList("NotAccess" , "", "", "", "", ""));
                //matchAdapter = new MatchAdapter(matchList);
                //matchlst.setAdapter(matchAdapter);
            }
            matchAdapter = new MatchAdapter(matchList);
            matchlst.setAdapter(matchAdapter);
        }
    }

    class MatchAdapter extends BaseAdapter {

        ArrayList<MatchList> Items = new ArrayList<MatchList>();

        MatchAdapter(ArrayList<MatchList> Items) {
            this.Items = Items;
        }

        @Override
        public int getCount() {
            return Items.size();
        }

        @Override
        public String getItem(int position) {
            return Items.get(position).team1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (this.Items.get(position).mode == "Access") {
                // Create New Inflater
                LayoutInflater general_inflater = getLayoutInflater();
                // Connect with other layout to display data on desiplay_matche layout
                View MatchView = general_inflater.inflate(R.layout.desiplay_matche, null);
                // Find places to display data in
                TextView team1 = (TextView) MatchView.findViewById(R.id.team1);
                TextView team2 = (TextView) MatchView.findViewById(R.id.team2);
                ImageView ImgTeam1 = (ImageView) MatchView.findViewById(R.id.imgTeam1);
                ImageView ImgTeam2 = (ImageView) MatchView.findViewById(R.id.imgTeam2);
                TextView resalt = (TextView) MatchView.findViewById(R.id.resalt);
                // Set the data in the places
                team1.setText(this.Items.get(position).team1);
                team2.setText(this.Items.get(position).team2);
                resalt.setText(this.Items.get(position).resalt);
                Picasso.with(getApplicationContext()).load(this.Items.get(position).imgTeam1).into(ImgTeam1);
                Picasso.with(getApplicationContext()).load(this.Items.get(position).imgTeam2).into(ImgTeam2);
                // Return this layout to display the data one by one
                return MatchView;
            } else {
                // Create New Inflater
                LayoutInflater error_inflater = getLayoutInflater();
                View NoConnection = error_inflater.inflate(R.layout.no_connection, null);
                TextView error_msg = (TextView) NoConnection.findViewById(R.id.error_text);
                error_msg.setText("Error conection check it out");
                return NoConnection;
            }
        }
    }
}
