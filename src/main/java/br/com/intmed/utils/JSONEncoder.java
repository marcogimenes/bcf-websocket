package br.com.intmed.utils;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class JSONEncoder implements Encoder.Text {

    @Override
    public void init(EndpointConfig config) {
        // do nothing
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public String encode(Object object) {     //converte string em gson
        return GsonUtil.gson.toJson(object);
    }
}
