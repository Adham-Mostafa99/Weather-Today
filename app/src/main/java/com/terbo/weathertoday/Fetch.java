package com.terbo.weathertoday;

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
import java.util.HashMap;

public class Fetch {
    public static String getHttpConnection(URL url) {
        String outputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            outputStream = getStream(httpURLConnection);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return outputStream;
    }

    public static String getStream(HttpURLConnection httpURLConnection) throws IOException {
        InputStream inputStream = null;
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
        return stringBuilder.toString();
    }

    public static HashMap<String, String> extractJson(String json) {
        Log.v("JSON: ", json);
        HashMap<String, String> data = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(json);

            //city
            String cityName = jsonObject.getString("name");

            //temp
            JSONObject main = jsonObject.getJSONObject("main");
            String temp = main.getString("temp");

            //weather
            JSONArray weather = jsonObject.getJSONArray("weather");
            JSONObject current = weather.getJSONObject(0);
            String mainWeather = current.getString("main");
            String descWeather = current.getString("description");

            data.put("city", cityName);
            data.put("temp", temp);
            data.put("mainWeather", mainWeather);
            data.put("descWeather", descWeather);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static HashMap<String, String> doInBackground(String url) {
        String output = null;
        output = getHttpConnection(getUrl(url));
        return extractJson(output);
    }

    public static URL getUrl(String url) {
        URL mUrl = null;
        try {
            mUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return mUrl;
    }
}
