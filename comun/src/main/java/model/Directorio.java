package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Directorio implements Serializable {
    private ArrayList<User> directorio;

    public Directorio() {
        directorio = new ArrayList<>();
    }

    public ArrayList<User> getDirectorio() {
        return directorio;
    }

    public void setDirectorio(ArrayList<User> directorio) {
        this.directorio = directorio;
    }

    public void add(User user) {
        directorio.add(user);
    }

    public void updateUser(User user){
        for (User u : directorio){
            if (u.getNombreUsuario().equals(user.getNombreUsuario())){
                directorio.set(directorio.indexOf(u), user);
            }
        }
    }

    public User getUsuarioPorNombre(String nombreUsuario) {
        return directorio.stream()
                .filter(u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario))
                .findFirst()
                .orElse(null);
    }



}
