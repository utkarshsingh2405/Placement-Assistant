package com.placementassist.institute.modules.listing;

import com.placementassist.institute.common.enums.OpportunityStatus;

import java.time.LocalDate;

/**
 * Common contract for external opportunities (Hackathons, Internships, Off-Campus Jobs).
 */
public interface ListingItem {

    Long getId();

    OpportunityStatus getStatus();

    void setStatus(OpportunityStatus status);

    LocalDate getDeadline();

    String getHowToApply();
}
