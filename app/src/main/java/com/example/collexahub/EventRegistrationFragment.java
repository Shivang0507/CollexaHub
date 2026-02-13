package com.example.collexahub;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;

import org.json.JSONObject;

public class EventRegistrationFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private static final String ARG_EVENT_TITLE = "event_title";
    private static final String ARG_PAID = "event_paid";
    private static final String ARG_AMOUNT = "event_amount";

    private String eventId, eventTitle, entryFee;
    private boolean isPaid;
    private String currentUid;

    private EditText etName, etEmail, etPhone,
            etEnrollment, etDepartment, etSem;

    public static EventRegistrationFragment newInstance(
            String eventId, String eventTitle,
            boolean isPaid, String entryFee) {

        EventRegistrationFragment fragment = new EventRegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_EVENT_TITLE, eventTitle);
        args.putBoolean(ARG_PAID, isPaid);
        args.putString(ARG_AMOUNT, entryFee);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_event_registration,
                container, false);

        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            eventTitle = getArguments().getString(ARG_EVENT_TITLE);
            isPaid = getArguments().getBoolean(ARG_PAID);
            entryFee = getArguments().getString(ARG_AMOUNT);
        }

        currentUid = FirebaseAuth.getInstance().getUid();

        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etEnrollment = view.findViewById(R.id.etEnrollment);
        etDepartment = view.findViewById(R.id.etDepartment);
        etSem = view.findViewById(R.id.etSem);

        view.findViewById(R.id.btnSubmitRegistration)
                .setOnClickListener(v -> submitForm());

        return view;
    }

    private void submitForm() {

        if (TextUtils.isEmpty(etName.getText())
                || TextUtils.isEmpty(etEmail.getText())
                || TextUtils.isEmpty(etPhone.getText())
                || TextUtils.isEmpty(etEnrollment.getText())
                || TextUtils.isEmpty(etSem.getText())) {

            Toast.makeText(getContext(),
                    "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPaid) {
            startPayment();
        } else {
            saveRegistration(null);
        }
    }

    private void startPayment() {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_SFaF2tMseHXUIj");

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Collexa Hub");
            options.put("description", eventTitle);
            options.put("currency", "INR");

            int amount = Integer.parseInt(entryFee) * 100;
            options.put("amount", amount);

            checkout.open(requireActivity(), options);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Called from Activity
    public void handlePaymentSuccess(String paymentId) {
        Toast.makeText(getContext(),
                "Payment Successful",
                Toast.LENGTH_SHORT).show();
        saveRegistration(paymentId);
    }

    private void saveRegistration(String paymentId) {

        String qrCode = eventId + "_" + currentUid + "_" + System.currentTimeMillis();

        RegistrationFormModel model =
                new RegistrationFormModel(
                        etName.getText().toString(),
                        etEmail.getText().toString(),
                        etPhone.getText().toString(),
                        etEnrollment.getText().toString(),
                        etDepartment.getText().toString(),
                        etSem.getText().toString(),
                        eventId,
                        currentUid,
                        qrCode,
                        false,
                        System.currentTimeMillis()
                );

        FirebaseDatabase.getInstance(
                        "https://collexa-hub-default-rtdb.asia-southeast1.firebasedatabase.app"
                )
                .getReference("events")
                .child(eventId)
                .child("registrations")
                .child(currentUid)
                .setValue(model)
                .addOnSuccessListener(a -> {

                    Toast.makeText(getContext(),
                            "Registration Successful",
                            Toast.LENGTH_SHORT).show();

                    // 🔥 Go back to StudentHomeFragment
                    requireActivity()
                            .getSupportFragmentManager()
                            .popBackStack(null,
                                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new StudentHomeFragment())
                            .commit();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Registration Failed",
                                Toast.LENGTH_SHORT).show()
                );

    }
}
