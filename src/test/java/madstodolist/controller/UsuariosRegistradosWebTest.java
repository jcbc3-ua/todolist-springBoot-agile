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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class UsuariosRegistradosWebTest {
    @Autowired
    private MockMvc mockMvc;

    // Declaramos los servicios como Autowired

    @Autowired
    private UsuarioService usuarioService;

    // Moqueamos el managerUserSession para poder moquear el usuario logeado
    @MockBean
    private ManagerUserSession managerUserSession;

    public Long addUsuarioBD() throws ParseException {
        // Añadimos usuarios a la base de datos
        UsuarioData usuario1 = new UsuarioData();
        usuario1.setEmail("jcbc3@ua");
        usuario1.setPassword("1234");
        usuario1.setNombre("juan");
        usuario1.setIsadmin(true);

        String fecha = "20011022";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");  //para asignar la fecha
        usuario1.setFechaNacimiento(dateFormat.parse(fecha));  //asigna la fecha

        usuario1 = usuarioService.registrar(usuario1);

        return usuario1.getId();


    }
    @Test
    public void listaUsuarios() throws Exception {
        // GIVEN
        Long usuarioId1 = addUsuarioBD();

        // Simula que primero retorna usuarioId1 y luego usuarioId2
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId1);

        // WHEN, THEN

        String url = "/registrados";

        this.mockMvc.perform(get(url))
                .andExpect((content().string(allOf(
                        containsString("jcbc3@ua")
                ))));


    }

    @Test
    public void descripcionUsuario() throws Exception{
        // GIVEN
        Long usuarioId1 = addUsuarioBD();

        // Simula que primero retorna usuarioId1 y luego usuarioId2
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId1);
        // WHEN, THEN

        String url = "/registrados/" + usuarioId1.toString();

        this.mockMvc.perform(get(url))
                .andExpect((content().string(allOf(
                        containsString("juan"),
                        containsString("jcbc3@ua"),
                        containsString("2001-10-22")
                ))));
    }

    @Test
    public void testListadoUsuarios_NonAdministratorListado() throws Exception {
        UsuarioData usuario1 = new UsuarioData();
        usuario1.setEmail("jcbc3@ua");
        usuario1.setPassword("1234");
        usuario1.setNombre("juan");
        usuario1.setIsadmin(false);
        usuario1 = usuarioService.registrar(usuario1);

        when(managerUserSession.usuarioLogeado()).thenReturn(usuario1.getId()); // Usuario logueado

        String url = "/registrados/";

        mockMvc.perform(get(url))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String errorMsg = result.getResponse().getErrorMessage();
                    assert(errorMsg.contains("No tienes permisos para acceder a esta página"));
                });
    }

    @Test
    public void testListadoUsuarios_NonAdministratorDescripcion() throws Exception {
        UsuarioData usuario1 = new UsuarioData();
        usuario1.setEmail("jcbc3@ua");
        usuario1.setPassword("1234");
        usuario1.setNombre("juan");
        usuario1.setIsadmin(false);
        usuario1 = usuarioService.registrar(usuario1);

        when(managerUserSession.usuarioLogeado()).thenReturn(usuario1.getId()); // Usuario logueado

        String url = "/registrados/" + usuario1.getId().toString();

        mockMvc.perform(get(url))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String errorMsg = result.getResponse().getErrorMessage();
                    assert(errorMsg.contains("No tienes permisos para acceder a esta página"));
                });
    }
}
