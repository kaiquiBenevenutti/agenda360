package br.com.ifsc.agenda360.service;

import br.com.ifsc.agenda360.database.model.ConsultaEntity;
import br.com.ifsc.agenda360.database.model.enums.OrigemContato;
import br.com.ifsc.agenda360.database.model.enums.StatusConsulta;
import br.com.ifsc.agenda360.database.model.enums.TipoConsulta;
import br.com.ifsc.agenda360.database.repository.IConsultaRepository;
import br.com.ifsc.agenda360.dto.ConsultaDto;
import br.com.ifsc.agenda360.dto.FinanceiroDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//imports do atestado
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;


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
                        .id(e.getId())
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
                .id(e.getId())
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
                        .id(e.getId())
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

    public byte[] gerarAtestadoPdf(UUID id) {
        ConsultaEntity e = consultasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        // 1. Formatação de Datas (ex: "11 de agosto de 2026")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
        String dataConsultaStr = e.getDataHora().format(formatter);
        String dataEmissaoStr = LocalDate.now().format(formatter);

        // 2. Lógica do Período (Matutino, Vespertino, Noturno)
        int hora = e.getDataHora().getHour();
        String periodo = (hora < 12) ? "matutino" : (hora < 18) ? "vespertino" : "noturno";

        // 3. Lógica do Tipo de Atendimento (Consulta ou Terapia)
        String tipoAtendimento = (e.getTipo() == TipoConsulta.TERAPIA) ? "terapia" : "consulta";

        // 4. Local (Tratamento para Enum, com Fallback para Gaspar)
        String cidade = "Gaspar";
        if (e.getLocal() != null) {
            String cidadeBruta = e.getLocal().toString(); // Extrai o nome do Enum (ex: "BLUMENAU")
            // Deixa a primeira letra maiúscula e o restante minúsculo
            cidade = cidadeBruta.substring(0, 1).toUpperCase() + cidadeBruta.substring(1).toLowerCase();
        }

        // 5. Geração do PDF em memória
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Adicionando a Logo com Posicionamento Absoluto
        try {
            // Busca a imagem dentro da pasta resources/static
            ClassPathResource imgFile = new ClassPathResource("static/logo.png");
            byte[] imageBytes = imgFile.getInputStream().readAllBytes();

            // Converte os bytes para o formato do iText e cria a Imagem
            ImageData data = ImageDataFactory.create(imageBytes);
            Image logo = new Image(data);

            // Reduzido de 700f para 250f. Uma página A4 tem 595 de largura.
            // 700 cortaria a imagem para fora do PDF.
            logo.scaleToFit(550f, 230f);

            // Posicionamento absoluto: fixa a logo no canto superior esquerdo
            // X = 50 (margem esquerda), Y = 730 (próximo ao topo da página)
            logo.setFixedPosition(25f, 670f);

            document.add(logo);

            // Adiciona espaços no fluxo normal para o título não ficar colado no topo da folha
            document.add(new Paragraph("\n\n\n\n\n\n"));
        } catch (Exception ex) {
            // Se a imagem não for encontrada, insere espaços em branco para manter a formatação
            document.add(new Paragraph("\n\n\n\n\n\n"));
            System.err.println("Erro ao carregar a logo: " + ex.getMessage());
        }

        // Título Centralizado
        Paragraph titulo = new Paragraph("Atestado de Comparecimento")
                .setBold()
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(40);
        document.add(titulo);

        // Corpo do texto Justificado
        String textoCorpo = String.format(
                "Atesto, para os devidos fins, que %s esteve presente no dia %s, no período %s, em %s de Hipnoterapia, na cidade de %s com o Psicoterapeuta/Hipnoterapeuta RICARDO BENEVENUTTI, portador do CPF 00411492977.",
                e.getNomePaciente(), dataConsultaStr, periodo, tipoAtendimento, cidade
        );
        Paragraph corpo = new Paragraph(textoCorpo)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setMarginBottom(60);
        document.add(corpo);

        // Data de Emissão Centralizada
        Paragraph dataEmissao = new Paragraph(String.format("Gaspar, %s", dataEmissaoStr))
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(80);
        document.add(dataEmissao);

        // Rodapé / Assinatura Centralizada
        Paragraph rodape = new Paragraph("Ricardo Benevenutti\nPsicoterapeuta/Hipnoterapeuta\nIRT 18307\nCNPJ 10294715000158")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(rodape);

        document.close();
        return baos.toByteArray();
    }

    // --- FUNÇÕES DA TELA DE FINANCEIRO ---

    public FinanceiroDto obterDadosFinanceiro(LocalDate dataInicio, LocalDate dataFim) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = (dataInicio != null) ? dataInicio : hoje.withDayOfMonth(1);
        LocalDate fim = (dataFim != null) ? dataFim : hoje.withDayOfMonth(hoje.lengthOfMonth());

        List<ConsultaEntity> consultasNoPeriodo = inicio.isAfter(fim)
                ? List.of()
                : buscarEntidadesPorPeriodo(inicio, fim);

        return FinanceiroDto.builder()
                .faturamentoTotal(calcularFaturamentoTotal(consultasNoPeriodo))
                .faturamentoSemanal(calcularFaturamentoSemanal(consultasNoPeriodo, inicio, fim))
                .origemClientes(calcularOrigemClientes(consultasNoPeriodo))
                .maioresCausas(calcularMaiorCausaProcura(consultasNoPeriodo))
                .taxaConclusao(calcularTaxaConclusao(consultasNoPeriodo))
                .build();
    }

    public List<ConsultaEntity> buscarEntidadesPorPeriodo(LocalDate inicio, LocalDate fim) {
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime fimDateTime = fim.atTime(23, 59, 59);
        return consultasRepository.findByDataHoraBetween(inicioDateTime, fimDateTime);
    }

    public BigDecimal calcularFaturamentoTotal(List<ConsultaEntity> consultas) {
        return consultas.stream()
                .filter(c -> isStatusFinalizado(c.getStatus()))
                .map(c -> c.getValor() != null ? c.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<FinanceiroDto.FaturamentoSemanalDto> calcularFaturamentoSemanal(
            List<ConsultaEntity> consultas, LocalDate inicio, LocalDate fim) {

        BigDecimal[] somas = new BigDecimal[]{
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        };

        long totalDias = Math.max(1L, ChronoUnit.DAYS.between(inicio, fim) + 1L);

        for (ConsultaEntity c : consultas) {
            if (!isStatusFinalizado(c.getStatus()) || c.getDataHora() == null) {
                continue;
            }
            BigDecimal valor = c.getValor() != null ? c.getValor() : BigDecimal.ZERO;
            long diasDecorridos = Math.max(0L, ChronoUnit.DAYS.between(inicio, c.getDataHora().toLocalDate()));
            int indiceSemana = (int) Math.min(3L, (diasDecorridos * 4L) / totalDias);
            somas[indiceSemana] = somas[indiceSemana].add(valor);
        }

        List<FinanceiroDto.FaturamentoSemanalDto> resultado = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            resultado.add(FinanceiroDto.FaturamentoSemanalDto.builder()
                    .semana("Semana " + (i + 1))
                    .valor(somas[i])
                    .build());
        }
        return resultado;
    }

    public FinanceiroDto.OrigemClientesDto calcularOrigemClientes(List<ConsultaEntity> consultas) {
        long instagram = 0;
        long recomendacao = 0;
        long facebook = 0;
        long outros = 0;

        for (ConsultaEntity c : consultas) {
            if (c.getComoConheceu() == null) {
                outros++;
                continue;
            }
            switch (c.getComoConheceu()) {
                case INSTAGRAM -> instagram++;
                case RECOMENDACAO -> recomendacao++;
                case FACEBOOK -> facebook++;
                case OUTROS -> outros++;
            }
        }

        return FinanceiroDto.OrigemClientesDto.builder()
                .instagram(instagram)
                .recomendacao(recomendacao)
                .facebook(facebook)
                .outros(outros)
                .build();
    }

    public List<FinanceiroDto.CausaProcuraDto> calcularMaiorCausaProcura(List<ConsultaEntity> consultas) {
        Map<String, Long> contagemPorChave = new LinkedHashMap<>();
        Map<String, String> rotuloOriginal = new LinkedHashMap<>();

        for (ConsultaEntity c : consultas) {
            if (c.getMotivoContato() == null || c.getMotivoContato().trim().isEmpty()) {
                continue;
            }
            String motivoLimpo = c.getMotivoContato().trim();
            String chave = motivoLimpo.toLowerCase(Locale.ROOT);

            contagemPorChave.put(chave, contagemPorChave.getOrDefault(chave, 0L) + 1L);
            rotuloOriginal.putIfAbsent(
                    chave,
                    motivoLimpo.substring(0, 1).toUpperCase(Locale.ROOT) + motivoLimpo.substring(1)
            );
        }

        return contagemPorChave.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(4)
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> FinanceiroDto.CausaProcuraDto.builder()
                        .causa(rotuloOriginal.get(entry.getKey()))
                        .quantidade(entry.getValue())
                        .build())
                .toList();
    }

    public FinanceiroDto.TaxaConclusaoDto calcularTaxaConclusao(List<ConsultaEntity> consultas) {
        long procuraram = consultas.size();
        long iniciaram = consultas.stream()
                .filter(c -> !isStatusCancelado(c.getStatus()))
                .count();
        long concluiram = consultas.stream()
                .filter(c -> isStatusFinalizado(c.getStatus()))
                .count();

        return FinanceiroDto.TaxaConclusaoDto.builder()
                .procuraram(procuraram)
                .iniciaram(iniciaram)
                .concluiram(concluiram)
                .build();
    }

    private boolean isStatusFinalizado(StatusConsulta status) {
        return status == StatusConsulta.CONSULTA_FINALIZADA
                || status == StatusConsulta.TERAPIA_FINALIZADA
                || status == StatusConsulta.RETORNO_FINALIZADO;
    }

    private boolean isStatusCancelado(StatusConsulta status) {
        return status == StatusConsulta.CONSULTA_CANCELADA
                || status == StatusConsulta.TERAPIA_CANCELADA
                || status == StatusConsulta.RETORNO_CANCELADO;
    }

    //Não vai ter delete, vamos só colocar o cancelar
}
