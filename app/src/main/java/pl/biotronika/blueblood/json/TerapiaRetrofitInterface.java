package pl.biotronika.blueblood.json;

import java.util.List;

import pl.biotronika.blueblood.json.gsonpojo.Terapia;
import pl.biotronika.blueblood.json.zapperpojo.ZapperTerapiaList;
import retrofit.http.GET;

public interface TerapiaRetrofitInterface {

    // asynchronously with a callback
    @GET("/terapie_list?_format=json")
    List<Terapia> getTerapie();

    @GET("/sites/default/files/2018-06/zapper.txt")
    ZapperTerapiaList getZapperList();

    @GET("/sites/default/files/2018-06/plasma.txt")
    ZapperTerapiaList getPlasmaList();

    @GET("/sites/default/files/2018-06/pitchfork.txt")
    ZapperTerapiaList getPitchforkList();

}

