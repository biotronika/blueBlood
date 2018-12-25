package pl.biotronika.freepemf.test;

import org.junit.Test;

import java.util.List;

import pl.biotronika.blueblood.json.TerapiaPojo;
import pl.biotronika.blueblood.json.TerapieApi;
import pl.biotronika.blueblood.json.gsonpojo.Terapia;
import pl.biotronika.blueblood.json.zapperpojo.ZapperTerapia;
import pl.biotronika.blueblood.json.zapperpojo.ZapperTerapiaList;

public class JsonUnitTest {

    @Test
    public void addition_isCorrect() {
        List<Terapia> listaTerapii =  TerapieApi.getTerapieLocal();

        System.out.println("size:" + listaTerapii.size());


        for (Terapia t: listaTerapii) {
            TerapiaPojo pojo = new TerapiaPojo(t);
            System.out.println(pojo.getName());

            String[] lines = pojo.getScript().split("\n");

            for (String toSend: lines ) {
                System.out.println("SENDING: " + toSend);
            }

            break;
        }
    }


    @Test
    public void zapper_isCorrect() {

        ZapperTerapiaList listaTerapii = TerapieApi.getZapperListRemote();
        System.out.println("size:" + listaTerapii.getZapperTerapiaList().size());

        for (ZapperTerapia t: listaTerapii.getZapperTerapiaList()) {
            System.out.println(t.getName());
        }
    }

    @Test
    public void plasma_isCorrect() {

        ZapperTerapiaList listaTerapii = TerapieApi.getPlasmaListLocal();
        System.out.println("size:" + listaTerapii.getZapperTerapiaList().size());

        for (ZapperTerapia t: listaTerapii.getZapperTerapiaList()) {
            System.out.println(t.getDescription());
        }

    }
}
