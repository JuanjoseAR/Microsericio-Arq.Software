/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.service.reserva.model;
 
import java.util.Date;
/**
 *
 * @author ACER
 */
public class Reserva {
     private int id;
    private String idCliente;
    private String idHabitacion;
    private Date fechaInicio;
    private Date fechaFin;
    private double montoReservado;
    private String estado; // PENDIENTE, CONFIRMADA, CANCELADA

    // Constructor vacío
    public Reserva() {}

    // Constructor completo
    public Reserva(int id, String idCliente, String idHabitacion, Date fechaInicio, Date fechaFin, double montoReservado, String estado) {
        this.id = id;
        this.idCliente = idCliente;
        this.idHabitacion = idHabitacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.montoReservado = montoReservado;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(String idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public double getMontoReservado() {
        return montoReservado;
    }

    public void setMontoReservado(double montoReservado) {
        this.montoReservado = montoReservado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
