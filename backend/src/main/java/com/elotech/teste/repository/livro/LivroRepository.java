package com.elotech.teste.repository.livro;

import com.elotech.teste.domain.entity.livro.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    @Query("""
                SELECT DISTINCT livro
                FROM Livro livro
                JOIN livro.categorias c
            
                WHERE EXISTS (
                    SELECT 1
                    FROM Emprestimo e
                    JOIN e.livro l2
                    JOIN l2.categorias c2
                    WHERE e.usuario.id = :idUsuario
                    AND c2 = c
                )
                AND NOT EXISTS (
                    SELECT 1
                    FROM Emprestimo e
                    WHERE e.usuario.id = :idUsuario
                    AND e.livro.id = livro.id
                )
               AND NOT EXISTS (
                    SELECT 1
                    FROM Emprestimo e
                    WHERE e.status IN (com.elotech.teste.domain.entity.emprestimo.EmprestimoStatus.ABERTO_ATRASADO,
                                 com.elotech.teste.domain.entity.emprestimo.EmprestimoStatus.ABERTO_EM_DIA)
                    AND e.livro.id = livro.id
                )
            """)
    List<Livro> listarRecomendados(@Param("idUsuario") Long idUsuario);

    List<Livro> findLivroByTituloContainingIgnoreCase(String titulo);

}
