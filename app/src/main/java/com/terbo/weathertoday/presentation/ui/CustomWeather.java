package com.terbo.weathertoday.presentation.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.terbo.weathertoday.R;
import com.terbo.weathertoday.core.models.WeatherContent;
import com.terbo.weathertoday.data.network.GetWeather;
import com.terbo.weathertoday.data.network.NetworkState;
import com.terbo.weathertoday.data.network.WeatherByCity;
import com.terbo.weathertoday.data.sharedpref.SharedPreference;
import com.terbo.weathertoday.data.sharedpref.WeatherOfflineData;
import com.terbo.weathertoday.presentation.abstracts.WeatherData;

import org.jetbrains.annotations.NotNull;


public class CustomWeather extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    TextView lastUpdate;
    Spinner citySpinner;
    TextView temp;
    TextView cityContent;
    TextView weatherContent;
    TextView descWeatherContent;
    SwipeRefreshLayout swipeRefreshLayout;

    private ProgressDialog dialog;
    private String[] cities;
    private String currentCity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_weather, container, false);
        initViews(view);
        init();
        initCitySpinner(citySpinner);
        chooseCity();
        return view;
    }

    public void init() {
        dialog = new ProgressDialog(getActivity());
        cities = getResources().getStringArray(R.array.cities);
        currentCity = cities[0];
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    public void initViews(@NotNull View view) {
        lastUpdate = view.findViewById(R.id.last_update_custom);
        citySpinner = view.findViewById(R.id.city_spinner);
        temp = view.findViewById(R.id.temp_custom);
        cityContent = view.findViewById(R.id.city_content_custom);
        weatherContent = view.findViewById(R.id.weather_content_custom);
        descWeatherContent = view.findViewById(R.id.desc_weather_content_custom);
        swipeRefreshLayout = view.findViewById(R.id.swipe_custom);
    }

    public void initCitySpinner(@NotNull Spinner spinner) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity()
                , R.layout.support_simple_spinner_dropdown_item
                , cities);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    public void create() {
        gettingWeather(currentCity);
    }

    public void gettingWeather(String city) {
        SharedPreference sharedPreference = new WeatherOfflineData(requireContext());
        if (NetworkState.isNetworkAvailable(requireContext()))
            getDataOnline(city, sharedPreference);
        else
            getDataOffline(sharedPreference);
    }

    public void getDataOnline(String city, SharedPreference sharedPreference) {
        WeatherByCity weather = new WeatherByCity();
        weather.setCity(city);
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
            citySpinner.setSelection(getPositionOfCity(weatherContent.getCityName()));
        } else {
            dialog.setMessage("Please connect to network and refresh");
            dialog.show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public int getPositionOfCity(String city) {
        for (int i = 0; i < cities.length; i++) {
            if (cities[i].equals(city))
                return i;
        }
        return 0;
    }

    public void setDataToView(@NotNull WeatherContent weatherContent) {
        this.cityContent.setText(weatherContent.getCityName());
        this.temp.setText(weatherContent.getTemp() + " C");
        this.weatherContent.setText(weatherContent.getMainWeather());
        this.descWeatherContent.setText(weatherContent.getDescWeather());
        this.lastUpdate.setText(weatherContent.getCurrentTime());
    }

    public void chooseCity() {
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshPage(cities[position]);
                currentCity = cities[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void refreshPage(String city) {
        gettingWeather(city);
    }

    @Override
    public void onRefresh() {
        gettingWeather(currentCity);
    }

}

