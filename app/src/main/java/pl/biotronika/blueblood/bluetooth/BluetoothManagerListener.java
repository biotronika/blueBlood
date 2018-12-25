package pl.biotronika.blueblood.bluetooth;

public interface BluetoothManagerListener {

    void appendLog(String s);

    void updateStatus();

    void updateDevice();

    void updateAllControls();

    void timerTick(int _value);

    void updateMemoryProgram(String program);
}