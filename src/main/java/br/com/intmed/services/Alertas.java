package br.com.intmed.services;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import br.com.intmed.websocket.LiveSocket;
import br.com.intmed.constants.MessageType;

public interface Alertas {

	//prot�tipo - obt�m json de string, consummer
    JsonObject getJsonObject(String url, Consumer<String> loggingCallback) throws IOException;
    
    //prot�tipo - sobrecarregado - obt�m json de string - parametro string
    JsonObject getJsonFromString(String string);
    
    //prot�tipo - obt�m base live URL
    String getBaseUrl();

    //prot�tipo - obt�m lista de ids monitorados
    ArrayList<String> getMonitoramentosIds(String mapKey);
    
    //prot�tipo - obt�m contagem de ultimas medi��es
    int getLastMedicoesCount(String mapKey);

    //retorna alertas de medi��es - aparelho senaii - par�metro mapkey
    default JsonObject alertas(String mapKey) throws IOException {

        ArrayList<String> monitoramentoIds = getMonitoramentosIds(mapKey);

        int pageSize = getLastMedicoesCount(mapKey);

        if(pageSize == 0 || monitoramentoIds.size() == 0){
            return getJsonFromString("{\"results\": [], \"count\": 0 }");
        }

        return alertas(monitoramentoIds, pageSize, mapKey);
    }

     //m�todo sobrecarregado - retorna alertas de medi��es - aparelho senaii - par�metro mapkey, pagesize, lista de ids
     default JsonObject alertas(ArrayList<String> monitoramentoIds, int pageSize, String mapKey) throws IOException {
    	  
        String url = getBaseUrl() + "alertas/?format=json&ativo=true&ordernar_por=-data_fim_alerta&page_size=" + pageSize;

        for (String id : monitoramentoIds) {
            url = url.concat("&monitoramento_id=" + id);
        }

        return getJsonObject(url, loggingRegistry -> LiveSocket.updateLastRequest(mapKey, MessageType.ALERTAS.toString(), loggingRegistry));
    }
}
