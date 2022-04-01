package br.com.intmed.api;

import br.com.intmed.websocket.LiveSocket;

import com.google.api.client.http.HttpStatusCodes;
import com.google.gson.JsonObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/reset")
public class ResetServlet {
    @GET
    @Path("/{mapKey}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response reset(@PathParam("mapKey") String mapKey) {
        JsonObject responseObject = new JsonObject();
        int status = 0;

        if (LiveSocket.clearAllSessions(mapKey)) {
            status = HttpStatusCodes.STATUS_CODE_OK;
            responseObject.addProperty("detail", String.format("%s reset done", mapKey));
        } else {
            status = HttpStatusCodes.STATUS_CODE_BAD_REQUEST;
            responseObject.addProperty("detail", String.format("%s not found", mapKey   ));
        }

        return Response.status(status).entity(responseObject.toString()).build();
    }
}
