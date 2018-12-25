
package pl.biotronika.blueblood.json.gsonpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Body {

    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("format")
    @Expose
    private String format;
    @SerializedName("processed")
    @Expose
    private String processed;
    @SerializedName("summary")
    @Expose
    private String summary;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

}
