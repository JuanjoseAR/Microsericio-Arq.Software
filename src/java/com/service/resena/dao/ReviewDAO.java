/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.service.resena.dao;

/**
 *
 * @author ACER
 */


import com.service.resena.bd.MongoBDConnection;
import com.service.resena.dto.ReviewDTO;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class ReviewDAO {
    
    private final MongoCollection<Document> reviewCollection;

    public ReviewDAO() {
        MongoDatabase db = MongoBDConnection.getDatabase();
        this.reviewCollection = db.getCollection("reviews");
    }

    public void createReview(ReviewDTO reviewDTO) {
        Document doc = new Document("roomId", reviewDTO.getRoomId())
                .append("clientId", reviewDTO.getClientId())
                .append("rating", reviewDTO.getRating())
                .append("comment", reviewDTO.getComment())
                .append("date", new Date());

        reviewCollection.insertOne(doc);
    }

    public List<ReviewDTO> getReviews(String roomId, Integer minRating) {
        List<Bson> filters = new ArrayList<>();
        filters.add(Filters.eq("roomId", roomId));
        if (minRating != null) {
            filters.add(Filters.gte("rating", minRating));
        }

        MongoCursor<Document> cursor = reviewCollection.find(Filters.and(filters))
                                                       .sort(Sorts.descending("date"))
                                                       .iterator();

        List<ReviewDTO> reviews = new ArrayList<>();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            ReviewDTO dto = new ReviewDTO();
            dto.setId(doc.getObjectId("_id").toHexString());
            dto.setRoomId(doc.getString("roomId"));
            dto.setClientId(doc.getString("clientId"));
            dto.setRating(doc.getInteger("rating"));
            dto.setComment(doc.getString("comment"));
            dto.setDate(doc.getDate("date"));
            reviews.add(dto);
        }

        return reviews;
    }
}
