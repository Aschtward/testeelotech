package com.elotech.teste.service.usuario;

import com.elotech.teste.api.dto.usuario.UsuarioDTO;
import com.elotech.teste.domain.entity.usuario.Usuario;
import com.elotech.teste.repository.usuario.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDTO save(UsuarioDTO dto) {
        Usuario usuario = Usuario.of(dto.nome(), dto.email(), dto.dataCadastro(), dto.telefone());
        usuarioRepository.save(usuario);
        return UsuarioDTO.of(usuario);
    }

    public UsuarioDTO update(UsuarioDTO dto) {
        Usuario usuario = loadUsuarioById(dto.id());
        usuario.setValues(dto.nome(), dto.email(), dto.dataCadastro(), dto.telefone());
        usuarioRepository.save(usuario);
        return UsuarioDTO.of(usuario);
    }

    public void delete(Long id) {
        Usuario usuario = loadUsuarioById(id);
        if (usuario == null) throw new IllegalArgumentException("Usuario não encontrado");
        usuarioRepository.delete(usuario);
    }

    public List<UsuarioDTO> listAll() {
        return usuarioRepository.findAll().stream().map(UsuarioDTO::of).toList();
    }

    public List<UsuarioDTO> listByNome(String nome) {
        return usuarioRepository.findUsuarioByNomeContainingIgnoreCase(nome).stream().map(UsuarioDTO::of).toList();
    }

    public Usuario loadUsuarioById(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.orElse(null);
    }
}
