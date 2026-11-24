package br.com.ifsc.agenda360.dto;


import br.com.ifsc.agenda360.database.model.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDto {

    private UUID id;

    @NotBlank
    private String nomePaciente;

    private String telefonePaciente;

    @NotNull
    private OrigemContato comoConheceu;

    @NotNull
    private TipoConsulta tipo;

    @NotNull
    private LocalConsulta local;

    private String descricaoLocal;

    @NotNull
    private LocalDateTime dataHora;

    private BigDecimal valor;

    private String motivoContato;

    @NotNull
    private StatusConsulta status;
}
