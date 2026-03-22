package com.elotech.teste.service.emprestimo;

import com.elotech.teste.api.dto.emprestimo.EmprestimoDTO;
import com.elotech.teste.domain.entity.emprestimo.Emprestimo;
import com.elotech.teste.domain.entity.emprestimo.EmprestimoStatus;
import com.elotech.teste.domain.entity.livro.Livro;
import com.elotech.teste.domain.entity.usuario.Usuario;
import com.elotech.teste.repository.emprestimo.EmprestimoRepository;
import com.elotech.teste.service.livro.LivroService;
import com.elotech.teste.service.usuario.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroService livroService;
    private final UsuarioService usuarioService;

    public EmprestimoDTO save(EmprestimoDTO payloadDTO) {
        if (livroPossuiEmprestimoAtivo(payloadDTO.getLivroId()))
            throw new IllegalArgumentException("Livro selecionado já esta relacionado ao um emprestimo");

        Livro livro = livroService.loadLivroById(payloadDTO.getLivroId());
        Usuario usuario = usuarioService.loadUsuarioById(payloadDTO.getUsuarioId());
        Emprestimo emprestimo = Emprestimo.of(usuario, livro, payloadDTO.periodoVencimento(), payloadDTO.dataEmprestimo(),
                payloadDTO.dataDevolucao(), payloadDTO.dataVencimento());

        return EmprestimoDTO.of(usuario, livro, emprestimoRepository.save(emprestimo));
    }

    public EmprestimoDTO update(EmprestimoDTO payloadDTO) {
        Emprestimo emprestimo = loadEmprestimoById(payloadDTO.id());

        if (emprestimo == null) throw new IllegalArgumentException("Emprestimo não encontrado");

        Usuario usuario = usuarioService.loadUsuarioById(payloadDTO.getUsuarioId());
        Long livroIdPayload = payloadDTO.getLivroId();

        boolean naoAlterouLivro = emprestimo.getLivroId().equals(livroIdPayload);

        if (naoAlterouLivro || !livroPossuiEmprestimoAtivo(livroIdPayload)) {
            Livro livro = naoAlterouLivro ? emprestimo.getLivro() : livroService.loadLivroById(livroIdPayload);
            emprestimo.setValues(usuario, livro, payloadDTO.periodoVencimento(), payloadDTO.dataEmprestimo(),
                    payloadDTO.dataDevolucao(), payloadDTO.dataVencimento());
            return EmprestimoDTO.of(usuario, livro, emprestimoRepository.save(emprestimo));
        }

        throw new IllegalArgumentException("Livro selecionado já esta relacionado a um emprestimo");
    }

    public void delete(Long id) {
        Emprestimo emprestimo = loadEmprestimoById(id);
        if (emprestimo == null) throw new IllegalArgumentException("Emprestimo não encontrado");
        emprestimoRepository.delete(emprestimo);
    }

    public void atualizarStatus(List<Long> ids) {
        List<Emprestimo> emprestimos = emprestimoRepository.findAllById(ids);
        emprestimos.forEach(Emprestimo::atualizarStatus);
        emprestimoRepository.saveAll(emprestimos);
    }

    public List<EmprestimoDTO> listAll() {
        return emprestimoRepository.listarEmprestimos().stream().map(EmprestimoDTO::of).toList();
    }

    public List<EmprestimoDTO> listByNomeUsuario(String nomeUsuario) {
        return emprestimoRepository.findEmprestimoByUsuarioNome(nomeUsuario).stream().map(EmprestimoDTO::of).toList();
    }

    public Emprestimo loadEmprestimoById(Long id) {
        Optional<Emprestimo> emprestimo = emprestimoRepository.findById(id);
        return emprestimo.orElse(null);
    }

    private boolean livroPossuiEmprestimoAtivo(Long livroId) {
        Livro livro = livroService.loadLivroById(livroId);
        return emprestimoRepository.existsByLivroAndStatusIn(livro, List.of(EmprestimoStatus.ABERTO_ATRASADO, EmprestimoStatus.ABERTO_EM_DIA));
    }

}
