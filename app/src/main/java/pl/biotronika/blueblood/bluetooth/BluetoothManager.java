package pl.biotronika.blueblood.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;
import pl.biotronika.blueblood.BiotronikaApplication;

@EBean(scope = EBean.Scope.Singleton)
public class BluetoothManager {

    private List<BluetoothManagerListener> _listeners = new CopyOnWriteArrayList<BluetoothManagerListener>();

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker = true;
    final byte delimiter = 10;
    double programLenght = 1;
    int programNo = 0;
    String frequency = "";
    String adapterName = "HC-05";  //
    String memoryProgram = "";
    boolean recordProgram = false;
    List<String> deviceFilterList = new ArrayList<String>();


    public BluetoothManager() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isBluetoothEnabled() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    public boolean isDeviceConnected() {
        return !stopWorker;
    }
    public int getProgram() {
        return programNo;
    }
    public void setProgram(int prog) {
        programNo=prog;
    }


    public boolean isDevicePaired() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getName().equals(adapterName)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    public boolean enableBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            appendLog("Bluetooth adapter enabled");
        } else {
            appendLog("Bluetooth adapter ALREADY enabled");
        }
        return true;
    }

    public boolean diasbleBluetooth() {
        if (mBluetoothAdapter.isEnabled()) {
            if (isDeviceConnected()) {
                closeConnection();
            }
            mBluetoothAdapter.disable();
            appendLog("Bluetooth adapter disabled");
        }
        return true;
    }

    void bondDevice() {

        if (mBluetoothAdapter == null) {
            appendLog("No bluetooth adapter available");
            return;
        }

        enableBluetooth();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(adapterName)) {
                    mmDevice = device;
                    break;
                }
            }
        }
        appendLog("Bluetooth Device Found");
    }


    public String[] getAdapterList() {

        List<String> devicesList = new ArrayList<String>();

        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                createAdapterFilter();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        if (filterDevice(device.getName())) {
                            devicesList.add(device.getName() + " - " + device.getAddress());
                        }
                    }
                }
            }
        }

        String[] devicesArray = new String[devicesList.size()];
        return devicesList.toArray(devicesArray);
    }

    List<String> getAdapterNames() {

        List<String> devicesList = new ArrayList<String>();

        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {

                createAdapterFilter();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        if (filterDevice(device.getName())) {
                            devicesList.add(device.getName());
                        }
                    }
                }
            }
        }

        return devicesList;
    }

    public void setAdapterName(int positionId) {
        adapterName = getAdapterNames().get(positionId);
    }


    public int getAdapterId() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {

                    int i = 0;
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getName().equals(adapterName)) {
                            return i;
                        }
                        i++;
                    }
                }
            }
        }

        return 0;
    }

    public void openConnection() {


        if (!isDeviceConnected()) {


            bondDevice();

            try {

                try {

                    if (mmDevice != null) {
                        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
                        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                        mmSocket.connect();
                    } else {
                        appendLog("No bluetooth deviceName");
                    }
                } catch (IOException ioException) {

                    appendLog("openConnection: Trying bluetooth fallback");

                    mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
                    mmSocket.connect();

                } catch (Exception e) {
                    stopWorker = true;
                    throw e;
                }

                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();

                beginListenForData();

            } catch (Exception ex) {
                stopWorker = true;
                ex.printStackTrace();
            }
        } else {
            appendLog("Device ALREADY connected");
        }

        appendLog("Bluetooth Opened");
        sendCommand(BluetoothCommands.STATUS);
        sendCommand(BluetoothCommands.DEVICE_NAME);


    }

    void beginListenForData() {
        final Handler handler = new Handler();


        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            parseResponse(data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                        appendLog("beginListenForData Exception:  " + ex.getLocalizedMessage());
                        ex.printStackTrace();
                    }
                }
                appendLog("beginListenForData interrupted");
            }
        });

        if (!stopWorker)
            workerThread.start();
    }


    public boolean uploadProgram(String _program) {

        boolean ret = true;
        ret &= sendCommand("mem\n");
        _program = _program.replace("\r", "");
        String[] lines = _program.split("\n");
        for (String toSend : lines) {
            if (!toSend.startsWith("@")) {
                ret &= sendCommand(toSend);
                appendLog("SEND: " + toSend);
            }
        }
        ret &= sendCommand("@");
        return ret;
    }

    public boolean sendCommand(String msg) {

        if (stopWorker)
            return false;

        try {
            msg += "\n";
            Log.i("SC", new String(msg.getBytes(), "UTF8"));
            mmOutputStream.write(msg.getBytes());

            return true;
        } catch (Exception ex) {
            stopWorker = true;
            appendLog("sendCommand Exception:  " + ex.getLocalizedMessage());
            closeConnection();
            updateAllControls();
            return false;
        }
    }

    public void closeConnection() {
        deviceStatus = DeviceStatus.OFF;
        deviceName = "";
        battery = "";

        try {
            stopWorker = true;
            if (null != mmOutputStream) {
                mmOutputStream.close();
            }
            if (null != mmInputStream)
                mmInputStream.close();
            if (null != mmSocket)
                mmSocket.close();

            appendLog("Bluetooth Closed\n");
        } catch (IOException ex) {
            stopWorker = true;
            appendLog("closeConnection Exception:  " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }

    void parseResponse(final String _response) {
        if (null == _response)
            return;

        String line = _response;

        if (_response.startsWith("R:")) {
            StringTokenizer st = new StringTokenizer(_response, ":");
            st.nextElement();
            st.nextElement();
            if (_response.startsWith("R:STATUS")) {
                deviceStatus = DeviceStatus.fromString(st.nextElement().toString());
                updateStatus();
            } else if (_response.startsWith("R:DEVICE:")) {
                deviceName = st.nextElement().toString();
                updateDevice();
            } else if (_response.startsWith("R:LS:BEGIN")) {
                st.nextElement();
                memoryProgram = "";
                recordProgram = true;
                line = null;
            } else if (_response.startsWith("R:LS:END")) {
                st.nextElement();
                recordProgram = false;
                updateMemoryProgram(memoryProgram);
            }
            double val = Double.parseDouble(st.nextElement().toString());
            battery = val / 10 + " V";
        } else if (_response.startsWith("P:")) {
            StringTokenizer st = new StringTokenizer(_response, ":");
            st.nextElement();
            st.nextElement();
            if (_response.startsWith("P:LEN")) {
                programLenght = Double.parseDouble(st.nextElement().toString());
            } else if (_response.startsWith("P:RUN")) {
                programNo = Integer.parseInt(st.nextElement().toString());
                double second = Double.parseDouble(st.nextElement().toString());
                frequency = st.nextElement().toString();

                int progress = (int) ((second * 100.0) / programLenght);
                timerTick(progress);
            }
        }

        if (recordProgram && line != null) {
            memoryProgram += line + '\n';
        }

        appendLog(_response);
    }

    public boolean isDevicePowerOff() {
        return DeviceStatus.OFF.equals(deviceStatus);
    }

    public boolean isDeviceReady() {
        return DeviceStatus.READY.equals(deviceStatus);
    }

    public void powerOnOff() {
        if (isDevicePowerOff()) {
            sendCommand(BluetoothCommands.ON);
        } else {
            sendCommand(BluetoothCommands.OFF);
        }
    }

    @Getter
    private DeviceStatus deviceStatus = DeviceStatus.OFF;
    @Getter
    private String deviceName = "";
    @Getter
    private String battery = "";

    public void addListener(final BluetoothManagerListener listener) {
        if (null != listener) {
            _listeners.add(listener);
        }
    }

    public void removeListener(final BluetoothManagerListener listener) {
        if (null != listener) {
            _listeners.remove(listener);
        }
    }


    private void appendLog(String s) {
        for (BluetoothManagerListener listener : _listeners) {
            listener.appendLog(s);
        }
        Log.i("BM", s);
    }

    private void updateStatus() {
        for (BluetoothManagerListener listener : _listeners) {
            listener.updateStatus();
        }
    }

    private void updateDevice() {
        for (BluetoothManagerListener listener : _listeners) {
            listener.updateDevice();
        }
    }

    private void updateAllControls() {
        for (BluetoothManagerListener listener : _listeners) {
            listener.updateAllControls();
        }
    }

    private void timerTick(int _value) {
        for (BluetoothManagerListener listener : _listeners) {
            listener.timerTick(_value);
        }
    }

    private void updateMemoryProgram(String program) {
        for (BluetoothManagerListener listener : _listeners) {
            listener.updateMemoryProgram(program);
        }
    }

    void createAdapterFilter() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BiotronikaApplication.getContext());
        Set<String> deviceSet = prefs.getStringSet("key_filter_bluetooth_devices", null);

        deviceFilterList = new ArrayList<String>();

        if (deviceSet != null) {
            for (String s : deviceSet) {

                if (s.equals("1")) {
                    deviceFilterList.add("HC-05");
                } else if (s.equals("2")) {
                    deviceFilterList.add("freePEMF");
                } else if (s.equals("3")) {
                    deviceFilterList.add("multiZAP++");
                }
            }
        }
    }

    boolean filterDevice(String name) {
        for (String filterName: deviceFilterList) {
            if (name.startsWith(filterName))
                return true;
        }
        return  false;
    }

}
