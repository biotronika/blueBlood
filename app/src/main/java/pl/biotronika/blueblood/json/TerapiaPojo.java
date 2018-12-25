package pl.biotronika.blueblood.json;

import java.util.ArrayList;
import java.util.List;

import pl.biotronika.blueblood.json.gsonpojo.FieldUrzadzenie;
import pl.biotronika.blueblood.json.gsonpojo.Terapia;
import pl.biotronika.blueblood.json.zapperpojo.ZapperTerapia;

public class TerapiaPojo {

    private long nid; // title
    private String name; // title
    private String description; // body
    private String script; // field_skrypt
    private List<String> devices; // field_urzadzenie

    public TerapiaPojo(Terapia terapia) {

        if (null == terapia) {
            return;
        }

        if (terapia.getNid() != null && terapia.getNid().get(0) != null)
            nid = terapia.getNid().get(0).getValue();

        if (terapia.getTitle() != null && terapia.getTitle().get(0) != null)
            name = terapia.getTitle().get(0).getValue().trim();

        if (terapia.getBody() != null && terapia.getBody().get(0) != null)
            description = terapia.getBody().get(0).getValue().trim();

        if (terapia.getFieldSkrypt() != null && terapia.getFieldSkrypt().get(0) != null)
            script = terapia.getFieldSkrypt().get(0).getValue().trim();

        devices = new ArrayList<String>();

        if (terapia.getFieldUrzadzenie() != null)
            for (FieldUrzadzenie u : terapia.getFieldUrzadzenie()) {
                devices.add(u.getValue().trim());
            }
    }

    public TerapiaPojo(ZapperTerapia terapia) {
        nid = terapia.getId() + 10000;
        name = terapia.getName();
        description = terapia.getDescription();
        script = terapia.getScript();
        devices = terapia.getDevices();
        if (null == devices) {
            devices = new ArrayList<String>();
        }
    }


    public Long getNid() {
        return nid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getScript() {
        return script;
    }

    public List<String> getDevices() {
        return devices;
    }
}
