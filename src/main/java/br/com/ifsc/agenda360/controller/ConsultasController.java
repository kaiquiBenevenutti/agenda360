package br.com.ifsc.agenda360.controller;

import br.com.ifsc.agenda360.database.model.enums.StatusConsulta;
import br.com.ifsc.agenda360.database.repository.IConsultaRepository;
import br.com.ifsc.agenda360.dto.ConsultaDto;
import br.com.ifsc.agenda360.dto.FinanceiroDto;
import br.com.ifsc.agenda360.service.ConsultasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/consultas")
@RequiredArgsConstructor
@Validated
public class ConsultasController {
    private final ConsultasService consultasService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void criarConsulta(@RequestBody @Valid ConsultaDto dto) {
        consultasService.CriarNovaConsulta(dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ConsultaDto> listarConsultas(@RequestParam(value = "status", required = false) StatusConsulta status) {
        if (status != null) {
            return consultasService.buscarConsultasPorStatus(status);
        }
        return consultasService.listarConsultas();
    }

    @GetMapping("/financeiro")
    @ResponseStatus(HttpStatus.OK)
    public FinanceiroDto obterDadosFinanceiro(
            @RequestParam(value = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return consultasService.obterDadosFinanceiro(dataInicio, dataFim);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ConsultaDto buscarConsultaPorId(@PathVariable UUID id){
        return consultasService.buscarConsultaPorId(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void editarConsulta(@PathVariable UUID id, @RequestBody @Valid ConsultaDto dto){
        consultasService.editarConsulta(id, dto);
    }

    @PatchMapping("/cancelar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarConsulta(@PathVariable UUID id){
        consultasService.cancelarConsulta(id);
    }

    @PatchMapping("/finalizar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void finalizarConsulta(@PathVariable UUID id){
        consultasService.finalizarConsulta(id);
    }

    @GetMapping("/{id}/atestado")
    public ResponseEntity<byte[]> gerarAtestado(@PathVariable UUID id) {
        byte[] pdf = consultasService.gerarAtestadoPdf(id);

        // Busca os dados da consulta para pegar o nome
        ConsultaDto consulta = consultasService.buscarConsultaPorId(id);

        // Remove espaços do nome do paciente para não quebrar o nome do arquivo
        String nomePaciente = consulta != null && consulta.getNomePaciente() != null
                ? consulta.getNomePaciente().replaceAll("\\s+", "_")
                : "paciente";

        String nomeArquivo = "atestado_" + nomePaciente + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Agora o arquivo baixa com o nome bonitinho (Ex: atestado_Kaiqui.pdf)
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
