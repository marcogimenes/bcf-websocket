package br.com.intmed.api;

import br.com.intmed.services.Heramed;
import br.com.intmed.websocket.LiveSocket;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//endpoint raiz , classe que retorna status do monitoramento
@Path("/status")
public class StatusServlet {

	//método GET com retorno Json
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        JsonObject response = new JsonObject();

        LiveSocket.sessionsMap.keySet().forEach(key -> {
            JsonObject parametrizacaoObject = new JsonObject();

            // Monitoramentos em execução
            JsonArray monitoramentosArray = new JsonArray();
            if (Heramed.mapMonitoramentosIds.containsKey(key)) {
                Heramed.mapMonitoramentosIds.get(key).forEach(monitoramentosArray::add);
            }
            parametrizacaoObject.add("monitoramentos", monitoramentosArray);

            // Clientes Conectados
            parametrizacaoObject.addProperty("clientesConectados", LiveSocket.sessionsMap.get(key).size());

            // Ultimas Requisições
            JsonObject ultimasRequisicoesObject = new JsonObject();
            if (LiveSocket.lastRequestMap.containsKey(key)) {
                LiveSocket.lastRequestMap.get(key).forEach(ultimasRequisicoesObject::addProperty);
            }
            parametrizacaoObject.add("ultimasRequisicoes", ultimasRequisicoesObject);

            // Timers
            JsonArray timersArray = new JsonArray();
            if (LiveSocket.timerMap.containsKey(key)) {
                LiveSocket.timerMap.get(key).forEach(timer -> {
                    timersArray.add(timer.toString());
                });
            }
            parametrizacaoObject.add("timers", timersArray);

            response.add(key, parametrizacaoObject);
        });

        return Response.status(200).entity(response.toString()).build();
    }
}