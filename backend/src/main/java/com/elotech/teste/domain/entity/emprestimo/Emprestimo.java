package com.elotech.teste.domain.entity.emprestimo;

import com.elotech.teste.domain.entity.abstractentity.AbstractEntity;
import com.elotech.teste.domain.entity.livro.Livro;
import com.elotech.teste.domain.entity.usuario.Usuario;
import com.elotech.teste.domain.valueobject.VoData;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Emprestimo extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @Column(nullable = false)
    private EmprestimoStatus status;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "data_emprestimo", nullable = false))
    private VoData dataEmprestimo;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "data_vencimento"))
    private VoData dataVencimento;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "data_devolucao"))
    private VoData dataDevolucao;

    public static Emprestimo of(Usuario usuario,
                                Livro livro,
                                Long periodoVencimento,
                                ZonedDateTime dataEmprestimo,
                                ZonedDateTime dataDevolucao,
                                ZonedDateTime dataVencimento) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setValues(usuario, livro, periodoVencimento, dataEmprestimo, dataDevolucao, dataVencimento);
        return emprestimo;
    }

    public void setValues(Usuario usuario,
                          Livro livro,
                          Long periodoVencimento,
                          ZonedDateTime dataEmprestimo,
                          ZonedDateTime dataDevolucao,
                          ZonedDateTime dataVencimento) {
        System.out.println(dataVencimento);
        this.usuario = usuario;
        this.livro = livro;
        this.dataEmprestimo = new VoData(dataEmprestimo);
        this.dataDevolucao = dataDevolucao != null ? new VoData(dataDevolucao) : null;
        if (dataVencimento != null) {
            this.dataVencimento = new VoData(dataVencimento);
        } else {
            this.dataVencimento = this.dataVencimento != null ? this.dataVencimento :
                    VoData.maisDias(this.dataEmprestimo, periodoVencimento);
        }
        definirStatus();
    }

    public void atualizarStatus() {
        this.dataDevolucao = VoData.hoje();
        definirStatus();
    }

    private void definirStatus() {
        if (this.dataDevolucao != null) {
            if (this.dataDevolucao.isDepoisDe(this.dataVencimento)) {
                this.status = EmprestimoStatus.FECHADO_ATRASADO;
                return;
            }
            this.status = EmprestimoStatus.FECHADO_EM_DIA;
            return;
        }
        if (this.dataVencimento.isDepoisDe(VoData.hoje())) {
            this.status = EmprestimoStatus.ABERTO_EM_DIA;
            return;
        }
        this.status = EmprestimoStatus.ABERTO_ATRASADO;
    }

    public Long getLivroId() {
        return this.livro.getId();
    }

    public ZonedDateTime getDataEmprestimoValor() {
        if (dataEmprestimo != null) return dataEmprestimo.getValor();
        return null;
    }

    public ZonedDateTime getDataDevolucaoValor() {
        if (dataDevolucao != null) return dataDevolucao.getValor();
        return null;
    }

    public ZonedDateTime getDataVencimentoValor() {
        if (dataVencimento != null) return dataVencimento.getValor();
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Emprestimo that = (Emprestimo) o;
        return Objects.equals(usuario, that.usuario) && Objects.equals(livro, that.livro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, livro);
    }
}
