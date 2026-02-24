package com.example.collexahub;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddEventDialogFragment extends DialogFragment {

    private static final String ARG_ROLE = "user_role";
    private static final String ARG_EVENT = "event_data";

    private EditText etTitle, etDesc, etDate, etTime, etVenue, etEntryFee;

    private EditText etEndDate, etEndTime;

    private Button btnPublish;
    private RadioGroup radioGroupFee;
    private RadioButton radioYes, radioNo;

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
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            // Keep transparent background so CardView handles shape
            getDialog().getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
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
        if (getArguments() != null) {
            userRole = getArguments().getString(ARG_ROLE);

            if (getArguments().containsKey(ARG_EVENT)) {
                event = (EventModel) getArguments().getSerializable(ARG_EVENT);
            }
        }

        etTitle = view.findViewById(R.id.etTitle);
        etDesc = view.findViewById(R.id.etDescription);
        etDate = view.findViewById(R.id.etDate);
        etTime = view.findViewById(R.id.etTime);
        etVenue = view.findViewById(R.id.etVenue);
        etEntryFee = view.findViewById(R.id.etEntryFee);

        etEndDate = view.findViewById(R.id.etEndDate);
        etEndTime = view.findViewById(R.id.etEndTime);

        radioGroupFee = view.findViewById(R.id.radioGroupFee);
        radioYes = view.findViewById(R.id.radioYes);
        radioNo = view.findViewById(R.id.radioNo);

        btnPublish = view.findViewById(R.id.btnPublish);

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());

        etEndDate.setOnClickListener(v -> showEndDatePicker());
        etEndTime.setOnClickListener(v -> showEndTimePicker());

        radioGroupFee.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioYes) {
                etEntryFee.setVisibility(View.VISIBLE);
            } else {
                etEntryFee.setVisibility(View.GONE);
                etEntryFee.setText("");
            }
        });

        if (event != null) {
            etTitle.setText(event.title);
            etDesc.setText(event.description);
            etDate.setText(event.date);
            etTime.setText(event.time);
            etVenue.setText(event.venue);

            etEndDate.setText(formatDateFromTimestamp(event.endTimestamp));
            etEndTime.setText(formatTimeFromTimestamp(event.endTimestamp));

            if (event.paid) {
                radioYes.setChecked(true);
                etEntryFee.setVisibility(View.VISIBLE);
                etEntryFee.setText(event.entryFee);
            } else {
                radioNo.setChecked(true);
            }

            btnPublish.setText("Update Event");
        }

        btnPublish.setOnClickListener(v -> saveEvent());

        return view;
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

    private void showEndDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) ->
                        etEndDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showEndTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(
                requireContext(),
                (view, hour, minute) ->
                        etEndTime.setText(String.format("%02d:%02d", hour, minute)),
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

        String endDate = etEndDate.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();

        boolean isPaid = radioYes.isChecked();
        String entryFee = "0";

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(date)
                || TextUtils.isEmpty(time)
                || TextUtils.isEmpty(endDate)
                || TextUtils.isEmpty(endTime)) {

            Toast.makeText(getContext(),
                    "Please fill all required fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPaid) {
            entryFee = etEntryFee.getText().toString().trim();
            if (TextUtils.isEmpty(entryFee)) {
                etEntryFee.setError("Enter fee amount");
                return;
            }
        }

        Calendar startCal = Calendar.getInstance();
        String[] sd = date.split("/");
        String[] st = time.split(":");
        startCal.set(Integer.parseInt(sd[2]),
                Integer.parseInt(sd[1]) - 1,
                Integer.parseInt(sd[0]),
                Integer.parseInt(st[0]),
                Integer.parseInt(st[1]));

        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        long startTimestamp = startCal.getTimeInMillis();

        Calendar endCal = Calendar.getInstance();
        String[] ed = endDate.split("/");
        String[] et = endTime.split(":");
        endCal.set(Integer.parseInt(ed[2]),
                Integer.parseInt(ed[1]) - 1,
                Integer.parseInt(ed[0]),
                Integer.parseInt(et[0]),
                Integer.parseInt(et[1]));

        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        long endTimestamp = endCal.getTimeInMillis();


        if (endTimestamp <= startTimestamp) {
            Toast.makeText(getContext(),
                    "End time must be after start time",
                    Toast.LENGTH_SHORT).show();
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
                    System.currentTimeMillis(),
                    isPaid,
                    entryFee,
                    startTimestamp,
                    endTimestamp
            );

            db.getReference("events").child(eventId).setValue(newEvent);

        } else {

            EventModel updatedEvent = new EventModel(
                    event.eventId,
                    title,
                    desc,
                    date,
                    time,
                    venue,
                    event.createdByUid,
                    event.createdByRole,
                    event.timestamp,
                    isPaid,
                    entryFee,
                    startTimestamp,
                    endTimestamp
            );

            db.getReference("events")
                    .child(event.eventId)
                    .setValue(updatedEvent);

        }

        dismiss();
    }
    private String formatDateFromTimestamp(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        return c.get(Calendar.DAY_OF_MONTH) + "/" +
                (c.get(Calendar.MONTH) + 1) + "/" +
                c.get(Calendar.YEAR);
    }

    private String formatTimeFromTimestamp(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        return String.format("%02d:%02d",
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE));
    }
}
