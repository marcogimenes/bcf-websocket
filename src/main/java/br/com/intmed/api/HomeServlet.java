package br.com.intmed.api;

import br.com.intmed.models.Config;
import br.com.intmed.services.Heramed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.util.Properties;

@Path("/config")
public class HomeServlet {
    @GET
    @Path("/")
    @Produces("application/json")
    public Response getConfig() {
        Heramed service = new Heramed();
        String version = "1.0.0";

        try {
            final Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
            version = properties.getProperty("version");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Config config = new Config(service.getBaseUrl(), Heramed.getBaseTimeSchedule(), version);
        return Response.status(200).entity(config).build();
    }
}
