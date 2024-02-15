package madstodolist.dto;

import java.io.Serializable;
import java.util.Objects;

// Data Transfer Object para la clase Tarea
public class TareaData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String titulo;
    private Long usuarioId;  // Esta es la ID del usuario asociado

    // Getters y setters

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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    // Sobreescribimos equals y hashCode para que dos tareas sean iguales
    // si tienen el mismo ID (ignoramos el resto de atributos)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TareaData)) return false;
        TareaData tareaData = (TareaData) o;
        return Objects.equals(id, tareaData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
