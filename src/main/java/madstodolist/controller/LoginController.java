package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.LoginData;
import madstodolist.dto.RegistroData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginData", new LoginData());
        return "formLogin";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginData loginData, Model model, HttpSession session) {
        UsuarioData usuario = usuarioService.findByEmail(loginData.geteMail());

        if (usuario != null && usuario.getBloqueado() != null && usuario.getBloqueado()) {
            // El usuario está bloqueado, muestra un mensaje de error
            model.addAttribute("error", "El acceso está bloqueado para este usuario. Por favor, contacta al administrador.");
            return "formLogin";
        }

        UsuarioService.LoginStatus loginStatus = usuarioService.login(loginData.geteMail(), loginData.getPassword());


        if (loginStatus == UsuarioService.LoginStatus.LOGIN_OK) {

            managerUserSession.logearUsuario(usuario.getId());

            if (usuario.getIsAdmin() != null && usuario.getIsAdmin()) {
                return "redirect:/registrados";
            } else {
                return "redirect:/usuarios/" + usuario.getId() + "/tareas";
            }
        } else if (loginStatus == UsuarioService.LoginStatus.USER_NOT_FOUND) {
            model.addAttribute("error", "No existe usuario");
            return "formLogin";
        } else if (loginStatus == UsuarioService.LoginStatus.ERROR_PASSWORD) {
            model.addAttribute("error", "Contraseña incorrecta");
            return "formLogin";
        }
        return "formLogin";
    }




    @GetMapping("/registro")
    public String showRegistrationForm(Model model) {
        if (!usuarioService.existsAdmin()) {
            model.addAttribute("existeadmin", "noexisteadmin");
        }
        model.addAttribute("registroData", new RegistroData());
        return "formRegistro";
    }


    @PostMapping("/registro")
   public String registroSubmit(@Valid RegistroData registroData, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "formRegistro";
        }


        if (usuarioService.findByEmail(registroData.getEmail()) != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.getEmail() + " ya existe");
            return "formRegistro";
        }

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail(registroData.getEmail());
        usuario.setPassword(registroData.getPassword());
        usuario.setFechaNacimiento(registroData.getFechaNacimiento());
        usuario.setNombre(registroData.getNombre());
        usuario.setIsadmin(registroData.getIsAdmin());

        usuarioService.registrar(usuario);
        return "redirect:/login";
   }

   @GetMapping("/logout")
   public String logout(HttpSession session) {
        managerUserSession.logout();
        return "redirect:/login";
   }
}
