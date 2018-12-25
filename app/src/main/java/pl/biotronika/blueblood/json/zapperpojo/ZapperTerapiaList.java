package pl.biotronika.blueblood.json.zapperpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ZapperTerapiaList {

    public ZapperTerapiaList() {
        zapperTerapiaList = new ArrayList<ZapperTerapia>();
    }

    @SerializedName("terapie")
    @Expose
    private List<ZapperTerapia> zapperTerapiaList;

    public List<ZapperTerapia> getZapperTerapiaList() {
        return zapperTerapiaList;
    }

    public void setZapperTerapiaList(List<ZapperTerapia> zapperTerapiaList) {
        this.zapperTerapiaList = zapperTerapiaList;
    }
}
