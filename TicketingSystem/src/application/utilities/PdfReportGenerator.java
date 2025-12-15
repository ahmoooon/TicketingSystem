package application.utilities;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for generating PDF reports from text content.
 * Uses Apache PDFBox library for PDF generation.
 */
public class PdfReportGenerator {
    
    private static final Logger logger = LoggerSetup.getLogger();
    
    // PDF Layout Constants
    private static final float MARGIN = 50;
    private static final float FONT_SIZE_TITLE = 16;
    private static final float FONT_SIZE_NORMAL = 10;
    private static final float LINE_HEIGHT = 15;
    private static final int MAX_CHARS_PER_LINE = 85;
    
    /**
     * Generates a PDF report from text content and saves it to the specified file.
     * 
     * @param reportContent The text content of the report
     * @param outputFile The file where the PDF should be saved
     * @param reportTitle The title of the report
     * @throws IOException if PDF generation or file writing fails
     */
    public static void generatePdfReport(String reportContent, File outputFile, String reportTitle) 
            throws IOException {
        
        logger.info("Generating PDF report: " + reportTitle);
        
        try (PDDocument document = new PDDocument()) {
            // Split content into lines
            List<String> lines = splitIntoLines(reportContent);
            
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Current Y position (starts from top)
            float yPosition = page.getMediaBox().getHeight() - MARGIN;
            
            // Write header
            yPosition = writeHeader(contentStream, page, reportTitle, yPosition);
            
            // Write content
            yPosition = writeContent(document, contentStream, page, lines, yPosition);
            
            contentStream.close();
            
            // Save document
            document.save(outputFile);
            logger.info("PDF report saved successfully: " + outputFile.getAbsolutePath());
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to generate PDF report", e);
            throw e;
        }
    }
    
    /**
     * Writes the report header (title and date).
     */
    private static float writeHeader(PDPageContentStream contentStream, PDPage page, 
                                     String title, float yPosition) throws IOException {
        
        // Company name
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), FONT_SIZE_TITLE);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("YSCM CINEMA - " + title);
        contentStream.endText();
        
        yPosition -= LINE_HEIGHT * 1.5f;
        
        // Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(new Date());
        
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), FONT_SIZE_NORMAL);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Generated: " + currentDate);
        contentStream.endText();
        
        yPosition -= LINE_HEIGHT * 2;
        
        // Separator line
        contentStream.moveTo(MARGIN, yPosition);
        contentStream.lineTo(page.getMediaBox().getWidth() - MARGIN, yPosition);
        contentStream.stroke();
        
        yPosition -= LINE_HEIGHT;
        
        return yPosition;
    }
    
    /**
     * Writes the main report content, handling page breaks.
     */
    private static float writeContent(PDDocument document, PDPageContentStream contentStream,
                                     PDPage currentPage, List<String> lines, float yPosition) 
            throws IOException {
        
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER), FONT_SIZE_NORMAL);
        
        for (String line : lines) {
            // Check if we need a new page
            if (yPosition < MARGIN + LINE_HEIGHT) {
                contentStream.close();
                
                currentPage = new PDPage(PDRectangle.A4);
                document.addPage(currentPage);
                
                contentStream = new PDPageContentStream(document, currentPage);
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER), FONT_SIZE_NORMAL);
                
                yPosition = currentPage.getMediaBox().getHeight() - MARGIN;
            }
            
            // Write line
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText(line);
            contentStream.endText();
            
            yPosition -= LINE_HEIGHT;
        }
        
        return yPosition;
    }
    
    /**
     * Splits text content into lines suitable for PDF rendering.
     */
    private static List<String> splitIntoLines(String content) {
        List<String> lines = new ArrayList<>();
        String[] rawLines = content.split("\n");
        
        for (String rawLine : rawLines) {
            if (rawLine.length() <= MAX_CHARS_PER_LINE) {
                lines.add(rawLine);
            } else {
                // Wrap long lines
                int start = 0;
                while (start < rawLine.length()) {
                    int end = Math.min(start + MAX_CHARS_PER_LINE, rawLine.length());
                    lines.add(rawLine.substring(start, end));
                    start = end;
                }
            }
        }
        
        return lines;
    }
    
    /**
     * Validates that the output file path is writable.
     */
    public static boolean validateOutputPath(File outputFile) {
        try {
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                return parentDir.mkdirs();
            }
            return true;
        } catch (SecurityException e) {
            logger.log(Level.WARNING, "Cannot write to path: " + outputFile.getAbsolutePath(), e);
            return false;
        }
    }
}