package com.staywise.hotel.model;
import jakarta.persistence.*;import java.time.LocalDate;
@Entity public class Booking {@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;@ManyToOne(optional=false) public Hotel hotel;public String guestName,email;public int guests;public LocalDate checkIn,checkOut;public double totalPrice;public String status="CONFIRMED";public Booking(){}}
