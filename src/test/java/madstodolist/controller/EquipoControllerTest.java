package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.EquipoNotFoundException;
import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Equipo;
import madstodolist.service.EquipoService;
import madstodolist.service.EquipoServiceException;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class EquipoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private UsuarioService usuarioService;


    @MockBean
    private ManagerUserSession managerUserSession;




    @Test
    public void listarEquipos() throws Exception {
        // GIVEN
        EquipoData equipo1 = equipoService.crearEquipo("Equipo1");
        EquipoData equipo2 = equipoService.crearEquipo("Equipo2");
        UsuarioData usuario = new UsuarioData();
        usuario.setNombre("Juan Carlos");
        usuario.setEmail("jcbc3@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);

        // Supongamos que el usuario está añadido al equipo1
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());



        // WHEN, THEN

        String url = "/equipos";

        this.mockMvc.perform(get(url))
                .andExpect((content().string(allOf(
                        containsString("Equipos"),
                        containsString("Equipo1"),
                        containsString("Equipo2")
                ))));
    }

    @Test
    public void usuariosDeEquipo() throws Exception{
        EquipoData equipo1 = equipoService.crearEquipo("Equipo1");
        //añado un usuario al equipo
        UsuarioData usuario = new UsuarioData();
        usuario.setNombre("Juan Carlos");
        usuario.setEmail("jcbc3@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        //añado otro usuario al equipo
        UsuarioData usuario2 = new UsuarioData();
        usuario2.setNombre("Alonso");
        usuario2.setEmail("alonso@ua");
        usuario2.setPassword("123");
        usuario2 = usuarioService.registrar(usuario2);
        equipoService.añadirUsuarioAEquipo(equipo1.getId(), usuario.getId());
        equipoService.añadirUsuarioAEquipo(equipo1.getId(), usuario2.getId());


        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());


        String url = "/equipos/" + equipo1.getId().toString();

        this.mockMvc.perform(get(url))
                .andExpect((content().string(allOf(
                        containsString("Juan Carlos"),
                        containsString("jcbc3@ua"),
                        containsString("Alonso"),
                        containsString("alonso@ua"),
                        containsString("Usuarios del Equipo: " + equipo1.getNombre())
                ))));


    }

    @Test
    public void testNuevoEquipoRedirectYAñadeEquipo() throws Exception {
        UsuarioData usuario = new UsuarioData();
        usuario.setNombre("Juan Carlos");
        usuario.setEmail("jcbc3@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);

        // Supongamos que el usuario está añadido al equipo1
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());


        // WHEN, THEN

        String urlPost = "/equipos/nuevo";
        String urlRedirect = "/equipos";

        this.mockMvc.perform(post(urlPost)
                        .param("nombre", "Equipo1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlRedirect));

        // y si después consultamos el listado de tareas con una petición
        // GET el HTML contiene la tarea añadida.

        this.mockMvc.perform(get(urlRedirect))
                .andExpect((content().string(containsString("Equipo1"))));
    }

    @Test
    public void testAñadirUsuarioAEquipo() throws Exception {
        // GIVEN
        EquipoData equipo = equipoService.crearEquipo("Mi Equipo");
        UsuarioData usuario = new UsuarioData();
        usuario.setNombre("Juan Carlos");
        usuario.setEmail("jcbc3@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);

        // Simula que el usuario está logueado
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        // WHEN
        String url = "/equipos/" + equipo.getId() + "/añadir";
        this.mockMvc.perform(get(url))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));

        // THEN
        // Verifica que el usuario se ha añadido correctamente al equipo
        List<UsuarioData> usuariosEnEquipo = equipoService.usuariosEquipo(equipo.getId());
        assertTrue(usuariosEnEquipo.contains(usuario));
    }

    @Test
    public void testEliminarDeEquipo() throws Exception {
        // GIVEN
        EquipoData equipo = equipoService.crearEquipo("Mi Equipo");
        UsuarioData usuario = new UsuarioData();
        usuario.setNombre("Juan Carlos");
        usuario.setEmail("jcbc3@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // Simula que el usuario está logueado
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        // WHEN
        String url = "/equipos/" + equipo.getId() + "/eliminar";
        this.mockMvc.perform(get(url))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));

        // THEN
        // Verifica que el usuario se ha eliminado correctamente del equipo
        List<UsuarioData> usuariosEnEquipo = equipoService.usuariosEquipo(equipo.getId());
        assertFalse(usuariosEnEquipo.contains(usuario));
    }

    @Test
    public void testBotonEliminar() throws Exception {
        // GIVEN
        EquipoData equipo = equipoService.crearEquipo("Equipo1");
        UsuarioData usuario = new UsuarioData();
        usuario.setNombre("Juan Carlos");
        usuario.setEmail("jcbc3@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());
        // Supongamos que el usuario está añadido al equipo1
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        // WHEN, THEN
        String url = "/equipos";

        this.mockMvc.perform(get(url))
                .andExpect(content().string(containsString("Eliminarme")));
    }

    @Test
    public void testBotonAñadir() throws Exception {
        // GIVEN
        EquipoData equipo = equipoService.crearEquipo("Equipo1");
        UsuarioData usuario = new UsuarioData();
        usuario.setNombre("Juan Carlos");
        usuario.setEmail("jcbc3@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);

        // Supongamos que el usuario está añadido al equipo1
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        // WHEN, THEN
        String url = "/equipos";

        this.mockMvc.perform(get(url))
                .andExpect(content().string(containsString("Añadirme")));
    }

    @Test
    public void testModificarEquipo() throws Exception {
        // GIVEN
        EquipoData equipo = equipoService.crearEquipo("EquipoOriginal");
        UsuarioData usuario = new UsuarioData();
        usuario.setNombre("Juan Carlos");
        usuario.setEmail("jcbc3@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // Simula que el usuario está logueado
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        // WHEN
        String url = "/equipos/" + equipo.getId() + "/modificar";
        String nuevoNombre = "EquipoModificado";
        this.mockMvc.perform(post(url)
                        .param("nombre", nuevoNombre))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));

        // THEN
        // Verifica que el nombre del equipo se ha modificado correctamente
        EquipoData equipoModificado = equipoService.recuperarEquipo(equipo.getId());
        assertEquals(nuevoNombre, equipoModificado.getNombre());
    }

    @Test
    public void borrarEquipoDevuelveOKyBorraEquipo() throws Exception {
        // GIVEN
        EquipoData equipo = equipoService.crearEquipo("EquipoABorrar");
        UsuarioData usuario = new UsuarioData();
        usuario.setNombre("Juan Carlos");
        usuario.setEmail("jcbc3@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // Supongamos que el usuario está añadido al equipo
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        // WHEN, THEN
        // Realizamos la petición DELETE para borrar el equipo,
        // se espera que el estado HTTP sea OK.

        String urlBorrar = "/equipos/" + equipo.getId();

        this.mockMvc.perform(delete(urlBorrar))
                .andExpect(status().isOk());

        // Y cuando se intenta recuperar el equipo, se lanza una excepción
        // TareaNotFoundException, indicando que el equipo no se encuentra.

        assertThrows(EquipoServiceException.class, () -> equipoService.recuperarEquipo(equipo.getId()));
    }














}
