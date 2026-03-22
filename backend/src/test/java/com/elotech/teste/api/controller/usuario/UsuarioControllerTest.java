package com.elotech.teste.api.controller.usuario;

import com.elotech.teste.IntegrationTest;
import com.elotech.teste.api.dto.usuario.UsuarioDTO;
import com.elotech.teste.infra.client.GoogleBooksRestClient;
import com.elotech.teste.repository.usuario.UsuarioRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsuarioControllerTest extends IntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GoogleBooksRestClient client;

    private static final String PATH = "/usuarios";
    private static final Long ID_USUARIO = 7L;

    @Test
    @SneakyThrows
    void save() {
        long quantidadeAntes = usuarioRepository.count();
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(UsuarioDataProvider.getUsuario())))
                .andExpect(status().isOk());

        long quantidadeUltimoSalvamento = usuarioRepository.count();
        assertThat(quantidadeAntes).isLessThan(quantidadeUltimoSalvamento);

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(UsuarioDataProvider.getUsuarioSemCampos())))
                .andExpect(status().isBadRequest());

        assertThat(quantidadeUltimoSalvamento).isEqualTo(usuarioRepository.count());
    }

    @Test
    @SneakyThrows
    void deleteTeste() {
        long quantidadeAntes = usuarioRepository.count();

        mockMvc.perform(delete(PATH + "/{id}", ID_USUARIO))
                .andExpect(status().isNoContent());

        long quantidadeAposRemover = usuarioRepository.count();
        assertThat(quantidadeAntes).isGreaterThan(quantidadeAposRemover);
    }

    @Test
    @SneakyThrows
    void update() {
        UsuarioDTO livroDTO = new UsuarioDTO(
                ID_USUARIO,
                "Nome alterado",
                "leoteste@gmail.com",
                ZonedDateTime.now(),
                "44991563680"
        );

        String response = mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(livroDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UsuarioDTO saved = objectMapper.readValue(response, UsuarioDTO.class);
        assertThat(saved.id()).isNotNull();
        assertThat(saved.nome()).isEqualTo("Nome alterado");
    }

    @Test
    @SneakyThrows
    void list() {
        String respostaJson = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UsuarioDTO> resposta = objectMapper.readValue(respostaJson, new TypeReference<List<UsuarioDTO>>() {
        });
        assertThat(resposta.size()).isEqualTo(usuarioRepository.count());
    }

    @Test
    @SneakyThrows
    void find() {
        String respostaJson = mockMvc.perform(get(PATH + "/{query}", "leo"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UsuarioDTO> resposta = objectMapper.readValue(respostaJson, new TypeReference<List<UsuarioDTO>>() {
        });
        assertThat(resposta.size()).isGreaterThan(0);

        respostaJson = mockMvc.perform(get(PATH + "/{query}", "umvalorquenaoexiste"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        resposta = objectMapper.readValue(respostaJson, new TypeReference<List<UsuarioDTO>>() {
        });
        assertThat(resposta.size()).isEqualTo(0);
    }
}