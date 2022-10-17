package com.kizitonwose.colorpreferencesample;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.kizitonwose.colorpreferencecompat.ColorPreferenceCompat;
import com.larswerkman.lobsterpicker.LobsterPicker;
import com.larswerkman.lobsterpicker.sliders.LobsterShadeSlider;

/**
 * Created by Kizito Nwose on 9/28/2016.
 */
public class PreferenceCompatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_compat);
        setTitle("ColorPreferenceCompat sample");
    }

    public static class MyPreferenceFragmentCompat extends PreferenceFragmentCompat {

        private final String CUSTOM_PICKER_PREF_KEY = "color_pref_lobster_compat";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_compat);

            findPreference(CUSTOM_PICKER_PREF_KEY).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showColorDialog(preference);
                    return true;
                }
            });
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            return super.onPreferenceTreeClick(preference);
        }

        private void showColorDialog(final Preference preference) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View colorView = inflater.inflate(R.layout.dialog_color, null);

            int color = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(CUSTOM_PICKER_PREF_KEY, Color.YELLOW);
            final LobsterPicker lobsterPicker = colorView.findViewById(R.id.lobsterPicker);
            LobsterShadeSlider shadeSlider = colorView.findViewById(R.id.shadeSlider);

            lobsterPicker.addDecorator(shadeSlider);
            lobsterPicker.setColorHistoryEnabled(true);
            lobsterPicker.setHistory(color);
            lobsterPicker.setColor(color);

            ColorPreferenceCompat colorPref = (ColorPreferenceCompat) preference;
            colorPref.setColorChoices(new int[] {
                    Color.BLUE,
                    Color.YELLOW,
                    Color.RED,
                    Color.BLACK
            });
            colorPref.setColorChoiceNames(new String[] {
                    "Blue",
                    "Yellow",
                    "Red"
            });
            colorPref.setColorNotSetString("Colour not set");

            new AlertDialog.Builder(getActivity())
                    .setView(colorView)
                    .setTitle("Choose Color")
                    .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((ColorPreferenceCompat) preference).setValue(lobsterPicker.getColor(), true);
                        }
                    })
                    .setNegativeButton("CLOSE", null)
                    .show();
        }


    }

}
