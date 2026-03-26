package com.example.collexahub;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.collexahub.notifications.Config;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotificationActivity extends AppCompatActivity {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    SharedPreferences sp;

    String[] appPermission = {};
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] appPermission33 = {Manifest.permission.POST_NOTIFICATIONS};
    private static final int PERMISSION_REQUEST_CODE = 1240;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        FirebaseApp.initializeApp(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        if (checkAndRequestPermission()) {
            onCreateNotification();
        }

        Log.d("FCM_DEBUG", "Before getToken call");

        FirebaseMessaging.getInstance().getToken();

       /* FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    // Get new FCM token
                    String token = task.getResult();
                    Log.e("FCM_TOKEN", token);
                });*/

        //Log.d("FCM","FCM Token : "+sp.getString(ConstantSp.FCM_ID,""));

    }

    private void onCreateNotification() {
        Log.d("FCM","Call OnCreate");
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("FCM","Broadcast");
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    Log.d("FCM","Broadcast if");
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    //Update FCM ID CODE
                    updateFCM();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    Log.d("FCM","Broadcast elseif");
                    String message = intent.getStringExtra("message");
                }
            }
        };
    }

    private void updateFCM() {
        Log.d("FCM","Update Method Call");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    Log.d("FCM","Update Method Call Add");
                    if (!task.isSuccessful()) {
                        Log.d("FCM","Update Method Call Success");
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("FCM", "Token: " + token);
                    sp.edit().putString(ConstantSp.FCM_ID, token).commit();
                    Log.d("FCM","Update Method Call SP");
                });
    }

    public boolean checkAndRequestPermission() {
        /*List<String> listPermission = new ArrayList<>();
        for (String perm : appPermission) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermission.add(perm);
            }
        }
        if (!listPermission.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermission.toArray(new String[listPermission.size()]), PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;*/
        List<String> listPermission = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            for (String perm : appPermission33) {
                if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                    listPermission.add(perm);
                }
            }
            if (!listPermission.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermission.toArray(new String[listPermission.size()]), PERMISSION_REQUEST_CODE);
                return false;
            } else {
                onCreateNotification();
                return true;
            }
        } else {
            for (String perm : appPermission) {
                if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                    listPermission.add(perm);
                }
            }
            if (!listPermission.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermission.toArray(new String[listPermission.size()]), PERMISSION_REQUEST_CODE);
                return false;
            } else {
                onCreateNotification();
                return true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            HashMap<String, Integer> permissionResult = new HashMap<>();
            int deniedCount = 0;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResult.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }
            if (deniedCount == 0) {
                onCreateNotification();
                //addInviteIntent();
            } else {
                for (Map.Entry<String, Integer> entry : permissionResult.entrySet()) {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permName)) {
                        showDialogPermission("", "This App needs Read External Storage And Location permissions to work whithout and problems.",
                                "Yes, Grant permissions", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        checkAndRequestPermission();
                                    }
                                },
                                "No, Exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finishAffinity();
                                    }
                                }, false);
                    } else {
                        showDialogPermission("", "You have denied some permissions. Allow all permissions at [Setting] > [Permissions]",
                                "Go to Settings", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, "No, Exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                }, false);
                        break;
                    }
                }
            }
        }
    }

    public AlertDialog showDialogPermission(String title, String msg, String positiveLable, DialogInterface.OnClickListener positiveOnClickListener, String negativeLable, DialogInterface.OnClickListener negativeOnClickListener, boolean isCancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(isCancelable);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLable, positiveOnClickListener);
        builder.setNegativeButton(negativeLable, negativeOnClickListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }

}