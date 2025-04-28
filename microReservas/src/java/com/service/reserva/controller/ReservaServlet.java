/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.service.reserva.controller;


import com.service.reserva.bd.PostgresConnection;
import com.service.reserva.model.Reserva;
import com.service.reserva.model.Factura;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ACER
 */
@WebServlet(name = "ReservaServlet", urlPatterns = {"/reservas"})
public class ReservaServlet extends HttpServlet{
    
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        Reserva reserva = gson.fromJson(reader, Reserva.class);

        // Validar que pague al menos el 50%
        if (reserva.getMontoReservado() <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Debe pagar al menos el 50% del valor de la habitación\"}");
            return;
        }

        try (Connection conn = PostgresConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction

            // Insertar la reserva
            String insertReserva = "INSERT INTO reservas (id_cliente, id_habitacion, fecha_inicio, fecha_fin, monto_reservado, estado) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
            PreparedStatement psReserva = conn.prepareStatement(insertReserva);
            psReserva.setString(1, reserva.getIdCliente());
            psReserva.setString(2, reserva.getIdHabitacion());
            psReserva.setDate(3, new java.sql.Date(reserva.getFechaInicio().getTime()));
            psReserva.setDate(4, new java.sql.Date(reserva.getFechaFin().getTime()));
            psReserva.setDouble(5, reserva.getMontoReservado());
            psReserva.setString(6, "PENDIENTE");

            ResultSet rsReserva = psReserva.executeQuery();
            int reservaId = 0;
            if (rsReserva.next()) {
                reservaId = rsReserva.getInt(1);
            }

            // Insertar la factura
            String insertFactura = "INSERT INTO facturas (id_reserva, monto_total, monto_pagado, fecha_pago) VALUES (?, ?, ?, ?)";
            PreparedStatement psFactura = conn.prepareStatement(insertFactura);
            psFactura.setInt(1, reservaId);
            psFactura.setDouble(2, reserva.getMontoReservado() * 2); // Asumimos monto_total = monto_pagado * 2
            psFactura.setDouble(3, reserva.getMontoReservado());
            psFactura.setDate(4, new java.sql.Date(new java.util.Date().getTime()));
            psFactura.executeUpdate();

            conn.commit(); // Commit transaction

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"message\": \"Reserva y factura creadas correctamente\"}");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String buscarHabitaciones = request.getParameter("habitaciones");
        String idCliente = request.getParameter("id_cliente");
        String idHabitacion = request.getParameter("id_habitacion");
        
            if (buscarHabitaciones != null) {
        // Simulamos habitaciones disponibles
        List<String> habitaciones = new ArrayList<>();
        habitaciones.add("Habitación 101 - Deluxe - $500");
        habitaciones.add("Habitación 102 - Estándar - $300");
        habitaciones.add("Habitación 201 - Suite - $700");

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(habitaciones));
        out.flush();
        return;
    }

        if (idCliente == null && idHabitacion == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Debe proporcionar id_cliente o id_habitacion\"}");
            return;
        }

        List<Reserva> reservas = new ArrayList<>();

        try (Connection conn = PostgresConnection.getConnection()) {
            String query = "SELECT * FROM reservas WHERE ";
            if (idCliente != null) {
                query += "id_cliente = ?";
            } else {
                query += "id_habitacion = ?";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, idCliente != null ? idCliente : idHabitacion);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setId(rs.getInt("id"));
                reserva.setIdCliente(rs.getString("id_cliente"));
                reserva.setIdHabitacion(rs.getString("id_habitacion"));
                reserva.setFechaInicio(rs.getDate("fecha_inicio"));
                reserva.setFechaFin(rs.getDate("fecha_fin"));
                reserva.setMontoReservado(rs.getDouble("monto_reservado"));
                reserva.setEstado(rs.getString("estado"));
                reservas.add(reserva);
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(reservas));
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String action = request.getParameter("action");
    String idReservaParam = request.getParameter("id_reserva");

    if (action == null || idReservaParam == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\":\"Se requiere action y id_reserva\"}");
        return;
    }

    int idReserva = Integer.parseInt(idReservaParam);

    try (Connection conn = PostgresConnection.getConnection()) {
        String updateReserva = "";

        if (action.equals("confirmar")) {
            updateReserva = "UPDATE reservas SET estado = 'CONFIRMADA' WHERE id = ?";
            // (Simular notificación: podrías en el futuro integrar un servicio de correos o WhatsApp aquí)
        } else if (action.equals("cancelar")) {
            updateReserva = "UPDATE reservas SET estado = 'CANCELADA' WHERE id = ?";
            // (Simular reembolso: aquí podrías marcar la factura como "reembolsada" en otro campo si quieres)
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Acción no válida\"}");
            return;
        }

        PreparedStatement ps = conn.prepareStatement(updateReserva);
        ps.setInt(1, idReserva);
        int rows = ps.executeUpdate();

        if (rows > 0) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Reserva actualizada correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Reserva no encontrada\"}");
        }

    } catch (Exception e) {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
    }
}
}
