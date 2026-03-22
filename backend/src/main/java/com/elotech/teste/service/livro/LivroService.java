package com.elotech.teste.service.livro;

import com.elotech.teste.api.dto.livro.GoogleBooksItensDTO;
import com.elotech.teste.api.dto.livro.LivroDTO;
import com.elotech.teste.domain.entity.livro.Livro;
import com.elotech.teste.repository.livro.LivroRepository;
import com.elotech.teste.service.googlebooks.GoogleBooksService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;
    private final GoogleBooksService googleBooksService;

    public LivroDTO save(LivroDTO dto) {
        Livro livro = Livro.of(dto.titulo(), dto.autor(), dto.isbn(), dto.dataPublicacao(), dto.categoria());
        livroRepository.save(livro);
        return LivroDTO.of(livro);
    }

    public LivroDTO update(LivroDTO dto) {
        Livro livro = loadLivroById(dto.id());
        livro.setValues(dto.titulo(), dto.autor(), dto.isbn(), dto.dataPublicacao(), dto.categoria());
        livroRepository.save(livro);
        return LivroDTO.of(livro);
    }

    public void delete(Long id) {
        Livro livro = loadLivroById(id);
        if (livro == null) throw new IllegalArgumentException("Livro não encontrado");
        livroRepository.delete(livro);
    }

    public List<LivroDTO> listAll() {
        return livroRepository.findAll().stream().map(LivroDTO::of).toList();
    }

    public List<LivroDTO> listByTitulo(String titulo) {
        return livroRepository.findLivroByTituloContainingIgnoreCase(titulo).stream().map(LivroDTO::of).toList();
    }

    public Livro loadLivroById(Long id) {
        Optional<Livro> livro = livroRepository.findById(id);
        return livro.orElse(null);
    }

    public List<LivroDTO> recomendarLivrosPorUsuario(Long idUsuario) {
        return livroRepository.listarRecomendados(idUsuario).stream().map(LivroDTO::of).toList();
    }

    public List<LivroDTO> listarLivrosDoGoogle(String consulta, int pagina) {
        return googleBooksService.buscarLivros(consulta, pagina).items()
                .stream()
                .map(GoogleBooksItensDTO::volumeInfo)
                .map(LivroDTO::of)
                .toList();
    }

}
