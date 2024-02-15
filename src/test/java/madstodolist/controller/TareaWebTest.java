package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class TareaWebTest {

    @Autowired
    private MockMvc mockMvc;

    // Declaramos los servicios como Autowired
    @Autowired
    private TareaService tareaService;

    @Autowired
    private UsuarioService usuarioService;

    // Moqueamos el managerUserSession para poder moquear el usuario logeado
    @MockBean
    private ManagerUserSession managerUserSession;

    // Método para inicializar los datos de prueba en la BD
    // Devuelve un mapa con los identificadores del usuario y de la primera tarea añadida

    Map<String, Long> addUsuarioTareasBD() {
        // Añadimos un usuario a la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);

        // Y añadimos dos tareas asociadas a ese usuario
        TareaData tarea1 = tareaService.nuevaTareaUsuario(usuario.getId(), "Lavar coche");
        tareaService.nuevaTareaUsuario(usuario.getId(), "Renovar DNI");

        // Devolvemos los ids del usuario y de la primera tarea añadida
        Map<String, Long> ids = new HashMap<>();
        ids.put("usuarioId", usuario.getId());
        ids.put("tareaId", tarea1.getId());
        return ids;

    }

    @Test
    public void listaTareas() throws Exception {
        // GIVEN
        // Un usuario con dos tareas en la BD
        Long usuarioId = addUsuarioTareasBD().get("usuarioId");

        // Moqueamos el método usuarioLogeado para que devuelva el usuario 1L,
        // el mismo que se está usando en la petición. De esta forma evitamos
        // que salte la excepción de que el usuario que está haciendo la
        // petición no está logeado.
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // se realiza la petición GET al listado de tareas del usuario,
        // el HTML devuelto contiene las descripciones de sus tareas.

        String url = "/usuarios/" + usuarioId.toString() + "/tareas";

        this.mockMvc.perform(get(url))
                .andExpect((content().string(allOf(
                        containsString("Lavar coche"),
                        containsString("Renovar DNI")
                ))));
    }

    @Test
    public void getNuevaTareaDevuelveForm() throws Exception {
        // GIVEN
        // Un usuario con dos tareas en la BD
        Long usuarioId = addUsuarioTareasBD().get("usuarioId");

        // Ver el comentario en el primer test
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // si ejecutamos una petición GET para crear una nueva tarea de un usuario,
        // el HTML resultante contiene un formulario y la ruta con
        // la acción para crear la nueva tarea.

        String urlPeticion = "/usuarios/" + usuarioId.toString() + "/tareas/nueva";
        String urlAction = "action=\"/usuarios/" + usuarioId.toString() + "/tareas/nueva\"";

        this.mockMvc.perform(get(urlPeticion))
                .andExpect((content().string(allOf(
                        containsString("form method=\"post\""),
                        containsString(urlAction)
                ))));
    }

    @Test
    public void postNuevaTareaDevuelveRedirectYAñadeTarea() throws Exception {
        // GIVEN
        // Un usuario con dos tareas en la BD
        Long usuarioId = addUsuarioTareasBD().get("usuarioId");

        // Ver el comentario en el primer test
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // realizamos la petición POST para añadir una nueva tarea,
        // el estado HTTP que se devuelve es un REDIRECT al listado
        // de tareas.

        String urlPost = "/usuarios/" + usuarioId.toString() + "/tareas/nueva";
        String urlRedirect = "/usuarios/" + usuarioId.toString() + "/tareas";

        this.mockMvc.perform(post(urlPost)
                        .param("titulo", "Estudiar examen MADS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlRedirect));

        // y si después consultamos el listado de tareas con una petición
        // GET el HTML contiene la tarea añadida.

        this.mockMvc.perform(get(urlRedirect))
                .andExpect((content().string(containsString("Estudiar examen MADS"))));
    }

    @Test
    public void deleteTareaDevuelveOKyBorraTarea() throws Exception {
        // GIVEN
        // Un usuario con dos tareas en la BD
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaLavarCocheId = ids.get("tareaId");

        // Ver el comentario en el primer test
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // realizamos la petición DELETE para borrar una tarea,
        // se devuelve el estado HTTP que se devuelve es OK,

        String urlDelete = "/tareas/" + tareaLavarCocheId.toString();

        this.mockMvc.perform(delete(urlDelete))
                .andExpect(status().isOk());

        // y cuando se pide un listado de tareas del usuario, la tarea borrada ya no aparece.

        String urlListado = "/usuarios/" + usuarioId + "/tareas";

        this.mockMvc.perform(get(urlListado))
                .andExpect(content().string(
                        allOf(not(containsString("Lavar coche")),
                                containsString("Renovar DNI"))));
    }

    @Test
    public void editarTareaActualizaLaTarea() throws Exception {
        // GIVEN
        // Un usuario con dos tareas en la BD
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaLavarCocheId = ids.get("tareaId");

        // Ver el comentario en el primer test
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // realizamos una petición POST al endpoint para editar una tarea

        String urlEditar = "/tareas/" + tareaLavarCocheId + "/editar";
        String urlRedirect = "/usuarios/" + usuarioId + "/tareas";

        this.mockMvc.perform(post(urlEditar)
                        .param("titulo", "Limpiar cristales coche"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlRedirect));

        // Y si realizamos un listado de las tareas del usuario
        // ha cambiado el título de la tarea modificada

        String urlListado = "/usuarios/" + usuarioId + "/tareas";

        this.mockMvc.perform(get(urlListado))
                .andExpect(content().string(containsString("Limpiar cristales coche")));
    }
}
