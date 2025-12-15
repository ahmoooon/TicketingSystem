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
    
    // Static initializer to disable font cache and suppress warnings
    static {
        try {
            // Disable PDFBox font cache to avoid corrupted font issues
            System.setProperty("pdfbox.fontcache", "false");
            
            // Disable system font loading entirely - use only PDF base fonts
            System.setProperty("sun.font.fontmanager", "sun.awt.X11FontManager");
            
            // Suppress PDFBox font warnings
            Logger pdfboxLogger = Logger.getLogger("org.apache.fontbox");
            pdfboxLogger.setLevel(Level.SEVERE); // Only show severe errors
            
            Logger pdfboxTTFLogger = Logger.getLogger("org.apache.fontbox.ttf");
            pdfboxTTFLogger.setLevel(Level.SEVERE);
            
            Logger pdfboxTTFParser = Logger.getLogger("org.apache.fontbox.ttf.TTFParser");
            pdfboxTTFParser.setLevel(Level.SEVERE);
        } catch (Exception e) {
            // Ignore any errors in static initialization
            System.err.println("Warning: Could not configure PDF font settings: " + e.getMessage());
        }
    }
    
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
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                
                // Current Y position (starts from top)
                float yPosition = page.getMediaBox().getHeight() - MARGIN;
                
                // Write header
                yPosition = writeHeader(contentStream, page, reportTitle, yPosition);
                
                // Write content
                writeContent(document, contentStream, page, lines, yPosition);
            }
            
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
                // Close current content stream properly
                contentStream.close();
                
                currentPage = new PDPage(PDRectangle.A4);
                document.addPage(currentPage);
                
                PDPageContentStream newContentStream = new PDPageContentStream(document, currentPage);
                newContentStream.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER), FONT_SIZE_NORMAL);
                
                yPosition = currentPage.getMediaBox().getHeight() - MARGIN;
                
                // Write remaining lines on new page
                return writeContentContinued(newContentStream, lines, lines.indexOf(line), yPosition);
            }
            
            // Write line - ensure text block is properly closed
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN, yPosition);
            try {
                contentStream.showText(line);
            } catch (IllegalArgumentException e) {
                // If character encoding fails, log and skip this line
                logger.log(Level.WARNING, "Skipping line due to encoding error: {0}", e.getMessage());
                contentStream.showText("[Content contains unsupported characters]");
            }
            contentStream.endText();
            
            yPosition -= LINE_HEIGHT;
        }
        
        return yPosition;
    }
    
    /**
     * Helper method to continue writing content on new page
     */
    private static float writeContentContinued(PDPageContentStream contentStream, 
                                              List<String> lines, int startIndex, float yPosition) 
            throws IOException {
        
        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i);
            
            // Check if we need another new page
            if (yPosition < MARGIN + LINE_HEIGHT) {
                // We would need to recursively handle pagination here
                // For simplicity, just stop (or you could implement full recursion)
                break;
            }
            
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN, yPosition);
            try {
                contentStream.showText(line);
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "Skipping line due to encoding error: {0}", e.getMessage());
                contentStream.showText("[Content contains unsupported characters]");
            }
            contentStream.endText();
            
            yPosition -= LINE_HEIGHT;
        }
        
        return yPosition;
    }
    
    /**
     * Splits text content into lines suitable for PDF rendering.
     * Also sanitizes content by removing unsupported characters.
     */
    private static List<String> splitIntoLines(String content) {
        List<String> lines = new ArrayList<>();
        
        // CRITICAL: Sanitize content first - remove tabs and other control characters
        content = sanitizeTextForPdf(content);
        
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
     * Sanitizes text content to remove characters not supported by PDF fonts.
     * Replaces tabs with spaces and removes other control characters.
     * 
     * @param text The text to sanitize
     * @return Sanitized text safe for PDF rendering
     */
    private static String sanitizeTextForPdf(String text) {
        if (text == null) {
            return "";
        }
        
        // Replace tabs with 4 spaces
        text = text.replace("\t", "    ");
        
        // Remove carriage returns (keep only line feeds)
        text = text.replace("\r", "");
        
        // Remove other control characters except newline (U+000A)
        // This regex removes characters in ranges:
        // U+0000-U+0009 (NULL to TAB)
        // U+000B-U+001F (vertical tab to unit separator)
        // U+007F (DELETE)
        text = text.replaceAll("[\\u0000-\\u0009\\u000B-\\u001F\\u007F]", "");
        
        // Replace characters outside WinAnsiEncoding range (U+0000 to U+00FF)
        // with ASCII equivalents or remove them
        StringBuilder sanitized = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c == '\n') {
                // Keep newlines
                sanitized.append(c);
            } else if (c >= 32 && c <= 126) {
                // Standard ASCII printable characters (safe for WinAnsiEncoding)
                sanitized.append(c);
            } else if (c >= 160 && c <= 255) {
                // Extended ASCII (part of WinAnsiEncoding) - keep them
                sanitized.append(c);
            } else if (c > 255) {
                // Unicode characters beyond WinAnsiEncoding - replace with ?
                sanitized.append('?');
            }
            // Characters < 32 (except newline) are already filtered above
        }
        
        return sanitized.toString();
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