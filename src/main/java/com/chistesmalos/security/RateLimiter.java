package com.chistesmalos.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiter para prevenir abuso de API.
 */
@Slf4j
@Component
public class RateLimiter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Resolvedor de bucket por IP o usuario.
     */
    private Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }

    /**
     * Crear un nuevo bucket con límite de 100 requests por minuto.
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Verificar si se puede procesar la solicitud.
     */
    public boolean allowRequest(String key) {
        Bucket bucket = resolveBucket(key);
        return bucket.tryConsume(1);
    }

    /**
     * Obtener información de tokens disponibles.
     */
    public long getAvailableTokens(String key) {
        Bucket bucket = resolveBucket(key);
        var probe = bucket.estimateAbilityToConsume(1);
        return probe.getNanosToWaitForRefill() == 0 ? 1 : 0;
    }
}
