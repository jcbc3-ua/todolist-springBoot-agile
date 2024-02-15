package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.LoginData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

@Controller
public class CuentaController {
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/cuenta")
    public String cuenta(@ModelAttribute LoginData loginData, Model model, HttpSession session) {
        // Llamada al servicio para comprobar si el login es correcto
        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();

        if(idUsuarioLogueado != null){
            model.addAttribute("barramenu","menulogeado");
            UsuarioData usuario = usuarioService.findById(idUsuarioLogueado);
            model.addAttribute("usuario", usuario);
        }else{
            model.addAttribute("barramenu","menunologeado");
        }
        return "cuenta";
    }
}
