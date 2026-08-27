package br.com.ifsc.agenda360.controller;

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
    public List<ConsultaDto> listarConsultas() {
        return consultasService.listarConsultas();
    }
}
