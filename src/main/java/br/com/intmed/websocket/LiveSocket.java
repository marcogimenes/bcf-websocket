package br.com.intmed.websocket;

import br.com.intmed.constants.MessageType;
import br.com.intmed.services.Base;

import br.com.intmed.utils.JSONEncoder; //importas para trabalhar com json
import com.google.gson.JsonArray; 
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.api.client.http.HttpResponseException;  //import para trabalhar com request http

import javax.websocket.OnOpen;  //imports para trabalhar com websockets
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Queue;
import java.util.Timer;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

//Extende BaseWebsocket
@ServerEndpoint(value="/live/{dataSource}/{codigoPosto}", encoders = {JSONEncoder.class})

public class LiveSocket extends BaseWebsocket {
	
	    public static final HashMap<String, Queue<Session>> sessionsMap = new HashMap<>();  //lista  de sessões
	    public static final HashMap<String, HashMap<String, String>> lastRequestMap = new HashMap<>(); //lista de ultimas requisições
	    public static final HashMap<String, ArrayList<Timer>> timerMap = new HashMap<>(); //lista de tarefas a serem executadas
	
	    //Ao abrir a sessão, efetua todas as requisições aos aparelhos
	    @OnOpen
	    public void onOpen(Session session, @PathParam("dataSource") String dataSource, @PathParam("codigoPosto") String codigoPosto) {
	    	
	    	//obtém os parâmetros desta sessão
	        String mapKey = this.getMapKey(session);
	
	        System.out.println(String.format("Client from %s connected", mapKey)); //msg de conexão
	
	        this.setSessionsMap(session); //coloca a sessão na lista
	
	        //executa de modo sincronizado, as tarefas da lista
	        synchronized (timerMap) {
	            if (!timerMap.containsKey(mapKey)) {
	                int baseTimeSchedule = Base.getBaseTimeScheduleMs(); //obtém tempo em ms
	                ArrayList<Timer> timers = new ArrayList<>();     //cria lista de array
	
	                //adidiona na lista o resultado da rotina de monitoramento
	                timers.add(this.onConnection("MonitoramentosTimer", baseTimeSchedule, () -> {
	                    this.monitoramentosTimerRoutine(mapKey, dataSource, codigoPosto);
	                }));
	
	                //adidiona na lista o resultado da rotina de medições	                
	                timers.add(this.onConnection("MedicoesTimer", baseTimeSchedule, () -> {
	                    this.medicoesTimerRoutine(mapKey);
	                }));
	
	                //adidiona na lista o resultado da rotina de alertas de medições
	                timers.add(this.onConnection("AlertasTimer", baseTimeSchedule, () -> {
	                    this.alertasTimerRoutine(mapKey);
	                }));
	
	                 //adidiona na lista o resultado da rotina de alertas de período
	                timers.add(this.onConnection("PeriodosAlertaTimer", baseTimeSchedule, () -> {
	                    this.periodosAlertaTimerRoutine(mapKey);
	                }));
	
	                //associa os valores medidos à key da sessão
	                timerMap.put(mapKey, timers);
	            }
	        }
    }

	 //Rotina de monitoramento
    public void monitoramentosTimerRoutine(String mapKey, String dataSource, String codigoPosto) throws IOException {
    	
    	//instancia objeto json
        JsonObject wsResponse = new JsonObject();
        wsResponse.addProperty("messageType", MessageType.MONITORAMENTOS.toString());

        try {
            
        	//obtém os dados do aparelho - em formato de lista de pacientes
        	JsonObject jsonServiceResponse = service.monitoramentos(dataSource, codigoPosto, mapKey);
            JsonArray pacientesList = jsonServiceResponse.get("results").getAsJsonArray();

            //instancia uma lista de ids
            ArrayList<String> monitoramentosIds = service.getMonitoramentosIds(mapKey);
            monitoramentosIds.clear();

            //percorre a lista de pacientes e obtém o id so aparelho do senai 
            for (JsonElement element : pacientesList) {
                String id = "";
                
                if(!element.getAsJsonObject().get("id_senai").isJsonNull()) {
                    id = element.getAsJsonObject().get("id_senai").getAsString();
                } else {
                    id = element.getAsJsonObject().get("id").getAsString();
                }
                monitoramentosIds.add(id);
            }

           //adiciona  dados ao json de resposta  
            wsResponse.add("content", jsonServiceResponse);

        } catch (HttpResponseException e) {
            wsResponse.addProperty("content", e.getContent());
        } catch (SocketTimeoutException e) {
            wsResponse.addProperty("content", e.getMessage());
        }

        //envia a resposta
        for (Session s : sessionsMap.get(mapKey)) {
            if (s.isOpen()) {
                s.getAsyncRemote().sendObject(wsResponse, result -> {
                    if (!result.isOK()) {
                        result.getException().printStackTrace();
                    }
                });
            }
        }
    }

   //Rotina de medições
    public void medicoesTimerRoutine(String mapKey) throws IOException {
    	
    	//instancia objeto json
        JsonObject wsResponse = new JsonObject();
        wsResponse.addProperty("messageType", MessageType.MEDICOES.toString());

        try {
        	
        	//obtém dados do aparelho
            JsonObject jsonServiceResponse = service.medicoes(mapKey);

            int medicoesCount = jsonServiceResponse.get("count").getAsInt();
            service.setLastMedicoesCount(mapKey, medicoesCount);

           //adiciona dados ao  json de resposta
            wsResponse.add("content", jsonServiceResponse);

        } catch (HttpResponseException e) {
            wsResponse.addProperty("content", e.getContent());
        } catch (SocketTimeoutException e) {
            wsResponse.addProperty("content", e.getMessage());
        }

        //envia a resposta
        for (Session s : sessionsMap.get(mapKey)) {
            if (s.isOpen()) {
                s.getAsyncRemote().sendObject(wsResponse, result -> {
                    if (!result.isOK()) {
                        result.getException().printStackTrace();
                    }
                });
            }
        }
    }

    //Rotina de alertas de medições
    public void alertasTimerRoutine(String mapKey) throws IOException {

    	//instancia objeto json
    	JsonObject wsResponse = new JsonObject();
        wsResponse.addProperty("messageType", MessageType.ALERTAS.toString());


         try {
        	 
        	//obtém dados do aparelho 
            JsonObject jsonServiceResponse = service.alertas(mapKey);
            
            //adiciona dados ao json de resposta
            wsResponse.add("content", jsonServiceResponse);
            
        } catch (HttpResponseException e) {
            wsResponse.addProperty("content", e.getContent());
        } catch (SocketTimeoutException e) {
            wsResponse.addProperty("content", e.getMessage());
        }

        //envia resposta 	
        for (Session s : sessionsMap.get(mapKey)) {
            if (s.isOpen()) {
                s.getAsyncRemote().sendObject(wsResponse, result -> {
                    if (!result.isOK()) {
                        result.getException().printStackTrace();
                    }
                });
            }
        }
    }

    //Rotina de alerta de períodos
    public void periodosAlertaTimerRoutine(String mapKey) throws IOException {
    	
    	//instancia objeto json
        JsonObject wsResponse = new JsonObject();
         wsResponse.addProperty("messageType", MessageType.PERIODOS_ALERTA.toString());

        try {
        	
        	//pega dados do aparelho
            JsonObject jsonServiceResponse = service.periodosAlerta(mapKey);
            
            //adiciona dados ao jsond e resposta
            wsResponse.add("content", jsonServiceResponse);
            
        } catch (HttpResponseException e) {
            wsResponse.addProperty("content", e.getContent());
        } catch (SocketTimeoutException e) {
            wsResponse.addProperty("content", e.getMessage());
        }

        //envia resposta	
        for (Session s : sessionsMap.get(mapKey)) {
            if (s.isOpen()) {
                s.getAsyncRemote().sendObject(wsResponse, result -> {
                    if (!result.isOK()) {
                        result.getException().printStackTrace();
                    }
                });
            }
        }
    }

    //Guarda data e hora da ultima requisição
    public static void updateLastRequest(String mapKey, String type, String requestUrl) {
    	
    	//instancia objeto timestamp
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        final String value = String.format("%s - %s",formatter.format(date), requestUrl);

        if (!lastRequestMap.containsKey(mapKey)) {
            lastRequestMap.put(mapKey, new HashMap<>());
        }

        //salva na lista
        lastRequestMap.get(mapKey).put(type, value);
    }

    @OnMessage
    public void onMessage(String message) {

    }

    //ao fechar, limpa timers
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        String mapKey = this.getMapKey(session);
        this.closeConnection(sessionsMap.get(mapKey), session, reason, () -> wipeTimers(mapKey));
    }

   //Cria uma lista das sessões
    public void setSessionsMap(Session session) {
    	
    	//pega os parâmetros da sessão
        String mapKey = this.getMapKey(session);

        //se não tiver os parâmetros na lista, coloca, se tiver, retorna
        if (!sessionsMap.containsKey(mapKey)) {
            Queue<Session> newQueue = new ConcurrentLinkedQueue<>();
            newQueue.add(session);
            sessionsMap.put(mapKey, newQueue);
        } else {
            sessionsMap.get(mapKey).add(session);
        }
    }

    //Fecha todas as sessões e limpa timers
    public static boolean clearAllSessions(String mapKey) {
        if (sessionsMap.containsKey(mapKey)) {
            Queue<Session> sessions = sessionsMap.get(mapKey);

            if (sessions.size() != 0) {
                sessions.forEach(session -> {
                    try {
                    	
                    	//fecha sessão
                        session.close(new CloseReason(CloseReason.CloseCodes.SERVICE_RESTART, "The service is restarting"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                wipeTimers(mapKey);
            }

            return true;
        }

        return false;
    }

    //Limpa timers
    public static void wipeTimers(String mapKey) {
        service.getMonitoramentosIds(mapKey).clear();
        if (timerMap.containsKey(mapKey)) {
            timerMap.get(mapKey).forEach(Timer::cancel);
            timerMap.get(mapKey).clear();
            timerMap.remove(mapKey);
        }
    }
}
