/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.service.resena.servelet;
import com.service.resena.model.Review;
import com.service.resena.bd.MongoBDConnection;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.conversions.Bson;

/**
 *
 * @author ACER
 */
@WebServlet(name = "ReviewServlet", urlPatterns = {"/reviews"})
public class ReviewServelet extends HttpServlet{

  
  
    private MongoCollection<Document> reviewCollection;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        MongoDatabase database = MongoBDConnection.getDatabase();
        reviewCollection = database.getCollection("reviews"); // Colección 'reviews'
    }

    // Crear una reseña (POST)
    @Override
   
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        Review review = gson.fromJson(reader, Review.class);

        Document document = new Document("roomId", review.getRoomId())
                .append("clientId", review.getClientId())
                .append("rating", review.getRating())
                .append("comment", review.getComment())
                .append("date", new Date());

        reviewCollection.insertOne(document);
        response.getWriter().write("holaaaa");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_CREATED);
        PrintWriter out = response.getWriter();
        out.print("{\"message\": \"Review created successfully\"}");
        out.flush();
    }

    // Obtener reseñas (GET)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String roomId = request.getParameter("roomId");
        String minRatingParam = request.getParameter("minRating");

        if (roomId == null || roomId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"roomId parameter is required\"}");
            return;
        }

        List<Bson> filters = new ArrayList<>();  // Cambiar Document a Bson
filters.add(Filters.eq("roomId", roomId)); // Filtro obligatorio por roomId

if (minRatingParam != null) {
    try {
        int minRating = Integer.parseInt(minRatingParam);
        filters.add(Filters.gte("rating", minRating)); // Filtrar por calificación mínima
    } catch (NumberFormatException e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\": \"minRating must be an integer\"}");
        return;
    }
}

// Obtener las reseñas desde la base de datos
List<Review> reviews = new ArrayList<>();
MongoCursor<Document> cursor = reviewCollection.find(Filters.and(filters))
                                              .sort(Sorts.descending("date"))
                                              .iterator();

//        .forEach(doc -> {
//            Review review = new Review();
//            review.setId(doc.getObjectId("_id"));
//            review.setRoomId(doc.getString("roomId"));
//            review.setClientId(doc.getString("clientId"));
//            review.setRating(doc.getInteger("rating"));
//            review.setComment(doc.getString("comment"));
//            review.setDate(doc.getDate("date"));
//            reviews.add(review);
//        });

while (cursor.hasNext()) {
    Document doc = cursor.next(); // Obtiene el siguiente documento

    // Crea el objeto Review y asigna los valores
    Review review = new Review();
    review.setId(doc.getObjectId("_id"));
    review.setRoomId(doc.getString("roomId"));
    review.setClientId(doc.getString("clientId"));
    review.setRating(doc.getInteger("rating"));
    review.setComment(doc.getString("comment"));
    review.setDate(doc.getDate("date"));

    // Agrega el objeto Review a la lista
    reviews.add(review);
}

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(reviews));
        out.flush();
    }
}
