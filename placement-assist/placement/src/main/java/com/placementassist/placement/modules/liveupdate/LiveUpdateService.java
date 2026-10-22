package com.placementassist.placement.modules.liveupdate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LiveUpdateService {

    private final LiveUpdateRepository liveUpdateRepository;
    private final SseBroadcaster sseBroadcaster;

    public LiveUpdateService(
            LiveUpdateRepository liveUpdateRepository,
            SseBroadcaster sseBroadcaster) {
        this.liveUpdateRepository = liveUpdateRepository;
        this.sseBroadcaster = sseBroadcaster;
    }

    @Transactional(readOnly = true)
    public List<LiveUpdate> getRecentUpdates(Long driveId) {
        return liveUpdateRepository.findByDriveIdOrderByCreatedAtDesc(driveId);
    }

    @Transactional(readOnly = true)
    public List<LiveUpdate> getAllRecentUpdates() {
        return liveUpdateRepository.findTop20ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Page<LiveUpdate> getPagedUpdates(Long driveId, Pageable pageable) {
        return liveUpdateRepository.findByDriveIdOrderByCreatedAtDesc(driveId, pageable);
    }

    @Transactional
    public LiveUpdate postUpdate(Long driveId, LiveUpdate update) {
        update.setDriveId(driveId);
        LiveUpdate saved = liveUpdateRepository.save(update);

        // Broadcast to all active SSE listeners
        sseBroadcaster.broadcast(driveId, saved);

        return saved;
    }
}
