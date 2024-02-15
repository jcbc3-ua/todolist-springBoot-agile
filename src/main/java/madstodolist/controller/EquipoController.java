package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.EquipoNotFoundException;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.controller.exception.UnauthorizedException;
import madstodolist.dto.EquipoData;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EquipoController {

    @Autowired
    ManagerUserSession managerUserSession;

    @Autowired
    EquipoService equipoService;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/equipos")
    public String listarEquipos(Model model, HttpSession session) {
        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();

        if (idUsuarioLogueado != null) {
            model.addAttribute("barramenu", "menulogeado");
            UsuarioData usuario = usuarioService.findById(idUsuarioLogueado);
            model.addAttribute("usuario", usuario);
        } else {
            model.addAttribute("barramenu", "menunologeado");
            throw new UnauthorizedException();
        }

        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();
        model.addAttribute("equipos", equipos);

        Map<Long, Boolean> anadidoMap = new HashMap<>();
        for (EquipoData equipo : equipos) {
            Boolean anadido = equipoService.perteneceUsuarioaEquipo(idUsuarioLogueado,equipo.getId());
            anadidoMap.put(equipo.getId(), anadido);
        }

        model.addAttribute("anadidoMap", anadidoMap);

        return "listadoequipos";
    }


    @GetMapping("/equipos/{id}")
    public String listarUsuariosEquipo(@PathVariable(value="id") Long idEquipo,Model model, HttpSession session) {
        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();

        if(idUsuarioLogueado != null){
            model.addAttribute("barramenu","menulogeado");
            UsuarioData usuario = usuarioService.findById(idUsuarioLogueado);
            model.addAttribute("usuario", usuario);
        }else{
            model.addAttribute("barramenu","menunologeado");
            throw new UnauthorizedException();
        }

        List<UsuarioData> usuarios = equipoService.usuariosEquipo(idEquipo);
        model.addAttribute("usuarios", usuarios);
        EquipoData equipo = equipoService.recuperarEquipo(idEquipo);
        model.addAttribute("equipo", equipo);

        return "usuariosequipos";
    }

    @GetMapping("/equipos/nuevo")
    public String formNuevoEquipo(@ModelAttribute EquipoData equipoData,Model model,HttpSession session) {
        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();

        if(idUsuarioLogueado != null){
            model.addAttribute("barramenu","menulogeado");
        }else{
            model.addAttribute("barramenu","menunologeado");
            throw new UnauthorizedException();
        }

        UsuarioData usuario = usuarioService.findById(idUsuarioLogueado);
        model.addAttribute("usuario", usuario);
        return "formNuevoEquipo";
    }

    @PostMapping("/equipos/nuevo")
    public String nuevoEquipo(@ModelAttribute EquipoData equipoData,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {

        equipoService.crearEquipo(equipoData.getNombre());
        flash.addFlashAttribute("mensaje", "Equipo creado correctamente");
        return "redirect:/equipos";
    }

    @GetMapping("/equipos/{id}/añadir")
    public String añadiraEquipo(@PathVariable(value="id")Long equipoId,Model model){

        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();

        if(idUsuarioLogueado != null){
            model.addAttribute("barramenu","menulogeado");
            UsuarioData usuario = usuarioService.findById(idUsuarioLogueado);
            model.addAttribute("usuario", usuario);

        }else{
            model.addAttribute("barramenu","menunologeado");
            throw new UnauthorizedException();
        }


        EquipoData equipo = equipoService.recuperarEquipo(equipoId);
        equipoService.añadirUsuarioAEquipo(equipoId, idUsuarioLogueado);


        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();
        model.addAttribute("equipos", equipos);
        List<UsuarioData> usuarios = equipoService.usuariosEquipo(equipoId);
        model.addAttribute("usuarios", usuarios);

        return "redirect:/equipos"; // Redirige de vuelta al listado de usuarios
    }

    @GetMapping("/equipos/{id}/eliminar")
    public String eliminardeEquipo(@PathVariable(value="id")Long equipoId,Model model){

        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();

        if(idUsuarioLogueado != null){
            model.addAttribute("barramenu","menulogeado");
            UsuarioData usuario = usuarioService.findById(idUsuarioLogueado);
            model.addAttribute("usuario", usuario);
        }else{
            model.addAttribute("barramenu","menunologeado");
            throw new UnauthorizedException();
        }


        EquipoData equipo = equipoService.recuperarEquipo(equipoId);
        equipoService.eliminarUsuariodeEquipo(equipoId, idUsuarioLogueado);

        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();
        model.addAttribute("equipos", equipos);
        List<UsuarioData> usuarios = equipoService.usuariosEquipo(equipoId);
        model.addAttribute("usuarios", usuarios);

        return "redirect:/equipos"; // Redirige de vuelta al listado de usuarios
    }





    @GetMapping("/equipos/{id}/modificar")
    public String formNuevoEquipo(@PathVariable(value="id") Long idEquipo, @ModelAttribute EquipoData equipoData,
                                  Model model,HttpSession session) {

        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();
        UsuarioData usuario = usuarioService.findById(idUsuarioLogueado);
        EquipoData equipo = equipoService.recuperarEquipo(idEquipo);

        if(idUsuarioLogueado != null){
            model.addAttribute("barramenu","menulogeado");
        }else if (!usuario.getIsAdmin() || idUsuarioLogueado == null){
            model.addAttribute("barramenu","menunologeado");
            throw new UnauthorizedException();
        }

        equipoData.setNombre(equipo.getNombre()); // Añade esta línea para establecer el valor del nombre
        model.addAttribute("equipo", equipo);
        model.addAttribute("usuario", usuario);
        return "formEditarEquipo";
    }


    @PostMapping("/equipos/{id}/modificar")
    public String nuevoEquipo(@PathVariable(value="id") Long idEquipo,@ModelAttribute EquipoData equipoData,
                              Model model, RedirectAttributes flash,
                              HttpSession session) {
        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();
        UsuarioData usuario = usuarioService.findById(idUsuarioLogueado);
        EquipoData equipo = equipoService.recuperarEquipo(idEquipo);
        if(idUsuarioLogueado != null){
            model.addAttribute("barramenu","menulogeado");
        }else if (!usuario.getIsAdmin() || idUsuarioLogueado == null){
            model.addAttribute("barramenu","menunologeado");
            throw new UnauthorizedException();
        }
        if(equipo == null){
            throw new UnauthorizedException();
        }

        model.addAttribute("equipo", equipo);
        equipoService.modificarEquipo(idEquipo, equipoData.getNombre());
        flash.addFlashAttribute("mensaje", "Equipo modificado correctamente");
        return "redirect:/equipos";
    }

    @DeleteMapping("/equipos/{id}")
    @ResponseBody
    // La anotación @ResponseBody sirve para que la cadena devuelta sea la resupuesta
    // de la petición HTTP, en lugar de una plantilla thymeleaf
    public String borrarEquipo(@PathVariable(value="id") Long idEquipo,
                               RedirectAttributes flash, HttpSession session,Model model) {
        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();
        UsuarioData usuario = usuarioService.findById(idUsuarioLogueado);
        EquipoData equipo = equipoService.recuperarEquipo(idEquipo);

        if(idUsuarioLogueado != null){
            model.addAttribute("barramenu","menulogeado");
        }else if (!usuario.getIsAdmin() || idUsuarioLogueado == null){
            model.addAttribute("barramenu","menunologeado");
            throw new UnauthorizedException();
        }
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }

        equipoService.borrarEquipo(idEquipo);
        return "";
    }



















}
