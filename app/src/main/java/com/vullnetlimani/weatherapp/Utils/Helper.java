package com.vullnetlimani.weatherapp.Utils;

import android.transition.Slide;

import androidx.appcompat.app.AppCompatActivity;

public class Helper {

    public static void makeCustomTransition(AppCompatActivity appCompatActivity, int id) {
        Slide slide = new Slide();
        slide.excludeTarget(id, true);
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        appCompatActivity.getWindow().setEnterTransition(slide);
        appCompatActivity.getWindow().setExitTransition(slide);
    }

}
