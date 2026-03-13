package com.hemanth.currency_service.controller;

import com.hemanth.currency_service.dto.AuditRecordRequestDto;
import com.hemanth.currency_service.dto.AuditRecordResponseDto;
import com.hemanth.currency_service.service.AuditRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/audit")
public class AuditRecordController {

    // Service is injected via constructor – clean, testable, enterprise style
    private final AuditRecordService auditRecordService;

    /**
     * CREATE a new audit record
     *
     * @param requestDto contains only "message" from the client
     */
    @PostMapping
    public ResponseEntity<AuditRecordResponseDto> createAuditRecord(
            @RequestBody AuditRecordRequestDto requestDto) {

        AuditRecordResponseDto response = auditRecordService.createAuditRecord(requestDto);

        // Return 201 CREATED
        return ResponseEntity.status(201).body(response);
    }

    /**
     * GET a single audit record by ID
     *
     * @param id primary key
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuditRecordResponseDto> getAuditRecordById(
            @PathVariable Long id) {

        AuditRecordResponseDto response = auditRecordService.getAuditRecordById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * GET all audit records
     */
    @GetMapping
    public ResponseEntity<List<AuditRecordResponseDto>> getAllAuditRecords() {

        List<AuditRecordResponseDto> list = auditRecordService.getAllAuditRecords();

        return ResponseEntity.ok(list);
    }

    /**
     * UPDATE audit record – only "message" field can be updated
     *
     * @param id primary key
     * @param requestDto contains updated message
     */
    @PutMapping("/{id}")
    public ResponseEntity<AuditRecordResponseDto> updateAuditRecord(
            @PathVariable Long id,
            @RequestBody AuditRecordRequestDto requestDto) {

        AuditRecordResponseDto updated = auditRecordService.updateAuditRecord(id, requestDto);

        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE audit record by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuditRecord(@PathVariable Long id) {

        auditRecordService.deleteAuditRecord(id);

        // Return 204 NO CONTENT
        return ResponseEntity.noContent().build();
    }
}
