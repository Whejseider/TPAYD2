package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Directorio implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<User> directorio;

    public Directorio() {
        this.directorio = new CopyOnWriteArrayList<>();
    }

    //Constructor copia
    public Directorio(Directorio original) {

        this.directorio = new CopyOnWriteArrayList<>();
        if (original != null && original.directorio != null) {
            for (User userOriginal : original.directorio) {
                this.directorio.add(new User(userOriginal));
            }
        }
    }

    public List<User> getDirectorio() {
        return directorio;
    }

    public void setDirectorio(List<User> directorio) {
        this.directorio = new CopyOnWriteArrayList<>(directorio);
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
