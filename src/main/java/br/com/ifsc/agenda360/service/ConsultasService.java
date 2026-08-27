package br.com.ifsc.agenda360.service;

import br.com.ifsc.agenda360.database.model.ConsultaEntity;
import br.com.ifsc.agenda360.database.model.enums.StatusConsulta;
import br.com.ifsc.agenda360.database.model.enums.TipoConsulta;
import br.com.ifsc.agenda360.database.repository.IConsultaRepository;
import br.com.ifsc.agenda360.dto.ConsultaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


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
        System.out.println("ID gerado: " + c.getId().toString());
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

    public ConsultaDto buscarConsultaPorId(UUID id){
        ConsultaEntity e = consultasRepository.findById(id).orElse(null);
        return ConsultaDto.builder()
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
                .build();
    }

    public List<ConsultaDto> buscarConsultasPorStatus(StatusConsulta status){

        return consultasRepository.findByStatus(status).stream()
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

    public void editarConsulta(UUID id, ConsultaDto dto){
        ConsultaEntity e = consultasRepository.findById(id).orElse(null);

        e.setNomePaciente(dto.getNomePaciente());
        e.setTelefonePaciente(dto.getTelefonePaciente());
        e.setComoConheceu(dto.getComoConheceu());
        e.setTipo(dto.getTipo());
        e.setLocal(dto.getLocal());
        e.setDescricaoLocal(dto.getDescricaoLocal());
        e.setDataHora(dto.getDataHora());
        e.setValor(dto.getValor());
        e.setMotivoContato(dto.getMotivoContato());
        e.setStatus(dto.getStatus());

        consultasRepository.save(e);
    }

    public void cancelarConsulta(UUID id){
        ConsultaEntity e = consultasRepository.findById(id).orElse(null);

        if(e.getTipo() == TipoConsulta.CONSULTA){
            consultasRepository.atualizarStatus(id, StatusConsulta.CONSULTA_CANCELADA);
        } else if(e.getTipo() == TipoConsulta.TERAPIA){
            consultasRepository.atualizarStatus(id, StatusConsulta.TERAPIA_CANCELADA);
        } else if(e.getTipo() == TipoConsulta.RETORNO){
            consultasRepository.atualizarStatus(id, StatusConsulta.RETORNO_CANCELADO);
        }
    }

    public void finalizarConsulta(UUID id){
        ConsultaEntity e = consultasRepository.findById(id).orElse(null);

        if(e.getTipo() == TipoConsulta.CONSULTA){
            consultasRepository.atualizarStatus(id, StatusConsulta.CONSULTA_FINALIZADA);
        } else if(e.getTipo() == TipoConsulta.TERAPIA){
            consultasRepository.atualizarStatus(id, StatusConsulta.TERAPIA_FINALIZADA);
        } else if(e.getTipo() == TipoConsulta.RETORNO){
            consultasRepository.atualizarStatus(id, StatusConsulta.RETORNO_FINALIZADO);
        }
    }

    //Não vai ter delete, vamos só colocar o cancelar
}