/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.service.resena.bd;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
/**
 *
 * @author ACER
 */
public class MongoBDConnection {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017/"; // Cambia si usas otra IP o puerto
    private static final String DATABASE_NAME = "servicio_habitaciones"; // Nombre de tu base de datos
    private static MongoClient mongoClient = null;

    // Método para obtener la conexión
    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
        }
        return mongoClient.getDatabase(DATABASE_NAME);
    }

    // Método para cerrar la conexión (opcional, depende de cómo administres tu servidor)
    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}
