package model;

import java.io.Serializable;


public class Comando implements Serializable {
    private TipoSolicitud tipoSolicitud;
    private TipoRespuesta tipoRespuesta;
    private Object contenido;

    public Comando(TipoSolicitud tipoSolicitud, Object contenido) {
        this.tipoSolicitud = tipoSolicitud;
        this.contenido = contenido;
    }

    public Comando(TipoSolicitud tipoSolicitud, TipoRespuesta tipoRespuesta) {
        this.tipoSolicitud = tipoSolicitud;
        this.tipoRespuesta = tipoRespuesta;
    }

    public Comando(TipoSolicitud tipoSolicitud, TipoRespuesta tipoRespuesta, Object contenido) {
        this.tipoSolicitud = tipoSolicitud;
        this.tipoRespuesta = tipoRespuesta;
        this.contenido = contenido;
    }

    public Comando(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public TipoSolicitud getTipoSolicitud() {
        return tipoSolicitud;
    }

    public TipoRespuesta getTipoRespuesta() {
        return tipoRespuesta;
    }

    public Object getContenido() {
        return contenido;
    }

    public void setContenido(Object contenido) {
        this.contenido = contenido;
    }

    public void setTipoAccion(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }
}
