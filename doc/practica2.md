# Documentacion Practica 2
### Problema con git 
He tenido un problema y es que cuando tenia todos los puntos obligatorios hechos, de alguna manera no tenia bien subido a la rama de git 
main el codigo, entonces al hacer el git pull me baje al repositorio local todo sin actualizar. He tenido que crear una rama para añadir 
todo esto y corregir los errores. Seguramente esten mal algunos commits pero lo tenia bien. Tuve que borrar la rama y volverla a crear 
y no se si todo eso esta reflejado. Tenia los puntos opcionales y sus test que me funcionaban perfectamente. En la rama admin introduzco 
tanto como el checkbox para ser usuario administrador como estos arreglos que he tenido que hacer. Lo siento por el desorden, pero lo 
tenia bien hasta que hice el pull.
## Listado de nuevas clases y metodos implementados: 
## Navbar
- Para la navbar, la he creado en el archivo fragments.html para posteriormente poder usarla
desde cualquier pagina. Para ello solo necesitamos usar el codigo: div th:replace="fragments::navbar"/. En esta barra se mostrara 
los enlaces ToDoList(pagina /about) Tareas(lista de tareas del usuario) y a la derecha el nombre del usuario logueado (en caso de estarlo) o los enlaces a las paginas de login y registro. 
Cuando el usuario esta logueado se muestra un desplegable con el enlace a la pagina cuenta y la opion de cerrar sesion. Para pasarle al navbar si el usuario esta logueado o no he implementado 
la siguiente logica:
  
        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();
        if(idUsuarioLogueado != null){
            model.addAttribute("barramenu","menulogeado");

        }else{
            model.addAttribute("barramenu","menunologeado");
        }
    Este trozo de codigo representa la forma de comprobar si el usuario esta logueado para poder mostrar en la barra su nombre. Obviamente mediante el model pasaremos el usuario para poder obtener el nombre:
        
        Usuario usuario = usuarioRepository.findById(idUsuarioLogueado).orElse(null);
        model.addAttribute("usuario",usuario);
- En cuanto al test de la navbar(navbarTest()) comprueba que tenga los valores ToDoList y Tareas en la barra.
## Pagina Acerca de y cuenta
- Para las paginas Acerca de y Cuenta (que usaremos en futuras practicas) he implementado plantillas thymeleaf llamadas about.html y cuenta.html. Creo que no es muy necesario comentar como he diseñado dichas paginas
puesto que son bastante simples y no tienen ninguna funcion mas que la navbar.
## Lista y detalles de los Usuarios 
  Para la lista de usuarios he creado una plantilla thymeleaf llamada usuariosregistrados.html. En esta pagina se mostrara una tabla con todos los usuarios registrados en la aplicacion. Esta pagina solo podra ser vista por
aquellos usuarios que sean administradores. Para ello he creado un checkbox en la pagina registro que por defecto esta desactivado. Si el usuario lo activa se le asignara el rol de administrador. Esta funcion la he comprobado y testeado
 mediante los test: estAccesoComoAdministrador
  - estAccesoComoAdministrador(). Que es un test para comprobar que cuando inicio sesion como admin me redirige a la pagina donde esta la lista de usuarios
  - testRegistroAdministradorExistente(). Que es un test para comprobar que cuando ya existe un adminitrador no aparece el checkbox en el formulario de registro

  En la pagina del listado de usuarios, encontraremos dos opciones(botones): 
   - El boton de descripcion nos lleva a la ruta /regisrtados/{id} donde se mostrara la informacion del usuario seleccionado. Para implementar esta funcion he creado un metodo GET en el
  controlador ListaUsuarioController que recibe el id del usuario seleccionado y lo busca en la base de datos. Si lo encuentra lo mostrara en la pagina con la plantilla descripcionsuario.html. Esto lo he testeado mediante el test: detallesUsuario() en la clase UsuariosRegistradosWebTest.
  
   - En cuanto a la otra opcion, consiste en un boton que puede bloquear o desbloquear usuario. Solo se podra en caso de ser usuario administrador. Para explicar como he implemntado esta funcionalidad primero hay que observar el siguiente codigo:
           
          <td><a class="btn btn-primary btn-xs" th:href="@{/registrados/{id}(id=${usuario.id})}"/>Descripción</a>

              <a class="btn btn-danger btn-xs" th:href="@{/bloquear/{id}(id=${usuario.id})}"
                  th:if="${usuario.bloqueado == false || usuario.bloqueado == null}" >Bloquear</a>

              <a class="btn btn-link btn-success" th:href="@{/desbloquear/{id}(id=${usuario.id})}"
                  th:if="${usuario.bloqueado == true }">Desbloquear</a>
          </td>
     De esta forma quiero destacar que mediante th:href navegamos a la ruta indicada. Esta ruta esta cogida por el controlador ListaUsuarioController que mediante un Get recibe el parametro del id del usuario a bloquear/desbloquear y ejecuta su funcion. Cabe destacar que en este metodo he usado los metodos bloquear y desbloquear que encontraremos en la capa de servicio. 

     Por ultima añadir que estos usuarios bloqueados, no son capaces de entrar a la pagina. En el login a traves del LoginController mediante el siguiente codigo:
            
     -      if (usuario != null && usuario.getBloqueado() != null && usuario.getBloqueado()) {
                  model.addAttribute("error", "El acceso está bloqueado para este usuario. Por favor, contacta al administrador.");
                  return "formLogin";
              }
   
     Dicha funcionalidad la comprobamos con el testNoAccesorUsuarioBloqueado() que comprueba que un usuario bloqueado no puede acceder a la pagina y muestra un mensaje de error.
# CONCLUSION
## Listado de nuevas clases y métodos implementados.
- Clase UnauthorizedException
- Clase CuentaController:
  - metodo cuenta() 
- Clase HomeController
  - metodo acercaDe() 
- Clase ListaUsuarioController
  - listadoUsuarios()
  - detallesUsuarios()
  - comprobarUsuarioLogeadoAdmin()
  - bloquear()
  - desbloquear()

- Clases UsuarioData,Usuario y RegistroData añadidos los atributos isAdmin y bloqueado para las funcionalidades pedidas con sus getter y setters
- Clase UsuarioRepository añadido el metodo existsByIsAdmin() para comprobar si existe un usuario administrador
- Clase UsuarioService añadido el metodo bloquear() y desbloquear() para bloquear y desbloquear usuarios y existsAdmin() que llama a existsByIsAdmin() de UsuarioRepository
## Listado de plantillas thyemeleaf añadidas.
- about.html
- cuenta.html
- descripcionsuario.html
- usuariosregistrados.html



    