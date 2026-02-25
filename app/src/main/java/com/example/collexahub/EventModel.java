package com.example.collexahub;

import java.io.Serializable;

public class EventModel implements Serializable {

    public String eventId;
    public String title;
    public String description;
    public String date;
    public String time;
    public String venue;
    public String createdByUid;
    public String createdByRole;
    public long timestamp;

    public boolean paid;
    public String entryFee;

    // Added
    public long startTimestamp;
    public long endTimestamp;

    // ✅ NEW (Team Event Fields)
    public boolean teamEvent;
    public int maxTeamSize;

    public EventModel() {
    }

    public EventModel(String eventId, String title, String description,
                      String date, String time, String venue,
                      String createdByUid, String createdByRole,
                      long timestamp) {

        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.createdByUid = createdByUid;
        this.createdByRole = createdByRole;
        this.timestamp = timestamp;

        // Default values (KEEPED)
        this.paid = false;
        this.entryFee = "0";

        // Default Team values
        this.teamEvent = false;
        this.maxTeamSize = 0;
    }

    public EventModel(String eventId, String title, String description,
                      String date, String time, String venue,
                      String createdByUid, String createdByRole,
                      long timestamp,
                      boolean paid, String entryFee) {

        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.createdByUid = createdByUid;
        this.createdByRole = createdByRole;
        this.timestamp = timestamp;
        this.paid = paid;
        this.entryFee = entryFee;

        // Default Team values
        this.teamEvent = false;
        this.maxTeamSize = 0;
    }

    public EventModel(String eventId, String title, String description,
                      String date, String time, String venue,
                      String createdByUid, String createdByRole,
                      long timestamp,
                      boolean paid, String entryFee,
                      long startTimestamp, long endTimestamp) {

        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.createdByUid = createdByUid;
        this.createdByRole = createdByRole;
        this.timestamp = timestamp;
        this.paid = paid;
        this.entryFee = entryFee;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;

        // Default Team values
        this.teamEvent = false;
        this.maxTeamSize = 0;
    }

    // ✅ NEW Constructor (Team Event Support)
    public EventModel(String eventId, String title, String description,
                      String date, String time, String venue,
                      String createdByUid, String createdByRole,
                      long timestamp,
                      boolean paid, String entryFee,
                      long startTimestamp, long endTimestamp,
                      boolean teamEvent, int maxTeamSize) {

        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.createdByUid = createdByUid;
        this.createdByRole = createdByRole;
        this.timestamp = timestamp;
        this.paid = paid;
        this.entryFee = entryFee;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.teamEvent = teamEvent;
        this.maxTeamSize = maxTeamSize;
    }

    public boolean isPaid() {
        return paid;
    }

    public String getEntryFee() {
        return entryFee;
    }

    // ✅ NEW Getters (Required for Team Events)
    public boolean isTeamEvent() {
        return teamEvent;
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }
}
