package com.elotech.teste.domain.entity.emprestimo;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum EmprestimoStatus {

    ABERTO_EM_DIA("AD", "Em aberto, dentro do prazo"),
    ABERTO_ATRASADO("AT", "Em aberto, fora do prazo"),
    FECHADO_EM_DIA("FD", "Fechado, dentro do prazo"),
    FECHADO_ATRASADO("FT", "Fechado, fora do prazo");

    private static final Map<String, EmprestimoStatus> MAP = new HashMap<>();

    static {
        for (EmprestimoStatus s : EmprestimoStatus.values()) {
            MAP.put(s.codigo, s);
        }
    }

    private final String codigo;
    private final String descricao;

    EmprestimoStatus(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static EmprestimoStatus fromCodigo(String codigo) {
        EmprestimoStatus status = MAP.get(codigo);
        if (status == null) {
            throw new IllegalArgumentException("Código inválido: " + codigo);
        }
        return status;
    }

    public boolean isAbertoEmPrazo() {
        return this == ABERTO_EM_DIA;
    }

    public boolean isAbertoAtrasado() {
        return this == ABERTO_ATRASADO;
    }

    public boolean isAberto() {
        return this.isAbertoAtrasado() || this.isAbertoEmPrazo();
    }

    public boolean isFechadoEmPrazo() {
        return this == FECHADO_EM_DIA;
    }

    public boolean isFechadoAtraso() {
        return this == FECHADO_ATRASADO;
    }
}
