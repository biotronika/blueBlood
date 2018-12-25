
package pl.biotronika.blueblood.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import pl.biotronika.blueblood.R;
import pl.biotronika.blueblood.bluetooth.BluetoothManager;
import pl.biotronika.blueblood.json.TerapieApi;

@EActivity
public class DetailActivity extends AppCompatActivity {

    @Bean
    BluetoothManager bluetoothManager;

    public static final String EXTRA_POSITION = "position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.detail_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final int postion = getIntent().getIntExtra(EXTRA_POSITION, 0);
        final Resources resources = getResources();
        String[] titles = TerapieApi.getNames();

        getSupportActionBar().setTitle(titles[postion % titles.length]);


        String[] placeDetails = TerapieApi.getDescriptions();
        WebView placeDetail = (WebView) findViewById(R.id.place_detail);
        placeDetail.loadDataWithBaseURL(null, placeDetails[postion % placeDetails.length], "text/html", "utf-8", null);

        String[] placeScripts = TerapieApi.getScripts();
        TextView placeSrcipt = (TextView) findViewById(R.id.script_value);
        placeSrcipt.setText(placeScripts[postion % placeScripts.length]);


        String[] placeDevices = TerapieApi.getDevices();
        TextView placedevice = (TextView) findViewById(R.id.device_value);
        placedevice.setText(placeDevices[postion % placeDevices.length]);

        FloatingActionButton fab = findViewById(R.id.fab);
        if (bluetoothManager.isDeviceReady()) {
            fab.show();
        } else {
            fab.hide();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean _result = false;
                if (bluetoothManager.isDeviceReady()) {
                    String[] placeScripts = TerapieApi.getScripts();
                    _result = bluetoothManager.uploadProgram(placeScripts[postion % placeScripts.length]);
                }

                if (_result) {
                    Snackbar.make(v, "Program uploaded",
                            Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(v, "Program upload error",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });


        if (TerapieApi.getFilter() != null) {
            TerapieApi.clearFilter();
            TerapieApi.resetList();
        }
    }
}
