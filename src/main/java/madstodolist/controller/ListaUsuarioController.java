package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.controller.exception.UnauthorizedException;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.dto.LoginData;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ListaUsuarioController {
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ManagerUserSession managerUserSession;


    @GetMapping("/registrados")
    public String listadoUsuarios(@ModelAttribute LoginData loginData,Model model, HttpSession session) {

        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();

        if(idUsuarioLogueado != null){
            UsuarioData usuario = usuarioService.findById(idUsuarioLogueado);
            model.addAttribute("barramenu", "menulogeado");
            model.addAttribute("usuario", usuario);

            if (usuario.getIsAdmin()) {

                List<UsuarioData> usuarios = usuarioService.allUsuario();
                model.addAttribute("usuarios", usuarios);
                return "usuariosregistrados";
            } else {
                // No es administrador, devuelve un código de error "No autorizado"
                throw new UnauthorizedException();
            }
        }else{
            model.addAttribute("barramenu", "menunologeado");
            return "login";
        }
    }

    @GetMapping("/registrados/{id}")
    public String detallesUsuarios(@PathVariable(value="id") Long idUsuario,@ModelAttribute LoginData loginData, Model model, HttpSession session) {
        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();
        UsuarioData usuario = usuarioService.findById(idUsuario);
        UsuarioData logeado = usuarioService.findById(idUsuarioLogueado);

        if (idUsuarioLogueado != null) {

            model.addAttribute("barramenu", "menulogeado");
            model.addAttribute("usuario", usuario);
            if (logeado.getIsAdmin()) {

                return "descripcionusuario";
            } else {
                throw new UnauthorizedException();
            }
        } else {
            model.addAttribute("barramenu", "menunologeado");
            return "login";
        }
    }

    private void comprobarUsuarioLogeadoAdmin() {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        UsuarioData usuario =  usuarioService.findById(idUsuarioLogeado);
        if (!usuario.getIsAdmin())
            throw new UsuarioNoLogeadoException();
    }


    @GetMapping("/bloquear/{id}")
    public String bloquear(@PathVariable(value="id")Long userId,Model model){
        comprobarUsuarioLogeadoAdmin();
        UsuarioData usuario = usuarioService.findById(userId);
        usuarioService.bloquear(userId);

        return "redirect:/registrados"; // Redirige de vuelta al listado de usuarios
    }

    @GetMapping("/desbloquear/{id}")
    public String desbloquear(@PathVariable(value="id")Long userId){
        comprobarUsuarioLogeadoAdmin();
        UsuarioData usuario = usuarioService.findById(userId);
        usuarioService.desbloquear(userId);

        return "redirect:/registrados"; // Redirige de vuelta al listado de usuarios
    }









}
