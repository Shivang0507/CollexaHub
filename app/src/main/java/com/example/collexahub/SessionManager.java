package com.example.collexahub;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "CollexaHubSession";
    private static final String KEY_LOGIN = "isLoggedIn";
    private static final String KEY_ROLE = "userRole";
    private static final String KEY_NAME = "userName";

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createSession(String fullName, String role) {
        editor.putBoolean(KEY_LOGIN, true);
        editor.putString(KEY_NAME, fullName);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public boolean isLoggedin() {
        return pref.getBoolean(KEY_LOGIN, false);
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, "");
    }

    public String getName() {
        return pref.getString(KEY_NAME, "User");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
