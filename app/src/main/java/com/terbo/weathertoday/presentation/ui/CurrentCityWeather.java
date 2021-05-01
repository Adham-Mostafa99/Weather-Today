package com.terbo.weathertoday.presentation.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.terbo.weathertoday.R;
import com.terbo.weathertoday.core.models.WeatherContent;
import com.terbo.weathertoday.data.network.NetworkState;
import com.terbo.weathertoday.data.network.WeatherByLocation;
import com.terbo.weathertoday.data.sharedpref.SharedPreference;
import com.terbo.weathertoday.data.sharedpref.WeatherOfflineData;
import com.terbo.weathertoday.presentation.LocationPermission;
import com.terbo.weathertoday.presentation.abstracts.WeatherData;

import org.jetbrains.annotations.NotNull;

import im.delight.android.location.SimpleLocation;

public class CurrentCityWeather extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    TextView lastUpdate;
    TextView temp;
    TextView cityContent;
    TextView weatherContent;
    TextView descWeatherContent;
    ConstraintLayout currentLocation;
    SwipeRefreshLayout swipeRefreshLayout;

    private SimpleLocation location;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_city_weather, container, false);
        initViews(view);
        init();

        return view;
    }

    public void create() {
        location = new SimpleLocation(requireActivity());

        LocationPermission locationPermission = new LocationPermission(requireContext(), requireActivity());
        locationPermission.checkLocationPermission();

        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            showError();
        } else {
            hideError();
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();
            gettingWeather(latitude, longitude);

        }
    }

    public void showError() {
        currentLocation.setVisibility(View.GONE);
        dialog.setMessage("please open Location and refresh");
        dialog.show();
    }

    public void hideError() {
        currentLocation.setVisibility(View.VISIBLE);
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (location.hasLocationEnabled())
            location.beginUpdates();
    }

    @Override
    public void onDestroy() {
        location.endUpdates();
        super.onDestroy();
    }

    public void initViews(@NotNull View view) {
        lastUpdate = view.findViewById(R.id.last_update);
        temp = view.findViewById(R.id.temp);
        cityContent = view.findViewById(R.id.city_content);
        weatherContent = view.findViewById(R.id.weather_content);
        descWeatherContent = view.findViewById(R.id.desc_weather_content);
        currentLocation = view.findViewById(R.id.current_location);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
    }

    public void init() {
        dialog = new ProgressDialog(getActivity());
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    public void gettingWeather(double lat, double lon) {
        SharedPreference sharedPreference = new WeatherOfflineData(requireContext());
        if (NetworkState.isNetworkAvailable(requireContext()))
            getDataOnline(lat, lon, sharedPreference);
        else
            getDataOffline(sharedPreference);
    }

    public void getDataOnline(double lat, double lon, SharedPreference sharedPreference) {
        WeatherByLocation weather = new WeatherByLocation();
        weather.setLocation(lat, lon);
        weather.getWeatherInfo(new WeatherData() {
            @Override
            public void data(WeatherContent content) {
                setDataToView(content);
                sharedPreference.setSharedPreferences(content);
                swipeRefreshLayout.setRefreshing(false);
                if (dialog != null)
                    dialog.dismiss();
            }
        });
    }

    public void getDataOffline(@NotNull SharedPreference sharedPreference) {
        WeatherContent weatherContent = sharedPreference.getSharedPreferences();
        if (weatherContent != null) {
            setDataToView(weatherContent);
        } else {
            dialog.setMessage("Please connect to network and refresh");
            dialog.show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public void setDataToView(@NotNull WeatherContent weatherContent) {
        this.cityContent.setText(weatherContent.getCityName());
        this.temp.setText(weatherContent.getTemp() + " C");
        this.weatherContent.setText(weatherContent.getMainWeather());
        this.descWeatherContent.setText(weatherContent.getDescWeather());
        this.lastUpdate.setText(weatherContent.getCurrentTime());
    }


    @Override
    public void onRefresh() {
        create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

}

