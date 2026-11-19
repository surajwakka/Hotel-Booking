
package com.staywise.hotel.controller;

import com.staywise.hotel.model.*;
import com.staywise.hotel.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final HotelRepository hotels;
    private final BookingRepository bookings;
    public AdminController(HotelRepository hotels, BookingRepository bookings) {
        this.hotels = hotels; this.bookings = bookings;
    }

    public record HotelRequest(String name, String city, String country, String description,
                               String imageUrl, double pricePerNight, double rating, int availableRooms) {}
    public record BookingStatusRequest(String status) {}

    @GetMapping("/dashboard")
    public Map<String,Object> dashboard() {
        List<Booking> all = bookings.findAll();
        double value = all.stream().filter(b -> !"CANCELLED".equalsIgnoreCase(b.status))
                .mapToDouble(b -> b.totalPrice).sum();
        long active = all.stream().filter(b -> !"CANCELLED".equalsIgnoreCase(b.status)).count();
        return Map.of("hotelCount", hotels.count(), "bookingCount", all.size(),
                "activeBookings", active, "cancelledBookings", all.size()-active,
                "bookingValue", value, "guestCount", all.stream().map(b -> b.email.toLowerCase()).distinct().count());
    }

    @GetMapping("/hotels")
    public List<Hotel> allHotels() { return hotels.findAll(); }

    @PostMapping("/hotels")
    public ResponseEntity<?> createHotel(@RequestBody HotelRequest q) {
        ResponseEntity<?> invalid = validate(q); if (invalid != null) return invalid;
        Hotel h = new Hotel(q.name().trim(), q.city().trim(), q.country().trim(),
                q.description(), q.imageUrl(), q.pricePerNight(), q.rating(), q.availableRooms());
        return ResponseEntity.status(HttpStatus.CREATED).body(hotels.save(h));
    }

    @PutMapping("/hotels/{id}")
    public ResponseEntity<?> updateHotel(@PathVariable Long id, @RequestBody HotelRequest q) {
        ResponseEntity<?> invalid = validate(q); if (invalid != null) return invalid;
        return hotels.findById(id).map(h -> {
            h.name=q.name().trim(); h.city=q.city().trim(); h.country=q.country().trim();
            h.description=q.description(); h.imageUrl=q.imageUrl(); h.pricePerNight=q.pricePerNight();
            h.rating=q.rating(); h.availableRooms=q.availableRooms();
            return ResponseEntity.ok(hotels.save(h));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/hotels/{id}")
    public ResponseEntity<?> deleteHotel(@PathVariable Long id) {
        if (!hotels.existsById(id)) return ResponseEntity.notFound().build();
        if (bookings.existsByHotelId(id))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","This hotel has booking history. Edit or deactivate it instead of deleting."));
        hotels.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bookings")
    public List<Booking> allBookings() {
        return bookings.findAll().stream().sorted(Comparator.comparing((Booking b)->b.id).reversed()).collect(Collectors.toList());
    }

    @PatchMapping("/bookings/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestBody BookingStatusRequest q) {
        if (q.status()==null || !Set.of("CONFIRMED","CANCELLED").contains(q.status().toUpperCase()))
            return ResponseEntity.badRequest().body(Map.of("error","Status must be CONFIRMED or CANCELLED."));
        return bookings.findById(id).map(b -> {
            String next=q.status().toUpperCase();
            if (!next.equals(b.status)) {
                if ("CANCELLED".equals(next)) b.hotel.availableRooms++;
                else if ("CANCELLED".equals(b.status)) {
                    if (b.hotel.availableRooms < 1) return ResponseEntity.badRequest().body(Map.of("error","No rooms available to reinstate this booking."));
                    b.hotel.availableRooms--;
                }
                b.status=next;
            }
            return ResponseEntity.ok(bookings.save(b));
        }).orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> validate(HotelRequest q) {
        if (q.name()==null || q.name().isBlank() || q.city()==null || q.city().isBlank()
                || q.country()==null || q.country().isBlank() || q.pricePerNight()<=0
                || q.rating()<0 || q.rating()>5 || q.availableRooms()<0)
            return ResponseEntity.badRequest().body(Map.of("error","Name, city, country and valid price, rating (0–5), and room count are required."));
        return null;
    }
}
