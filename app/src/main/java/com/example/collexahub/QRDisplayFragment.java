package com.example.collexahub;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRDisplayFragment extends Fragment {

    public static QRDisplayFragment newInstance(String qrText) {
        QRDisplayFragment f = new QRDisplayFragment();
        Bundle b = new Bundle();
        b.putString("qr", qrText);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        ImageView imageView = new ImageView(getContext());

        try {
            String qrText = getArguments().getString("qr");
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(
                    qrText,
                    BarcodeFormat.QR_CODE,
                    600,
                    600
            );
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageView;
    }
}
