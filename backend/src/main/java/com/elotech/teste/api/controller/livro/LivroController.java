package com.elotech.teste.api.controller.livro;

import com.elotech.teste.api.dto.livro.LivroDTO;
import com.elotech.teste.service.livro.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroService livroService;

    @PostMapping
    public ResponseEntity<LivroDTO> save(@Valid @RequestBody LivroDTO dto) {
        return ResponseEntity.ok(livroService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        livroService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<LivroDTO> update(@Valid @RequestBody LivroDTO dto) {
        return ResponseEntity.ok(livroService.update(dto));
    }

    @GetMapping
    public ResponseEntity<List<LivroDTO>> list() {
        return ResponseEntity.ok(livroService.listAll());
    }

    @GetMapping("/{titulo}")
    public ResponseEntity<List<LivroDTO>> find(@PathVariable String titulo) {
        return ResponseEntity.ok(livroService.listByTitulo(titulo));
    }

    @GetMapping("/recomendar/{idUsuario}")
    public ResponseEntity<List<LivroDTO>> recomendar(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(livroService.recomendarLivrosPorUsuario(idUsuario));
    }

    @GetMapping("/google/{consulta}/{pagina}")
    public ResponseEntity<List<LivroDTO>> listGoogle(@PathVariable String consulta, @PathVariable int pagina) {
        return ResponseEntity.ok(livroService.listarLivrosDoGoogle(consulta, pagina));
    }
}
