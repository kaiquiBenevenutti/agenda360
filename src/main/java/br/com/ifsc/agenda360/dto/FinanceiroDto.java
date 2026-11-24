package br.com.ifsc.agenda360.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceiroDto {

    private BigDecimal faturamentoTotal;
    private List<FaturamentoSemanalDto> faturamentoSemanal;
    private OrigemClientesDto origemClientes;
    private List<CausaProcuraDto> maioresCausas;
    private TaxaConclusaoDto taxaConclusao;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaturamentoSemanalDto {
        private String semana;
        private BigDecimal valor;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrigemClientesDto {
        private long instagram;
        private long recomendacao;
        private long facebook;
        private long outros;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CausaProcuraDto {
        private String causa;
        private long quantidade;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxaConclusaoDto {
        private long procuraram;
        private long iniciaram;
        private long concluiram;
    }
}
