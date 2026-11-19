package com.staywise.hotel.repository;
import com.staywise.hotel.model.Hotel;import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;
public interface HotelRepository extends JpaRepository<Hotel,Long>{List<Hotel> findByCityContainingIgnoreCase(String city);}
