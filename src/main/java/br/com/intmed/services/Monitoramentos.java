package br.com.intmed.services;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import br.com.intmed.websocket.LiveSocket;
import br.com.intmed.constants.MessageType;

public interface Monitoramentos {

    //m�todo prot�tipo - obt�m lista de ids monitorados
	ArrayList<String> getMonitoramentosIds(String mapKey);

    //m�todo prot�tipo - obt�m objeto json de string
	JsonObject getJsonObject(String url, Consumer<String> loggingCallback) throws IOException;
	
	//obt�m base url live
    String getBaseLiveUrl();

    //retorna monitoramento senai - string, datasource, codigo posto e mapkey
    default JsonObject monitoramentos(String status, String dataSource, String codigoPosto, String mapKey) throws IOException {
    	
        String url = getBaseLiveUrl() + "monitoramentos/?status=" + status + "&contexto=" + dataSource + "&codigo_posto=" + codigoPosto + "&format=json";

        return getJsonObject(url, loggingRegistry -> LiveSocket.updateLastRequest(mapKey, MessageType.MONITORAMENTOS.toString(), loggingRegistry));
    }

    //metodo sobrecarregado - retorna monitoramento senai - datasource, string, mapkey
    default JsonObject monitoramentos(String dataSource, String codigoPosto, String mapKey) throws IOException {
    	
        return monitoramentos("running", dataSource, codigoPosto, mapKey);
    }
}
