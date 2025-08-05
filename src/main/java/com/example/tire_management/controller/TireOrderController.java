package com.example.tire_management.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.tire_management.model.TireOrder;
import com.example.tire_management.repository.TireOrderRepository;
import com.example.tire_management.repository.TireRequestRepository;
import com.example.tire_management.service.EmailService;
import com.example.tire_management.service.TireOrderService;

@RestController
@RequestMapping("/api/tire-orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class TireOrderController {

    @Autowired
    private TireOrderService tireOrderService;

    @Autowired
    private TireRequestRepository tireRequestRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TireOrderRepository tireOrderRepository;

    // GET: All orders (Admin view)
    @GetMapping
    public ResponseEntity<List<TireOrder>> getAllOrders() {
        return ResponseEntity.ok(tireOrderService.getAllOrders());
    }

    // GET: Orders for a specific seller (vendorEmail) - Seller Dashboard
    @GetMapping("/vendor/{vendorEmail}")
    public ResponseEntity<List<TireOrder>> getOrdersByVendorEmail(@PathVariable String vendorEmail) {
        List<TireOrder> orders = tireOrderService.getOrdersByVendorEmail(vendorEmail);
        return ResponseEntity.ok(orders);
    }

    // GET: Order by ID
    @GetMapping("/{id}")
    public ResponseEntity<TireOrder> getOrderById(@PathVariable String id) {
        return tireOrderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Create new order
    @PostMapping
    public ResponseEntity<TireOrder> createOrder(@RequestBody TireOrder order) {
        TireOrder created = tireOrderService.createOrder(order);
        return ResponseEntity.status(201).body(created);
    }

    // PUT: Update existing order
    @PutMapping("/{id}")
    public ResponseEntity<TireOrder> updateOrder(@PathVariable String id, @RequestBody TireOrder order) {
        Optional<TireOrder> existing = tireOrderService.getOrderById(id);
        if (existing.isPresent()) {
            TireOrder updated = tireOrderService.updateOrder(id, order);
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE: Delete order by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        Optional<TireOrder> existing = tireOrderService.getOrderById(id);
        if (existing.isPresent()) {
            tireOrderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // PUT: Confirm order (status = confirmed) and notify user
    @PutMapping("/{id}/confirm")
    public ResponseEntity<TireOrder> confirmOrder(@PathVariable String id) {
        TireOrder updatedOrder = tireOrderService.confirmOrder(id);
        return ResponseEntity.ok(updatedOrder);
    }

    // PUT: Reject order (status = rejected) with reason and notify user
    @PutMapping("/{id}/reject")
    public ResponseEntity<TireOrder> rejectOrder(@PathVariable String id, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rejection reason is required");
        }
        TireOrder updatedOrder = tireOrderService.rejectOrder(id, reason);
        return ResponseEntity.ok(updatedOrder);
    }
}
