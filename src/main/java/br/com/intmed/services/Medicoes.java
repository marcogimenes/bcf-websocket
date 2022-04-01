package br.com.intmed.services;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import br.com.intmed.websocket.LiveSocket;
import br.com.intmed.constants.MessageType;

public interface Medicoes {

    //m�todo prot�tipo - obt�m lista de ids monitorados
	ArrayList<String> getMonitoramentosIds(String mapKey);

	//m�todo prot�tipo - converte string em json
    JsonObject getJsonFromString(String string);

    //m�todo prot�tipo - converte string em objeto json    		
    JsonObject getJsonObject(String url, Consumer<String> loggingCallback) throws IOException;
    
    //m�todo prot�tipo - obt�m a live URL de base
    String getBaseLiveUrl();

    //retorna medi��es senai - parametro Array list de ids e mapkey
    default JsonObject medicoes(ArrayList<String> ids, String mapKey) throws IOException{
        if (ids.size() > 0) {
            String url = getBaseLiveUrl() + "medicoes/?format=json";

            for (String id : ids) {
                try {
                    Integer.parseInt(id);
                    url = url.concat("&id=" +  Integer.parseInt(id));
                } catch (Exception e) {
                    url = url.concat("&id_senai=" +  id);
                }

            }

            return getJsonObject(url, loggingRegistry -> LiveSocket.updateLastRequest(mapKey, MessageType.MEDICOES.toString(), loggingRegistry));
        }

        return getJsonFromString("{\"results\": [], \"count\": 0 }");
    }

   //metodo sobrecarregado, retorna medi��es senai - parametro mapkey 
    default JsonObject medicoes(String mapKey) throws IOException{
    	
        return medicoes(getMonitoramentosIds(mapKey), mapKey);
        
    }
}
