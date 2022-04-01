package br.com.intmed.websocket;

import br.com.intmed.services.Heramed;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.Session;

import java.io.IOException;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;


public class BaseWebsocket {

   //cria um novo serviço Heramed
	protected static Heramed service = new Heramed();

    protected Timer onConnection(String timerName, int period, OnConnectedAction onConnectedAction) {
    	
        Timer timer = new Timer(timerName);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            try {
                onConnectedAction.handle();
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        }, 0, period);

        return timer;
    }

   //desconecta a sessão
    protected void closeConnection(Queue<Session> sessions, Session session, CloseReason reason, OnClosedAction onClosedAction ) {
        sessions.remove(session);

        if(sessions.size() == 0) {
            onClosedAction.handle();
        }

        System.out.println("Client from " + session.getPathParameters().toString() + " disconnected. CloseCode: " + reason.getCloseCode().getCode() + " - " + reason.getCloseCode().toString());
    }

    //obtém os parâmetros do query string - aqui chamado de mapkey
    protected String getMapKey(Session session) {
        return session.getPathParameters().toString();
    }

    //protótipo - evento
    public interface OnConnectedAction {
        void handle() throws IOException, EncodeException;
    }
    //protótipo - evento
    public interface OnClosedAction {
        void handle();
    }
}
