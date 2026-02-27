package com.example.collexahub;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.*;

import java.util.Collections;

public class QRScannerFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 101;

    private DecoratedBarcodeView barcodeView;
    private boolean isScanned = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view =
                inflater.inflate(R.layout.fragment_qr_scanner,
                        container,
                        false);

        barcodeView =
                view.findViewById(R.id.barcode_scanner);

        barcodeView.getBarcodeView()
                .setDecoderFactory(
                        new DefaultDecoderFactory(
                                Collections.singletonList(
                                        BarcodeFormat.QR_CODE)));

        checkCameraPermission();

        return view;
    }

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            startScanning();

        } else {

            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        }
    }

    private void startScanning() {

        barcodeView.decodeContinuous(result -> {

            if (isScanned) return;

            String scannedText = result.getText();

            if (scannedText == null ||
                    scannedText.trim().isEmpty())
                return;

            isScanned = true;
            barcodeView.pause();

            VolunteerVerificationFragment fragment =
                    VolunteerVerificationFragment
                            .newInstance(scannedText.trim());

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeView != null)
            barcodeView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeView != null) {
            isScanned = false;
            barcodeView.resume();
        }
    }
}