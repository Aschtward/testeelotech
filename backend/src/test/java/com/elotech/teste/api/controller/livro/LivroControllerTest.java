package com.elotech.teste.api.controller.livro;

import com.elotech.teste.IntegrationTest;
import com.elotech.teste.api.dto.livro.LivroDTO;
import com.elotech.teste.domain.valueobject.VoData;
import com.elotech.teste.infra.client.GoogleBooksRestClient;
import com.elotech.teste.repository.livro.LivroRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LivroControllerTest extends IntegrationTest {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GoogleBooksRestClient client;

    private static final String PATH = "/livros";
    private static final Long ID_LIVRO_SEM_EMPRESTIMO = 11L;

    private static final Long ID_USUARIO_COM_RECOMENDACOES = 1L;
    private static final Long ID_USUARIO_SEM_RECOMENDACOES = 7L;

    @Test
    @SneakyThrows
    void save() {
        long quantidadeAntes = livroRepository.count();
        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(LivroDataProvider.getLivro())))
                .andExpect(status().isOk());

        long quantidadeUltimoSalvamento = livroRepository.count();
        assertThat(quantidadeAntes).isLessThan(quantidadeUltimoSalvamento);

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(LivroDataProvider.getLivroSemCampos())))
                .andExpect(status().isBadRequest());

        assertThat(quantidadeUltimoSalvamento).isEqualTo(livroRepository.count());
    }

    @Test
    @SneakyThrows
    void deleteTeste() {
        long quantidadeAntes = livroRepository.count();

        mockMvc.perform(delete(PATH + "/{id}", ID_LIVRO_SEM_EMPRESTIMO))
                .andExpect(status().isNoContent());

        long quantidadeAposRemover = livroRepository.count();
        assertThat(quantidadeAntes).isGreaterThan(quantidadeAposRemover);
    }

    @Test
    @SneakyThrows
    void update() {
        LivroDTO livroDTO = new LivroDTO(
                ID_LIVRO_SEM_EMPRESTIMO,
                "Clean code",
                "Robert Cecil Martin",
                "123114123",
                VoData.hoje().getValor(),
                List.of("Programação", "Computação")
        );

        String response = mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(livroDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LivroDTO saved = objectMapper.readValue(response, LivroDTO.class);
        assertThat(saved.id()).isNotNull();
        assertThat(saved.categoria().size()).isGreaterThan(1);
        assertThat(saved.titulo()).isEqualTo("Clean code");
    }

    @Test
    @SneakyThrows
    void list() {
        String respostaJson = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<LivroDTO> resposta = objectMapper.readValue(respostaJson, new TypeReference<List<LivroDTO>>() {
        });
        assertThat(resposta.size()).isEqualTo(livroRepository.count());
    }

    @Test
    @SneakyThrows
    void find() {
        String respostaJson = mockMvc.perform(get(PATH + "/{query}", "clean"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<LivroDTO> resposta = objectMapper.readValue(respostaJson, new TypeReference<List<LivroDTO>>() {
        });
        assertThat(resposta.size()).isGreaterThan(0);

        respostaJson = mockMvc.perform(get(PATH + "/{query}", "umvalorquenaoexiste"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        resposta = objectMapper.readValue(respostaJson, new TypeReference<List<LivroDTO>>() {
        });
        assertThat(resposta.size()).isEqualTo(0);
    }

    @Test
    @SneakyThrows
    void recomendar() {
        String respostaJson = mockMvc.perform(get(PATH + "/recomendar/{id}", ID_USUARIO_COM_RECOMENDACOES))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<LivroDTO> resposta = objectMapper.readValue(respostaJson, new TypeReference<List<LivroDTO>>() {
        });
        assertThat(resposta.isEmpty()).isFalse();

        respostaJson = mockMvc.perform(get(PATH + "/recomendar/{id}", ID_USUARIO_SEM_RECOMENDACOES))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        resposta = objectMapper.readValue(respostaJson, new TypeReference<List<LivroDTO>>() {
        });
        assertThat(resposta.isEmpty()).isTrue();
    }
}