package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity
{
    public static String TAG = "TimelineActivity";

    TwitterClient client;

    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);


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