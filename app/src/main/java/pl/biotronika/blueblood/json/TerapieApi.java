package pl.biotronika.blueblood.json;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.biotronika.blueblood.BiotronikaApplication;
import pl.biotronika.blueblood.json.gsonpojo.Terapia;
import pl.biotronika.blueblood.json.zapperpojo.ZapperTerapia;
import pl.biotronika.blueblood.json.zapperpojo.ZapperTerapiaList;
import retrofit.RestAdapter;

public class TerapieApi {

    public static final String BASE_URL = "https://biotronika.pl";

    static List<Terapia> terapie;
    static List<TerapiaPojo> terapiePojo;

    static Comparator<TerapiaPojo> currentComparator;

    static String[] names;
    static String[] descriptions;
    static String[] scripts;
    static String[] devices;

    static boolean enableExtendedList;
    static boolean enableBasicList;
    static boolean enablePitchforkList;
    static boolean includeFreePemf;
    static boolean includeMultiZAP;
    static boolean filterDevices;

    static String filterString = null;

    public static List<Terapia> getTerapie() {

        if (null == terapie) {
            terapie = getTerapieLocal();
        }
        return terapie;
    }

    public static List<Terapia> getTerapieLocal() {
        Gson gson = new Gson();
        return gson.fromJson(getJSONFromAsset("terapie.json"), new TypeToken<List<Terapia>>() {
        }.getType());
    }

    public static ZapperTerapiaList getZapperListLocal() {
        Gson gson = new Gson();
        return gson.fromJson(getJSONFromAsset("zapper.json"), new TypeToken<ZapperTerapiaList>() {
        }.getType());
    }

    public static ZapperTerapiaList getPlasmaListLocal() {
        Gson gson = new Gson();
        return gson.fromJson(getJSONFromAsset("plasma.json"), new TypeToken<ZapperTerapiaList>() {
        }.getType());
    }

    public static ZapperTerapiaList getPitchforkListLocal() {
        Gson gson = new Gson();
        return gson.fromJson(getJSONFromAsset("pitchfork.json"), new TypeToken<ZapperTerapiaList>() {
        }.getType());
    }

    public static List<Terapia> getTerapieRemote() {
        TerapiaRetrofitInterface terapiaRetrofitInterface = new
                RestAdapter.Builder().setEndpoint(BASE_URL).build().create(TerapiaRetrofitInterface.class);
        return terapiaRetrofitInterface.getTerapie();
    }

    public static ZapperTerapiaList getZapperListRemote() {
        TerapiaRetrofitInterface terapiaRetrofitInterface = new
                RestAdapter.Builder().setEndpoint(BASE_URL).build().create(TerapiaRetrofitInterface.class);
        return terapiaRetrofitInterface.getZapperList();
    }

    public static ZapperTerapiaList getPlasmaListRemote() {
        TerapiaRetrofitInterface terapiaRetrofitInterface = new
                RestAdapter.Builder().setEndpoint(BASE_URL).build().create(TerapiaRetrofitInterface.class);
        return terapiaRetrofitInterface.getPlasmaList();
    }

    public static ZapperTerapiaList getPitchforkListRemote() {
        TerapiaRetrofitInterface terapiaRetrofitInterface = new
                RestAdapter.Builder().setEndpoint(BASE_URL).build().create(TerapiaRetrofitInterface.class);
        return terapiaRetrofitInterface.getPitchforkList();
    }


    public static List<TerapiaPojo> getTerapiePojo() {

        if (null == terapiePojo) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BiotronikaApplication.getContext());
            enableExtendedList = prefs.getBoolean("key_enable_extended_therapies", false);
            enableBasicList = prefs.getBoolean("key_enable_basic_therapies", true);
            enablePitchforkList = prefs.getBoolean("key_enable_pitchfork_therapies", false);
            includeFreePemf = prefs.getBoolean("key_dev_freepemf", true);
            includeMultiZAP = prefs.getBoolean("key_dev_multizap", true);
            filterDevices = prefs.getBoolean("key_device_filter", false);


            currentComparator = sortByNameAsc;

            terapiePojo = new ArrayList<TerapiaPojo>();
            if (enableBasicList)
                putTerapiePojo();

            if (enableExtendedList)
                putPlasmaPojo();

            if (enablePitchforkList)
                putPitchforkPojo();


            sortTable(terapiePojo);
        }
        return terapiePojo;
    }


    static void putTerapiePojo() {
        for (Terapia t : getTerapie()) {
            TerapiaPojo tp = new TerapiaPojo(t);
            putPojo(tp);
        }
    }

    static void putPlasmaPojo() {
        for (ZapperTerapia t : getPlasmaListLocal().getZapperTerapiaList()) {
            TerapiaPojo tp = new TerapiaPojo(t);
            putPojo(tp);
        }
    }

    static void putPitchforkPojo() {
        for (ZapperTerapia t : getPitchforkListLocal().getZapperTerapiaList()) {
            TerapiaPojo tp = new TerapiaPojo(t);
            putPojo(tp);
        }
    }

    static void putPojo(TerapiaPojo tp) {
        if (!filterDevices) {
            performFiltering(tp);
        } else if (tp.getDevices().contains("freePEMF") && includeFreePemf) {
            performFiltering(tp);
        } else if (tp.getDevices().contains("multiZAP++") && includeMultiZAP) {
            performFiltering(tp);
        }
    }

    static void performFiltering(TerapiaPojo tp) {
        if (null == filterString) {
            terapiePojo.add(tp);
        } else {
            String filterableString = tp.getName();
            if (filterableString.toLowerCase().contains(filterString)) {
                terapiePojo.add(tp);
            }
        }
    }

    static void sortTable(List<TerapiaPojo> _terapiePojo) {

        Collections.sort(_terapiePojo, currentComparator);

        List<String> nameList = new ArrayList<String>();
        List<String> descriptionList = new ArrayList<String>();
        List<String> scriptList = new ArrayList<String>();
        List<String> devicesList = new ArrayList<String>();

        for (TerapiaPojo tp : _terapiePojo) {
            nameList.add(tp.getName());
            descriptionList.add(tp.getDescription());
            scriptList.add(tp.getScript());
            devicesList.add(Arrays.toString(tp.getDevices().toArray()));

        }
        names = nameList.toArray(new String[nameList.size()]);
        descriptions = descriptionList.toArray(new String[descriptionList.size()]);
        scripts = scriptList.toArray(new String[scriptList.size()]);
        devices = devicesList.toArray(new String[devicesList.size()]);
    }


    public static String[] getNames() {
        getTerapiePojo();
        return names;
    }

    public static String[] getDevices() {
        getTerapiePojo();
        return devices;
    }

    public static String[] getDescriptions() {
        getTerapiePojo();
        return descriptions;
    }

    public static String[] getScripts() {
        getTerapiePojo();
        return scripts;
    }


    static String getJSONFromAsset(String fileName) {
        String json = null;
        try {

            InputStream is = BiotronikaApplication.getContext().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

// COMPARATORS

    public static void sortByNameAsc() {
        currentComparator = sortByNameAsc;
        sortTable(terapiePojo);
    }

    public static void sortByNameDesc() {
        currentComparator = sortByNameDesc;
        sortTable(terapiePojo);
    }


    public static void sortByIdAsc() {
        currentComparator = sortByIdAsc;
        sortTable(terapiePojo);
    }

    public static void sortByIdDesc() {
        currentComparator = sortByIdDesc;
        sortTable(terapiePojo);
    }


    private static Comparator<TerapiaPojo> sortByNameAsc = new Comparator<TerapiaPojo>() {
        public int compare(TerapiaPojo o1, TerapiaPojo o2) {
            return o1.getName().compareTo(o2.getName());

        }
    };

    private static Comparator<TerapiaPojo> sortByNameDesc = new Comparator<TerapiaPojo>() {
        public int compare(TerapiaPojo o1, TerapiaPojo o2) {
            return o2.getName().compareTo(o1.getName());

        }
    };

    private static Comparator<TerapiaPojo> sortByIdAsc = new Comparator<TerapiaPojo>() {
        public int compare(TerapiaPojo o1, TerapiaPojo o2) {
            return o1.getNid().compareTo(o2.getNid());

        }
    };

    private static Comparator<TerapiaPojo> sortByIdDesc = new Comparator<TerapiaPojo>() {
        public int compare(TerapiaPojo o1, TerapiaPojo o2) {
            return o2.getName().compareTo(o1.getName());

        }
    };


    public static void setFilter(String _filter) {
        filterString = _filter.toLowerCase();
    }

    public static void clearFilter() {
        filterString = null;
    }

    public static String getFilter() {
        return filterString;
    }

    public static void resetList() {
        terapiePojo = null;
    }


}
