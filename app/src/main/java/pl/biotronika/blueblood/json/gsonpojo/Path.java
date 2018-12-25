
package pl.biotronika.blueblood.json.gsonpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Path {

    @SerializedName("alias")
    @Expose
    private Object alias;
    @SerializedName("pid")
    @Expose
    private Object pid;
    @SerializedName("langcode")
    @Expose
    private String langcode;

    public Object getAlias() {
        return alias;
    }

    public void setAlias(Object alias) {
        this.alias = alias;
    }

    public Object getPid() {
        return pid;
    }

    public void setPid(Object pid) {
        this.pid = pid;
    }

    public String getLangcode() {
        return langcode;
    }

    public void setLangcode(String langcode) {
        this.langcode = langcode;
    }

}
