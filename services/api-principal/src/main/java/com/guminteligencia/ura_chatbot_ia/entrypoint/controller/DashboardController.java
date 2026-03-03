package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
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
    public ResponseEntity<ResponseDto<MetricResponseDto<Long>>> getTotalContacts(
            @ModelAttribute DashboardRequestDto request) {
        long totalContacts = dashboardUseCase.getTotalContacts(
                request.getYear(), request.getMonth(), request.getDay(), request.getDdd(), request.getStatus(), request.getIdUsuario());

        ResponseDto<MetricResponseDto<Long>> response = new ResponseDto<>(new MetricResponseDto<>(totalContacts));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contatos-hoje/{idUsuario}")
    public ResponseEntity<ResponseDto<MetricResponseDto<Long>>> getContactsToday(@PathVariable("idUsuario") UUID idUsuario) {
        long contactsToday = dashboardUseCase.getContactsToday(idUsuario);
        ResponseDto<MetricResponseDto<Long>> response = new ResponseDto<>(new MetricResponseDto<>(contactsToday));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/taxa-resposta")
    public ResponseEntity<ResponseDto<MetricResponseDto<Double>>> getResponseRate(
            @ModelAttribute DashboardRequestDto request) {
        double responseRate = dashboardUseCase.getResponseRate(
                request.getYear(), request.getMonth(), request.getDay(), request.getDdd(), request.getStatus(), request.getIdUsuario());
        ResponseDto<MetricResponseDto<Double>> response = new ResponseDto<>(new MetricResponseDto<>(responseRate));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/media-por-vendedor")
    public ResponseEntity<ResponseDto<MetricResponseDto<Double>>> getAveragePerSeller(
            @ModelAttribute DashboardRequestDto request) {
        double average = dashboardUseCase.getAverageContactsPerSeller(
                request.getYear(), request.getMonth(), request.getDay(), request.getDdd(), request.getStatus(), request.getIdUsuario());
        ResponseDto<MetricResponseDto<Double>> response = new ResponseDto<>(new MetricResponseDto<>(average));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contatos-por-dia")
    public ResponseEntity<ResponseDto<ChartDataResponseDto>> getContactsByDay(
            @ModelAttribute DashboardRequestDto request) {
        ChartDataResponseDto chartData = ChartDataResponseMapper.paraDto(dashboardUseCase.getContactsByDay(
                request.getYear(), request.getMonth(), request.getDdd(), request.getStatus(), request.getIdUsuario()));
        ResponseDto<ChartDataResponseDto> response = new ResponseDto<>(chartData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contatos-por-hora")
    public ResponseEntity<ResponseDto<ChartDataResponseDto>> getContactsByHour(
            @ModelAttribute DashboardRequestDto request) {
        ChartDataResponseDto chartData = ChartDataResponseMapper.paraDto(dashboardUseCase.getContactsByHour(
                request.getYear(), request.getMonth(), request.getDay(), request.getDdd(), request.getStatus(), request.getIdUsuario()));
        ResponseDto<ChartDataResponseDto> response = new ResponseDto<>(chartData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contatos-paginado")
    public ResponseEntity<ResponseDto<ContactListResponseDto>> getPaginatedContacts(
            @ModelAttribute DashboardRequestDto request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContactDashboardDto> contactsPage = dashboardUseCase.getPaginatedContacts(
                request.getYear(), request.getMonth(), request.getDay(), request.getDdd(), request.getStatus(), pageable, request.getIdUsuario()).map(ContactDashboardMapper::paraDto);
        ResponseDto<ContactListResponseDto> response = new ResponseDto<>(ContactListResponseDto.fromPage(contactsPage));
        return ResponseEntity.ok(response);
    }
}