package com.pies.api.projeto.integrado.pies_backend.service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.RelatorioIndividualDTO;

import lombok.RequiredArgsConstructor;

/**
 * Serviço profissional para geração de PDFs de relatórios individuais.
 * 
 * Este serviço gera PDFs formatados e organizados com todas as informações
 * do relatório individual do educando, seguindo padrões profissionais de
 * documentação educacional.
 */
@Service
@RequiredArgsConstructor
public class RelatorioPDFService {

    // Cores personalizadas para o documento
    private static final DeviceRgb COR_PRIMARIA = new DeviceRgb(41, 128, 185); // Azul profissional
    private static final DeviceRgb COR_SECUNDARIA = new DeviceRgb(52, 73, 94); // Cinza escuro

    /**
     * Gera um PDF profissional do relatório individual.
     * 
     * @param relatorio DTO do relatório individual com todos os dados
     * @return Array de bytes contendo o PDF gerado
     * @throws RuntimeException se houver erro na geração do PDF
     */
    public byte[] gerarPDF(RelatorioIndividualDTO relatorio) {
        // ByteArrayOutputStream NÃO deve ser fechado - será lido após fechar os outros recursos
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Usa try-with-resources para garantir fechamento correto
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            // Configuração da página com margens adequadas
            document.setMargins(50, 50, 50, 50);
            
            // ========== CABEÇALHO ==========
            adicionarCabecalho(document);
            
            // ========== INFORMAÇÕES DO EDUCANDO ==========
            adicionarSecaoInformacoes(document, relatorio);
            
            // ========== CONTEÚDO DO RELATÓRIO ==========
            adicionarSecao(document, "DADOS FUNCIONAIS", relatorio.dadosFuncionais());
            adicionarSecao(document, "FUNCIONALIDADE COGNITIVA", relatorio.funcionalidadeCognitiva());
            adicionarSecao(document, "ALFABETIZAÇÃO E LETRAMENTO", relatorio.alfabetizacaoLetramento());
            adicionarSecao(document, "ADAPTAÇÕES CURRICULARES", relatorio.adaptacoesCurriculares());
            adicionarSecao(document, "PARTICIPAÇÃO NAS ATIVIDADES PROPOSTAS", relatorio.participacaoAtividades());
            adicionarSecao(document, "AUTONOMIA", relatorio.autonomia());
            adicionarSecao(document, "INTERAÇÃO COM A PROFESSORA", relatorio.interacaoProfessora());
            adicionarSecao(document, "ATIVIDADES DE VIDA DIÁRIA (AVDs)", relatorio.atividadesVidaDiaria());
            
            // ========== RODAPÉ ==========
            adicionarRodape(document, relatorio);
            
            // O try-with-resources fecha automaticamente: Document -> PdfDocument -> PdfWriter
            // Isso finaliza o PDF corretamente
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do relatório: " + e.getMessage(), e);
        }
        
        // Lê os bytes APÓS o try-with-resources ter fechado todos os recursos
        // O ByteArrayOutputStream permanece aberto e acessível
        byte[] pdfBytes = baos.toByteArray();
        
        // Valida se o PDF foi gerado
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new RuntimeException("PDF gerado está vazio");
        }
        
        // Valida se começa com o header correto de PDF (%PDF)
        if (pdfBytes.length < 4) {
            throw new RuntimeException("PDF gerado está muito pequeno: " + pdfBytes.length + " bytes");
        }
        
        // Verifica o header do PDF
        String header = new String(pdfBytes, 0, Math.min(4, pdfBytes.length));
        if (!header.equals("%PDF")) {
            throw new RuntimeException("PDF gerado não possui header válido. Header encontrado: " + header);
        }
        
        return pdfBytes;
    }
    
    /**
     * Adiciona o cabeçalho profissional do documento.
     */
    private void adicionarCabecalho(Document document) {
        // Título principal
        Paragraph titulo = new Paragraph("RELATÓRIO FINAL INDIVIDUAL")
                .setFontSize(22)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(COR_PRIMARIA)
                .setMarginBottom(5);
        document.add(titulo);
        
        // Subtítulo
        Paragraph subtitulo = new Paragraph("Sistema PIES - Programa de Inclusão e Educação Especial")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(COR_SECUNDARIA)
                .setMarginBottom(20);
        document.add(subtitulo);
        
        // Linha separadora
        document.add(new Paragraph().setMarginBottom(15));
    }
    
    /**
     * Adiciona seção com informações do educando e professor.
     */
    private void adicionarSecaoInformacoes(Document document, RelatorioIndividualDTO relatorio) {
        // Título da seção
        Paragraph tituloSecao = new Paragraph("INFORMAÇÕES GERAIS")
                .setFontSize(14)
                .setBold()
                .setFontColor(COR_PRIMARIA)
                .setMarginTop(10)
                .setMarginBottom(10);
        document.add(tituloSecao);
        
        // Tabela com informações
        Table tabela = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(20);
        
        // Linha do Educando
        adicionarLinhaTabela(tabela, "Educando:", 
                relatorio.educandoNome() != null ? relatorio.educandoNome() : "Não informado");
        
        // Linha do Professor
        adicionarLinhaTabela(tabela, "Professor Responsável:", 
                relatorio.professorNome() != null ? relatorio.professorNome() : "Não informado");
        
        // Linha da Data
        String dataFormatada = relatorio.dataCriacao() != null ? 
                relatorio.dataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")) : 
                "Não informado";
        adicionarLinhaTabela(tabela, "Data de Criação:", dataFormatada);
        
        document.add(tabela);
    }
    
    /**
     * Adiciona uma seção de conteúdo ao documento.
     */
    private void adicionarSecao(Document document, String tituloSecao, String conteudo) {
        if (conteudo == null || conteudo.trim().isEmpty()) {
            conteudo = "Não informado.";
        }
        
        // Título da seção com estilo
        Paragraph titulo = new Paragraph(tituloSecao)
                .setFontSize(12)
                .setBold()
                .setFontColor(COR_PRIMARIA)
                .setMarginTop(15)
                .setMarginBottom(8)
                .setBorderBottom(new SolidBorder(COR_PRIMARIA, 1))
                .setPaddingBottom(5);
        document.add(titulo);
        
        // Conteúdo da seção
        Paragraph texto = new Paragraph(conteudo)
                .setFontSize(10)
                .setMarginBottom(12)
                .setTextAlignment(TextAlignment.JUSTIFIED);
        document.add(texto);
    }
    
    /**
     * Adiciona uma linha à tabela de informações.
     */
    private void adicionarLinhaTabela(Table tabela, String label, String valor) {
        Paragraph labelPara = new Paragraph(label)
                .setBold()
                .setFontSize(10)
                .setFontColor(COR_SECUNDARIA);
        
        Paragraph valorPara = new Paragraph(valor != null ? valor : "Não informado")
                .setFontSize(10);
        
        tabela.addCell(labelPara);
        tabela.addCell(valorPara);
    }
    
    /**
     * Adiciona o rodapé profissional do documento.
     */
    private void adicionarRodape(Document document, RelatorioIndividualDTO relatorio) {
        // Espaçamento
        document.add(new Paragraph().setMarginTop(30));
        
        // Linha separadora (usando String.repeat ou alternativa compatível)
        StringBuilder linhaSeparadora = new StringBuilder();
        for (int i = 0; i < 80; i++) {
            linhaSeparadora.append('-');
        }
        Paragraph linha = new Paragraph(linhaSeparadora.toString())
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY)
                .setMarginBottom(10);
        document.add(linha);
        
        // Texto do rodapé
        Paragraph rodape = new Paragraph("Documento gerado automaticamente pelo Sistema PIES")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);
        document.add(rodape);
        
        // ID do relatório (pequeno)
        if (relatorio.id() != null) {
            Paragraph idRelatorio = new Paragraph("ID: " + relatorio.id())
                    .setFontSize(7)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.LIGHT_GRAY)
                    .setMarginTop(3);
            document.add(idRelatorio);
        }
    }
}
