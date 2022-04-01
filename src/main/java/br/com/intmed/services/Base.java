package br.com.intmed.services;

import br.com.intmed.utils.GsonUtil;
import com.google.api.client.http.HttpResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import static br.com.intmed.utils.GsonUtil.gson;

//Classe com constantes e funções básicas
public class Base {
	
	//URI da API
    protected static final String BASE_URL = "http://18.228.43.112/batimentos-api/";

    protected static final String BASE_TIME_SCHEDULE = "10";

    //pega o response e adiciona ao um json string
    protected JsonElement  getResponseJson(HttpResponse response) {
     	
		    JsonElement json = null;
		
		    try {
		        json = GsonUtil.jsonParser.parse(response.parseAsString());
		        
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    
		    return json;
     }
    
    //pega o response e adiciona a um json objeto
    protected JsonObject  getJsonObjectResult(HttpResponse response) {

    		return getResponseJson(response).getAsJsonObject();
        
    }

    //pega o response e adiciona a um json Array
    protected JsonArray getJsonArrayResult(HttpResponse response) {
       
    	  return getResponseJson(response).getAsJsonObject().getAsJsonArray("results");
   	}

   //pega o response e adiciona a um objeto json
    protected JsonElement getJsonElementResult(HttpResponse response) {
    	
        				return getResponseJson(response).getAsJsonObject();
    }

    //converte um json em string
    protected String getStringFromJson(HttpResponse response) {
    	
        String result = gson.toJson(getResponseJson(response));

        return result;
    }

    //converte uma string em json
    public JsonObject getJsonFromString(String string){
    	
        JsonElement json = GsonUtil.jsonParser.parse(string);

        return json.getAsJsonObject();
    }

    //retorna a URL de backend live
    public String getBaseLiveUrl() {
    	
        String backendUrl = System.getProperty("BACKEND_URL");
        backendUrl = backendUrl == null ? System.getenv("BACKEND_URL"): backendUrl;

        if(backendUrl!=null){
            return backendUrl + "live/";
        }

        return BASE_URL + "live/";
    }

    //retorna a URL de backend
    public String getBaseUrl() {
        String backendUrl = System.getProperty("BACKEND_URL");
        backendUrl = backendUrl == null ? System.getenv("BACKEND_URL"): backendUrl;

        if(backendUrl!=null){
            return backendUrl;
        }

        return BASE_URL;
    }

    //retorna o BASE_TIME
    public static Integer getBaseTimeSchedule() {
        String baseTimeSchedule = System.getProperty("BASE_TIME_SCHEDULE");

        if(baseTimeSchedule!=null){
            return Integer.parseInt(baseTimeSchedule);
        }
        return Integer.parseInt(BASE_TIME_SCHEDULE);
    }

    //retorna o BASE_TIME em microssegundos
    public static Integer getBaseTimeScheduleMs() {
        return getBaseTimeSchedule() * 1000;
    }
}
