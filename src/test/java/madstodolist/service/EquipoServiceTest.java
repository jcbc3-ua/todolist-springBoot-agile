package madstodolist.service;

import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import madstodolist.dto.EquipoData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class EquipoServiceTest {

    @Autowired
    EquipoService equipoService;

    @Autowired
    UsuarioService usuarioService;

    @Test
    public void crearRecuperarEquipo() {
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        assertThat(equipo.getId()).isNotNull();

        EquipoData equipoBd = equipoService.recuperarEquipo(equipo.getId());
        assertThat(equipoBd).isNotNull();
        assertThat(equipoBd.getNombre()).isEqualTo("Proyecto 1");
    }

    @Test
    public void listadoEquiposOrdenAlfabetico() {
        // GIVEN
        // Dos equipos en la base de datos
        equipoService.crearEquipo("Proyecto BBB");
        equipoService.crearEquipo("Proyecto AAA");

        // WHEN
        // Recuperamos los equipos
        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();

        // THEN
        // Los equipos están ordenados por nombre
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto AAA");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto BBB");
    }

    @Test
    public void añadirUsuarioAEquipo() {
        // GIVEN
        // Un usuario y un equipo en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");

        // WHEN
        // Añadimos el usuario al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // THEN
        // El usuario pertenece al equipo
        List<UsuarioData> usuarios = equipoService.usuariosEquipo(equipo.getId());
        assertThat(usuarios).hasSize(1);
        assertThat(usuarios.get(0).getEmail()).isEqualTo("user@ua");
    }

    @Test
    public void recuperarEquiposDeUsuario() {
        // GIVEN
        // Un usuario y dos equipos en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo1 = equipoService.crearEquipo("Proyecto 1");
        EquipoData equipo2 = equipoService.crearEquipo("Proyecto 2");
        equipoService.añadirUsuarioAEquipo(equipo1.getId(), usuario.getId());
        equipoService.añadirUsuarioAEquipo(equipo2.getId(), usuario.getId());

        // WHEN
        // Recuperamos los equipos del usuario
        List<EquipoData> equipos = equipoService.equiposUsuario(usuario.getId());

        // THEN
        // El usuario pertenece a los dos equipos
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto 1");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto 2");
    }

    @Test
    public void comprobarExcepciones() {
        // Comprobamos las excepciones lanzadas por los métodos
        // recuperarEquipo, añadirUsuarioAEquipo, usuariosEquipo y equiposUsuario
        assertThatThrownBy(() -> equipoService.recuperarEquipo(1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(1L, 1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.usuariosEquipo(1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.equiposUsuario(1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.crearEquipo(""))
                .isInstanceOf(EquipoServiceException.class);




        // Creamos un equipo pero no un usuario y comprobamos que también se lanza una excepción
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(equipo.getId(), 1L))
                .isInstanceOf(EquipoServiceException.class);
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        UsuarioData usuario1 = usuario;
        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario1.getId()))
                .isInstanceOf(EquipoServiceException.class);

    }

    @Test
    public void eliminarUsuariodeEquipo() {
        // GIVEN
        // Un usuario y un equipo en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        List<UsuarioData> usuarios = equipoService.usuariosEquipo(equipo.getId());
        assertThat(usuarios).hasSize(1);
        // WHEN
        // Eliminamos el equipo
        equipoService.eliminarUsuariodeEquipo(equipo.getId(),usuario.getId());

        // THEN
        // El equipo ya no existe
        usuarios = equipoService.usuariosEquipo(equipo.getId());
        assertThat(usuarios).isEmpty();
    }

    @Test
    public void usuarioPerteneceaEquipo () {
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        // GIVEN 
        // Un usuario y un equipo en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setNombre("juan");
        usuario.setEmail("user@ua");
        usuario.setPassword("123");

        usuario = usuarioService.registrar(usuario);

        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // WHEN
        // Comprobamos si el usuario pertenece al equipo

        // THEN
        // El usuario pertenece al equipo
        Boolean pertenece = equipoService.perteneceUsuarioaEquipo(usuario.getId(),equipo.getId());
        assertThat(pertenece).isTrue();
    }

    @Test
    public void testBorrarEquipo() {
        // GIVEN
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");

        // WHEN
        // Borramos el equipo correspondiente al identificador,
        equipoService.borrarEquipo(equipo.getId());

        // THEN
        // Verificamos que el equipo ya no existe en la base de datos.
        assertThatThrownBy(() -> equipoService.recuperarEquipo(equipo.getId()))
                .isInstanceOf(EquipoServiceException.class);

    }

    @Test
    public void testModificarNombreEquipo() {
        // GIVEN

        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");

        // WHEN
        // modificamos el equipo correspondiente al identificador,

        equipoService.modificarEquipo(equipo.getId(), "Proyecto ABB");

        // THEN
        // al buscar por el identificador en la base de datos se devuelve la tarea modificada

        EquipoData equipomodificado = equipoService.recuperarEquipo(equipo.getId());
        assertThat(equipomodificado.getNombre()).isEqualTo("Proyecto ABB");

        // y el usuario tiene también esa tarea modificada.
        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();
        assertThat(equipos).contains(equipomodificado);
    }


}