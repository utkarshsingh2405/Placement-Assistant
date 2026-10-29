package com.placementassist.placement.modules.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WebPushService {

    private static final Logger log = LoggerFactory.getLogger(WebPushService.class);

    private final SubscriptionRepository subscriptionRepository;

    public WebPushService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public PushSubscriptionEntity saveSubscription(Long userId, String rollNo, String endpoint, String p256dh, String auth) {
        return subscriptionRepository.findByEndpoint(endpoint)
                .map(existing -> {
                    existing.setP256dh(p256dh);
                    existing.setAuth(auth);
                    existing.setRollNo(rollNo);
                    existing.setUserId(userId);
                    return subscriptionRepository.save(existing);
                })
                .orElseGet(() -> subscriptionRepository.save(PushSubscriptionEntity.builder()
                        .userId(userId)
                        .rollNo(rollNo)
                        .endpoint(endpoint)
                        .p256dh(p256dh)
                        .auth(auth)
                        .build()));
    }

    public void sendPush(String rollNo, String title, String body, String url) {
        List<PushSubscriptionEntity> subs = subscriptionRepository.findByRollNo(rollNo);
        log.info("[WEB_PUSH] Dispatching push notification to {} devices for rollNo={}: title='{}', body='{}'",
                subs.size(), rollNo, title, body);
    }
}
