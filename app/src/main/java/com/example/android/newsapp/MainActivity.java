package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {
    //This is C library for encoded key, comment this out if You got this from GitHub
    //Based on tutorial from: https://blog.squareboat.com/securing-api-keys-inside-android-apps-using-android-ndk-be2e7e29dfd3
    static {
        System.loadLibrary("native-lib");
    }

    //Also part of C method. Comment out next two lines out if You got this from github
    public native String getAPIKey();

    private String secretKey = new String(Base64.decode(getAPIKey(), Base64.DEFAULT));

    private static final String LOG_TAG = MainActivity.class.getName();
    //Url for Guardian API
    private static final String NEWS_REQUEST_URL = "https://content.guardianapis.com/search?";
    private static final int ARTICLE_LOADER_ID = 1;
    private ArticleAdapter mAdapter;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView articleListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        articleListView.setEmptyView(mEmptyStateTextView);
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        articleListView.setAdapter(mAdapter);
        //OnItemClickListener to send user to web
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Article currentArticle = mAdapter.getItem(position);
                Uri articleUri = Uri.parse(currentArticle.getaUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);
                startActivity(websiteIntent);
            }
        });
        // Checking state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Getting details on network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText("No internet connection");
        }
    }


    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
        String minArtNo = sharedPrefs.getString(
                getString(R.string.settings_min_articles_key),
                getString(R.string.settings_min_articles_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        String searchFor = sharedPrefs.getString(
                getString(R.string.settings_search_key),
                getString(R.string.settings_search_default)
        );

        // Append query parameter and its value.
        uriBuilder.appendQueryParameter("q", searchFor);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", minArtNo);
        uriBuilder.appendQueryParameter("show-fields", "byline");
        //You can replace secretKey withs "test" or Your own, if You downloaded this code from GitHub
        uriBuilder.appendQueryParameter("api-key", secretKey);
        Log.v("Main Activity", "url is: " + uriBuilder.toString());
        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setText("No data for articles");
        mAdapter.clear();
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
