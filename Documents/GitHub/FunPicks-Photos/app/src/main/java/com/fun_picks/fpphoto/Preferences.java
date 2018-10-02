
package com.fun_picks.fpphoto;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Preferences extends PreferenceActivity {
	private static final String TAG = "Preferences";
        @SuppressWarnings("deprecation")
		@Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                Log.i(TAG, "onCreate: entered.  calling addPreferencesFromResource()");
                addPreferencesFromResource(R.xml.preferences);

        }
}