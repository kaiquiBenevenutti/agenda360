package br.com.ifsc.agenda360.database.model;

import br.com.ifsc.agenda360.database.model.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consultas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome_paciente", nullable = false, length = 70)
    private String nomePaciente;

    @Column(name = "telefone_paciente")
    private String telefonePaciente;

    @Enumerated(EnumType.STRING)
    @Column(name = "como_conheceu", length = 30, nullable = false)
    private OrigemContato comoConheceu;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20, nullable = false)
    private TipoConsulta tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "local_selecionado", length = 20, nullable = false)
    private LocalConsulta local;

    @Column(name = "local_outro_descricao", length = 100)
    private String descricaoLocal;

    @Column(name = "data_hora_consulta", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "motivo_contato", length = 300)
    private String motivoContato;
}
