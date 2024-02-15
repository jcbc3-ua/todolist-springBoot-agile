package madstodolist.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "equipos")
public class Equipo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nombre;

    // Relación muchos-a-uno entre tareas y usuario
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "equipo_usuario",
            joinColumns = { @JoinColumn(name = "fk_equipo") },
            inverseJoinColumns = {@JoinColumn(name = "fk_usuario")})
    Set<Usuario> usuarios = new HashSet<>();



    public Equipo() {}

    // Al crear una tarea la asociamos automáticamente a un usuario
    public Equipo(String nombre) {
        this.nombre = nombre;
    }


    public Long getId(){
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public Set<Usuario> getUsuarios(){
        return usuarios;
    }
    public void addUsuario(Usuario usuario) {
        // Hay que actualiar ambas colecciones, porque
        // JPA/Hibernate no lo hace automáticamente
        this.getUsuarios().add(usuario);
        usuario.getEquipos().add(this);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Equipo equipo = (Equipo) obj;
        if (id != null && equipo.id != null)
            // Si tenemos los ID, comparamos por ID
            return Objects.equals(id, equipo.id);
        return nombre.equals(equipo.nombre);

    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
