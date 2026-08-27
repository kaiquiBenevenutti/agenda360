package br.com.ifsc.agenda360.controller;

import br.com.ifsc.agenda360.database.model.enums.StatusConsulta;
import br.com.ifsc.agenda360.database.repository.IConsultaRepository;
import br.com.ifsc.agenda360.dto.ConsultaDto;
import br.com.ifsc.agenda360.service.ConsultasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public List<ConsultaDto> listarConsultas(@RequestParam(required = false) StatusConsulta status) {
        if (status != null) {
            return consultasService.buscarConsultasPorStatus(status);
        }
        return consultasService.listarConsultas();
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
    @ResponseStatus(HttpStatus.OK)
    public void finalizarConsulta(@PathVariable UUID id){
        consultasService.finalizarConsulta(id);
    }
}
