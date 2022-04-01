package br.com.intmed.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

//annotations fazem as propertyes adquirirem formato XML
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "config")

public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "backend_api_url")
    String backendApiUrl;

    @XmlElement(name = "base_time_schedule")
    Integer baseTimeSchedule;

    @XmlElement(name = "version")
    String version;

   //construtor vazio
    public Config(){}

    //construtor sobrecarregado - backend API URL, base time, versão
    public Config(String backendApiUrl, Integer baseTimeSchedule, String version) {
        this.backendApiUrl = backendApiUrl;
        this.baseTimeSchedule = baseTimeSchedule;
        this.version = version;
    }

    
    //geters e setters
    //---------------
    /**
     * @return the baseTimeSchedule
     */
    public Integer getBaseTimeSchedule() {
        return baseTimeSchedule;
    }

    /**
     * @param baseTimeSchedule the baseTimeSchedule to set
     */
    public void setBaseTimeSchedule(Integer baseTimeSchedule) {
        this.baseTimeSchedule = baseTimeSchedule;
    }

    /**
     * @return the backendApiIrl
     */
    public String getBackendApiUrl() {
        return backendApiUrl;
    }

    /**
     * @param backendApiUrl the backendApiIrl to set
     */
    public void setBackendApiUrl(String backendApiUrl) {
        this.backendApiUrl = backendApiUrl;
    }

    /**
     * @param version Websocket version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return Websocket version
     */
    public String getVersion() {
        return this.version;
    }

}
