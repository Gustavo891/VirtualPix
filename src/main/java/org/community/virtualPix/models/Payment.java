package org.community.virtualPix.models;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter @Setter
public class Payment {
    private final String paymentId;
    private String status;
    private final String playerName;
    private final UUID playerUUID;
    private final double amount;
    private final long timestamp;
    private final String qrCode;
    private final String qrCodeBase64;

    public Payment(String paymentId, String status, Player player, double amount,
                   String qrCode, String qrCodeBase64) {
        this.paymentId = Objects.requireNonNull(paymentId, "paymentId não pode ser nulo");
        this.status = Objects.requireNonNull(status, "status não pode ser nulo");
        this.playerName = Objects.requireNonNull(player.getName(), "playerName não pode ser nulo");
        this.playerUUID = Objects.requireNonNull(player.getUniqueId(), "playerUUID não pode ser nulo");
        this.amount = amount;
        this.timestamp = Instant.now().getEpochSecond();
        this.qrCode = Objects.requireNonNull(qrCode, "qrCode não pode ser nulo");
        this.qrCodeBase64 = Objects.requireNonNull(qrCodeBase64, "qrCodeBase64 não pode ser nulo");
    }

    public Payment(Document doc) {
        this.paymentId = doc.getString("id-pagamento");
        this.status = doc.getString("status");
        this.playerName = doc.getString("nome-jogador");
        this.playerUUID = UUID.fromString(doc.getString("uuid-jogador"));
        this.amount = doc.getDouble("valor");
        this.timestamp = doc.getLong("timestamp");
        this.qrCode = doc.getString("qr_code");
        this.qrCodeBase64 = doc.getString("qr_code_base64");
    }

    public Document toDocument() {
        return new Document()
                .append("id-pagamento", paymentId)
                .append("status", status)
                .append("nome-jogador", playerName)
                .append("uuid-jogador", playerUUID.toString())
                .append("valor", amount)
                .append("timestamp", timestamp)
                .append("qr_code", qrCode)
                .append("qr_code_base64", qrCodeBase64);
    }

    // Getters e Setters
    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPlayerName() { return playerName; }
    public UUID getPlayerUUID() { return playerUUID; }
    public double getAmount() { return amount; }
    public long getTimestamp() { return timestamp; }
    public String getQrCode() { return qrCode; }
    public String getQrCodeBase64() { return qrCodeBase64; }
}