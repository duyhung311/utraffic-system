package com.hcmut.admin.utrafficsystem.model;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class GoogleSignInData {
    private static GoogleSignInClient googleSignInClient;

    public static void setValue(GoogleSignInClient googleSignInClient) {
        GoogleSignInData.googleSignInClient = googleSignInClient;
    }

    public static GoogleSignInClient getValue() {
        return GoogleSignInData.googleSignInClient;
    }
}
