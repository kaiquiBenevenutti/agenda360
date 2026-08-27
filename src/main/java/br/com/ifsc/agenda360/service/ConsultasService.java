package br.com.ifsc.agenda360.service;

import br.com.ifsc.agenda360.database.model.ConsultaEntity;
import br.com.ifsc.agenda360.database.repository.IConsultaRepository;
import br.com.ifsc.agenda360.dto.ConsultaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ConsultasService {
    private final IConsultaRepository consultasRepository;

    public void CriarNovaConsulta(ConsultaDto dto) {
        ConsultaEntity c = ConsultaEntity.builder()
                .nomePaciente(dto.getNomePaciente())
                .telefonePaciente(dto.getTelefonePaciente())
                .comoConheceu(dto.getComoConheceu())
                .tipo(dto.getTipo())
                .local(dto.getLocal())
                .descricaoLocal(dto.getDescricaoLocal())
                .dataHora(dto.getDataHora())
                .valor(dto.getValor())
                .motivoContato(dto.getMotivoContato())
                .status(dto.getStatus())
                .build();

        consultasRepository.save(c);
    }

    public List<ConsultaDto> listarConsultas(){
        return consultasRepository.findAll().stream()
                .map(e -> ConsultaDto.builder()
                        .nomePaciente(e.getNomePaciente())
                        .telefonePaciente(e.getTelefonePaciente())
                        .comoConheceu(e.getComoConheceu())
                        .tipo(e.getTipo())
                        .local(e.getLocal())
                        .descricaoLocal(e.getDescricaoLocal())
                        .dataHora(e.getDataHora())
                        .valor(e.getValor())
                        .motivoContato(e.getMotivoContato())
                        .status(e.getStatus())
                        .build()
                ).toList();
    }

    //Não vai ter delete, vamos só colocar o cancelar
}