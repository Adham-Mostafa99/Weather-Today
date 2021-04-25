package com.terbo.weathertoday;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import im.delight.android.location.SimpleLocation;

import static android.content.Context.MODE_PRIVATE;

public class CurrentCityWeather extends Fragment implements LoaderManager.LoaderCallbacks<HashMap<String, String>>
        , SwipeRefreshLayout.OnRefreshListener {
    TextView lastUpdate, lastUpdateText;
    TextView temp;
    TextView cityContent, cityContentText;
    TextView weatherContent, weatherContentText;
    TextView descWeatherContent, descWeatherContentText;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView isOpen;
    Button refresh;

    private SimpleLocation location;
    private ProgressDialog dialog;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    public static final String MY_PREFS_NAME = "LastUpdate";
    private LoaderManager loaderManager;

    private String key = "96e32140b3209f2476517e342dcbf041";

    private String url = "https://api.openweathermap.org/data/2.5/weather?q="
            + "cairo"
            + "&mode=json"
            + "&units=metric"
            + "&appid=" + key;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_city_weather, container, false);
        initViews(view);
        init();


        // construct a new instance of SimpleLocation
        location = new SimpleLocation(requireActivity());


        return view;
    }

    public void create() {


        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            showError();
        }


        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();


        getDataOffline(String.valueOf(latitude), String.valueOf(longitude));

        if (isNetworkAvailable()) {
            initLoader();
        } else {
            if (!getDataOffline(String.valueOf(latitude), String.valueOf(longitude))) {
                dialog.setMessage("Please connect to network");
                dialog.show();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
//        location.beginUpdates();


    }


    @Override
    public void onPause() {
        location.endUpdates();
        super.onPause();
    }

    public void initViews(View view) {
        lastUpdate = view.findViewById(R.id.last_update);
        lastUpdateText = view.findViewById(R.id.last_update_text);
        temp = view.findViewById(R.id.temp);
        cityContent = view.findViewById(R.id.city_content);
        cityContentText = view.findViewById(R.id.city);
        weatherContent = view.findViewById(R.id.weather_content);
        weatherContentText = view.findViewById(R.id.weather);
        descWeatherContent = view.findViewById(R.id.desc_weather_content);
        descWeatherContentText = view.findViewById(R.id.desc_weather);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        isOpen = view.findViewById(R.id.open_location);
        refresh = view.findViewById(R.id.is_open);

    }

    public void showError() {
        lastUpdate.setVisibility(View.INVISIBLE);
        lastUpdateText.setVisibility(View.INVISIBLE);
        temp.setVisibility(View.INVISIBLE);
        cityContent.setVisibility(View.INVISIBLE);
        cityContentText.setVisibility(View.INVISIBLE);
        weatherContent.setVisibility(View.INVISIBLE);
        weatherContentText.setVisibility(View.INVISIBLE);
        descWeatherContent.setVisibility(View.INVISIBLE);
        descWeatherContentText.setVisibility(View.INVISIBLE);
        isOpen.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.VISIBLE);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (location.hasLocationEnabled()) {
                    create();
                    hideError();
                    onRefresh();
                } else
                    Toast.makeText(getActivity(), "please turn on location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void hideError() {
        lastUpdate.setVisibility(View.VISIBLE);
        lastUpdateText.setVisibility(View.VISIBLE);
        temp.setVisibility(View.VISIBLE);
        cityContent.setVisibility(View.VISIBLE);
        cityContentText.setVisibility(View.VISIBLE);
        weatherContent.setVisibility(View.VISIBLE);
        weatherContentText.setVisibility(View.VISIBLE);
        descWeatherContent.setVisibility(View.VISIBLE);
        descWeatherContentText.setVisibility(View.VISIBLE);
        isOpen.setVisibility(View.INVISIBLE);
        refresh.setVisibility(View.INVISIBLE);
    }

    public void setUrl(String latitude, String longitude) {

        this.url = "https://api.openweathermap.org/data/2.5/weather?"
                + "lat=" + latitude
                + "&lon=" + longitude
                + "&mode=json"
                + "&units=metric"
                + "&appid=" + key;
        Log.v("url new : ", url);

    }

    public String getUrl() {
        return url;
    }

    public boolean getDataOffline(String latitude, String longitude) {
        if (sharedPreferences.contains("temp")) {
            updateUi(sharedPreferences.getString("city", "")
                    , sharedPreferences.getString("temp", "")
                    , sharedPreferences.getString("weather", "")
                    , sharedPreferences.getString("desc", "")
                    , sharedPreferences.getString("lastTime", ""));

            setUrl(latitude, longitude);

            return true;
        }
        return false;
    }

    public boolean refreshLoader() {
        if (isNetworkAvailable()) {
            final String latitude = String.valueOf(location.getLatitude());
            final String longitude = String.valueOf(location.getLongitude());
            setUrl(latitude, longitude);
            loaderManager.restartLoader(1, null, this);
            return true;
        } else {
            Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void initLoader() {
        loaderManager.initLoader(1, null, this);
    }

    public void init() {


        dialog = new ProgressDialog(getActivity());
        editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        sharedPreferences = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        swipeRefreshLayout.setOnRefreshListener(this);
        loaderManager = LoaderManager.getInstance(getActivity());

    }


    public void updateUi(String city, String temp, String weather, String desc, String time) {
        this.temp.setText(temp + " C");
        cityContent.setText(city);
        weatherContent.setText(weather);
        descWeatherContent.setText(desc);
        lastUpdate.setText(time);
        if (isNetworkAvailable()) {
            editor.putString("lastTime", time);
            editor.putString("city", city);
            editor.putString("temp", temp);
            editor.putString("weather", weather);
            editor.putString("desc", desc);
            editor.apply();
        }
        dialog.dismiss();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @NonNull
    @Override
    public Loader<HashMap<String, String>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.v("Url", getUrl());

        return new FetchData(requireActivity(), getUrl());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<HashMap<String, String>> loader, HashMap<String, String> data) {

        Log.v("data: ", data.toString());
        //city
        String cityName = data.get("city");

        //temp
        String temp = data.get("temp");

        //weather
        String mainWeather = data.get("mainWeather");
        String descWeather = data.get("descWeather");

        Date currentTime = Calendar.getInstance().getTime();
        updateUi(cityName, temp, mainWeather, descWeather, currentTime.toString());
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<HashMap<String, String>> loader) {

    }

    @Override
    public void onRefresh() {
        if (!refreshLoader())
            swipeRefreshLayout.setRefreshing(false);
    }


}

