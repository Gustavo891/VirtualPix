package org.community.virtualPix.controllers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginLogger;

import java.util.logging.Logger;

public class MongoController {
    private MongoClient mongoClient;
    private MongoCollection<Document> paymentsCollection;
    private Logger logger;

    public MongoController(String connectionString, Logger logger) {
        this.logger = logger;
        try {
            this.mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase("minecraft_payments");
            this.paymentsCollection = database.getCollection("pix_payments");
            logger.info("Conectado ao MongoDB com sucesso!");
        } catch (Exception e) {
            logger.severe("Erro ao conectar ao MongoDB: " + e.getMessage());
            throw new RuntimeException("Falha na conexão com MongoDB", e);
        }
    }

    public void savePayment(Document paymentDoc) {
        paymentsCollection.insertOne(paymentDoc);
    }

    public void updatePaymentStatus(String paymentId, String newStatus) {
        paymentsCollection.updateOne(
                new Document("id-pagamento", paymentId),
                new Document("$set", new Document("status", newStatus))
        );
    }

    public Iterable<Document> getPendingPayments() {
        return paymentsCollection.find(new Document("status", "pending"));
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}