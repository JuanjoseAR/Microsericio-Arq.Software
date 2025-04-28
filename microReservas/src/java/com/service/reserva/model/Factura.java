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
public class Factura {
        private int id;
    private int idReserva;
    private double montoTotal;
    private double montoPagado;
    private Date fechaPago;

    // Constructor vacío
    public Factura() {}

    // Constructor completo
    public Factura(int id, int idReserva, double montoTotal, double montoPagado, Date fechaPago) {
        this.id = id;
        this.idReserva = idReserva;
        this.montoTotal = montoTotal;
        this.montoPagado = montoPagado;
        this.fechaPago = fechaPago;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }
}
