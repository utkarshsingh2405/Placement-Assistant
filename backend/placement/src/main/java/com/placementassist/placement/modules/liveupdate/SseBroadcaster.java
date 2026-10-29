package com.placementassist.placement.modules.liveupdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseBroadcaster {

    private static final Logger log = LoggerFactory.getLogger(SseBroadcaster.class);

    // Timeout: 30 minutes
    private static final Long EMITTER_TIMEOUT = 30 * 60 * 1000L;

    private final Map<Long, List<SseEmitter>> emittersByDrive = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long driveId) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT);

        emittersByDrive.computeIfAbsent(driveId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(driveId, emitter));
        emitter.onTimeout(() -> removeEmitter(driveId, emitter));
        emitter.onError(e -> removeEmitter(driveId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected to live drive stream for drive #" + driveId));
        } catch (IOException e) {
            removeEmitter(driveId, emitter);
        }

        return emitter;
    }

    public void broadcast(Long driveId, LiveUpdate update) {
        List<SseEmitter> emitters = emittersByDrive.get(driveId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("LIVE_UPDATE")
                        .id(String.valueOf(update.getId()))
                        .data(update));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }

        emitters.removeAll(deadEmitters);
    }

    private void removeEmitter(Long driveId, SseEmitter emitter) {
        List<SseEmitter> list = emittersByDrive.get(driveId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) {
                emittersByDrive.remove(driveId);
            }
        }
    }

    @Scheduled(fixedRate = 25000)
    public void sendHeartbeats() {
        emittersByDrive.forEach((driveId, emitters) -> {
            List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("HEARTBEAT")
                            .comment("ping"));
                } catch (Exception e) {
                    deadEmitters.add(emitter);
                }
            }
            emitters.removeAll(deadEmitters);
        });
    }
}
