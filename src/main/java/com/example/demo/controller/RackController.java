package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Rack;
import com.example.demo.repository.RackRepository;

@RestController
@RequestMapping("/api/racks")
public class RackController {

    @Autowired
    private RackRepository rackRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public List<Rack> getAllRacks() {
        return rackRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public Rack createRack(@RequestBody Rack rack) {
        return rackRepository.save(rack);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Rack> updateRack(@PathVariable Long id, @RequestBody Rack rackDetails) {
        Rack rack = rackRepository.findById(id).orElse(null);
        if (rack == null) {
            return ResponseEntity.notFound().build();
        }

        rack.setSection(rackDetails.getSection());
        rack.setColumnNumber(rackDetails.getColumnNumber());
        return ResponseEntity.ok(rackRepository.save(rack));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> deleteRack(@PathVariable Long id) {
        if (!rackRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rackRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Swap column positions between two racks
    @PostMapping("/swap")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> swapRacks(@RequestBody java.util.Map<String, Long> payload) {
        Long idA = payload.get("rackIdA");
        Long idB = payload.get("rackIdB");
        Rack rackA = rackRepository.findById(idA).orElse(null);
        Rack rackB = rackRepository.findById(idB).orElse(null);
        if (rackA == null || rackB == null) {
            return ResponseEntity.badRequest().body("Invalid rack IDs");
        }

        int tempCol = rackA.getColumnNumber();
        rackA.setColumnNumber(rackB.getColumnNumber());
        rackB.setColumnNumber(tempCol);

        rackRepository.save(rackA);
        rackRepository.save(rackB);
        return ResponseEntity.ok(java.util.Map.of("rackA", rackA, "rackB", rackB));
    }
}
