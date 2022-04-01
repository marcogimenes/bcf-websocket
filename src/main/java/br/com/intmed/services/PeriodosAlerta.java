package br.com.intmed.services;

import br.com.intmed.constants.MessageType;
import br.com.intmed.websocket.LiveSocket;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public interface PeriodosAlerta {

    //m�todo prot�tipo - obt�m  objeto json de string
    JsonObject getJsonObject(String url, Consumer<String> loggingCallback) throws IOException;
    
    // m�todo prot�tipo - sobrecarregado - obt�m json de string
    JsonObject getJsonFromString(String string);
    
    //m�todo prot�tipo - obt�m base live URL
    String getBaseUrl();

    //m�todo prot�tipo - obt�m lista de ids monitorados
    ArrayList<String> getMonitoramentosIds(String mapKey);

   //obt�m alertas de per�odos do aparelho do senai - mapkey
    default JsonObject periodosAlerta(String mapKey) throws IOException {

        ArrayList<String> monitoramentoIds = getMonitoramentosIds(mapKey);
        int pageSize = monitoramentoIds.size() * 10;

        if(pageSize == 0){
            return getJsonFromString("{\"results\": [], \"count\": 0 }");
        }

        return periodosAlerta(monitoramentoIds, pageSize, mapKey);
    }

    //m�todo sobrecarregado - obt�m alertas de per�odos do aparelho do senai - mapkey, monitoramentoids, pagesize
    default JsonObject periodosAlerta(ArrayList<String> monitoramentoIds, int pageSize, String mapKey) throws IOException {
        String url = getBaseUrl() + "periodos-alerta/?format=json&ordering=-data_inicio&page_size=" + pageSize;

        for (String id : monitoramentoIds) {
            url = url.concat("&monitoramento_id=" + id);
        }

        return getJsonObject(url, loggingRegistry -> LiveSocket.updateLastRequest(mapKey, MessageType.PERIODOS_ALERTA.toString(), loggingRegistry));
    }
}
