package com.vullnetlimani.weatherapp.Activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.vullnetlimani.weatherapp.Database.DatabaseHelper;
import com.vullnetlimani.weatherapp.Database.LoadDatabase;
import com.vullnetlimani.weatherapp.Helper.AsyncHelper;
import com.vullnetlimani.weatherapp.Helper.GPSTracker;
import com.vullnetlimani.weatherapp.Helper.MySharedPreferences;
import com.vullnetlimani.weatherapp.Helper.WeatherHelper;
import com.vullnetlimani.weatherapp.R;
import com.vullnetlimani.weatherapp.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseActivity {

    public static final int GRANTED = 0;
    public static final int DENIED = 1;
    public static final int BLOCKED_OR_NEVER_ASKED = 2;
    public static final String LOG_TAG = "MainActivityLog";
    static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    public boolean gpsFounded = false;
    TextView actual_weather, current_time, current_location, desc, temperature, feels_like, wind_speed, pressure, humidity, tomorrowStatTextView, tomorrow_desc, tomorrow_temp;
    TextView error_code_textView;
    ImageView todayStat_ImageView, tomorrowStat_ImageView;
    NestedScrollView scroll_view, error_scrollView;
    CardView card_view, card_tomorrow;
    String city, language, unit;
    String latitude, longitude;
    boolean coordinate_search;
    private String[] mPermissions;

    private FloatingActionButton auto_locate_fab;
    private CoordinatorLayout coordinateLayout;
    private GPSTracker gpsTracker;
    private boolean isGpsSettingsLaunched = false;
    private FrameLayout mFrameLayout;
    private Toolbar searchToolbar;
    private Menu search_menu;
    private MenuItem item_search;
    private DatabaseHelper databaseHelper;
    private SimpleCursorAdapter suggestionAdapter;


    @Override
    public void onBackPressed() {

        if (item_search != null && item_search.isActionViewExpanded()) {
            item_search.collapseActionView();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:

                circleReveal(true, true);

                item_search.expandActionView();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupTheme();
        setTheme(Theme);

        setContentView(R.layout.activity_main);

        LoadViews();
        setupSwipe();
        setupToolbar();
        setSearchToolbar();
        init();
        getWeatherData();
        initBottomBar();
        setupNavigationDrawer();

        auto_locate_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (PermissionAllChecker()) {
                    case GRANTED:
                        gpsFounded = false;
                        autoLocate();

                        // scroll_view.setVisibility(View.GONE);
                        break;
                    case DENIED:
                        CheckPermissions();
                        break;
                    case BLOCKED_OR_NEVER_ASKED:
                        permissionBlocked();
                        break;
                    default:
                        break;
                }
            }
        });


    }

    private void initBottomBar() {

        setupBottomBar();

        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.d(LOG_TAG, "onTabSelected - " + tab.getPosition());

                if (tab.getPosition() == 0)
                    MySharedPreferences.setNormalPref(MainActivity.this, Constants.list_unit_code_key, getString(R.string.metric));
                else
                    MySharedPreferences.setNormalPref(MainActivity.this, Constants.list_unit_code_key, getString(R.string.imperial));

                getWeatherData();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayout.Tab tab_zero = bottomTabLayout.getTabAt(0);
        TabLayout.Tab tab_one = bottomTabLayout.getTabAt(1);

        if (unit.contains(getString(R.string.metric))) {
            bottomTabLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (tab_zero != null)
                        tab_zero.select();
                }
            });
        } else {
            bottomTabLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (tab_one != null)
                        tab_one.select();
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {

            int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED && ACCESS_COARSE_LOCATION == PackageManager.PERMISSION_GRANTED) {
                autoLocate();
            } else {
                snackBarMessage(getString(R.string.permission_denied));
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void snackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(coordinateLayout, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(MaterialColors.getColor(this, R.attr.floatButtonColor, Color.WHITE))
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setAnchorView(auto_locate_fab)
                .setActionTextColor(MaterialColors.getColor(this, R.attr.floatButtonRippleColor, Color.WHITE))
                .setTextColor(MaterialColors.getColor(this, R.attr.floatButtonRippleColor, Color.WHITE));
        snackbar.show();
    }

    private void CheckPermissions() {

        int result;

        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : mPermissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {

            ActivityCompat.requestPermissions(MainActivity.this, listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        }

    }

    private void permissionBlocked() {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
        dialog.setTitle(R.string.alert_title_permission_blocked);
        dialog.setMessage(R.string.alert_message_permission_blocked);
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                i.setData(uri);
                startActivity(i);
            }
        });

        dialog.show();
    }

    private void extractDataFromServer(String yourCurrentRequest, String yourTomorrowRequest) {

        AsyncHttpClient client = new AsyncHttpClient();

        // Koha per Sot
        client.get(yourCurrentRequest, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String data = new String(responseBody);

                Log.d(LOG_TAG, "Your Current Data - " + data);

                if (!data.equals("")) {

                    mWeatherHelper.ParseTodayData(data);

                    updateTodayData();

                    scroll_view.setVisibility(View.VISIBLE);
                    error_scrollView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Log.e(LOG_TAG, "error - ", error);
                scroll_view.setVisibility(View.GONE);

                MySharedPreferences.setNormalPref(MainActivity.this, Constants.edit_text_location_key, Constants.LOCATION_CITY_DEFAULT);
                error_scrollView.setVisibility(View.VISIBLE);
                error_code_textView.setText(error.getMessage());

            }
        });

        // Koha per neser
        client.get(yourTomorrowRequest, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String data = new String(responseBody);

                Log.d(LOG_TAG, "Your Tomorrow Data - " + data);

                if (!data.equals("")) {

                    mWeatherHelper.ParseTomorrowData(data);

                    UpdateTomorrowData();

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Log.e(LOG_TAG, "error - ", error);

            }
        });
    }

    private void autoLocate() {

        if (gpsTracker == null) {

            gpsTracker = new GPSTracker(MainActivity.this, new GPSTracker.LocationInterface() {
                @Override
                public void autoLocateInfo(double latitude, double longitude) {

                    Log.d(LOG_TAG, "autoLocateInfo latitude - " + latitude);
                    Log.d(LOG_TAG, "autoLocateInfo longitude - " + longitude);

                    if (!gpsFounded) {

                        gpsFounded = true;

                        MySharedPreferences.setBooleanPref(MainActivity.this, Constants.coordinate_search_key, true);
                        MySharedPreferences.setNormalPref(MainActivity.this, Constants.latitude_pref_key, String.valueOf(latitude));
                        MySharedPreferences.setNormalPref(MainActivity.this, Constants.longitude_pref_key, String.valueOf(longitude));

                        getWeatherData();

                        mSwipeRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mSwipeRefreshLayout.isRefreshing())
                                    mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }, 500);
                    }

                }
            });

        }

        if (gpsTracker.getLocation()) {
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            showGpsSettings();
        }

    }

    private void showGpsSettings() {

        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this);
        alertDialog.setTitle(R.string.allert_title_gps);
        alertDialog.setMessage(R.string.alert_message_gps);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.alert_positive_btn_gps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
                isGpsSettingsLaunched = true;
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        snackBarMessage(getString(R.string.snack_bar_gps_turned_off));
                    }
                }, 300);
            }
        });

        alertDialog.show();

    }

    public int PermissionAllChecker() {

        if (MySharedPreferences.isFirstTimeAskingPermission(this)) {

            MySharedPreferences.firstTimeAskingPermission(this, false);

            for (String permission : mPermissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return DENIED;
                }
            }

        } else {
            for (String permission : mPermissions) {

                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                        return DENIED;

                    } else {

                        return BLOCKED_OR_NEVER_ASKED;
                    }
                }
            }
        }

        return GRANTED;
    }


    private void LoadViews() {

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.navigation_view);

        mFrameLayout = findViewById(R.id.mFrameLayout);

        coordinateLayout = findViewById(R.id.coordinateLayout);

        mSwipeRefreshLayout = findViewById(R.id.main_swipe_refresh_layout);

        bottomTabLayout = findViewById(R.id.bottomTabLayout);

        auto_locate_fab = findViewById(R.id.auto_locate_fab);

        card_view = findViewById(R.id.card_view);
        card_tomorrow = findViewById(R.id.card_tomorrow);

        scroll_view = findViewById(R.id.scroll_view);
        error_scrollView = findViewById(R.id.error);

        todayStat_ImageView = findViewById(R.id.todayStat_ImageView);
        tomorrowStat_ImageView = findViewById(R.id.detail_main_icon);

        actual_weather = findViewById(R.id.actual_weather);
        current_time = findViewById(R.id.current_date);
        current_location = findViewById(R.id.current_location);
        desc = findViewById(R.id.desc);
        temperature = findViewById(R.id.temperature);
        feels_like = findViewById(R.id.feels_like);
        wind_speed = findViewById(R.id.wind_speed);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);
        tomorrowStatTextView = findViewById(R.id.detail_city_text);
        tomorrow_desc = findViewById(R.id.detail_description);
        tomorrow_temp = findViewById(R.id.detail_main_temp);

        error_code_textView = findViewById(R.id.error_code_textView);

        scroll_view.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroll_view.setVisibility(View.GONE);
        error_scrollView.setVisibility(View.GONE);

    }

    private void init() {

        mPermissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        mWeatherHelper = new WeatherHelper();

        mSwipeRefreshLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scroll_view.getScrollY();
                mSwipeRefreshLayout.setEnabled(scrollY == 0);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeatherData();
            }
        });

        databaseHelper = new DatabaseHelper(MainActivity.this);

        if (databaseHelper.checkDatabase()) {
            openDatabase();
        } else {
            LoadDatabase mLoadDatabase = new LoadDatabase(MainActivity.this);
            mLoadDatabase.doInBackground();
        }

    }

    private void refreshLastTimeCheck() {
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());
        current_time.setText(getString(R.string.setTimeDate, currentDate, currentTime));
    }

    public void openDatabase() {
        try {
            databaseHelper.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getWeatherData() {

        city = MySharedPreferences.getNormalPref(MainActivity.this, Constants.edit_text_location_key, Constants.LOCATION_CITY_DEFAULT);
        latitude = MySharedPreferences.getNormalPref(MainActivity.this, Constants.latitude_pref_key, "0");
        longitude = MySharedPreferences.getNormalPref(MainActivity.this, Constants.longitude_pref_key, "0");
        coordinate_search = MySharedPreferences.getBooleanPref(MainActivity.this, Constants.coordinate_search_key, false);
        unit = MySharedPreferences.getNormalPref(MainActivity.this, Constants.list_unit_code_key, Constants.UNI_CODE_DEFAULT);
        language = Constants.Language_EN;

        Log.d(LOG_TAG, "Coordinate Search = " + coordinate_search);

        if (coordinate_search) {
            extractDataFromServer(
                    AsyncHelper.checkCurrentWeather(latitude, longitude, unit, language),
                    AsyncHelper.checkTomorrowWeather(latitude, longitude, unit, language, Constants.WEATHER_TOMORROW_TIME)
            );
        } else {
            extractDataFromServer(
                    AsyncHelper.checkCurrentWeather(city, unit, language),
                    AsyncHelper.checkTomorrowWeather(city, unit, language, Constants.WEATHER_TOMORROW_TIME)
            );
        }

        mSwipeRefreshLayout.setRefreshing(true);
    }

    public void launchDetails(View view) {
        if (view.getId() == R.id.view_more_btn)
            startActivity(new Intent(this, DailyForecastActivity.class));
    }

    private void updateTodayData() {

        Log.d(LOG_TAG, "updateTodayData()");

        updatedNavigationDetails();

        MySharedPreferences.setNormalPref(MainActivity.this, Constants.edit_text_location_key, mWeatherHelper.getCity());
        MySharedPreferences.setNormalPref(MainActivity.this, Constants.latitude_pref_key, mWeatherHelper.getLatitude());
        MySharedPreferences.setNormalPref(MainActivity.this, Constants.longitude_pref_key, mWeatherHelper.getLongitude());

        current_location.setText(getString(R.string.setCity_Country, mWeatherHelper.getCity(), mWeatherHelper.getCountry()));

        refreshLastTimeCheck();

        if (unit.contains(getString(R.string.metric))) {
            temperature.setText(getString(R.string.setTempC, mWeatherHelper.getCurrent_Temp()));
            feels_like.setText(getString(R.string.setFeelsLikeC, mWeatherHelper.getFeels_like()));
            wind_speed.setText(getString(R.string.setKMH, mWeatherHelper.getSpeed()));
        } else {
            temperature.setText(getString(R.string.setTempF, mWeatherHelper.getCurrent_Temp()));
            feels_like.setText(getString(R.string.setFeelsLikeF, mWeatherHelper.getFeels_like()));
            wind_speed.setText(getString(R.string.setMPH, mWeatherHelper.getSpeed()));
        }

        desc.setText(mWeatherHelper.getDescription());

        humidity.setText(getString(R.string.setHUM, mWeatherHelper.getHumidity()));
        pressure.setText(getString(R.string.setPRE, mWeatherHelper.getPressure()));

        animateTodayCard();

        todayStat_ImageView.setImageResource(mWeatherHelper.getWeatherIcon());

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void animateTodayCard() {

        current_time.setAlpha(0);
        current_time.animate().setDuration(250).alpha(1);

        current_location.setAlpha(0);
        current_location.animate().setDuration(250).alpha(1);

        temperature.setAlpha(0);
        temperature.animate().setDuration(250).alpha(1);

        feels_like.setAlpha(0);
        feels_like.animate().setDuration(250).alpha(1);

        desc.setAlpha(0);
        desc.animate().setDuration(250).alpha(1);

        wind_speed.setAlpha(0);
        wind_speed.animate().setDuration(250).alpha(1);

        humidity.setAlpha(0);
        humidity.animate().setDuration(250).alpha(1);

        pressure.setAlpha(0);
        pressure.animate().setDuration(250).alpha(1);

        todayStat_ImageView.setImageAlpha(0);
        ValueAnimator imageAnim = ValueAnimator.ofInt(0, 255);
        imageAnim.setDuration(250);
        imageAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                todayStat_ImageView.setImageAlpha((Integer) animation.getAnimatedValue());
            }
        });
        imageAnim.start();


        // Set Color to Card
        int oldColor = MySharedPreferences.getNormalPref_Integer(
                MainActivity.this,
                Constants.today_card_current_color_key,
                0);

        int colorTo = ContextCompat.getColor(this, mWeatherHelper.getWeatherColor());
        int colorFrom;

        if (oldColor == 0) {
            colorFrom = getResources().getColor(android.R.color.transparent);
        } else {
            colorFrom = getResources().getColor(oldColor);
        }

        Log.d(LOG_TAG, "colorFrom - " + colorFrom);
        Log.d(LOG_TAG, "colorTo - " + colorTo);

        MySharedPreferences.setNormalPref_Integer(MainActivity.this, Constants.today_card_current_color_key, mWeatherHelper.getWeatherColor());

//        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
//        colorAnimation.setDuration(250);
//        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                card_view.setCardBackgroundColor((int) animation.getAnimatedValue());
//            }
//        });
//        colorAnimation.start();
    }

    private void UpdateTomorrowData() {

        if (unit.contains(getString(R.string.metric)))
            tomorrow_temp.setText(getString(R.string.setTempC, mWeatherHelper.getTomorrow_temp()));
        else
            tomorrow_temp.setText(getString(R.string.setTempF, mWeatherHelper.getTomorrow_temp()));

        tomorrow_desc.setText(mWeatherHelper.getTomorrow_desc());

        animateTomorrowCard();

        tomorrowStat_ImageView.setImageResource(mWeatherHelper.getWeatherIcon());

    }

    private void animateTomorrowCard() {
        tomorrow_temp.setAlpha(0);
        tomorrow_temp.animate().setDuration(250).alpha(1);

        tomorrow_desc.setAlpha(0);
        tomorrow_desc.animate().setDuration(250).alpha(1);

        tomorrowStat_ImageView.setImageAlpha(0);
        ValueAnimator imageAnim = ValueAnimator.ofInt(0, 255);
        imageAnim.setDuration(250);
        imageAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tomorrowStat_ImageView.setImageAlpha((Integer) animation.getAnimatedValue());
            }
        });
        imageAnim.start();

        // Set Tomorrow Card Color
        int oldColor = MySharedPreferences.getNormalPref_Integer(
                MainActivity.this,
                Constants.tomorrow_card_current_color_key,
                0);

        int colorTo = ContextCompat.getColor(this, mWeatherHelper.getWeatherColor());
        int colorFrom;

        if (oldColor == 0) {
            colorFrom = getResources().getColor(android.R.color.transparent);
        } else {
            colorFrom = getResources().getColor(oldColor);
        }

        Log.d(LOG_TAG, "colorFrom - " + colorFrom);
        Log.d(LOG_TAG, "colorTo - " + colorTo);

        MySharedPreferences.setNormalPref_Integer(MainActivity.this, Constants.tomorrow_card_current_color_key, mWeatherHelper.getWeatherColor());

//        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
//        colorAnimation.setDuration(250);
//        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                card_tomorrow.setCardBackgroundColor((int) animation.getAnimatedValue());
//            }
//        });
//        colorAnimation.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isGpsSettingsLaunched) {
            isGpsSettingsLaunched = false;
            autoLocate();
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (item_search != null && item_search.isActionViewExpanded())
            item_search.collapseActionView();
    }

    private void setSearchToolbar() {

        mFrameLayout.setBackgroundColor(MaterialColors.getColor(this, R.attr.searchToolbarColor, Color.BLACK));

        searchToolbar = findViewById(R.id.searchToolbar);

        if (searchToolbar != null) {

            searchToolbar.inflateMenu(R.menu.menu_search);
            search_menu = searchToolbar.getMenu();

            searchToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleReveal(true, false);
                }
            });

            item_search = search_menu.findItem(R.id.action_filter_search);

            item_search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    setVisibilityToolbar(false);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    circleReveal(true, false);
                    return true;
                }
            });

            initSearchView();

        }

    }

    private void initSearchView() {

        searchToolbar.setBackgroundColor(MaterialColors.getColor(this, R.attr.floatButtonRippleColor, Color.WHITE));
        searchToolbar.setTitleTextColor(MaterialColors.getColor(this, R.attr.searchToolbarText, Color.WHITE));
        searchToolbar.setCollapseIcon(R.drawable.ic_back_search);

        MenuItem search = search_menu.findItem(R.id.action_filter_search);
        search.setIcon(R.drawable.ic_search);
        SearchView searchView = (SearchView) search.getActionView();

        searchView.findViewById(androidx.appcompat.R.id.search_plate)
                .setBackgroundColor(Color.TRANSPARENT);

        EditText textSearch = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        textSearch.setHint(R.string.search);
        textSearch.setHintTextColor(MaterialColors.getColor(this, R.attr.searchToolbarIconColor, Color.WHITE));
        textSearch.setTextColor(MaterialColors.getColor(this, R.attr.searchToolbarText, Color.WHITE));

        //Cursori venojet
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            GradientDrawable drawable = (GradientDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.cursor, null);
            if (drawable != null) {
                drawable.setColor(MaterialColors.getColor(this, R.attr.colorAccent, Color.BLACK));
            }
            textSearch.setTextCursorDrawable(drawable);
        }

        final String[] from = new String[]{DatabaseHelper.DATABASE_CITY, DatabaseHelper.DATABASE_ISO2, DatabaseHelper.DATABASE_ADMIN_NAME};
        final int[] to = new int[]{R.id.suggestion_text, R.id.country_text, R.id.extra_info};

        suggestionAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.suggestion_row, null, from, to, 0) {

            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }
        };

        suggestionAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                if (view.getId() == R.id.suggestion_text) {

                    TextView city_text = view.findViewById(R.id.suggestion_text);
                    String city_text_value = cursor.getString(columnIndex);
                    city_text.setText(getString(R.string.city_cursor_string, city_text_value));

                    return true;

                } else if (view.getId() == R.id.country_text) {

                    TextView country_text = view.findViewById(R.id.country_text);
                    String country_text_value = cursor.getString(columnIndex);
                    country_text.setText(getString(R.string.country_cursor_string, country_text_value));

                    return true;

                } else if (view.getId() == R.id.extra_info) {

                    TextView extra_info = view.findViewById(R.id.extra_info);
                    extra_info.setText(cursor.getString(columnIndex));

                    String extra_info_text = cursor.getString(columnIndex);
                    String check_Country = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATABASE_ISO2));

                    // Log.d(LOG_TAG, "Country - " + check_Country);

                    if (check_Country.equals(Constants.UNITED_STATES_EXTRA_INFO)) {
                        extra_info.setText(getString(R.string.extra_cursor_string, extra_info_text));
                    } else {
                        extra_info.setText("");
                    }

                    return true;

                } else {
                    return false;
                }
            }
        });

        searchView.setSuggestionsAdapter(suggestionAdapter);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                searchView.clearFocus();
                searchView.setFocusable(false);

                CursorAdapter cursorAdapter = searchView.getSuggestionsAdapter();
                Cursor cursor = cursorAdapter.getCursor();
                cursor.moveToPosition(position);

                String clicked_city = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATABASE_CITY));
                String clicked_lat = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATABASE_LAT));
                String clicked_lng = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATABASE_LNG));

                searchView.setQuery(clicked_city, false);

                callSearch(clicked_city, clicked_lat, clicked_lng);

                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.setFocusable(false);
                callSearch(query, null, null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Cursor cursor = databaseHelper.getSuggestions(newText);

                if (cursor.getCount() > 0)
                    suggestionAdapter.changeCursor(cursor);

                return false;
            }
        });
    }

    public void callSearch(String query, String lat, String lng) {

        item_search.collapseActionView();

        if (lat == null) {

            Log.d(LOG_TAG, "You Searched - " + query);

            MySharedPreferences.setBooleanPref(MainActivity.this, Constants.coordinate_search_key, false);
            MySharedPreferences.setNormalPref(MainActivity.this, Constants.edit_text_location_key, query);

        } else {

            Log.d(LOG_TAG, "LAT - " + lat);
            Log.d(LOG_TAG, "LNG - " + lng);

            MySharedPreferences.setBooleanPref(MainActivity.this, Constants.coordinate_search_key, true);
            MySharedPreferences.setNormalPref(MainActivity.this, Constants.latitude_pref_key, lat);
            MySharedPreferences.setNormalPref(MainActivity.this, Constants.longitude_pref_key, lng);

        }

        getWeatherData();

    }

    public void circleReveal(final boolean containsOverflow, final boolean isShow) {

        // make the view visible and start the animation
        final int startAnimFrom = 2;

        searchToolbar.post(new Runnable() {
            @Override
            public void run() {

                int width = searchToolbar.getWidth();

                width -= (startAnimFrom * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);

                if (containsOverflow)
                    width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

                int cx = width;
                int cy = searchToolbar.getHeight() / 2;

                Animator anim;

                if (isShow)
                    anim = ViewAnimationUtils.createCircularReveal(searchToolbar, cx, cy, 0, (float) width);
                else
                    anim = ViewAnimationUtils.createCircularReveal(searchToolbar, cx, cy, (float) width, 0);

                // make the view invisible when the animation is done
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!isShow) {
                            searchToolbar.setVisibility(View.GONE);
                            setVisibilityToolbar(true);
                            super.onAnimationEnd(animation);
                        }
                    }
                });

                anim.setDuration(220);

                if (isShow)
                    searchToolbar.setVisibility(View.VISIBLE);
                // start the animation
                anim.start();

            }
        });

    }

}