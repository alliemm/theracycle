package com.theracycle.services;

import com.theracycle.models.MoodEntry;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GeminiService is the AI implementation of AIService
 *  - Builds a structured clinical prompt from patient entries
 *  - Call the Gemini API
 *  - Parse the response safely
 *  - Return a fallback string if the API is unavailable
 * AI ROLE: Gemini distills raw journal text into thematic clinical language
 * The Java layer handles all data storage, validation, flagging logic, and
 * report structuring — AI only augments the final summary
 */
public class GeminiService implements AIService {
    private static final String API_KEY = "AIzaSyC2uUbQhgP3Y4UQVKTI3wB2e3TvXUyC7X8";
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent";
    private static final int TIMEOUT_SECONDS = 15;
    @Override
    public String generateSummary(Collection<MoodEntry> entries) {
        if (entries.isEmpty()) {
            return "No patient data available for AI analysis.";
        }
        String prompt = buildPrompt(entries);
        String jsonPayload = buildJsonPayload(prompt);
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("X-goog-api-key", API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return parseGeminiResponse(response.body());
            } else {
                return "AI service returned status " + response.statusCode()
                        + ". Using Java-generated summary only.";
            }
        } catch (Exception e) {
            return "AI service unavailable (" + e.getClass().getSimpleName() + "). "
                    + "Review the Java-generated log above for clinical data.";
        }
    }
    // Builds a structured clinical prompt — keeping the AI focused on
    // thematic analysis, not raw data (which Java already handles)
    private String buildPrompt(Collection<MoodEntry> entries) {
        List<MoodEntry> concerning = entries.stream()
                .filter(MoodEntry::isConcerning)
                .collect(Collectors.toList());
        String allNotes = entries.stream()
                .map(e -> "Score " + e.getScore() + ": " + e.getNote())
                .collect(Collectors.joining("\n"));
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a clinical assistant supporting a licensed therapist. ");
        prompt.append("A patient submitted the following mood logs this week. ");
        prompt.append("Provide a concise, professional clinical summary (3-5 sentences) ");
        prompt.append("identifying emotional themes and any areas of concern. ");
        prompt.append("Do NOT diagnose. Do NOT use the patient's name.\n\n");
        prompt.append("PATIENT LOGS:\n").append(allNotes);
        if (!concerning.isEmpty()) {
            prompt.append("\n\nNOTE: ").append(concerning.size())
                    .append(" entries were flagged as low mood (score ≤ 3). ");
            prompt.append("Please address these in your summary.");
        }
        return prompt.toString();
    }
    // Builds the Gemini REST API JSON request body
    private String buildJsonPayload(String prompt) {
        // Escape quotes and newlines for safe JSON embedding
        String escaped = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");

        return "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + escaped + "\" }] }] }";
    }
    // Safely extracts the text response from Gemini's JSON without external libraries
    // Falls back gracefully if the response format changes
    private String parseGeminiResponse(String rawBody) {
        try {
            // Gemini response: candidates[0].content.parts[0].text
            String marker = "\"text\": \"";
            int start = rawBody.indexOf(marker);
            if (start == -1) return "AI response received but could not be parsed.";
            start += marker.length();
            // Find closing quote - skip escaped quotes
            int end = start;
            while (end < rawBody.length()) {
                if (rawBody.charAt(end) == '"' && rawBody.charAt(end - 1) != '\\') break;
                end++;
            }
            String text = rawBody.substring(start, end)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");
            return text.isBlank() ? "AI returned an empty response." : text;
        } catch (Exception e) {
            return "AI response parsing failed: " + e.getMessage();
        }
    }
}
