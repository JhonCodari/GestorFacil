package com.JhonCodari.GestorFacil.service.impl;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.JhonCodari.GestorFacil.repository.RefreshTokenRepository;

@Service
public class RefreshTokenCleanupService {
    
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenCleanupService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void limparTokensExpirados() {
        var agora = Instant.now();
        refreshTokenRepository.deleteByDataExpiracaoBefore(agora);
    }
}
