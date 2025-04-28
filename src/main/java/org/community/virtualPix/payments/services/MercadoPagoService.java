
package org.community.virtualPix.payments.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.community.virtualPix.models.Payment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class MercadoPagoService {
    private final String accessToken;
    private final HttpClient httpClient;
    private final Gson gson;
    private final Logger logger;

    public MercadoPagoService(String accessToken, Logger logger) {
        this.accessToken = accessToken;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.logger = logger;
    }

    public Payment createPixPayment(double amount, String description, String playerEmail, Player player) throws Exception {
        try {
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("description", description);
            paymentData.put("external_reference", UUID.randomUUID().toString());
            paymentData.put("transaction_amount", amount); // Mantém como double
            paymentData.put("payment_method_id", "pix");

            Map<String, Object> payer = new HashMap<>();
            payer.put("email", playerEmail);
            paymentData.put("payer", payer);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mercadopago.com/v1/payments"))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .header("X-Idempotency-Key", UUID.randomUUID().toString())
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(paymentData)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201) {
                logger.warning("Erro na API do Mercado Pago: " + response.body());
                throw new RuntimeException("Falha ao criar pagamento: " + response.body());
            }

            // Processamento seguro da resposta
            return parseApiResponse(response.body(), player);

        } catch (Exception e) {
            logger.severe("Erro na comunicação com Mercado Pago: " + e.getMessage());
            throw e;
        }
    }

    private Payment parseApiResponse(String jsonResponse, Player player) throws Exception {
        try {
            System.out.println("Resposta completa do Mercado Pago:\n" + jsonResponse);

            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

            // Extrair campos obrigatórios com verificação
            String id = jsonObject.has("id") ? jsonObject.get("id").getAsString() : "unknown";
            double value = jsonObject.has("transaction_amount") ? jsonObject.get("transaction_amount").getAsDouble() : 0.0;

            // Extrair campos aninhados do PIX com verificação
            String qr_code = "";
            String qr_code_64 = "";

            if (jsonObject.has("point_of_interaction")) {
                JsonObject poi = jsonObject.getAsJsonObject("point_of_interaction");
                if (poi.has("transaction_data")) {
                    JsonObject transactionData = poi.getAsJsonObject("transaction_data");
                    qr_code = transactionData.has("qr_code") ? transactionData.get("qr_code").getAsString() : "";
                    qr_code_64 = transactionData.has("qr_code_base64") ? transactionData.get("qr_code_base64").getAsString() : "";
                }
            }

            System.out.println("ID: " + id);
            System.out.println("QR Code: " + qr_code);
            System.out.println("QR Code Base64: " + (qr_code_64.length() > 20 ? qr_code_64.substring(0, 20) + "..." : qr_code_64));

            return new Payment(id, "pending", player, value, qr_code, qr_code_64);
        } catch (Exception e) {
            logger.severe("Erro ao analisar resposta do Mercado Pago: " + e.getMessage());
            throw new Exception("Falha ao processar resposta da API: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> checkPaymentStatus(String paymentId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mercadopago.com/v1/payments/" + paymentId))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta: " + response.body());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha ao verificar pagamento: " + response.body());
        }

        return gson.fromJson(response.body(), Map.class);
    }
}