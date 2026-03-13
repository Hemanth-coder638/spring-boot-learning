package com.hemanth.currency_service.service;

import com.hemanth.currency_service.dto.AuditRecordRequestDto;
import com.hemanth.currency_service.dto.AuditRecordResponseDto;
import com.hemanth.currency_service.entity.AuditRecord;
import com.hemanth.currency_service.exception.ResourceNotFoundException;
import com.hemanth.currency_service.repository.AuditRecordRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Builder
@Slf4j
public class AuditRecordServiceImpl implements AuditRecordService {

    private final AuditRecordRepository auditRecordRepository;

    @Override
    public AuditRecordResponseDto createAuditRecord(AuditRecordRequestDto requestDto) {

        log.info("Creating a new AuditRecord with message: {}", requestDto.getMessage());

        AuditRecord record = new AuditRecord();
        record.setMessage(requestDto.getMessage());

        AuditRecord saved = auditRecordRepository.save(record);

        log.info("AuditRecord created successfully with ID: {}", saved.getId());

        return convertToResponse(saved);
    }

    @Override
    public AuditRecordResponseDto getAuditRecordById(Long id) {

        log.debug("Fetching AuditRecord with ID: {}", id);

        AuditRecord record = auditRecordRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("AuditRecord not found for ID: {}", id);
                    return new ResourceNotFoundException("AuditRecord not found for id: " + id);
                });

        log.info("AuditRecord fetched successfully for ID: {}", id);

        return convertToResponse(record);
    }

    @Override
    public List<AuditRecordResponseDto> getAllAuditRecords() {

        log.debug("Fetching all AuditRecords");

        List<AuditRecord> list = auditRecordRepository.findAll();

        log.info("Total {} AuditRecords fetched", list.size());

        return list.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AuditRecordResponseDto updateAuditRecord(Long id, AuditRecordRequestDto requestDto) {

        log.debug("Updating AuditRecord with ID: {}", id);

        AuditRecord record = auditRecordRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Update failed. AuditRecord not found for ID: {}", id);
                    return new ResourceNotFoundException("AuditRecord not found for id: " + id);
                });

        record.setMessage(requestDto.getMessage());

        AuditRecord updated = auditRecordRepository.save(record);

        log.info("AuditRecord updated successfully for ID: {}", id);

        return convertToResponse(updated);
    }

    @Override
    public void deleteAuditRecord(Long id) {

        log.debug("Attempting to delete AuditRecord with ID: {}", id);

        AuditRecord record = auditRecordRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Delete failed. AuditRecord not found for ID: {}", id);
                    return new ResourceNotFoundException("AuditRecord not found for id: " + id);
                });

        auditRecordRepository.delete(record);

        log.info("AuditRecord deleted successfully for ID: {}", id);
    }


    // --- Utility mapper ---
    private AuditRecordResponseDto convertToResponse(AuditRecord record) {

        return AuditRecordResponseDto.builder()
                .id(record.getId())
                .message(record.getMessage())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .createdBy(record.getCreatedBy())
                .updatedBy(record.getUpdatedBy())
                .build();
    }
}
