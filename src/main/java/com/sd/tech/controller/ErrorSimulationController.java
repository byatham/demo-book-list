package com.sd.tech.controller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/errors")
public class ErrorSimulationController {

    private static final Logger log =
            LoggerFactory.getLogger(ErrorSimulationController.class);

    private String logError(int status, String message) {

        log.error(
                "ERROR API HIT | Status: {} | Message: {} | Timestamp: {}",
                status,
                message,
                LocalDateTime.now()
        );

        return message;
    }

    @GetMapping("/400")
    public String badRequest() {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                logError(400, "Invalid request parameters")
        );
    }

    @GetMapping("/401")
    public String unauthorized() {
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                logError(401, "Authentication required")
        );
    }

    @GetMapping("/403")
    public String forbidden() {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                logError(403, "Access denied")
        );
    }

    @GetMapping("/404")
    public String notFound() {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                logError(404, "Customer not found")
        );
    }

    @GetMapping("/405")
    public String methodNotAllowed() {
        throw new ResponseStatusException(
                HttpStatus.METHOD_NOT_ALLOWED,
                logError(405, "HTTP method not allowed")
        );
    }

    @GetMapping("/408")
    public String requestTimeout() {
        logError(408, "Request timeout simulated");

        throw new ResponseStatusException(
                HttpStatus.REQUEST_TIMEOUT,
                "Request timeout simulated"
        );
    }

    @GetMapping("/409")
    public String conflict() {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                logError(409, "Customer already exists")
        );
    }

    @GetMapping("/415")
    public String unsupportedMediaType() {
        throw new ResponseStatusException(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                logError(415, "Unsupported content type")
        );
    }

    @GetMapping("/422")
    public String unprocessableEntity() {
        throw new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                logError(422, "Validation failed")
        );
    }

    @GetMapping("/429")
    public String tooManyRequests() {
        throw new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS,
                logError(429, "Rate limit exceeded")
        );
    }

    @GetMapping("/500")
    public String internalServerError() {
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                logError(500, "Internal server error")
        );
    }

    @GetMapping("/502")
    public String badGateway() {
        throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                logError(502, "Upstream service error")
        );
    }

    @GetMapping("/503")
    public String serviceUnavailable() {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                logError(503, "Service unavailable")
        );
    }

    @GetMapping("/504")
    public String gatewayTimeout() {
        throw new ResponseStatusException(
                HttpStatus.GATEWAY_TIMEOUT,
                logError(504, "Gateway timeout")
        );
    }

    @GetMapping("/null-pointer")
    public String nullPointerException() {
        try {
            String value = null;
            return value.toUpperCase();
        } catch (Exception e) {
            log.error("ERROR API HIT | NullPointerException", e);
            throw e;
        }
    }

    @GetMapping("/arithmetic")
    public String arithmeticException() {
        try {
            int result = 10 / 0;
            return "Result: " + result;
        } catch (Exception e) {
            log.error("ERROR API HIT | ArithmeticException", e);
            throw e;
        }
    }

    @GetMapping("/runtime")
    public String runtimeException() {
        RuntimeException ex =
                new RuntimeException("Unexpected application exception");

        log.error("ERROR API HIT | RuntimeException", ex);

        throw ex;
    }

    @GetMapping("/illegal-argument")
    public String illegalArgumentException() {
        IllegalArgumentException ex =
                new IllegalArgumentException("Invalid argument");

        log.error("ERROR API HIT | IllegalArgumentException", ex);

        throw ex;
    }
}