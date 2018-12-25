package pl.biotronika.blueblood.bluetooth;

public enum DeviceStatus {
    READY("READY"),
    OFF("OFF"),
    WORKING("WORKING"),
    PAUSED("PAUSED");

    private final String text;
    DeviceStatus(final String text) {
        this.text = text;
    }
    @Override
    public String toString() {
        return text;
    }

    public boolean equals(DeviceStatus _input) {
        return _input.toString().equals(text);
    }

    public static DeviceStatus fromString(String text) {
        for (DeviceStatus b : DeviceStatus.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }


}