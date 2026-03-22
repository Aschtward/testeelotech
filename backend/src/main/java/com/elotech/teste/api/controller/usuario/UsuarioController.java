package com.elotech.teste.api.controller.usuario;

import com.elotech.teste.api.dto.usuario.UsuarioDTO;
import com.elotech.teste.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioDTO> save(@Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<UsuarioDTO> update(@Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.update(dto));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> list() {
        return ResponseEntity.ok(usuarioService.listAll());
    }

    @GetMapping("/{nome}")
    public ResponseEntity<List<UsuarioDTO>> find(@PathVariable String nome) {
        return ResponseEntity.ok(usuarioService.listByNome(nome));
    }
}
