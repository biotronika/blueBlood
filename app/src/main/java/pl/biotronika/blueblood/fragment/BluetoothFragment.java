package pl.biotronika.blueblood.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import pl.biotronika.blueblood.BiotronikaApplication;
import pl.biotronika.blueblood.R;
import pl.biotronika.blueblood.bluetooth.BluetoothCommands;
import pl.biotronika.blueblood.bluetooth.BluetoothManager;
import pl.biotronika.blueblood.bluetooth.BluetoothManagerListener;
import pl.biotronika.blueblood.bluetooth.DeviceStatus;

@EFragment(R.layout.fragment_bluetooth)
public class BluetoothFragment extends Fragment {

    @Bean
    BluetoothManager bluetoothManager;

    final String TAG = "BF";
    String[] freePEMFprograms = new String[]{"Memory Program", "1: Standard program", "2: Earth regeneration", "3: Antistress & meditation", "4: Pineal gland"};

    private BluetoothManagerListener bluetoothManagerListener;

    @ViewById(R.id.entry)
    EditText entryEditText;

    @ViewById(R.id.log_window)
    EditText logWindow;

    @ViewById(R.id.adapter_status_icon)
    ImageView adapterStatus;

    @ViewById(R.id.device_connection_icon)
    ImageView deviceConnectionStatus;

    @ViewById(R.id.devices_dropdown_list)
    Spinner devicesDropdown;

    @ViewById(R.id.device_connection_switch)
    Switch deviceConnectionSwitch;

    @ViewById(R.id.device_status_name)
    TextView deviceNameText;

    @ViewById(R.id.device_status_battery)
    TextView batteryText;

    @ViewById(R.id.device_status)
    TextView deviceStatusText;

    @ViewById(R.id.send)
    ImageButton sendButton;

    @ViewById(R.id.power_on_off)
    ImageButton powerButton;

    @ViewById(R.id.memory_info)
    ImageButton memoryButton;

    @ViewById(R.id.play)
    ImageButton playButton;

    @ViewById(R.id.stop)
    ImageButton stopButton;

    @ViewById(R.id.programs_dropdown_list)
    Spinner programsDropdown;

    int programPosition = 0;


    boolean isProgramRunning = false;
    boolean isPause = false;

    @ViewById(R.id.progress_bar_busy)
    ProgressBar spinner;

    @ViewById(R.id.progress_bar_timer)
    ProgressBar timerProgressBar;

    IntentFilter connectionFilter = new IntentFilter();
    private final BroadcastReceiver connectionBroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    localAppendLog("connectionBroadcast: ACTION_ACL_CONNECTED");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    localAppendLog("connectionBroadcast: ACTION_ACL_DISCONNECTED");
                    getBluetoothManager().closeConnection();
                    setSwtitches();
                    break;
            }
        }
    };

    @AfterViews
    protected void initViews() {

        regiterListener();

        connectionFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        connectionFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        getActivity().registerReceiver(connectionBroadcast, connectionFilter);

        spinner.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, freePEMFprograms);
        programsDropdown.setAdapter(adapter);

        deviceConnectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                connectDevice(isChecked);
            }
        });

        //Send Button
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (!getBluetoothManager().sendCommand(entryEditText.getText().toString())) {
                            // błąd
                            setDeviceConnectionStatus(2);
                        }
                        closeKeyboard();
                    }
                });
            }
        });

        //Send Button
        powerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getBluetoothManager().powerOnOff();
                setSwtitches();
            }
        });


        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        // add your code here
                        play();
                    }
                });
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        // add your code here
                        stop();
                    }
                });

            }
        });


        memoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        getBluetoothManager().sendCommand(BluetoothCommands.DISPLAY_MEMORY);
                    }
                });

            }
        });

        programsDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                programPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        devicesDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        // add your code here
                        getBluetoothManager().setAdapterName(position);
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        timerProgressBar.setProgress(0);
        timerProgressBar.setVisibility(View.GONE);

        setSwtitches();
        setDevicesDropdown();

        devicesDropdown.setSelection(getBluetoothManager().getAdapterId());
        programsDropdown.setSelection(getBluetoothManager().getProgram());

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BiotronikaApplication.getContext());

        boolean showCommandBar = prefs.getBoolean("key_show_command_bar", false);
        if (showCommandBar) {
            entryEditText.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.VISIBLE);
        } else {
            entryEditText.setVisibility(View.GONE);
            sendButton.setVisibility(View.GONE);
        }
        boolean showLogWindow = prefs.getBoolean("key_show_log_window", false);
        if (showLogWindow) {
            logWindow.setVisibility(View.VISIBLE);
        } else {
            logWindow.setVisibility(View.GONE);
        }

        boolean autoConnect = prefs.getBoolean("key_auto_connect", false);
        if (autoConnect) {
            connectDevice(true);
        }

    }


    public void showProgramDialog(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.popup, null);

        TextView textview = dialogView.findViewById(R.id.popup_message);
        textview.setText(message);

        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Memory program");


        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        b.show();
    }

    void setDevicesDropdown() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getBluetoothManager().getAdapterList());
        devicesDropdown.setAdapter(adapter);
    }

    void connectDevice(final boolean isChecked) {
        spinner.setVisibility(View.VISIBLE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // add your code here

                if (isChecked) {
                    getBluetoothManager().openConnection();
                } else {

                    getBluetoothManager().closeConnection();
                }
                setSwtitches();

                spinner.setVisibility(View.GONE);
            }
        });
    }


    void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null)
            inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), 0);
    }


    void setSwtitches() {

        setAdapterStatus(getBluetoothManager().isBluetoothEnabled());

        deviceConnectionSwitch.setChecked(getBluetoothManager().isDeviceConnected());
        setDeviceConnectionStatus(getBluetoothManager().isDeviceConnected() ? 1 : 0);

        localUpdateDevice();
        localUpdateStatus();

        reEnableControls();
    }

    void localUpdateStatus() {

        programsDropdown.setSelection(getBluetoothManager().getProgram());

        batteryText.setText(getBluetoothManager().getBattery());
        setDeviceStatusLabel(getBluetoothManager().getDeviceStatus().toString());

        if (DeviceStatus.WORKING.equals(getBluetoothManager().getDeviceStatus())) {
            isProgramRunning = true;
            timerProgressBar.setVisibility(View.VISIBLE);
        } else if (DeviceStatus.PAUSED.equals(getBluetoothManager().getDeviceStatus())) {
            isPause = true;
            isProgramRunning = true;
            timerProgressBar.setVisibility(View.VISIBLE);
        } else {
            isPause = false;
            isProgramRunning = false;
        }

        reEnableControls();
    }

    void localUpdateDevice() {
        batteryText.setText(getBluetoothManager().getBattery());
        deviceNameText.setText(getBluetoothManager().getDeviceName());
    }

    void setAdapterStatus(boolean _value) {
        if (_value) {
            adapterStatus.setImageResource(R.drawable.ic_bluetooth_black_24dp);
            adapterStatus.setColorFilter(getResources().getColor(R.color.green));
        } else {
            adapterStatus.setImageResource(R.drawable.ic_bluetooth_disabled_black_24dp);
            adapterStatus.setColorFilter(getResources().getColor(R.color.button_grey));
        }
    }

    void setDeviceStatusLabel(String _value) {

        deviceStatusText.setText(_value);

        if (null == _value)
            return;

        if (_value.equals("READY")) {
            deviceStatusText.setTextColor(getActivity().getResources().getColor(R.color.green));
            timerProgressBar.setVisibility(View.GONE);
        } else if (_value.equals("OFF")) {
            deviceStatusText.setTextColor(getActivity().getResources().getColor(R.color.button_grey));
            timerProgressBar.setVisibility(View.GONE);
        } else {
            deviceStatusText.setTextColor(getActivity().getResources().getColor(R.color.red));
        }

    }

    void setDeviceConnectionStatus(int _value) {
        if (_value == 1) {
            deviceConnectionStatus.setImageResource(R.drawable.ic_phonelink_black_24dp);
            deviceConnectionStatus.setColorFilter(getResources().getColor(R.color.green));
        } else if (_value == 0) {
            deviceConnectionStatus.setImageResource(R.drawable.ic_phonelink_off_black_24dp);
            deviceConnectionStatus.setColorFilter(getResources().getColor(R.color.button_grey));
        } else if (_value == 2) {
            deviceConnectionStatus.setImageResource(R.drawable.ic_phonelink_erase_black_24dp);
            deviceConnectionStatus.setColorFilter(getResources().getColor(R.color.red));
        }
    }

    void setPowerButtonIcons() {

        powerButton.setEnabled(getBluetoothManager().isDeviceConnected());
        if (getBluetoothManager().isDeviceConnected()) {

            if (getBluetoothManager().isDevicePowerOff()) {
                powerButton.setImageResource(R.drawable.ic_power_settings_new_black_24dp);
                powerButton.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
            } else {
                powerButton.setImageResource(R.drawable.ic_close_black_24dp);
                powerButton.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
            }
        } else {
            powerButton.setImageResource(R.drawable.ic_cancel_black_24dp);
            powerButton.setColorFilter(getResources().getColor(R.color.button_grey), PorterDuff.Mode.SRC_ATOP);
        }
    }

    void setPlayerButtonIcons() {
        if (isProgramRunning) {
            if (isPause) {
                playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                playButton.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
            } else {
                playButton.setImageResource(R.drawable.ic_pause_black_24dp);
                playButton.setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
            }
        } else {
            playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            playButton.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
        }
        stopButton.setEnabled(isProgramRunning);
    }


    void reEnableControls() {

        setPowerButtonIcons();
        setPlayerButtonIcons();

        powerButton.setEnabled(getBluetoothManager().isDeviceConnected());
        enableCommandControls(!getBluetoothManager().isDevicePowerOff() && getBluetoothManager().isDeviceConnected());
        setupMemoryButton(bluetoothManager.isDeviceReady());
        devicesDropdown.setEnabled(!getBluetoothManager().isDeviceConnected());
    }

    void enableCommandControls(boolean _value) {
        sendButton.setEnabled(_value);
        entryEditText.setEnabled(_value);
        programsDropdown.setEnabled(!isProgramRunning);
        if (getBluetoothManager().isDevicePowerOff()) {
            programsDropdown.setEnabled(false);
        }
        playButton.setEnabled(_value);

        stopButton.setEnabled(isProgramRunning);

        if (!_value) {
            playButton.setColorFilter(getResources().getColor(R.color.button_grey), PorterDuff.Mode.SRC_ATOP);
            stopButton.setColorFilter(getResources().getColor(R.color.button_grey), PorterDuff.Mode.SRC_ATOP);
        } else {
            stopButton.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        }


    }

    void setupMemoryButton(boolean _value) {

        memoryButton.setEnabled(_value);

        if (_value) {
            memoryButton.setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_ATOP);
        } else {
            memoryButton.setColorFilter(getResources().getColor(R.color.button_grey), PorterDuff.Mode.SRC_ATOP);
        }
    }


    void play() {

        if (!isProgramRunning) {
            getBluetoothManager().setProgram(programPosition);

            if (programPosition > 0) {
                getBluetoothManager().sendCommand(BluetoothCommands.PROG + programPosition);
            } else {
                getBluetoothManager().sendCommand(BluetoothCommands.PROG_MEMORY);
            }
            timerProgressBar.setVisibility(View.VISIBLE);
            isProgramRunning = true;
        } else {

            if (isPause) {
                getBluetoothManager().sendCommand(BluetoothCommands.START);
            } else {
                getBluetoothManager().sendCommand(BluetoothCommands.PAUSE);
            }
            isPause = !isPause;
        }
        reEnableControls();
    }

    void stop() {
        if (isProgramRunning) {
            getBluetoothManager().sendCommand(BluetoothCommands.STOP);
            isProgramRunning = false;
            isPause = false;
            timerProgressBar.setVisibility(View.GONE);
        }
        reEnableControls();
    }


    @Override
    public void onDestroy() {
        unregisterListener();

        super.onDestroy();
        localAppendLog("onDestroy()");

        try {
            getActivity().unregisterReceiver(connectionBroadcast);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onResume() {

        super.onResume();
        localAppendLog("onResume()");
        getBluetoothManager().sendCommand(BluetoothCommands.STATUS);
        getActivity().registerReceiver(connectionBroadcast, connectionFilter);
    }


    @Override
    public void onPause() {

        super.onPause();
        localAppendLog("onPause()");
        try {
            getActivity().unregisterReceiver(connectionBroadcast);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setSwtitches();
    }

    public void localAppendLog(String s) {
        if (null != logWindow)
            logWindow.append(s + "\n");
    }

    BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }

    void regiterListener() {

        if (bluetoothManagerListener != null)
            return;

        bluetoothManagerListener = new BluetoothManagerListener() {
            public void appendLog(final String s) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // add your code here
                        localAppendLog(s);
                    }
                });

            }

            public void updateStatus() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // add your code here
                        localUpdateStatus();
                    }
                });

            }

            public void updateDevice() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // add your code here
                        localUpdateDevice();
                    }
                });

            }

            public void updateAllControls() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // add your code here
                        setSwtitches();
                    }
                });
            }

            public void timerTick(final int _value) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // add your code here
                        timerProgressBar.setProgress(_value);
                    }
                });
            }


            public void updateMemoryProgram(final String program) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // add your code here
                        showProgramDialog(program);
                    }
                });
            }
        };

        bluetoothManager.addListener(bluetoothManagerListener);

    }


    void unregisterListener() {
        if (bluetoothManagerListener != null)
            bluetoothManager.removeListener(bluetoothManagerListener);
    }

}