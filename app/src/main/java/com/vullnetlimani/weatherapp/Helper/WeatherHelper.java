package com.vullnetlimani.weatherapp.Helper;

import android.util.Log;

import com.vullnetlimani.weatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class WeatherHelper implements Serializable {

    public static final String LOG_TAG = "WeatherHelperLog";

    public static final String Weather_NAME = "name";
    public static final String Weather_WEATHER = "weather";
    public static final String Weather_ID = "id";
    public static final String Weather_ICON = "icon";
    public static final String Weather_DESCRIPTION = "description";
    public static final String Weather_SYS = "sys";
    public static final String Weather_COUNTRY = "country";
    public static final String Weather_SUNRISE = "sunrise";
    public static final String Weather_SUNSET = "sunset";
    public static final String Weather_MAIN = "main";
    public static final String Weather_TEMP = "temp";
    public static final String Weather_FEELS_LIKE = "feels_like";
    public static final String Weather_HUMIDITY = "humidity";
    public static final String Weather_PRESSURE = "pressure";
    public static final String Weather_WIND = "wind";
    public static final String Weather_SPEED = "speed";
    public static final String Weather_WIND_SPEED = "wind_speed";
    public static final String Weather_LIST = "list";
    public static final String Weather_DAILY = "daily";
    public static final String Weather_DT = "dt";
    public static final String Weather_DAY_TEMP = "day";
    public static final String Weather_COORD = "coord";
    public static final String Weather_LON = "lon";
    public static final String Weather_LAT = "lat";
    public static final String Weather_HOURLY = "hourly";
    double latitude, longitude;
    private Integer weatherIcon = R.drawable.ic_clear_sky_day;
    private Integer weatherColor = R.color.sunny_weather;
    private String city;
    private String description;
    private String formattedDater;
    private String tomorrow_desc;
    private String country;
    private Integer weather_id, sunrise, sunset, humidity, tomorrow_weather_id;
    private Double current_Temp;
    private Double daily_temp;
    private Double feels_like;
    private Double speed;
    private Double pressure;
    private Double tomorrow_temp;
    private Double population;
    private long time;

    public String  getLatitude() {
        return String.valueOf(latitude);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return String.valueOf(longitude);
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Double getDaily_temp() {
        return daily_temp;
    }

    public void setDaily_Temp(Double daily_temp) {
        this.daily_temp = daily_temp;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Integer getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String ID) {
        switch (ID) {
            case "01d":
                this.weatherIcon = R.drawable.ic_clear_sky_day;
                break;
            case "02d":
                this.weatherIcon = R.drawable.ic_few_clouds_day;
                break;
            case "03d":
                this.weatherIcon = R.drawable.ic_scattered_clouds;
                break;
            case "04d":
                this.weatherIcon = R.drawable.ic_scattered_clouds;
                break;
            case "09d":
                this.weatherIcon = R.drawable.ic_shower_rain;
                break;
            case "10d":
                this.weatherIcon = R.drawable.ic_rain_day;
                break;
            case "11d":
                this.weatherIcon = R.drawable.ic_thunderstorm;
                break;
            case "13d":
                this.weatherIcon = R.drawable.ic_snowing;
                break;
            case "50d":
                this.weatherIcon = R.drawable.ic_mist;
                break;
            case "01n":
                this.weatherIcon = R.drawable.ic_clear_sky_night;
                break;
            case "02n":
                this.weatherIcon = R.drawable.ic_few_clouds_night;
                break;
            case "03n":
                this.weatherIcon = R.drawable.ic_scattered_clouds;
                break;
            case "04n":
                this.weatherIcon = R.drawable.ic_scattered_clouds;
                break;
            case "09n":
                this.weatherIcon = R.drawable.ic_shower_rain;
                break;
            case "10n":
                this.weatherIcon = R.drawable.ic_rain_night;
                break;
            case "11n":
                this.weatherIcon = R.drawable.ic_thunderstorm;
                break;
            case "13n":
                this.weatherIcon = R.drawable.ic_snowing;
                break;
            case "50n":
                this.weatherIcon = R.drawable.ic_mist;
                break;
            default:
                this.weatherIcon = R.drawable.ic_clear_sky_day;
                break;

        }
    }

    public Integer getWeatherColor() {
        return weatherColor;
    }

    public void setWeatherColor(Integer ID) {

        switch (ID) {
            case 200:
            case 201:
            case 202:
            case 210:
            case 211:
            case 212:
            case 221:
            case 230:
            case 231:
            case 232:
                this.weatherColor = R.color.sunny_weather;
                break;
            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 321:

                this.weatherColor = R.color.bad_weather;
                break;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case 511:
            case 520:
            case 521:
            case 522:
            case 531:

                this.weatherColor = R.color.rainy_weather;
                break;
            case 600:
            case 601:
            case 602:
            case 611:
            case 612:
            case 615:
            case 616:
            case 620:
            case 621:
            case 622:

                this.weatherColor = R.color.snow_weather;
                break;
            case 700:
            case 711:
            case 721:
            case 731:
            case 741:
            case 751:
            case 761:
            case 762:
            case 771:
            case 781:

                this.weatherColor = R.color.warning_Weather;
                break;
            case 800:

                this.weatherColor = R.color.clear_sky_color;
                break;
            case 801:

                this.weatherColor = R.color.rainy_weather;
                break;
            case 802:
            case 803:
            case 804:

                this.weatherColor = R.color.good_weather;
                break;
            case 900:
            case 901:
            case 902:
            case 903:
            case 904:
            case 905:
            case 906:

                this.weatherColor = R.color.warning_Weather;
                break;
            default:

                this.weatherColor = R.color.sunny_weather;
                break;

        }
    }

    public Double getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(Double feels_like) {
        this.feels_like = feels_like;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getPopulation() {
        return population;
    }

    public void setPopulation(Double population) {
        this.population = population;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = capitalizeWord(description);
    }

    public String getFormattedDater() {
        return formattedDater;
    }

    public void setFormattedDater(String formattedDater) {
        this.formattedDater = formattedDater;
    }

    public String getTomorrow_desc() {
        return tomorrow_desc;
    }

    public void setTomorrow_desc(String tomorrow_desc) {
        this.tomorrow_desc = capitalizeWord(tomorrow_desc);
    }

    public Integer getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(Integer weather_id) {
        this.weather_id = weather_id;
    }

    public Integer getSunrise() {
        return sunrise;
    }

    public void setSunrise(Integer sunrise) {
        this.sunrise = sunrise;
    }

    public Integer getSunset() {
        return sunset;
    }

    public void setSunset(Integer sunset) {
        this.sunset = sunset;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getTomorrow_weather_id() {
        return tomorrow_weather_id;
    }

    public void setTomorrow_weather_id(Integer tomorrow_weather_id) {
        this.tomorrow_weather_id = tomorrow_weather_id;
    }

    public Double getCurrent_Temp() {
        return current_Temp;
    }

    public void setCurrent_Temp(Double current_Temp) {
        this.current_Temp = current_Temp;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Double getTomorrow_temp() {
        return tomorrow_temp;
    }

    public void setTomorrow_temp(Double tomorrow_temp) {
        this.tomorrow_temp = tomorrow_temp;
    }

    public void ParseTodayData(String data) {

        Log.i(LOG_TAG, "ParseTodayData - " + data);

        try {

            JSONObject reader = new JSONObject(data);

            JSONObject coordinates = reader.getJSONObject(Weather_COORD);
            setLongitude(coordinates.getDouble(Weather_LON));
            setLatitude(coordinates.getDouble(Weather_LAT));

            String city = reader.getString(Weather_NAME);
            setCity(city);

            JSONArray weatherList = reader.getJSONArray(Weather_WEATHER);
            JSONObject weatherListObject = weatherList.getJSONObject(0);

            setWeather_id(weatherListObject.getInt(Weather_ID));

            setWeatherColor(weatherListObject.getInt(Weather_ID));// ketu vendoset card color for today
            setWeatherIcon(weatherListObject.getString(Weather_ICON));// ketu vendoset card icon for today

            setDescription(weatherListObject.getString(Weather_DESCRIPTION));

            JSONObject sys = reader.getJSONObject(Weather_SYS);
            setCountry(sys.getString(Weather_COUNTRY));
            setSunrise(sys.getInt(Weather_SUNRISE));
            setSunset(sys.getInt(Weather_SUNSET));

            JSONObject temp = reader.getJSONObject(Weather_MAIN);

            setCurrent_Temp(temp.getDouble(Weather_TEMP));
            setFeels_like(temp.getDouble(Weather_FEELS_LIKE));
            setHumidity(temp.getInt(Weather_HUMIDITY));
            setPressure(temp.getDouble(Weather_PRESSURE));

            JSONObject wind = reader.getJSONObject(Weather_WIND);
            setSpeed(wind.getDouble(Weather_SPEED));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }

    }

    private String capitalizeWord(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public void ParseTomorrowData(String data) {

        Log.i(LOG_TAG, "ParseTomorrowData - " + data);

        try {

            JSONObject reader = new JSONObject(data);
            JSONArray list = reader.getJSONArray(Weather_LIST);

            // Tomorrow Stats
            JSONObject tomorrowJSONList = list.getJSONObject(7);
            JSONObject tomorrowTemp = tomorrowJSONList.getJSONObject(Weather_MAIN);
            setTomorrow_temp(tomorrowTemp.getDouble(Weather_TEMP));

            JSONArray tomorrowWeather = tomorrowJSONList.getJSONArray(Weather_WEATHER);
            JSONObject tomorrowJSONWeather = tomorrowWeather.getJSONObject(0);

            setTomorrow_weather_id(tomorrowJSONWeather.getInt(Weather_ID));
            setTomorrow_desc(tomorrowJSONWeather.getString(Weather_DESCRIPTION));

            setWeatherColor(tomorrowJSONWeather.getInt(Weather_ID));// ketu vendoset card color for today
            setWeatherIcon(tomorrowJSONWeather.getString(Weather_ICON));// ketu vendoset card icon for today

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }

    }
}
