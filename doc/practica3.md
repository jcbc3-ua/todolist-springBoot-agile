# Documentacion Practica 3
## Base de datos
En primer lugar podemos observar como está creada la base de datos con la que hemos trabajado durante el desarrollo de la aplicacion. En la siguiente imagen vemos como esta creada con las tablas indicadas por el enunciado. En el apartado tables es donde encontramos la tabla tareas,usuarios,equipos y equipos_usuario que es la que contiene la relacion entre equipos y usuarios

La base de datos en el panel Database es la siguiente:

![Captura Base de datos con tablas](https://raw.githubusercontent.com/mads-ua-23-24/mads-todolist-jcbc3-ua/main/images/img.png?token=GHSAT0AAAAAACIUT6MCKELQCN2BS4I5PNBEZKQX3NQ)

## Rutas (endpoints) definidas para las acciones

Para esta práctica ha sido necesario especificar las siguientes rutas con las cuales he podido desarrollar las funcionalidades pedidas por el enunciado.
En primer lugar comentar que la ruta `/equipos/{id}` la he usado tanto para mostrar los usuarios que contiene un equipo como para borrar un equipo con DeleteMapping. 
1. Ruta `/equipos`: La ruta se encarga de mostrar todos los equipos que existen. 

   - **Clases y metodos**: El controlador dispone de un metodo get con la ruta indicada llamado **_listarEquipos_**. Este metodo se encarga de recuperar todos los equipos que existen mediante el metodo de servicio **_findAllOrdenadoPorNombre()_** que devuelve los equipos ordenados por orden alfabético. [^1]
   - **Plantillas thymeleaf**: Mediante el uso de model enviamos a la plantilla thymeleaf **_listadoequipos.html_** los equipos recuperados por el metodo mencionado en el apartado anterior para poder mostrarlos en nuestra pagina.
   - **Tests**: En cuanto a los test para esta ruta, encontramos el test **_listarEquipos_** que basicamente crea dos equipos y comprueba que en la ruta indicada aparezcan los nombres de los dos equipos. Ademas encontramos los tests **_testBotonAñadir_** y **_testBotonEliminar_** que comprueban que se muestre el boton de añadirme o eliminarme de un equipo si estoy o no en el

2. Ruta `/equipos/nuevo`: Esta ruta se encarga de crear un nuevo equipo. En la ruta _/equipos_ disponemos de un boton para crear un equipo. Este boton nos lleva a la ruta _/equipos/nuevo_ donde nos sale un campo para introducir el nombre del nuevo equipo que queremos crear.
   
   - **Clases y metodos**: El metodo get **_formNuevoEquipo_** se encarga de mostrar el formulario para la creacion de un nuevo equipo. Usa la plantilla **_formNuevoEquipo_** para pedir al usuario el nombre del equipo. Mediante el metodo post llamado **_nuevoEquipo_** obtenemos el nombre y con el metodo de la capa de servicios llamado crearEquipo() creamos el equipo definitivamente y nos redirigimos a la ruta `/equipos` para mostrar el ya nuevo equipo creado 
   - **Plantillas thymeleaf**: La plantilla **_formNuevoEquipo_** contiene el formulario para crear el nuevo equipo. Es un formulario muy sencillo en el cual a partir de un input obtenemos el nombre que queremos darle al nuevo equipo
   - **Tests**: Para comprobar esta funcionalidad he creado un test llamado **_testNuevoEquipoRedirectYAñadeEquipo_** el nombre es bastante explicito y se entiende que comprueba que se redirige a la ruta **_/equipos_** correctamente y que cuando añadimos un equipo con un nombre se añade correctamente tambien.
   
3. Ruta `/equipos/{id}` (usuarios del equipo): Esta ruta es la encargada de mostrar todos los usuarios que contiene un equipo. Mediante un boton llamado usuarios podemos acceder a ella.

   - **Clases y metodos**: En el controlador definimos el metodo **_listarUsuariosEquipo_** que recibe el id del equipo y metiante el metodo de la clase de servicio llamado **_usuariosEquipo()_** recuperar todos los usuarios que pertenecen al equipo y devuelve la plantilla usuariosequipos donde los muestra.
   - **Plantillas thymeleaf**: La plantilla **_usuariosequipos.html_** muestra todos los usuarios como ya he comentado. 
   - **Tests**: El test encargado de comprobar esta funcionalidad es **_usuariosDeEquipo_** que crea un equipo y dos usuarios y los añade y comprueba que en la ruta indicada se encuentran los nombres y emails de los usuarios que pertenecen al equipo

4. Ruta `/equipos/{id}/añadir`: Esta ruta sirve para que un usuario se añada a une equipo.

   - **Clases y metodos**: En el controlador encontramos el metodo **_añadiraEquipo_** que usa los metodos de la capa de servicio **_recuperarEquipo_** y **_añadirUsuarioAEquipo_** para añadir al usuario logueado al equipo en el cual ha pulsado el boton `añadirme`. 
   - **Plantillas thymeleaf**: Esta ruta redirige a la ruta **/equipos** donde se muestran todos los equipos
   - **Tests**: El test **_testAñadirUsuarioAEquipo_** se encarga de comprobar que se redirige cuando la url es la indicada y que el usuario logueado se ha añadido correctamente al equipo.

5. Ruta `/equipos/{id}/eliminar`: Esta ruta define la funcionalidad para un usuario de eliminarse de un equipo.

   - **Clases y metodos**: Esta funcionalidad es muy similar a la comentada en el caso anterior. El metodo del controlador llamado **_eliminardeEquipo_** usa los metodos de la capa de servicio **_recuperarEquipo_** para obtener el equipo y **_eliminarUsuariodeEquipo_** que elimina al usuario logueado definitivamente del equipo
   - **Plantillas thymeleaf**: Como en el caso anterior redirigimos la ruta a **/equipos** 
   - **Tests**: El test llamado **_testEliminarDeEquipo_** hace exactamente la misma comprobacion que en el caso anterior pero eliminando

6. Ruta `/equipos/{id}/modificar`:`: Esta ruta modifica el nombre de un equipo. Es importante tener en cuenta que tanto esta como la siguiente ruta solo debe mostrarse si el usuario es administrador.

   - **Clases y metodos**: En la capa del controlador tenemos el metodo get **_formNuevoEquipo_** que muestra el formulario encargado de pedir los datos para modificar el nombre del equipo. En este caso se comprueba que el usuario sea administrador. Si no lo es lanza una excepcion.El metodo post **_nuevoEquipo_** sera el encargado de cambiar el nombre mediante el metodo de la capa de servicios **_modificarEquipo_** que modifica el nombre del equipo. 
   - **Plantillas thymeleaf**: En este caso no usamos ninguna plantilla nueva si no que la ruta redirige a la ruta **/equipos** donde se mostrara el equipo con el nuevo nombre
   - **Tests**: El test **_testModificarEquipo_** crea un equipo y comprueba que cuando se accede a la ruta indicada se redirige a /equipos y cuando se le proporciona un nombre en esa url efectivamente se cambie el nombre del equipo.
  
7. Ruta `/equipos/{id} ` (borrar equipo): Por ultimo esta ruta se encarga de borrar un equipo. Obviamente esto solo lo podra realizar el usuari administrador.

   - **Clases y metodos**:  En el controlador se define el metodo delete llamado **_borrarEquipo_** que utiliza el metodo de la capa de servicios llamado **_borrarEquipo_** y asi realiza el borrado
   - **Plantillas thymeleaf**: En esta ruta (para borrar un equipo) no necesita plantilla por lo que la funcion devuelve ""
   - **Tests**: El test que realiza esta comprobacion se llama **_borrarEquipoDevuelveOKyBorraEquipo_** que comprueba que el estado es correcto cuando se indica la direccion url con la funcion delete y que se lanza la excepción **EquipoServiceException** cuando se llama al metodo para recuperar el equipo que ya no existe 

## Explicacion relevante de codigo

[^1]: En la ruta `/equipos` posteriormente he añadido el uso de un bucle en el cual dependiendo del equipo compruebo si el usuario logueado pertenece al equipo. Este metodo llamado **_perteneceUsuarioaEquipo()_** lo cree para mostar el boton de añadirme o eliminarme dependiendo de si el usuario estaba o no en el equipo. Me parece interesante contar esta funcionalidad y la manera en la que la he implementado. Se crea un map con el id del equipo y un Boolean y en la plantilla realiza la comprobacion para mostrar el boton de eliminarme o el de añadirme

Metodo **listaEquipo** en el controlador:
```
    Map<Long, Boolean> anadidoMap = new HashMap<>();
      for (EquipoData equipo : equipos) {
        Boolean anadido = equipoService.perteneceUsuarioaEquipo(idUsuarioLogueado,equipo.getId());
        anadidoMap.put(equipo.getId(), anadido);
      }
```

Plantilla **listadoequipos.html**:

```
    <a class="btn btn-danger text-white" th:if="${anadidoMap[equipo.id]}" th:href="@{/equipos/{id}/eliminar(id=${equipo.id})}">Eliminarme</a>
    <a class="btn btn-warning text-white" th:if="${usuario.getIsAdmin()}" th:href="@{/equipos/{id}/modificar(id=${equipo.id})}">Modificar equipo</a>
```