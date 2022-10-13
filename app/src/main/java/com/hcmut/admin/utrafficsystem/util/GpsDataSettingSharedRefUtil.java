package com.hcmut.admin.utrafficsystem.util;

import android.content.Context;
import android.content.SharedPreferences;

public class GpsDataSettingSharedRefUtil extends SharedReferencesUtil {
    private static final String BOOK_MARKED_OPTION_FIELD = "bookmarked_field";

    public static void saveGpsDataSetting(Context context, boolean value) {
        SharedPreferences.Editor editor = getSharedReferenceEditor(context);
        editor.putBoolean(BOOK_MARKED_OPTION_FIELD, value);
        editor.apply();
    }

    public static boolean loadGpsDataSetting(Context context) {
        return getSharedReference(context).getBoolean(BOOK_MARKED_OPTION_FIELD, false);
    }
}
