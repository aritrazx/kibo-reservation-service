package com.kibo.reservation.scheduler;

import com.kibo.reservation.service.HoldService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HoldExpirationScheduler {
    private final HoldService holdService;

    @Scheduled(fixedDelayString = "${reservation.expiry-delay-ms:5000}")
    @SchedulerLock(name = "holdExpirationScheduler.runHoldExpirationScheduler",
            lockAtMostFor = "PT45M",
    lockAtLeastFor = "PT10M") //Increased from 30s to 10m to avoid multiple instances of the scheduler running at the same time
    public void expire() {
        holdService.expireDueHolds();
    }
}
