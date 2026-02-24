package com.example.collexahub;

public interface OnEventRegisterClickListener {

    void onRegisterClick(EventModel event);

    void onMyQRClick(String qrCode);
    void onEditClick(EventModel event);

    void onDeleteClick(EventModel event);

    void onViewRegistrationsClick(EventModel event);
}
