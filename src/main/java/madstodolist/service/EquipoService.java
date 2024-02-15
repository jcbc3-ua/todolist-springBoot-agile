package madstodolist.service;

import madstodolist.dto.EquipoData;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.repository.EquipoRepository;
import madstodolist.repository.TareaRepository;
import madstodolist.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipoService {

    @Autowired
    EquipoRepository equipoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;



    @Transactional
    public EquipoData crearEquipo(String nombre) {
        Equipo equipo = new Equipo(nombre);
        if(equipo.getNombre().isEmpty()){
            throw new EquipoServiceException("El nombre del equipo no puede estar vacío");
        }
        equipoRepository.save(equipo);

        return modelMapper.map(equipo, EquipoData.class);
    }

    @Transactional(readOnly = true)
    public EquipoData recuperarEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if(equipo == null){
            throw new EquipoServiceException("El equipo no existe");
        }

        return modelMapper.map(equipo, EquipoData.class);
    }

    @Transactional
    public List<EquipoData> findAllOrdenadoPorNombre(){
        List<Equipo> equipos = equipoRepository.findAll();

        List<EquipoData> equiposOrdenados = equipos.stream()
                .map(equipo -> modelMapper.map(equipo, EquipoData.class))
                .sorted(Comparator.comparing(EquipoData::getNombre))
                .collect(Collectors.toList());

        return equiposOrdenados;
    }

    @Transactional
    public void añadirUsuarioAEquipo(Long idEquipo, Long idUsuario) {
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);

        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if(equipo == null || usuario == null){
            throw new EquipoServiceException("El equipo no existe");
        }else if(equipo.getUsuarios().contains(usuario)){
            throw new EquipoServiceException("El usuario ya pertenece al equipo");
        }


        equipo.addUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioData> usuariosEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if(equipo == null){
            throw new EquipoServiceException("El equipo no existe");
        }

        // Hacemos uso de Java Stream API para mapear la lista de entidades a DTOs.
        return equipo.getUsuarios().stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<EquipoData> equiposUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if(usuario == null){
            throw new EquipoServiceException("El usuario no existe");
        }
        // Hacemos uso de Java Stream API para mapear la lista de entidades a DTOs.
        return usuario.getEquipos().stream()
                .map(equipo -> modelMapper.map(equipo, EquipoData.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarUsuariodeEquipo(Long idEquipo,Long idUsuario){
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        equipo.getUsuarios().remove(usuario);
        usuario.getEquipos().remove(equipo);

    }

    @Transactional
    public Boolean perteneceUsuarioaEquipo(Long idUsuario, Long idEquipo) {
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);

        if (equipo != null && usuario != null) {
            return equipo.getUsuarios().contains(usuario);
        }

        return false;
    }

    @Transactional
    public void borrarEquipo(Long idEquipo) {
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        if(equipo == null){
            throw new EquipoServiceException("El equipo no existe");
        }
        equipoRepository.delete(equipo);
    }

    @Transactional
    public EquipoData modificarEquipo(Long idEquipo,String nombre){
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        if(equipo == null){
            throw new EquipoServiceException("El equipo no existe");
        }
        equipo.setNombre(nombre);
        equipo = equipoRepository.save(equipo);
        return modelMapper.map(equipo, EquipoData.class);
    }



}