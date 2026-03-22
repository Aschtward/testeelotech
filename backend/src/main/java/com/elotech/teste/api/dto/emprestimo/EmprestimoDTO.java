package com.elotech.teste.api.dto.emprestimo;

import com.elotech.teste.api.dto.livro.LivroDTO;
import com.elotech.teste.api.dto.usuario.UsuarioDTO;
import com.elotech.teste.domain.entity.emprestimo.Emprestimo;
import com.elotech.teste.domain.entity.emprestimo.EmprestimoStatus;
import com.elotech.teste.domain.entity.livro.Livro;
import com.elotech.teste.domain.entity.usuario.Usuario;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.ZonedDateTime;

public record EmprestimoDTO(
        Long id,
        String nomeUsuario,
        String tituloLivro,
        ZonedDateTime dataEmprestimo,
        ZonedDateTime dataVencimento,
        ZonedDateTime dataDevolucao,
        Long periodoVencimento,
        String status,
        LivroDTO livro,
        UsuarioDTO usuario
) {

    public static EmprestimoDTO of(Usuario usuario, Livro livro, Emprestimo emprestimo) {
        return new EmprestimoDTO(
                emprestimo.getId(),
                usuario.getNome(),
                livro.getTitulo(),
                emprestimo.getDataEmprestimoValor(),
                emprestimo.getDataVencimentoValor(),
                emprestimo.getDataDevolucaoValor(),
                null,
                emprestimo.getStatus().getDescricao(),
                new LivroDTO(livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getIsbn(),
                        livro.getDataPublicacaoValor(), livro.getCategorias()),
                new UsuarioDTO(usuario.getId(), usuario.getNome(), usuario.getEmailValor(),
                        usuario.getDataCadastroValor(), usuario.getTelefoneValor())
        );
    }

    public static EmprestimoDTO of(Emprestimo emprestimo) {
        Livro livro = emprestimo.getLivro();
        Usuario usuario = emprestimo.getUsuario();

        return new EmprestimoDTO(
                emprestimo.getId(),
                emprestimo.getUsuario().getNome(),
                emprestimo.getLivro().getTitulo(),
                emprestimo.getDataEmprestimoValor(),
                emprestimo.getDataVencimentoValor(),
                emprestimo.getDataDevolucaoValor(),
                null,
                emprestimo.getStatus().getDescricao(),
                new LivroDTO(livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getIsbn(),
                        livro.getDataPublicacaoValor(), livro.getCategorias()),
                new UsuarioDTO(usuario.getId(), usuario.getNome(), usuario.getEmailValor(),
                        usuario.getDataCadastroValor(), usuario.getTelefoneValor())
        );
    }

    @JsonIgnore
    public long getLivroId() {
        if(this.livro() == null) throw new IllegalArgumentException("Não é possível criar empréstimo sem informar o livro");
        return this.livro().id();
    }

    @JsonIgnore
    public long getUsuarioId() {
        return this.usuario.id();
    }
}
