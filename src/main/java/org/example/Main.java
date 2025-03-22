package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class Main {

    private static final String OLLAMA_URL = "http://localhost:11434/api/chat";  // URL do Ollama
    private static final String MODEL_NAME = "deepseek-coder:6.7b";  // Nome do modelo
    private static final String OUTPUT_TXT = "resposta_deepseek.txt";  // Arquivo de saída

    private static String lerArquivo(String caminho) {
        // Função para ler o conteúdo de um arquivo
        try (BufferedReader reader = new BufferedReader(new FileReader(caminho))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static void salvarTextoNoArquivo(String nomeArquivo, String conteudo) {
        try {
            Files.writeString(Path.of(nomeArquivo), conteudo, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Arquivo salvo: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo: " + nomeArquivo);
            e.printStackTrace();
        }
    }

    private static void enviarParaDeepSeek(Map<String, String> arquivos, String pergunta) {
        StringBuilder contextoArquivos = new StringBuilder();

        for (Map.Entry<String, String> entry : arquivos.entrySet()) {
            String nome = entry.getKey();
            String caminho = entry.getValue();
            String conteudo = lerArquivo(caminho);

            contextoArquivos.append("\n### Arquivo: ").append(nome).append("\n```java\n").append(conteudo).append("\n```\n");
        }

        String mensagemUsuario = "Aqui estão alguns arquivos de código para análise:\n"
                + contextoArquivos
                + "\nMinha pergunta é: " + pergunta;

        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("model", MODEL_NAME);

            JsonArray messages = new JsonArray();
            JsonObject userMessage = new JsonObject();
            userMessage.addProperty("role", "user");
            userMessage.addProperty("content", mensagemUsuario);
            messages.add(userMessage);

            payload.add("messages", messages);
            payload.addProperty("stream", false);

            // Converter para JSON String
            Gson gson = new Gson();
            String jsonPayload = gson.toJson(payload);

            // Enviar requisição
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Recebe resposta
            int status = conn.getResponseCode();
            if (status == 200) {
                String respostaJson;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder responseBuilder = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        responseBuilder.append(responseLine.trim());
                    }
                    respostaJson = responseBuilder.toString();
                }

                // Salva a resposta completa como JSON (opcional)
                salvarTextoNoArquivo("resposta_completa.json", respostaJson);

                // Extrai só o "content" e salva como txt
                JsonObject jsonObject = JsonParser.parseString(respostaJson).getAsJsonObject();
                String conteudoResposta = jsonObject.getAsJsonObject("message").get("content").getAsString();

                salvarTextoNoArquivo(OUTPUT_TXT, conteudoResposta);
                System.out.println("Resposta salva em '" + OUTPUT_TXT + "'");

            } else {
                System.err.println("Erro na requisição: HTTP " + status);
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Exemplo de uso
        Map<String, String> arquivos = new HashMap<>();
        arquivos.put("Main.java", "C:/Users/Cauet/OneDrive/Área de Trabalho/Estudos Java/Codigos de estudo/Teste/src/main/java/org/example/Main.java");

        String pergunta = "Explique o que essa aplicação faz e sugira melhorias no código.";

        enviarParaDeepSeek(arquivos, pergunta);
    }
}
