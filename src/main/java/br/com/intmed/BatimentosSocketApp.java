package br.com.intmed;

import br.com.intmed.api.HomeServlet;
import br.com.intmed.api.ResetServlet;
import br.com.intmed.api.StatusServlet;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

//endpoint da aplicação
@ApplicationPath("api")
public class BatimentosSocketApp extends Application {

    private Set<Object> singletons = new HashSet<Object>();  //lista de objetos
    private Set<Class<?>> empty = new HashSet<Class<?>>(); 

    //construtor da classe - cria uma lista de servlets rodando na aplicação
    public BatimentosSocketApp() {
        singletons.add(new HomeServlet()); //instancia um servlet do serviço haramed com base no config 
        singletons.add(new ResetServlet()); // instancia um objeto servlet de reset
        singletons.add(new StatusServlet());  //instancia  um objeto servlet de statussta
    }

    @Override
    public Set<Class<?>> getClasses() {
        return empty;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
