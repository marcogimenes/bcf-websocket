package br.com.intmed.services;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;


public class Heramed extends Base implements Medicoes, Monitoramentos, Alertas, PeriodosAlerta {

    //variáveis
	public static final HashMap<String, ArrayList<String>> mapMonitoramentosIds = new HashMap<>();
    public static final HashMap<String, Integer> lastMedicoesCount = new HashMap<>();

    //obtém lista de ids monitorados
    public ArrayList<String> getMonitoramentosIds(String mapKey) {
        if (mapMonitoramentosIds.containsKey(mapKey)) {
            return mapMonitoramentosIds.get(mapKey);
        }

        ArrayList<String> newArray = new ArrayList<>();
        mapMonitoramentosIds.put(mapKey, newArray);

        return newArray;
    }

    //obtém ultimas medições
    public int getLastMedicoesCount(String mapKey) {
        return lastMedicoesCount.getOrDefault(mapKey, 0);
    }

    //obtém ultimas medições - sobrecarregado
    public void setLastMedicoesCount(String mapKey, int value) {
        lastMedicoesCount.put(mapKey, value);
    }

    //obtém objeto json de url, consummer
    public JsonObject getJsonObject(String url, Consumer<String> loggingCallback) throws IOException {
    	
    	//cria um http header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType("application/json; charset=utf-8");
        httpHeaders.setContentEncoding("utf-8");
        httpHeaders.setAcceptEncoding("utf-8");

        //cria um construtor de request
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        
        //seta parametros do request
        HttpRequest httpRequest = requestFactory.buildGetRequest(new GenericUrl(url));
        httpRequest.setReadTimeout(150000);
        
        //seta o header no request
        httpRequest.setHeaders(httpHeaders);

        Long startTime = System.currentTimeMillis();
        try {
        	
        	//executa o construtor de request
            HttpResponse contentResponse = httpRequest.execute();

            //log de sistema
            Long endTime = System.currentTimeMillis();
            String loggingRegistry = String.format("%s - %d - %dms", contentResponse.getRequest().getUrl().toString(), contentResponse.getStatusCode(), endTime - startTime);

            System.out.println(loggingRegistry);
            loggingCallback.accept(loggingRegistry);
            
            //retorna o objeto request/response
            return getJsonObjectResult(contentResponse);

        } catch (HttpResponseException e) {
            Long endTime = System.currentTimeMillis();
            String loggingRegistry = String.format("%s - %d - %dms", httpRequest.getUrl().toString(), e.getStatusCode(), endTime - startTime);

            System.out.println(loggingRegistry);
            loggingCallback.accept(loggingRegistry);

            throw e;
        } catch (SocketTimeoutException e) {
            Long endTime = System.currentTimeMillis();
            String loggingRegistry = String.format("%s - %s - %dms", httpRequest.getUrl().toString(), e.getMessage(), endTime - startTime);

            System.out.println(loggingRegistry);
            loggingCallback.accept(loggingRegistry);

            throw e;
        }
    }
}
