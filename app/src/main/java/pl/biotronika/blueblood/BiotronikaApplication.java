package pl.biotronika.blueblood;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class BiotronikaApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        BiotronikaApplication.context = getApplicationContext();
    }

    public static Context getContext() {
        return BiotronikaApplication.context;
    }
}