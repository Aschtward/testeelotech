package com.elotech.teste.api.controller.emprestimo;

import com.elotech.teste.api.dto.emprestimo.EmprestimoDTO;
import com.elotech.teste.service.emprestimo.EmprestimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<EmprestimoDTO> save(@Valid @RequestBody EmprestimoDTO dto) {
        return ResponseEntity.ok(emprestimoService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        emprestimoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<EmprestimoDTO> update(@Valid @RequestBody EmprestimoDTO dto) {
        return ResponseEntity.ok(emprestimoService.update(dto));
    }

    @GetMapping
    public ResponseEntity<List<EmprestimoDTO>> list() {
        return ResponseEntity.ok(emprestimoService.listAll());
    }

    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<List<EmprestimoDTO>> find(@PathVariable String nomeUsuario) {
        return ResponseEntity.ok(emprestimoService.listByNomeUsuario(nomeUsuario));
    }

    @PatchMapping
    public ResponseEntity<Void> atualizarStatus(@RequestBody List<Long> ids) {
        emprestimoService.atualizarStatus(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
