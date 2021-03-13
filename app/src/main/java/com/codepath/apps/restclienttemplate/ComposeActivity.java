package com.codepath.apps.restclienttemplate;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    Button btnTweet;
    EditText etCompose;
    final int ET_MAX_LENGHT = 140;

    static final String TAG = " ComposeActivity";

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = RestApplication.getRestClient(this);


        etCompose = findViewById(R.id.et_compose);
        btnTweet = findViewById(R.id.btn_tweet);



        //adding character count


        //connecting button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final String tweetContent = etCompose.getText().toString();
                //creating intent to
                //check if tf is empty
                if(tweetContent.isEmpty())
                {
                    Toast.makeText(ComposeActivity.this, "The text field cannot be empty!", Toast.LENGTH_LONG).show();

                    return;
                }
                //check is tf is too lng
                if (etCompose.toString().length() > ET_MAX_LENGHT)
                {
                    Toast.makeText(ComposeActivity.this, "Too many characters !", Toast.LENGTH_LONG).show();
                    return;
                }

                client.publishTweet( tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json)
                    {

                        try
                        {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.e(TAG, "Published tweet " + tweet.body);

                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable)
                    {
                        Log.e(TAG, "OnFailure", throwable);

                    }
                });


            }
        });


    }
}