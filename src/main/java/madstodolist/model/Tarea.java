package madstodolist.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tareas")
public class Tarea implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String titulo;

    @NotNull
    // Relación muchos-a-uno entre tareas y usuario
    @ManyToOne
    // Nombre de la columna en la BD que guarda físicamente
    // el ID del usuario con el que está asociado una tarea
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Constructor vacío necesario para JPA/Hibernate.
    // No debe usarse desde la aplicación.
    public Tarea() {}

    // Al crear una tarea la asociamos automáticamente a un usuario
    public Tarea(Usuario usuario, String titulo) {
        this.titulo = titulo;
        setUsuario(usuario); // Esto añadirá la tarea a la lista de tareas del usuario
    }

    // Getters y setters básicos

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // Getters y setters de la relación muchos-a-uno con Usuario

    public Usuario getUsuario() {
        return usuario;
    }

    // Método para establecer la relación con el usuario

    public void setUsuario(Usuario usuario) {
        // Comprueba si el usuario ya está establecido
        if(this.usuario != usuario) {
            this.usuario = usuario;
            // Añade la tarea a la lista de tareas del usuario
            usuario.addTarea(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        if (id != null && tarea.id != null)
            // Si tenemos los ID, comparamos por ID
            return Objects.equals(id, tarea.id);
        // si no comparamos por campos obligatorios
        return titulo.equals(tarea.titulo) &&
                usuario.equals(tarea.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, usuario);
    }
}
