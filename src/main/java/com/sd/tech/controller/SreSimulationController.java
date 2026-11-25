package com.sd.tech.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.micrometer.core.instrument.MeterRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/sre")
@Profile("sre-lab")
@ConditionalOnProperty(
    name = "sre.lab.enabled",
    havingValue = "true"
)
public class SreSimulationController {

    private static final Logger log =
            LoggerFactory.getLogger(SreSimulationController.class);

    // Maximum retained memory for all simulations.
    private static final long MAX_RETAINED_BYTES =
            128L * 1024 * 1024;

    private static final List<byte[]> MEMORY_HOLDER =
            new ArrayList<>();

    private static final AtomicLong RETAINED_BYTES =
            new AtomicLong(0);

    private static final Object MEMORY_LOCK = new Object();

    public SreSimulationController(MeterRegistry registry) {

        registry.gauge(
                "sre_simulated_retained_memory_bytes",
                RETAINED_BYTES
        );

        log.info("SRE simulation controller initialized");
    }

    // ------------------------------------------------
    // 1. MEMORY PRESSURE
    // ------------------------------------------------

    @PostMapping("/memory/pressure")
    public ResponseEntity<String> memoryPressure(
            @RequestParam(defaultValue = "32") int mb) {

        String incidentId = UUID.randomUUID().toString();

        validateMemoryRequest(mb);

        byte[] memory = new byte[mb * 1024 * 1024];

        // Touch allocated pages to make memory commitment observable.
        for (int i = 0; i < memory.length; i += 4096) {
            memory[i] = 1;
        }

        synchronized (MEMORY_LOCK) {

            if (RETAINED_BYTES.get() + memory.length
                    > MAX_RETAINED_BYTES) {

                log.warn(
                    "MEMORY_LIMIT_REACHED incidentId={} requestedMB={} retainedMB={}",
                    incidentId, mb,
                    RETAINED_BYTES.get() / 1024 / 1024
                );

                return ResponseEntity.status(
                        HttpStatus.INSUFFICIENT_STORAGE
                ).body("Memory simulation limit reached");
            }

            MEMORY_HOLDER.add(memory);
            RETAINED_BYTES.addAndGet(memory.length);
        }

        log.warn(
            "MEMORY_PRESSURE_CREATED incidentId={} allocatedMB={} retainedMB={}",
            incidentId, mb,
            RETAINED_BYTES.get() / 1024 / 1024
        );

        return ResponseEntity.ok(
                "Memory pressure created. Incident ID: " + incidentId
        );
    }

    // ------------------------------------------------
    // 2. MEMORY LEAK SIMULATION
    // ------------------------------------------------

    @PostMapping("/memory/leak")
    public ResponseEntity<String> memoryLeak(
            @RequestParam(defaultValue = "8") int mb) {

        String incidentId = UUID.randomUUID().toString();

        log.warn(
            "MEMORY_LEAK_SIMULATION_STARTED incidentId={} requestedMB={}",
            incidentId, mb
        );

        return memoryPressure(mb);
    }

    // ------------------------------------------------
    // 3. RELEASE MEMORY
    // ------------------------------------------------

    @PostMapping("/memory/release")
    public ResponseEntity<String> releaseMemory() {

        long released;

        synchronized (MEMORY_LOCK) {

            released = RETAINED_BYTES.get();

            MEMORY_HOLDER.clear();
            RETAINED_BYTES.set(0);
        }

        log.warn(
            "MEMORY_RELEASED releasedMB={}",
            released / 1024 / 1024
        );

        return ResponseEntity.ok(
                "Released " + released / 1024 / 1024 + " MB. " +
                "Memory is now eligible for garbage collection."
        );
    }

    // ------------------------------------------------
    // 4. CPU SPIKE
    // ------------------------------------------------

    @PostMapping("/cpu/spike")
    public ResponseEntity<String> cpuSpike(
            @RequestParam(defaultValue = "5") int seconds) {

        if (seconds < 1 || seconds > 10) {
            throw new IllegalArgumentException(
                    "CPU duration must be between 1 and 10 seconds"
            );
        }

        long incidentStart = System.currentTimeMillis();

        log.warn(
            "CPU_SPIKE_STARTED durationSeconds={}", seconds
        );

        long endTime =
                System.nanoTime() + seconds * 1_000_000_000L;

        long counter = 0;

        while (System.nanoTime() < endTime) {
            counter += Math.sqrt(counter + 12345);
        }

        log.warn(
            "CPU_SPIKE_COMPLETED durationMs={} operations={}",
            System.currentTimeMillis() - incidentStart,
            counter
        );

        return ResponseEntity.ok(
                "CPU spike completed for " + seconds + " seconds"
        );
    }

    // ------------------------------------------------
    // 5. SLOW API
    // ------------------------------------------------

    @GetMapping("/api/slow")
    public ResponseEntity<String> slowApi(
            @RequestParam(defaultValue = "5000") long delayMs)
            throws InterruptedException {

        if (delayMs < 0 || delayMs > 10000) {
            throw new IllegalArgumentException(
                    "Delay must be between 0 and 10000 milliseconds"
            );
        }

        long start = System.currentTimeMillis();

        log.warn("SLOW_API_STARTED delayMs={}", delayMs);

        Thread.sleep(delayMs);

        long duration = System.currentTimeMillis() - start;

        log.info("SLOW_API_COMPLETED durationMs={}", duration);

        return ResponseEntity.ok(
                "Slow API completed in " + duration + " ms"
        );
    }

    // ------------------------------------------------
    // 6. HTTP 500
    // ------------------------------------------------

    @GetMapping("/error/500")
    public ResponseEntity<String> serverError() {

        log.error("SIMULATED_HTTP_500 Server error generated");

        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR
        ).body("Simulated HTTP 500");
    }

    // ------------------------------------------------
    // 7. HTTP 404
    // ------------------------------------------------

    @GetMapping("/error/404")
    public ResponseEntity<String> notFound() {

        log.warn("SIMULATED_HTTP_404 Resource not found");

        return ResponseEntity.status(
                HttpStatus.NOT_FOUND
        ).body("Simulated HTTP 404");
    }

    // ------------------------------------------------
    // 8. RUNTIME EXCEPTION
    // ------------------------------------------------

    @GetMapping("/error/exception")
    public ResponseEntity<String> runtimeException() {

        log.error(
            "SIMULATED_RUNTIME_EXCEPTION Runtime failure requested"
        );

        throw new IllegalStateException(
                "Simulated production runtime exception"
        );
    }

    // ------------------------------------------------
    // 9. THREAD PRESSURE
    // ------------------------------------------------

    @GetMapping("/threads/hold")
    public ResponseEntity<String> threadPressure(
            @RequestParam(defaultValue = "5000") long durationMs)
            throws InterruptedException {

        if (durationMs < 100 || durationMs > 10000) {
            throw new IllegalArgumentException(
                    "Duration must be between 100 and 10000 ms"
            );
        }

        log.warn(
            "THREAD_HOLD_STARTED thread={} durationMs={}",
            Thread.currentThread().getName(), durationMs
        );

        Thread.sleep(durationMs);

        log.warn(
            "THREAD_HOLD_COMPLETED thread={}",
            Thread.currentThread().getName()
        );

        return ResponseEntity.ok("Thread hold completed");
    }

    // ------------------------------------------------
    // 10. DATABASE FAILURE SIMULATION
    // ------------------------------------------------

    @GetMapping("/database/failure")
    public ResponseEntity<String> databaseFailure() {

        log.error(
            "DATABASE_FAILURE_SIMULATED Connection refused by database"
        );

        return ResponseEntity.status(
                HttpStatus.SERVICE_UNAVAILABLE
        ).body("Simulated database unavailable");
    }

    // ------------------------------------------------
    // 11. DOWNSTREAM TIMEOUT SIMULATION
    // ------------------------------------------------

    @GetMapping("/dependency/timeout")
    public ResponseEntity<String> dependencyTimeout() {

        log.error(
            "DOWNSTREAM_TIMEOUT External service did not respond"
        );

        return ResponseEntity.status(
                HttpStatus.GATEWAY_TIMEOUT
        ).body("Simulated downstream timeout");
    }

    // ------------------------------------------------
    // 12. LOG LEVEL TESTING
    // ------------------------------------------------

    @GetMapping("/logs/generate")
    public ResponseEntity<String> generateLogs() {

        String incidentId = UUID.randomUUID().toString();

        log.trace("TRACE incidentId={} Trace log generated", incidentId);

        log.debug("DEBUG incidentId={} Debug log generated", incidentId);

        log.info("INFO incidentId={} Application operating normally", incidentId);

        log.warn("WARN incidentId={} Warning generated", incidentId);

        log.error("ERROR incidentId={} Simulated error generated", incidentId);

        return ResponseEntity.ok(
                "All log levels generated. Incident ID: " + incidentId
        );
    }

    // ------------------------------------------------
    // 13. MEMORY STATUS
    // ------------------------------------------------

    @GetMapping("/memory/status")
    public ResponseEntity<String> memoryStatus() {

        Runtime runtime = Runtime.getRuntime();

        long used = runtime.totalMemory() - runtime.freeMemory();

        long max = runtime.maxMemory();

        double percentage = (used * 100.0) / max;

        log.info(
            "MEMORY_STATUS usedMB={} maxMB={} usagePercent={} retainedMB={}",
            used / 1024 / 1024,
            max / 1024 / 1024,
            String.format("%.2f", percentage),
            RETAINED_BYTES.get() / 1024 / 1024
        );

        return ResponseEntity.ok(
                "Heap used MB: " + used / 1024 / 1024 +
                ", Heap max MB: " + max / 1024 / 1024 +
                ", Heap usage: " + String.format("%.2f", percentage) + "%" +
                ", Simulated retained MB: " +
                RETAINED_BYTES.get() / 1024 / 1024
        );
    }

    private void validateMemoryRequest(int mb) {

        if (mb < 1 || mb > 64) {
            throw new IllegalArgumentException(
                    "Memory request must be between 1 and 64 MB"
            );
        }
    }
}
