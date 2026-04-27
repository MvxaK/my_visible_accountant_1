package org.cook.extracter.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.cook.extracter.model.ExtractedReceipt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReceiptService {

    private static final String TEMP_DIR = "/tmp/extracter";

    private static final Pattern TOTAL_KEYWORD_PATTERN = Pattern.compile(
            "(?i)(итого|итог|жиын|total|к\\s*оплате|сумма|sum|amount|всего)" +
                    "[^\\d]{0,20}(\\d[\\d\\s'.,]*)",
            Pattern.UNICODE_CASE
    );

    private static final Pattern CURRENCY_PATTERN = Pattern.compile(
            "(?:₸|KZT|тнг|тг)\\s*(\\d[\\d\\s'.,]{0,15})" +
                    "|(\\d[\\d\\s'.,]{0,15})\\s*(?:₸|KZT|тнг|тг)",
            Pattern.UNICODE_CASE
    );

    private static final Pattern STANDALONE_AMOUNT = Pattern.compile(
            "^\\s*(\\d{3,}[\\d\\s'.,]*)\\s*$",
            Pattern.MULTILINE
    );

    public ExtractedReceipt processReceipt(MultipartFile file) throws Exception {
        String text = extractText(file);
        return parseReceipt(text);
    }

    private String extractText(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) filename = "";

        if (filename.toLowerCase().endsWith(".pdf")) {
            try (PDDocument document = Loader.loadPDF(file.getBytes())) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document).trim();
                if (!text.isEmpty()) return text;
                return ocrPdf(file);
            }
        } else {
            return ocrImage(file);
        }
    }

    private String ocrPdf(MultipartFile file) throws Exception {
        StringBuilder result = new StringBuilder();
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFRenderer renderer = new PDFRenderer(document);
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 300);
                String fileName = "pdf_page_" + System.currentTimeMillis() + "_" + i + ".png";
                File tempFile = new File(TEMP_DIR, fileName);

                saveImage(image, tempFile);

                result.append(runTesseract(tempFile.getName())).append("\n");
                tempFile.delete();
            }
        }
        return result.toString();
    }

    private String ocrImage(MultipartFile file) throws Exception {
        BufferedImage image = ImageIO.read(file.getInputStream());
        String fileName = "img_" + System.currentTimeMillis() + ".png";
        File tempFile = new File(TEMP_DIR, fileName);

        saveImage(image, tempFile);

        try {
            return runTesseract(tempFile.getName());
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private void saveImage(BufferedImage image, File file) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        boolean wrote = ImageIO.write(image, "png", file);
        if (!wrote) {
            throw new IOException("Could not write image to file: " + file.getAbsolutePath());
        }
    }

    private String runTesseract(String filename) throws Exception {
        //"docker", "exec", "tesseract_ocr",
        ProcessBuilder pb = new ProcessBuilder(
                "tesseract",
                TEMP_DIR + "/" + filename,
                "stdout",
                "-l", "rus+eng",
                "--psm", "6"
        );
        pb.redirectErrorStream(true);

        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Tesseract failed (Code " + exitCode + "): " + result.toString());
            }
            return result.toString();
        }
    }


    public ExtractedReceipt parseReceipt(String text) {
        if (text == null || text.isEmpty())
            return new ExtractedReceipt(0.0, "KZT", text);

        String normalized = normalizeText(text);
        Double amount = findByKeyword(normalized);
        if (amount == null) amount = findByCurrency(normalized);
        if (amount == null) amount = findMaxStandaloneAmount(normalized);

        return new ExtractedReceipt(amount != null ? amount : 0.0, "KZT", text);
    }

    private String normalizeText(String text) {
        return text.replaceAll("(\\d)'(\\d)", "$1$2")
                .replaceAll("\r\n", "\n")
                .replaceAll("\r", "\n");
    }

    private Double findByKeyword(String text) {
        Matcher matcher = TOTAL_KEYWORD_PATTERN.matcher(text);
        Double result = null;
        while (matcher.find()) {
            Double val = parseAmount(matcher.group(2));
            if (val != null && (result == null || val > result)) result = val;
        }
        return result;
    }

    private Double findByCurrency(String text) {
        Matcher matcher = CURRENCY_PATTERN.matcher(text);
        Double result = null;
        while (matcher.find()) {
            String raw = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            Double val = parseAmount(raw);
            if (val != null && (result == null || val > result)) result = val;
        }
        return result;
    }

    private Double findMaxStandaloneAmount(String text) {
        Matcher matcher = STANDALONE_AMOUNT.matcher(text);
        Double max = null;
        while (matcher.find()) {
            Double val = parseAmount(matcher.group(1));
            if (val != null && val > 10 && (max == null || val > max)) max = val;
        }
        return max;
    }

    private Double parseAmount(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String clean = raw.trim().replaceAll("\\s+", "");
        if (clean.matches(".*\\d[.,]\\d{3}[.,].*") || clean.matches(".*\\d\\.\\d{3},\\d{2}"))
            clean = clean.replace(".", "").replace(",", ".");
        else if (clean.matches(".*\\d,\\d{3}\\.\\d{2}"))
            clean = clean.replace(",", "");
        else
            clean = clean.replace(",", ".");

        clean = clean.replaceAll("[^\\d.]", "");
        int lastDot = clean.lastIndexOf('.');
        if (lastDot != -1)
            clean = clean.substring(0, lastDot).replace(".", "") + "." + clean.substring(lastDot + 1);

        if (clean.isEmpty()) return null;
        try {
            double val = Double.parseDouble(clean);
            return (val <= 0 || val > 99_999_999) ? null : val;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}