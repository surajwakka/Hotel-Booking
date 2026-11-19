package com.staywise.hotel.model;
import jakarta.persistence.*;
@Entity public class Hotel {@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;public String name,city,country;@Column(length=1200) public String description;public String imageUrl;public double pricePerNight,rating;public int availableRooms;public Hotel(){}public Hotel(String n,String c,String co,String d,String i,double p,double r,int rooms){name=n;city=c;country=co;description=d;imageUrl=i;pricePerNight=p;rating=r;availableRooms=rooms;}}
