package com.vullnetlimani.weatherapp.about;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.Snackbar;
import com.vullnetlimani.weatherapp.Activity.BaseActivity;
import com.vullnetlimani.weatherapp.BuildConfig;
import com.vullnetlimani.weatherapp.R;

import java.util.Calendar;

public class About extends BaseActivity {

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupTheme();
        setTheme(Theme);
        setContentView(R.layout.activity_about);

        setupToolbar();
        setToolbarBackIcon();

        initCore();
    }

    private void initCore() {

        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);

        TextView copyRightTextView = findViewById(R.id.copyRightTextView);
        String copyrights = getString(R.string.copyRightMessage, Calendar.getInstance().get(Calendar.YEAR));
        copyRightTextView.setText(copyrights);

        AboutModel aboutModel = new AboutModel(About.this);

        String versionNumber = getString(R.string.versionNumber, BuildConfig.VERSION_NAME);
        aboutModel.addCustomItem(R.drawable.ic_information, MaterialColors.getColor(this, R.attr.aboutItemColor, Color.WHITE), versionNumber);
        aboutModel.addCustomItem(R.drawable.creative_hub, 0, getString(R.string.creative_hub));
        aboutModel.addEmail(getString(R.string.email_address), getString(R.string.email_dev));
        aboutModel.addWebsite(getString(R.string.website_url), getString(R.string.website));
        aboutModel.addFacebook(getString(R.string.facebook_username), getString(R.string.facebook_message));
        aboutModel.addInstagram(getString(R.string.insta_username), getString(R.string.instagram_message));
        aboutModel.addYoutube(getString(R.string.youtube_channel_id), getString(R.string.sub_on_youtube));
        aboutModel.addPlayStore(getPackageName(), getString(R.string.play_store));
        aboutModel.addGitHub(getString(R.string.github_username), getString(R.string.github));

        AboutRecyclerAdapter aboutRecyclerAdapter = new AboutRecyclerAdapter(About.this, aboutModel.getMyAboutListModel());
        LinearLayoutManager layoutManager = new LinearLayoutManager(About.this);

        recyclerView.setLayoutManager(layoutManager);

        aboutRecyclerAdapter.setItemClickListener(new AboutRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Intent intent) {

                switch (position) {
                    case 0:
                        SnackBarMessage(getString(R.string.latest_version));
                        break;
                    case 1:
                        SnackBarMessage(getString(R.string.join_the_academy));
                        break;
                    default:

                        if (intent != null) {

                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                SnackBarMessage(getString(R.string.not_installed));
                            }

                        }
                        break;
                }


            }
        });

        recyclerView.setAdapter(aboutRecyclerAdapter);
    }

    public void SnackBarMessage(String message) {

        Snackbar snackbar = Snackbar.make(findViewById(R.id.aboutParentLayout), message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(MaterialColors.getColor(this, R.attr.floatButtonColor, Color.WHITE))
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setActionTextColor(MaterialColors.getColor(this, R.attr.floatButtonRippleColor, Color.WHITE))
                .setTextColor(MaterialColors.getColor(this, R.attr.floatButtonRippleColor, Color.WHITE));
        snackbar.show();

    }

}