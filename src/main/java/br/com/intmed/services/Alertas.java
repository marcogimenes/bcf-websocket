package br.com.intmed.services;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import br.com.intmed.websocket.LiveSocket;
import br.com.intmed.constants.MessageType;

public interface Alertas {

	//protótipo - obtém json de string, consummer
    JsonObject getJsonObject(String url, Consumer<String> loggingCallback) throws IOException;
    
    //protótipo - sobrecarregado - obtém json de string - parametro string
    JsonObject getJsonFromString(String string);
    
    //protótipo - obtém base live URL
    String getBaseUrl();

    //protótipo - obtém lista de ids monitorados
    ArrayList<String> getMonitoramentosIds(String mapKey);
    
    //protótipo - obtém contagem de ultimas medições
    int getLastMedicoesCount(String mapKey);

    //retorna alertas de medições - aparelho senaii - parâmetro mapkey
    default JsonObject alertas(String mapKey) throws IOException {

        ArrayList<String> monitoramentoIds = getMonitoramentosIds(mapKey);

        int pageSize = getLastMedicoesCount(mapKey);

        if(pageSize == 0 || monitoramentoIds.size() == 0){
            return getJsonFromString("{\"results\": [], \"count\": 0 }");
        }

        return alertas(monitoramentoIds, pageSize, mapKey);
    }

     //método sobrecarregado - retorna alertas de medições - aparelho senaii - parâmetro mapkey, pagesize, lista de ids
     default JsonObject alertas(ArrayList<String> monitoramentoIds, int pageSize, String mapKey) throws IOException {
    	  
        String url = getBaseUrl() + "alertas/?format=json&ativo=true&ordernar_por=-data_fim_alerta&page_size=" + pageSize;

        for (String id : monitoramentoIds) {
            url = url.concat("&monitoramento_id=" + id);
        }

        return getJsonObject(url, loggingRegistry -> LiveSocket.updateLastRequest(mapKey, MessageType.ALERTAS.toString(), loggingRegistry));
    }
}
