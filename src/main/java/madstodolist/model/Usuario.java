package madstodolist.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String email;
    private String nombre;
    private String password;

    private Boolean isAdmin;

    private Boolean bloqueado;
    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    // La relación es lazy por defecto,
    // es necesario acceder a la lista de tareas para que se carguen
    @OneToMany(mappedBy = "usuario")
    Set<Tarea> tareas = new HashSet<>();

    @ManyToMany(mappedBy = "usuarios")
    Set<Equipo> equipos = new HashSet<>();

    // Constructor vacío necesario para JPA/Hibernate.
    // No debe usarse desde la aplicación.
    public Usuario() {}

    // Constructor público con los atributos obligatorios. En este caso el correo electrónico.
    public Usuario(String email) {
        this.email = email;
    }

    // Getters y setters atributos básicos

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsAdmin() {
    	return isAdmin;
    }

    public void setIsadmin(Boolean isadmin) {
    	this.isAdmin = isadmin;
    }

    public Boolean getBloqueado() {
    	return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
    	this.bloqueado = bloqueado;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    // Getters y setters de la relación

    public Set<Tarea> getTareas() {
        return tareas;
    }

    // Método helper para añadir una tarea a la lista y establecer la relación inversa
    public void addTarea(Tarea tarea) {
        // Si la tarea ya está en la lista, no la añadimos
        if (tareas.contains(tarea)) return;
        // Añadimos la tarea a la lista
        tareas.add(tarea);
        // Establecemos la relación inversa del usuario en la tarea
        if (tarea.getUsuario() != this) {
            tarea.setUsuario(this);
        }
    }

    public Set<Equipo> getEquipos() {
    	return equipos;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        if (id != null && usuario.id != null)
            // Si tenemos los ID, comparamos por ID
            return Objects.equals(id, usuario.id);
        // si no comparamos por campos obligatorios
        return email.equals(usuario.email);
    }

    @Override
    public int hashCode() {
        // Generamos un hash basado en los campos obligatorios
        return Objects.hash(email);
    }
}
