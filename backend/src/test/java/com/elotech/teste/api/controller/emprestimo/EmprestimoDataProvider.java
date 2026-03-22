package com.elotech.teste.api.controller.emprestimo;

import com.elotech.teste.api.dto.emprestimo.EmprestimoDTO;
import com.elotech.teste.api.dto.livro.LivroDTO;
import com.elotech.teste.api.dto.usuario.UsuarioDTO;
import com.elotech.teste.domain.entity.emprestimo.EmprestimoStatus;

import java.time.ZonedDateTime;
import java.util.List;

public class EmprestimoDataProvider {

    private static final Long ID_USUARIO = 1L;
    private static final Long ID_LIVRO_SEM_EMPRESTIMO = 11L;

    private static final UsuarioDTO USUARIO = new UsuarioDTO(
            ID_USUARIO, "Leonardo Goulart", "leo091945@gmail.com",
            ZonedDateTime.now(), "+5544991563680"
    );

    private static final LivroDTO LIVRO_SEM_EMPRESTIMO = new LivroDTO(
            ID_LIVRO_SEM_EMPRESTIMO, "A tormenta de espadas", "George R. R. Martin",
            "9788554514037", ZonedDateTime.parse("2019-04-23T00:00:00Z"), List.of("Fiction")
    );

    private static final LivroDTO LIVRO_COM_EMPRESTIMO = new LivroDTO(
            2L, "Clean Code", "Robert C. Martin",
            "9780132350884", ZonedDateTime.parse("2009-01-01T00:00:00Z"), List.of("Computers")
    );

    public static EmprestimoDTO getEmprestimoAbertoEmDia() {
        return new EmprestimoDTO(
                null, USUARIO.nome(), LIVRO_SEM_EMPRESTIMO.titulo(),
                ZonedDateTime.now(), ZonedDateTime.now().plusDays(30),
                null, null,
                EmprestimoStatus.ABERTO_EM_DIA.getDescricao(),
                LIVRO_SEM_EMPRESTIMO, USUARIO
        );
    }

    public static EmprestimoDTO getEmprestimoAbertoAtrasado() {
        return new EmprestimoDTO(
                null, USUARIO.nome(), LIVRO_SEM_EMPRESTIMO.titulo(),
                ZonedDateTime.now().minusDays(10), ZonedDateTime.now().minusDays(1),
                null, null,
                EmprestimoStatus.ABERTO_ATRASADO.getDescricao(),
                LIVRO_SEM_EMPRESTIMO, USUARIO
        );
    }

    public static EmprestimoDTO getEmprestimoFechadoEmDia() {
        return new EmprestimoDTO(
                null, USUARIO.nome(), LIVRO_SEM_EMPRESTIMO.titulo(),
                ZonedDateTime.now().minusDays(10), ZonedDateTime.now().plusDays(5),
                ZonedDateTime.now(), null,
                EmprestimoStatus.FECHADO_EM_DIA.getDescricao(),
                LIVRO_SEM_EMPRESTIMO, USUARIO
        );
    }

    public static EmprestimoDTO getEmprestimoFechadoAtrasado() {
        return new EmprestimoDTO(
                null, USUARIO.nome(), LIVRO_SEM_EMPRESTIMO.titulo(),
                ZonedDateTime.now().minusDays(20), ZonedDateTime.now().minusDays(5),
                ZonedDateTime.now(), null,
                EmprestimoStatus.FECHADO_ATRASADO.getDescricao(),
                LIVRO_SEM_EMPRESTIMO, USUARIO
        );
    }

    public static EmprestimoDTO getEmprestimoComPeriodoVencimento() {
        return new EmprestimoDTO(
                null, USUARIO.nome(), LIVRO_SEM_EMPRESTIMO.titulo(),
                ZonedDateTime.now(), null,
                null, 20L,
                EmprestimoStatus.ABERTO_EM_DIA.getDescricao(),
                LIVRO_SEM_EMPRESTIMO, USUARIO
        );
    }

    public static EmprestimoDTO getEmprestimoSemCamposObrigatorios() {
        return new EmprestimoDTO(
                null, null, null,
                null, null,
                null, null,
                null, null, null
        );
    }

    public static EmprestimoDTO getEmprestimoComLivroJaEmprestado() {
        return new EmprestimoDTO(
                null, USUARIO.nome(), LIVRO_COM_EMPRESTIMO.titulo(),
                ZonedDateTime.now(), ZonedDateTime.now().plusDays(30),
                null, null,
                EmprestimoStatus.ABERTO_EM_DIA.getDescricao(),
                LIVRO_COM_EMPRESTIMO, USUARIO
        );
    }
}
