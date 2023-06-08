package com.hcmut.admin.utraffictest.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedReferencesUtil {
    private static final String SHARED_REF_NAME = "app_shared_ref";

    static SharedPreferences getSharedReference(Context context) {
        return context.getSharedPreferences(SHARED_REF_NAME, Context.MODE_PRIVATE);
    }

    static SharedPreferences.Editor getSharedReferenceEditor(Context context) {
        return context.getSharedPreferences(SHARED_REF_NAME, Context.MODE_PRIVATE).edit();
    }
}
