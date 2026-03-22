package com.elotech.teste.domain.entity.livro;

import com.elotech.teste.domain.entity.abstractentity.AbstractEntity;
import com.elotech.teste.domain.valueobject.VoData;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Livro extends AbstractEntity implements Comparable<Livro> {

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    @Column(nullable = false)
    private String isbn;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "data_publicacao", nullable = false))
    private VoData dataPublicacao;

    @ElementCollection
    private List<String> categorias;

    public static Livro of(String titulo,
                           String autor,
                           String isbn,
                           ZonedDateTime dataPublicacao,
                           List<String> categoria) {
        Livro livro = new Livro();
        livro.setValues(titulo, autor, isbn, dataPublicacao, categoria);
        return livro;
    }

    public void setValues(String titulo,
                          String autor,
                          String isbn,
                          ZonedDateTime dataPublicacao,
                          List<String> categoria) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.dataPublicacao = new VoData(dataPublicacao);
        this.categorias = categoria;
    }

    public void initialize() {
        Hibernate.initialize(categorias);
    }

    public ZonedDateTime getDataPublicacaoValor() {
        return this.getDataPublicacao().getValor();
    }

    @Override
    public int compareTo(Livro o) {
        return Comparator
                .comparing(Livro::getTitulo)
                .thenComparing(Livro::getAutor)
                .compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Livro livro = (Livro) o;
        return Objects.equals(titulo, livro.titulo) &&
                Objects.equals(autor, livro.autor) &&
                Objects.equals(isbn, livro.isbn) &&
                Objects.equals(dataPublicacao, livro.dataPublicacao) &&
                Objects.equals(categorias, livro.categorias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, autor, isbn, dataPublicacao, categorias);
    }
}
