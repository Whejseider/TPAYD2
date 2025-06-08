package model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


public class Comando implements Serializable {
    private static final long serialVersionUID = 1L;
    private TipoSolicitud tipoSolicitud;
    private TipoRespuesta tipoRespuesta;
    private Object contenido;
    private List<Object> contenidos;

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

    public Comando(TipoSolicitud tipoSolicitud, TipoRespuesta tipoRespuesta, List<Object> contenidos){
        this.tipoSolicitud = tipoSolicitud;
        this.tipoRespuesta = tipoRespuesta;
        this.contenidos = contenidos;
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

    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public void setTipoRespuesta(TipoRespuesta tipoRespuesta) {
        this.tipoRespuesta = tipoRespuesta;
    }

    public List<Object> getContenidos() {
        return contenidos;
    }

    public void setContenidos(List<Object> contenidos) {
        this.contenidos = contenidos;
    }

    public void setContenido(Object contenido) {
        this.contenido = contenido;
    }

    public void setTipoAccion(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comando comando = (Comando) o;
        return tipoSolicitud == comando.tipoSolicitud && tipoRespuesta == comando.tipoRespuesta && Objects.equals(contenido, comando.contenido);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoSolicitud, tipoRespuesta, contenido);
    }
}
