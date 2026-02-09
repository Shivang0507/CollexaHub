package com.example.collexahub;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddEventDialogFragment extends DialogFragment {

    private static final String ARG_ROLE = "user_role";
    private static final String ARG_EVENT = "event_data";

    private EditText etTitle, etDesc, etDate, etTime, etVenue;
    private Button btnPublish;

    private String userRole;
    private EventModel event;

    public AddEventDialogFragment() {}

    public static AddEventDialogFragment newInstance(String role) {
        AddEventDialogFragment fragment = new AddEventDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROLE, role);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddEventDialogFragment newInstance(String role, EventModel event) {
        AddEventDialogFragment fragment = new AddEventDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROLE, role);
        args.putSerializable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userRole = getArguments().getString(ARG_ROLE);
            event = (EventModel) getArguments().getSerializable(ARG_EVENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_add_event_dialog,
                container,
                false
        );

        etTitle = view.findViewById(R.id.etTitle);
        etDesc = view.findViewById(R.id.etDescription);
        etDate = view.findViewById(R.id.etDate);
        etTime = view.findViewById(R.id.etTime);
        etVenue = view.findViewById(R.id.etVenue);
        btnPublish = view.findViewById(R.id.btnPublish);

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());

        if (event != null) {
            etTitle.setText(event.title);
            etDesc.setText(event.description);
            etDate.setText(event.date);
            etTime.setText(event.time);
            etVenue.setText(event.venue);
            btnPublish.setText("Update Event");
        }

        btnPublish.setOnClickListener(v -> saveEvent());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) ->
                        etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();

        new TimePickerDialog(
                requireContext(),
                (view, hour, minute) ->
                        etTime.setText(String.format("%02d:%02d", hour, minute)),
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
        ).show();
    }

    private void saveEvent() {

        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app"
        );

        if (event == null) {

            String eventId = db.getReference("events").push().getKey();

            EventModel newEvent = new EventModel(
                    eventId,
                    title,
                    desc,
                    date,
                    time,
                    venue,
                    FirebaseAuth.getInstance().getUid(),
                    userRole,
                    System.currentTimeMillis()
            );

            db.getReference("events").child(eventId).setValue(newEvent);

        } else {

            db.getReference("events").child(event.eventId).child("title").setValue(title);
            db.getReference("events").child(event.eventId).child("description").setValue(desc);
            db.getReference("events").child(event.eventId).child("date").setValue(date);
            db.getReference("events").child(event.eventId).child("time").setValue(time);
            db.getReference("events").child(event.eventId).child("venue").setValue(venue);
        }

        dismiss();
    }
}
