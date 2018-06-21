package com.example.android.newsapp;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * QueryUtils mostly based on Quake app from Udacity.
 */
public final class QueryUtils {

    /**
     * Tag for log
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Private constructor
     */
    private QueryUtils() {
        throw new AssertionError("No instances for you!");
    }

    public static List<Article> fetchArticleData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list
        List<Article> articles = extractFeatureFromJson(jsonResponse);

        return articles;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the articles JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Creating String that will contain Json response
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //This function will read from String data and add values to create new object in ArrayList
    private static List<Article> extractFeatureFromJson(String articlesJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(articlesJSON)) {
            return null;
        }

        // Create an empty ArrayList
        List<Article> articles = new ArrayList<>();

        // Try to parse
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(articlesJSON);

            // Extract the JSONObject and then JSONArray from it
            JSONObject response = baseJsonResponse.getJSONObject("response");
            JSONArray articleArray = response.getJSONArray("results");

            // For each item in array we extract info.
            for (int i = 0; i < articleArray.length(); i++) {
                // We will also need "Fields" for Authors. This is why I extract  JSONObject from array.
                JSONObject currentArticle = articleArray.getJSONObject(i);
                String artAuthor;
                if (currentArticle.has("fields")) {
                    JSONObject extraContent = currentArticle.getJSONObject("fields");
                    artAuthor = extraContent.getString("byline");
                } else {
                    artAuthor = "Author unknown";
                }

                String artName = currentArticle.getString("webTitle");
                String artDate = currentArticle.getString("webPublicationDate");
                String artLink = currentArticle.getString("webUrl");
                String artSection = currentArticle.getString("sectionName");
                Article article = new Article(artName, artLink, artDate, artSection, artAuthor);
                articles.add(article);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        }

        return articles;
    }

}

