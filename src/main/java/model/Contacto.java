package model;

public class Contacto extends User {
    private String IP;

    public Contacto(String nombreUsuario, String IP, Integer puerto) {
        super(nombreUsuario, puerto);
        this.IP = IP;
    }


    @Override
    public String getIP() {
        return IP;
    }

    @Override
    public void setIP(String IP) {
        this.IP = IP;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "IP='" + IP + '\'' +
                '}';
    }
}
