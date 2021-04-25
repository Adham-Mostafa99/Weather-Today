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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.util.Objects;

import butterknife.BindView;

import static android.content.Context.MODE_PRIVATE;

public class CustomWeather extends Fragment implements LoaderManager.LoaderCallbacks<HashMap<String, String>>
        , SwipeRefreshLayout.OnRefreshListener {
    TextView lastUpdate;
    Spinner citySpinner;
    TextView temp;
    TextView cityContent;
    TextView weatherContent;
    TextView descWeatherContent;
    SwipeRefreshLayout swipeRefreshLayout;


    private ProgressDialog dialog;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    public static final String MY_PREFS_NAME = "LastUpdate";
    private LoaderManager loaderManager;
    private String[] cities;

    private String key = "96e32140b3209f2476517e342dcbf041";
    private String city = "cairo";

    private String url = "https://api.openweathermap.org/data/2.5/weather?q="
            + city
            + "&mode=json"
            + "&units=metric"
            + "&appid=" + key;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_weather, container, false);
        initViews(view);
        init();
        initCitySpinner(citySpinner);



        return view;
    }

    public void create(){

        getDataOffline();

        if (isNetworkAvailable()) {
            initLoader();
        } else {
            if (!getDataOffline()) {
                dialog.setMessage("Please connect to network");
                dialog.show();
            }
        }
    }



    public void initViews(View view) {
        lastUpdate = view.findViewById(R.id.last_update_custom);
        citySpinner = view.findViewById(R.id.city_spinner);
        temp = view.findViewById(R.id.temp_custom);
        cityContent = view.findViewById(R.id.city_content_custom);
        weatherContent = view.findViewById(R.id.weather_content_custom);
        descWeatherContent = view.findViewById(R.id.desc_weather_content_custom);
        swipeRefreshLayout =view.findViewById(R.id.swipe_custom);
    }

    public void setUrl(String city) {
        this.url = "https://api.openweathermap.org/data/2.5/weather?q="
                + city
                + "&mode=json"
                + "&units=metric"
                + "&appid=" + key;
    }

    public String getUrl() {
        return url;
    }

    public boolean getDataOffline() {
        if (sharedPreferences.contains("temp")) {
            updateUi(sharedPreferences.getString("city", "")
                    , sharedPreferences.getString("temp", "")
                    , sharedPreferences.getString("weather", "")
                    , sharedPreferences.getString("desc", "")
                    , sharedPreferences.getString("lastTime", ""));

            setUrl(sharedPreferences.getString("city", ""));

            int position = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("lastPositionSpinner", "")));
            citySpinner.setSelection(position);
            return true;
        }
        return false;
    }

    public boolean refreshLoader() {
        if (isNetworkAvailable()) {
            loaderManager.restartLoader(2, null, this);
            return true;
        } else {
            Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void initLoader() {
        loaderManager.initLoader(2, null, this);
    }

    public void init() {

        dialog = new ProgressDialog(getActivity());
        cities = getResources().getStringArray(R.array.cities);
        editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        sharedPreferences = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        swipeRefreshLayout.setOnRefreshListener(this);
        loaderManager = LoaderManager.getInstance(getActivity());

    }

    public void initCitySpinner(Spinner spinner) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity()
                , R.layout.support_simple_spinner_dropdown_item
                , cities);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putString("lastPositionSpinner", String.valueOf(position));
                editor.apply();

                city = cities[position];
                setUrl(city);
                refreshLoader();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

