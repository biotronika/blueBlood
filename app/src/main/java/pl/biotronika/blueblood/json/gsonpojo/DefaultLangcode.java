
package pl.biotronika.blueblood.json.gsonpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DefaultLangcode {

    @SerializedName("value")
    @Expose
    private boolean value;

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

}
