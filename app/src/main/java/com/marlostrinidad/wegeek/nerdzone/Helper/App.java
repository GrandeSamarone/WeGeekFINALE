package com.marlostrinidad.wegeek.nerdzone.Helper;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.appcompat.app.AppCompatDelegate;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.marlostrinidad.wegeek.nerdzone.BuildConfig;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;


import java.io.File;

import static android.app.UiModeManager.MODE_NIGHT_AUTO;

/**
 * Created by fulanoeciclano on 19/05/2018.
 */

public class App extends MultiDexApplication {

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate() {
        super.onCreate();



        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseApp.initializeApp(this);
        MultiDex.install(this);
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(getApplicationContext())
                .setBaseDirectoryPath(new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),"hq"))
                .setBaseDirectoryName(String.valueOf(R.string.app_name))
                .setMaxCacheSize(200*1024*1024)//200MB
                .build();
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();
        Fresco.initialize(this, imagePipelineConfig);
        EmojiManager.install(new GoogleEmojiProvider());


        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_AUTO);



        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().build());
        }

    }




}