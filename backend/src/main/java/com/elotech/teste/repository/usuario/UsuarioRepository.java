package com.elotech.teste.repository.usuario;

import com.elotech.teste.domain.entity.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findUsuarioByNomeContainingIgnoreCase(String nome);
}
