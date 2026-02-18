package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.dashboard.*;
import com.guminteligencia.ura_chatbot_ia.application.usecase.DashboardUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.ChartDataResponseMapper;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.ContactDashboardMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;

    @GetMapping("/total-contatos")
    public ResponseEntity<MetricResponseDto<Long>> getTotalContacts(
            @ModelAttribute DashboardRequestDto request) {
        long totalContacts = dashboardUseCase.getTotalContacts(
                request.getYear(), request.getMonth(), request.getDay(), request.getDdd(), request.getStatus(), request.getIdUsuario());
        return ResponseEntity.ok(new MetricResponseDto<>(totalContacts));
    }

    @GetMapping("/contatos-hoje/{idUsuario}")
    public ResponseEntity<MetricResponseDto<Long>> getContactsToday(@PathVariable("idUsuario") UUID idUsuario) {
        long contactsToday = dashboardUseCase.getContactsToday(idUsuario);
        return ResponseEntity.ok(new MetricResponseDto<>(contactsToday));
    }

    @GetMapping("/taxa-resposta")
    public ResponseEntity<MetricResponseDto<Double>> getResponseRate(
            @ModelAttribute DashboardRequestDto request) {
        double responseRate = dashboardUseCase.getResponseRate(
                request.getYear(), request.getMonth(), request.getDay(), request.getDdd(), request.getStatus(), request.getIdUsuario());
        return ResponseEntity.ok(new MetricResponseDto<>(responseRate));
    }

    @GetMapping("/media-por-vendedor")
    public ResponseEntity<MetricResponseDto<Double>> getAveragePerSeller(
            @ModelAttribute DashboardRequestDto request) {
        double average = dashboardUseCase.getAverageContactsPerSeller(
                request.getYear(), request.getMonth(), request.getDay(), request.getDdd(), request.getStatus(), request.getIdUsuario());
        return ResponseEntity.ok(new MetricResponseDto<>(average));
    }

    @GetMapping("/contatos-por-dia")
    public ResponseEntity<ChartDataResponseDto> getContactsByDay(
            @ModelAttribute DashboardRequestDto request) {
        ChartDataResponseDto chartData = ChartDataResponseMapper.paraDto(dashboardUseCase.getContactsByDay(
                request.getYear(), request.getMonth(), request.getDdd(), request.getStatus(), request.getIdUsuario()));
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/contatos-por-hora")
    public ResponseEntity<ChartDataResponseDto> getContactsByHour(
            @ModelAttribute DashboardRequestDto request) {
        ChartDataResponseDto chartData = ChartDataResponseMapper.paraDto(dashboardUseCase.getContactsByHour(
                request.getYear(), request.getMonth(), request.getDay(), request.getDdd(), request.getStatus(), request.getIdUsuario()));
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/contatos-paginado")
    public ResponseEntity<ContactListResponseDto> getPaginatedContacts(
            @ModelAttribute DashboardRequestDto request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContactDashboardDto> contactsPage = dashboardUseCase.getPaginatedContacts(
                request.getYear(), request.getMonth(), request.getDay(), request.getDdd(), request.getStatus(), pageable, request.getIdUsuario()).map(ContactDashboardMapper::paraDto);
        return ResponseEntity.ok(ContactListResponseDto.fromPage(contactsPage));
    }
}