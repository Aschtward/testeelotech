package com.elotech.teste.domain.entity.usuario;

import com.elotech.teste.domain.entity.abstractentity.AbstractEntity;
import com.elotech.teste.domain.valueobject.VoData;
import com.elotech.teste.domain.valueobject.VoEmail;
import com.elotech.teste.domain.valueobject.VoTelefone;
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
public class Usuario extends AbstractEntity {

    @Column(nullable = false)
    private String nome;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "email", nullable = false))
    private VoEmail email;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "data_cadastro", nullable = false))
    private VoData dataCadastro;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "telefone", nullable = false))
    private VoTelefone telefone;

    public static Usuario of(String nome,
                             String email,
                             ZonedDateTime dataCadastro,
                             String telefone) {
        Usuario usuario = new Usuario();
        usuario.setValues(nome, email, dataCadastro, telefone);
        return usuario;
    }

    public void setValues(String nome,
                          String email,
                          ZonedDateTime dataCadastro,
                          String telefone) {
        this.nome = nome;
        this.email = new VoEmail(email);
        this.dataCadastro = new VoData(dataCadastro);
        this.telefone = new VoTelefone(telefone);
    }

    public ZonedDateTime getDataCadastroValor() {
        return this.getDataCadastro().getValor();
    }

    public String getEmailValor() {
        return this.getEmail().getValor();
    }

    public String getTelefoneValor() {
        return this.getTelefone().getValor();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(nome, usuario.nome) && Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, email);
    }
}
