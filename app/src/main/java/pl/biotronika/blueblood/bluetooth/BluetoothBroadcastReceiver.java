package pl.biotronika.blueblood.bluetooth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;

import pl.biotronika.blueblood.R;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    final String TAG = "BluetoothBroadcastReceiver";
    final String PATH_SETTINGS_PKG = "com.android.settings";
    final String PATH_SETTINGS_BLUETOOTH = ".bluetooth.BluetoothSettings";
    final int ID = 46709394;

    SharedPreferences prefs;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;

        String tickerText = null;
        String contentText = "Address: " + device.getAddress();

        String action = intent.getAction();
        Log.d(TAG, action);

        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
            Log.d(TAG, "Bond state changed to " + state);

            if (state == BluetoothDevice.BOND_BONDED
                    && prefs.getBoolean(Key.PAIRED, true)) {
                tickerText = "Paired with " + device.getName();
            }
            else if (state == BluetoothDevice.BOND_BONDING
                    && prefs.getBoolean(Key.PAIRING, true)) {
                tickerText = "Pairing with " + device.getName() + "...";
            }
            else if (state == BluetoothDevice.BOND_NONE
                    && prefs.getBoolean(Key.UNPAIRED, true)) {
                tickerText = "Unpaired with " + device.getName();
            }
            else return;
        }
        else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)
                && prefs.getBoolean(Key.CONNECTED, true)) {
            Log.d(TAG, "Connected");
            tickerText = "Connected to " + device.getName();
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)
                && prefs.getBoolean(Key.DISCONNECTED, true)) {
            Log.d(TAG, "Disconnected");
            tickerText = "Disconnected from " + device.getName();
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)
                && prefs.getBoolean(Key.DISCONNECT_REQUESTED, true)) {
            Log.d(TAG, "Disconnect requested");
            tickerText = "Request disconnect from " + device.getName();
        }
        else return;

        notification = constructNotification(context, tickerText, tickerText, contentText);
        manager.notify(ID, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Notification constructNotification(Context context, String tickerText, String titleText, String contentText) {


        Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
        notificationIntent.setClassName(PATH_SETTINGS_PKG, PATH_SETTINGS_PKG + PATH_SETTINGS_BLUETOOTH);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);


        Notification.Builder builder = new Notification.Builder(context)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.logo)
                .setWhen(System.currentTimeMillis())
                .setTicker(tickerText)
                .setContentTitle(titleText);
        Notification notification = builder.build();

        notification.tickerText = tickerText;

        if (prefs.getBoolean(Key.LIGHTS, true)) {
            notification.defaults |= Notification.DEFAULT_LIGHTS;
        }
        else if (prefs.getBoolean(Key.SOUND, true)) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        }
        else if (prefs.getBoolean(Key.VIBRATE, true)) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        return notification;
    }


    final class Key {
        public static final String PAIRED = "key_paired";
        public static final String PAIRING = "key_pairing";
        public static final String UNPAIRED = "key_unpaired";
        public static final String CONNECTED = "key_connected";
        public static final String DISCONNECTED = "key_disconnected";
        public static final String DISCONNECT_REQUESTED = "key_disconnect_requested";

        public static final String LIGHTS = "key_lights";
        public static final String SOUND = "key_sound";
        public static final String VIBRATE = "key_vibrate";
    }
}