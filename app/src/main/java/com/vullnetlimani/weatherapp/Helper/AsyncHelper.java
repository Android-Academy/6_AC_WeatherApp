package com.vullnetlimani.weatherapp.Helper;

public class AsyncHelper {

    public static final String FORECAST_MAIN_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";
    public static final String FORECAST_MAIN_GEO_URL = "http://api.openweathermap.org/data/2.5/forecast?lat=";

    public static final String ONE_CALL_GEO_URL = "https://api.openweathermap.org/data/2.5/onecall?lat=";

    public static final String CURRENT_MAIN_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static final String CURRENT_MAIN_GEO_URL = "http://api.openweathermap.org/data/2.5/weather?lat=";

    public static final String UNITS = "&units=";
    public static final String LANG = "&lang=";
    public static final String CNT_DAY = "&cnt=";
    public static final String APP_ID = "&APPID=";
    public static final String API_KEY = "470537f3f585c426b66d50ca30b96560";
    public static final String LATITUDE = "&lat=";
    public static final String LONGITUDE = "&lon=";
    private static final String EX_ALL_INC_DAILY = "&exclude=current,hourly,minutely";
    private static final String EX_ALL_INC_HOURLY = "&exclude=current,daily,minutely";


    public static String checkCurrentWeather(String city, String unit, String language) {
        return CURRENT_MAIN_URL + city + UNITS + unit + LANG + language + APP_ID + API_KEY;
    }

    public static String checkCurrentWeather(String latitude, String longitude, String unit, String language) {
        return CURRENT_MAIN_GEO_URL + latitude + LONGITUDE + longitude + UNITS + unit + LANG + language + APP_ID + API_KEY;

        //http://api.openweathermap.org/data/2.5/weather?lat=41.5305927&lon=20.9522643&units=metric&lang=en&APPID=470537f3f585c426b66d50ca30b96560
    }

    public static String checkTomorrowWeather(String city, String unit, String language, String Days) {
        return FORECAST_MAIN_URL + city + UNITS + unit + LANG + language + CNT_DAY + Days + APP_ID + API_KEY;
    }

    public static String checkTomorrowWeather(String latitude, String longitude, String unit, String language, String Days) {
        return FORECAST_MAIN_GEO_URL + latitude + LONGITUDE + longitude + UNITS + unit + LANG + language + CNT_DAY + Days + APP_ID + API_KEY;
    }

    public static String checkDailyWeather(String latitude, String longitude, String unit, String language) {
        return ONE_CALL_GEO_URL + latitude + LONGITUDE + longitude + EX_ALL_INC_DAILY + UNITS + unit + LANG + language + APP_ID + API_KEY;
    }

    public static String checkHourlyWeather(String latitude, String longitude, String unit, String language) {
        return ONE_CALL_GEO_URL + latitude + LONGITUDE + longitude + EX_ALL_INC_HOURLY + UNITS + unit + LANG + language + APP_ID + API_KEY;
    }
}
