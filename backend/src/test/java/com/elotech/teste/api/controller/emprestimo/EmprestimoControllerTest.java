package com.elotech.teste.api.controller.emprestimo;

import com.elotech.teste.IntegrationTest;
import com.elotech.teste.api.dto.emprestimo.EmprestimoDTO;
import com.elotech.teste.repository.emprestimo.EmprestimoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmprestimoControllerTest extends IntegrationTest {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String PATH = "/emprestimos";
    private static final Long ID_EMPRESTIMO_ABERTO = 1L;

    @Test
    @SneakyThrows
    void save() {
        long quantidadeAntes = emprestimoRepository.count();

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(EmprestimoDataProvider.getEmprestimoAbertoEmDia())))
                .andExpect(status().isOk());

        long quantidadeAposSalvar = emprestimoRepository.count();
        assertThat(quantidadeAntes).isLessThan(quantidadeAposSalvar);
    }

    @Test
    @SneakyThrows
    void saveSemCamposObrigatorios() {
        long quantidadeAntes = emprestimoRepository.count();

        assertThrows(Exception.class, () ->
                mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(EmprestimoDataProvider.getEmprestimoSemCamposObrigatorios())))
        );

        assertThat(quantidadeAntes).isEqualTo(emprestimoRepository.count());
    }

    @Test
    @SneakyThrows
    void saveComLivroJaEmprestado() {
        assertThrows(Exception.class, () ->
                mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(EmprestimoDataProvider.getEmprestimoComLivroJaEmprestado())))
        );
    }

    @Test
    @SneakyThrows
    void saveAbertoEmDia() {
        String respostaJson = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(EmprestimoDataProvider.getEmprestimoAbertoEmDia())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EmprestimoDTO resposta = objectMapper.readValue(respostaJson, EmprestimoDTO.class);
        assertThat(resposta.status()).isEqualTo("Em aberto, dentro do prazo");
        assertThat(resposta.dataDevolucao()).isNull();
    }

    @Test
    @SneakyThrows
    void saveAbertoAtrasado() {
        String respostaJson = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(EmprestimoDataProvider.getEmprestimoAbertoAtrasado())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EmprestimoDTO resposta = objectMapper.readValue(respostaJson, EmprestimoDTO.class);
        assertThat(resposta.status()).isEqualTo("Em aberto, fora do prazo");
        assertThat(resposta.dataDevolucao()).isNull();
    }

    @Test
    @SneakyThrows
    void saveFechadoEmDia() {
        String respostaJson = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(EmprestimoDataProvider.getEmprestimoFechadoEmDia())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EmprestimoDTO resposta = objectMapper.readValue(respostaJson, EmprestimoDTO.class);
        assertThat(resposta.status()).isEqualTo("Fechado, dentro do prazo");
        assertThat(resposta.dataDevolucao()).isNotNull();
    }

    @Test
    @SneakyThrows
    void saveFechadoAtrasado() {
        String respostaJson = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(EmprestimoDataProvider.getEmprestimoFechadoAtrasado())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EmprestimoDTO resposta = objectMapper.readValue(respostaJson, EmprestimoDTO.class);
        assertThat(resposta.status()).isEqualTo("Fechado, fora do prazo");
        assertThat(resposta.dataDevolucao()).isNotNull();
    }

    @Test
    @SneakyThrows
    void saveComPeriodoVencimento() {
        String respostaJson = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(EmprestimoDataProvider.getEmprestimoComPeriodoVencimento())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EmprestimoDTO resposta = objectMapper.readValue(respostaJson, EmprestimoDTO.class);
        assertThat(resposta.dataVencimento()).isNotNull();
        assertThat(resposta.status()).isEqualTo("Em aberto, dentro do prazo");
    }

    @Test
    @SneakyThrows
    void deleteTeste() {
        long quantidadeAntes = emprestimoRepository.count();

        mockMvc.perform(delete(PATH + "/{id}", ID_EMPRESTIMO_ABERTO))
                .andExpect(status().isNoContent());

        long quantidadeAposRemover = emprestimoRepository.count();
        assertThat(quantidadeAntes).isGreaterThan(quantidadeAposRemover);
    }

    @Test
    @SneakyThrows
    void update() {
        EmprestimoDTO original = EmprestimoDataProvider.getEmprestimoFechadoEmDia();
        String savedJson = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(original)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EmprestimoDTO saved = objectMapper.readValue(savedJson, EmprestimoDTO.class);

        EmprestimoDTO updated = new EmprestimoDTO(
                saved.id(), saved.nomeUsuario(), saved.tituloLivro(),
                saved.dataEmprestimo(), saved.dataEmprestimo().plusDays(60),
                null, null,
                saved.status(), saved.livro(), saved.usuario()
        );

        String updatedJson = mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updated)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EmprestimoDTO resposta = objectMapper.readValue(updatedJson, EmprestimoDTO.class);
        assertThat(resposta.id()).isEqualTo(saved.id());
        assertThat(resposta.dataDevolucao()).isNull();
    }

    @Test
    @SneakyThrows
    void list() {
        String respostaJson = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<EmprestimoDTO> resposta = objectMapper.readValue(respostaJson, new TypeReference<List<EmprestimoDTO>>() {
        });
        assertThat(resposta.size()).isEqualTo(emprestimoRepository.count());
    }

    @Test
    @SneakyThrows
    void find() {
        String respostaJson = mockMvc.perform(get(PATH + "/{nomeUsuario}", "Leonardo"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<EmprestimoDTO> resposta = objectMapper.readValue(respostaJson, new TypeReference<List<EmprestimoDTO>>() {
        });
        assertThat(resposta.size()).isGreaterThan(0);

        respostaJson = mockMvc.perform(get(PATH + "/{nomeUsuario}", "umvalorquenaoexiste"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        resposta = objectMapper.readValue(respostaJson, new TypeReference<List<EmprestimoDTO>>() {
        });
        assertThat(resposta.size()).isEqualTo(0);
    }

    @Test
    @SneakyThrows
    void atualizarStatus() {
        String respostaAntes = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<EmprestimoDTO> emprestimosAntes = objectMapper.readValue(respostaAntes, new TypeReference<List<EmprestimoDTO>>() {
        });
        EmprestimoDTO aberto = emprestimosAntes.stream()
                .filter(e -> e.status().contains("aberto"))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(patch(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(List.of(aberto.id()))))
                .andExpect(status().isOk());

        String respostaDepois = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<EmprestimoDTO> emprestimosDepois = objectMapper.readValue(respostaDepois, new TypeReference<List<EmprestimoDTO>>() {
        });
        EmprestimoDTO atualizado = emprestimosDepois.stream()
                .filter(e -> e.id().equals(aberto.id()))
                .findFirst()
                .orElseThrow();

        assertThat(atualizado.dataDevolucao()).isNotNull();
        assertThat(atualizado.status()).contains("Fechado");
    }
}
