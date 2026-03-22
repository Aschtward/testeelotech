package com.elotech.teste.repository.emprestimo;

import com.elotech.teste.domain.entity.emprestimo.Emprestimo;
import com.elotech.teste.domain.entity.emprestimo.EmprestimoStatus;
import com.elotech.teste.domain.entity.livro.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    boolean existsByLivroAndStatusIn(Livro livro, List<EmprestimoStatus> status);

    @Query("""
                SELECT e
                FROM Emprestimo e
                JOIN e.usuario u
                JOIN e.livro l
            """)
    List<Emprestimo> listarEmprestimos();

    @Query("""
                SELECT e
                FROM Emprestimo e
                JOIN e.usuario u
                JOIN e.livro l
                WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
            """)
    List<Emprestimo> findEmprestimoByUsuarioNome(@Param("nome") String nome);

}
