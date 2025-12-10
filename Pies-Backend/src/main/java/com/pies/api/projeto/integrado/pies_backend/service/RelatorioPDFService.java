package com.pies.api.projeto.integrado.pies_backend.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.itextpdf.io.font.PdfEncodings; // IMPORTANTE: Necessário para os símbolos
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.RelatorioIndividualDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RelatorioPDFService {

    private static final int FONT_SIZE_TITULO = 12;
    private static final int FONT_SIZE_TEXTO = 11;

    public byte[] gerarPDF(RelatorioIndividualDTO relatorio, byte[] imagemLogo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            // CORREÇÃO 1: Usar codificação CP1252 para permitir símbolos como • (bullet point)
            PdfFont fontTimes = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN, PdfEncodings.CP1252);
            document.setFont(fontTimes);

            // Margens padrão A4
            document.setMargins(20, 60, 50, 60);
            
            // 1. CABEÇALHO
            adicionarCabecalhoInstitucional(document, imagemLogo);
            
            // 2. IDENTIFICAÇÃO
            adicionarTituloIdentificacao(document);       
            adicionarLinhaNome(document, relatorio);      
            adicionarLinhaDataPeriodo(document, relatorio); 
            adicionarLinhaDiagnostico(document, relatorio); 
            
            // 3. DADOS FUNCIONAIS
            adicionarDadosFuncionais(document, relatorio);
            
            // 4. CONTEÚDO TÉCNICO
            adicionarSecaoTecnica(document, "FUNCIONALIDADE COGNITIVA", relatorio.funcionalidadeCognitiva());
            adicionarSecaoTecnica(document, "ALFABETIZAÇÃO E LETRAMENTO", relatorio.alfabetizacaoLetramento());
            adicionarSecaoTecnica(document, "ADAPTAÇÕES CURRICULARES", relatorio.adaptacoesCurriculares());
            adicionarSecaoTecnica(document, "PARTICIPAÇÃO NAS ATIVIDADES PROPOSTAS", relatorio.participacaoAtividades());
            adicionarSecaoTecnica(document, "AUTONOMIA", relatorio.autonomia());
            adicionarSecaoTecnica(document, "INTERAÇÃO COM A PROFESSORA", relatorio.interacaoProfessora());
            adicionarSecaoTecnica(document, "ATIVIDADES DE VIDA DIÁRIA (AVDs)", relatorio.atividadesVidaDiaria());

            // 5. ASSINATURAS
            adicionarAreaAssinaturas(document, relatorio);
            
            // 6. RODAPÉ
            adicionarRodapeInstitucional(document);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do relatório: " + e.getMessage(), e);
        }
        
        return baos.toByteArray();
    }
    
    // =================================================================================
    // MÉTODOS DE IDENTIFICAÇÃO (Mantidos conforme sua validação anterior)
    // =================================================================================

    private void adicionarTituloIdentificacao(Document document) {
        Paragraph tituloSecao = new Paragraph("1. IDENTIFICAÇÃO DO(A) ALUNO(A):")
                .setBold()
                .setFontSize(FONT_SIZE_TITULO)
                .setMarginBottom(0);
        document.add(tituloSecao);
    }

    private void adicionarLinhaNome(Document document, RelatorioIndividualDTO relatorio) {
        Table tabelaNome = new Table(UnitValue.createPercentArray(new float[]{0.6f, 12f}))
                .useAllAvailableWidth()
                .setMarginBottom(1f);

        Cell cellLabel = new Cell().add(new Paragraph("Nome:").setBold().setMargin(0))
                .setBorder(Border.NO_BORDER)
                .setPadding(1f)
                .setVerticalAlignment(VerticalAlignment.BOTTOM);
        
        String valor = relatorio.educandoNome() != null ? relatorio.educandoNome() : "";
        Cell cellValor = new Cell()
                .add(new Paragraph(valor).setMargin(0).setPaddingBottom(1))
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(0.8f))
                .setPadding(1f)
                .setVerticalAlignment(VerticalAlignment.BOTTOM);

        tabelaNome.addCell(cellLabel);
        tabelaNome.addCell(cellValor);
        document.add(tabelaNome);
    }

    private void adicionarLinhaDataPeriodo(Document document, RelatorioIndividualDTO relatorio) {
        // Ajustando as proporções para um layout mais compacto
        // Novo arranjo: 3.8f (Data label) | 3.2f (Data valor) | 1.5f (Período label) | 1.5f (Período valor)
        Table tabela = new Table(UnitValue.createPercentArray(new float[]{1.5f, 2f, 1f, 2f}))
                .useAllAvailableWidth()
                .setMarginBottom(0.8f);

        // 1. Data Label - Texto alinhado à direita sem padding
        Cell c1 = new Cell().add(new Paragraph("Data de Nascimento:").setBold().setMargin(0))
                .setBorder(Border.NO_BORDER)
                .setPadding(0)
                .setPaddingRight(0) // Sem padding direito
                .setVerticalAlignment(VerticalAlignment.BOTTOM)
                .setTextAlignment(TextAlignment.JUSTIFIED);

        // 2. Data Valor - Linha começando imediatamente
        String textoData = "";
        if (relatorio.dataNascimento() != null) {
            textoData = relatorio.dataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        
        // Usar Paragraph com mínimo de margem
        Paragraph pData = new Paragraph(textoData)
                .setMargin(0)
                .setPadding(0)
                .setPaddingBottom(1);
        
        Cell c2 = new Cell().add(pData)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(0.8f)) // A linha preta
                .setPadding(0)
                .setPaddingLeft(0f) // ZERO padding esquerdo
                .setVerticalAlignment(VerticalAlignment.BOTTOM);

        // 3. Período Label
        Cell c3 = new Cell().add(new Paragraph("Período:").setBold().setMargin(0))
                .setBorder(Border.NO_BORDER)
                .setPadding(0)
                .setPaddingLeft(0f) // Espaço entre Data e Período
                .setPaddingRight(0)
                .setVerticalAlignment(VerticalAlignment.BOTTOM)
                .setTextAlignment(TextAlignment.RIGHT);

        // 4. Período Valor
        String periodo = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        
        Paragraph pPeriodo = new Paragraph(periodo)
                .setMargin(0)
                .setPadding(0)
                .setPaddingBottom(1);
        
        Cell c4 = new Cell().add(pPeriodo)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(0.8f))
                .setPadding(0)
                .setPaddingLeft(0) // ZERO padding esquerdo
                .setVerticalAlignment(VerticalAlignment.BOTTOM);

        tabela.addCell(c1);
        tabela.addCell(c2);
        tabela.addCell(c3);
        tabela.addCell(c4);
        document.add(tabela);
    }

    private void adicionarLinhaDiagnostico(Document document, RelatorioIndividualDTO relatorio) {
        Table tabela = new Table(UnitValue.createPercentArray(new float[]{1.4f, 10f}))
                .useAllAvailableWidth()
                .setMarginBottom(10); 

        Cell cellLabel = new Cell().add(new Paragraph("Diagnóstico:").setBold().setMargin(0))
                .setBorder(Border.NO_BORDER)
                .setPadding(1f)
                .setVerticalAlignment(VerticalAlignment.BOTTOM);
        
        String valorCid = relatorio.educandoCid() != null ? relatorio.educandoCid() : "";
        
        Cell cellValor = new Cell()
                .add(new Paragraph(valorCid).setMargin(0).setPaddingBottom(1)) 
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(0.8f))
                .setPadding(1f)
                .setVerticalAlignment(VerticalAlignment.BOTTOM);
        
        tabela.addCell(cellLabel);
        tabela.addCell(cellValor);
        document.add(tabela);
    }

    private void adicionarSecaoTecnica(Document document, String titulo, String conteudo) {
        // Tabela para alinhar: [Bolinha] [Texto Completo]
        Table tabela = new Table(UnitValue.createPercentArray(new float[]{0.8f, 15f}))
                .useAllAvailableWidth()
                .setBorder(Border.NO_BORDER)
                .setMarginBottom(5); // Espaço entre um tópico e outro

        // 1. Célula da Bolinha
        Cell cellBullet = new Cell()
                .add(new Paragraph("\u2022").setFontSize(18).setBold().setVerticalAlignment(VerticalAlignment.TOP)) // Bullet Point
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingRight(5)
                .setPaddingTop(0)
                .setVerticalAlignment(VerticalAlignment.TOP);

        // 2. Montagem do Texto (Título Negrito + Conteúdo Normal)
        Paragraph paragrafoMisto = new Paragraph()
                .setFontSize(12)
                .setTextAlignment(TextAlignment.JUSTIFIED);
        
        // Parte A: Título em Negrito
        com.itextpdf.layout.element.Text textoTitulo = new com.itextpdf.layout.element.Text(titulo + ": ")
                .setBold();
        
        // Parte B: Conteúdo Normal
        String conteudoFinal = (conteudo != null && !conteudo.trim().isEmpty()) 
                ? conteudo.trim() 
                : "Não informado.";
        
        // Remove quebras de linha manuais para o texto fluir na linha do título
        conteudoFinal = conteudoFinal.replaceAll("\\r?\\n", " "); 
        
        com.itextpdf.layout.element.Text textoConteudo = new com.itextpdf.layout.element.Text(conteudoFinal);

        // Adiciona as partes ao parágrafo
        paragrafoMisto.add(textoTitulo);
        paragrafoMisto.add(textoConteudo);

        // Célula do Texto
        Cell cellTexto = new Cell()
                .add(paragrafoMisto)
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(0)
                .setPaddingTop(0)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        tabela.addCell(cellBullet);
        tabela.addCell(cellTexto);
        
        document.add(tabela);
    }

    // =================================================================================
    // OUTROS MÉTODOS
    // =================================================================================

    private void adicionarCabecalhoInstitucional(Document document, byte[] imagemLogo) {
        if (imagemLogo != null && imagemLogo.length > 0) {
            try {
                ImageData imageData = ImageDataFactory.create(imagemLogo);
                Image img = new Image(imageData);
                img.setWidth(UnitValue.createPercentValue(39));
                img.setHorizontalAlignment(HorizontalAlignment.CENTER);
                img.setMarginBottom(5);
                document.add(img);
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem: " + e.getMessage());
            }
        }

        document.add(new Paragraph("AVALIAÇÃO DESCRITIVA")
                .setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("ATENDIMENTO EDUCACIONAL ESPECIALIZADO - 2025")
                .setBold().setFontSize(12).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10));
    }

    private void adicionarDadosFuncionais(Document document, RelatorioIndividualDTO relatorio) {
        document.add(new Paragraph("2. DADOS FUNCIONAIS:")
                .setBold().setFontSize(FONT_SIZE_TITULO).setMarginTop(5).setMarginBottom(2)); // Margem reduzida

        String textoPadrao = "Introduza aqui um texto inicial, informando com que frequência semanal o educando participa dos atendimentos, o tempo de duração de cada atendimento e o objetivo geral dessas atividades.";
        String texto = (relatorio.dadosFuncionais() != null && !relatorio.dadosFuncionais().isEmpty()) 
                ? relatorio.dadosFuncionais() : textoPadrao;
        
        document.add(new Paragraph(texto)
                .setFontSize(FONT_SIZE_TEXTO)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setMarginBottom(15));
    }

    private void adicionarAreaAssinaturas(Document document, RelatorioIndividualDTO relatorio) {
        document.add(new Paragraph().setMarginTop(40));
        Table tabela = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        
        adicionarAssinatura(tabela, "PROFESSOR(A) DE ATENDIMENTO EDUCACIONAL ESPECIALIZADO");
        tabela.addCell(new Cell().add(new Paragraph("\n")).setBorder(Border.NO_BORDER));
        adicionarAssinatura(tabela, "ASSINATURA DO(A) RESPONSÁVEL");
        tabela.addCell(new Cell().add(new Paragraph("\n")).setBorder(Border.NO_BORDER));
        
        // Data
        Cell cellData = new Cell()
                .add(new Paragraph("______/_______/_______").setBold().setFontSize(10).setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph("DATA").setBold().setFontSize(10).setTextAlignment(TextAlignment.CENTER))
                .setBorder(Border.NO_BORDER);
        tabela.addCell(cellData);

        document.add(tabela);
    }

    private void adicionarAssinatura(Table tabela, String titulo) {
        Cell cell = new Cell()
                .add(new Paragraph("__________________________________________________________").setBold().setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph(titulo).setBold().setFontSize(10).setTextAlignment(TextAlignment.CENTER))
                .setBorder(Border.NO_BORDER);
        tabela.addCell(cell);
    }

    private void adicionarRodapeInstitucional(Document document) {
        document.add(new Paragraph().setMarginTop(20));
        document.add(new Paragraph()
                .add("Associação de Pais e Amigos de Pessoas Especiais de Quixadá - APAPEQ\n")
                .add("Rua Basilio Pinto, 2651, Combate. 63902-100/Quixadá-CE\n")
                .add("CNAS: 8742-07/12/2003   CNPJ: 02.328.891/0001-35\n")
                .add("Email: apapeqqxd@gmail.com")
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLACK));
    }
}