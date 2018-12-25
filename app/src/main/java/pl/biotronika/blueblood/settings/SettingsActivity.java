package pl.biotronika.blueblood.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import pl.biotronika.blueblood.BiotronikaApplication;
import pl.biotronika.blueblood.R;
import pl.biotronika.blueblood.json.TerapieApi;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.settings_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BiotronikaApplication.getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    public static class SettingsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("key_enable_extended_therapies")
                || key.equals("key_dev_freepemf")
                || key.equals("key_dev_multizap")
                || key.equals("key_device_filter")
                || key.equals("key_enable_basic_therapies")
                || key.equals("key_enable_pitchfork_therapies")
                ) {
            TerapieApi.resetList();
        }
    }

}
