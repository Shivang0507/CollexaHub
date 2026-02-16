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

        // Default values
        this.paid = false;
        this.entryFee = "0";
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
    }


    public boolean isPaid() {
        return paid;
    }

    public String getEntryFee() {
        return entryFee;
    }
}
