package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.dto.LoginData;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class TareaController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    @Autowired
    ManagerUserSession managerUserSession;

    private void comprobarUsuarioLogeado(Long idUsuario) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (!idUsuario.equals(idUsuarioLogeado))
            throw new UsuarioNoLogeadoException();
    }


    @GetMapping("/usuarios/{id}/tareas/nueva")
    public String formNuevaTarea(@PathVariable(value="id") Long idUsuario,
                                 @ModelAttribute TareaData tareaData, Model model,
                                 HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);
        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();

        if(idUsuarioLogueado != null){
            model.addAttribute("barramenu","menulogeado");
        }else{
            model.addAttribute("barramenu","menunologeado");
        }

        UsuarioData usuario = usuarioService.findById(idUsuario);
        model.addAttribute("usuario", usuario);
        return "formNuevaTarea";
    }

    @PostMapping("/usuarios/{id}/tareas/nueva")
    public String nuevaTarea(@PathVariable(value="id") Long idUsuario, @ModelAttribute TareaData tareaData,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);

        tareaService.nuevaTareaUsuario(idUsuario, tareaData.getTitulo());
        flash.addFlashAttribute("mensaje", "Tarea creada correctamente");
        return "redirect:/usuarios/" + idUsuario + "/tareas";
     }

    @GetMapping("/usuarios/{id}/tareas")
    public String listadoTareas(@PathVariable(value="id") Long idUsuario,LoginData loginData, Model model, HttpSession session) {
        comprobarUsuarioLogeado(idUsuario);
        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();

        if(idUsuarioLogueado != null){
            model.addAttribute("barramenu","menulogeado");
        }else{
            model.addAttribute("barramenu","menunologeado");
        }

        UsuarioData usuario = usuarioService.findById(idUsuario);
        List<TareaData> tareas = tareaService.allTareasUsuario(idUsuario);
        model.addAttribute("usuario", usuario);
        model.addAttribute("tareas", tareas);



        return "listaTareas";
    }






    @GetMapping("/tareas/{id}/editar")
    public String formEditaTarea(@PathVariable(value="id") Long idTarea, @ModelAttribute TareaData tareaData,
                                 Model model, HttpSession session) {
        TareaData tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }
        comprobarUsuarioLogeado(tarea.getUsuarioId());

        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();

        if(idUsuarioLogueado != null){
            model.addAttribute("barramenu","menulogeado");

        }else{
            model.addAttribute("barramenu","menunologeado");
        }

        model.addAttribute("tarea", tarea);
        tareaData.setTitulo(tarea.getTitulo());
        UsuarioData usuario = usuarioService.findById(tarea.getUsuarioId());
        model.addAttribute("usuario", usuario);
        return "formEditarTarea";
    }


    @PostMapping("/tareas/{id}/editar")
    public String grabaTareaModificada(@PathVariable(value="id") Long idTarea, @ModelAttribute TareaData tareaData,
                                       Model model, RedirectAttributes flash, HttpSession session) {
        TareaData tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        Long idUsuario = tarea.getUsuarioId();

        comprobarUsuarioLogeado(idUsuario);

        tareaService.modificaTarea(idTarea, tareaData.getTitulo());
        flash.addFlashAttribute("mensaje", "Tarea modificada correctamente");
        return "redirect:/usuarios/" + tarea.getUsuarioId() + "/tareas";
    }

    @DeleteMapping("/tareas/{id}")
    @ResponseBody
    // La anotación @ResponseBody sirve para que la cadena devuelta sea la resupuesta
    // de la petición HTTP, en lugar de una plantilla thymeleaf
    public String borrarTarea(@PathVariable(value="id") Long idTarea, RedirectAttributes flash, HttpSession session) {
        TareaData tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        comprobarUsuarioLogeado(tarea.getUsuarioId());

        tareaService.borraTarea(idTarea);
        return "";
    }


}

