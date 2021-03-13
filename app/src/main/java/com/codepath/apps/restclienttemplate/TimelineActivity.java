package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity
{
    public static String TAG = "TimelineActivity";

    TwitterClient client;

    private final int REQUEST_CODE = 20;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    SwipeRefreshLayout swipeContainer;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(resultCode ==RESULT_OK && requestCode == REQUEST_CODE)
        {
            //getting the tweet from the intent
           Tweet tweet = Parcels.unwrap( data.getParcelableExtra("tweet"));

           tweets.add(0, tweet);


           adapter.notifyItemInserted(0);
           rvTweets.smoothScrollToPosition(0);
            //update the RV with the new data

            //m

        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.mCompose)
        {

            Toast.makeText(this, "Composed", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }

       return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);



        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setTitle("My Twitter Clone");
        String title = actionBar.getTitle().toString();
       // actionBar.hide();





        client = RestApplication.getRestClient(this);


        swipeContainer = findViewById(R.id.swipeContainer);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener()
        {

            @Override
            public void onRefresh() {
                populateHomeTimeline();
            }
        });

        //finding the recyclerview
        rvTweets = findViewById(R.id.rv_timeline);
        //initiate the list of tweets and the adpater
         tweets = new ArrayList<>();
         adapter = new TweetAdapter(this, tweets);

         LinearLayoutManager layoutManager = new LinearLayoutManager(this);


         //Recyclerview Setup
        rvTweets.setLayoutManager( new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view)
            {
                loadMoreData();

            }
        };

        //adding the scrolllistener to the RecyclerView
        rvTweets.addOnScrollListener(scrollListener);



        populateHomeTimeline();

    }

    // this is where we will make another API call to get the next page of tweets and add the objects to our current list of tweets
    public void loadMoreData() {
        // 1. Send an API request to retrieve appropriate paginated data
        // 2. Deserialize and construct new model objects from the API response
        // 3. Append the new data objects to the existing set of items inside the array of items
        // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }

    private void populateHomeTimeline()
    {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Onsuccess!" + json.toString()) ;
                JSONArray jsonArray = json.jsonArray;

                try {
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));

                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "Json excep", e);
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                Log.e(TAG, "OnFailure!", throwable);


            }
        });
    }






}