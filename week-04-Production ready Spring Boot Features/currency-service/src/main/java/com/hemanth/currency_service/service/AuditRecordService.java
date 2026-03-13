package com.hemanth.currency_service.service;

import com.hemanth.currency_service.dto.AuditRecordRequestDto;
import com.hemanth.currency_service.dto.AuditRecordResponseDto;

import java.util.List;

public interface AuditRecordService {

    AuditRecordResponseDto createAuditRecord(AuditRecordRequestDto requestDto);

    AuditRecordResponseDto getAuditRecordById(Long id);

    List<AuditRecordResponseDto> getAllAuditRecords();

    AuditRecordResponseDto updateAuditRecord(Long id, AuditRecordRequestDto requestDto);

    void deleteAuditRecord(Long id);
}

