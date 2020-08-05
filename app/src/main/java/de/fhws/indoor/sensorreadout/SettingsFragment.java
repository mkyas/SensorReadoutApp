package de.fhws.indoor.sensorreadout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.provider.DocumentsContract;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;
import android.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.io.File;
import java.nio.file.FileStore;

import de.fhws.indoor.sensorreadout.loggers.DataFolder;
import de.fhws.indoor.sensorreadout.sensors.WiFiRTT;

/**
 * @author Markus Ebner
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        if (!WiFiRTT.isSupported(getContext())) {
            SwitchPreferenceCompat useFtmPreference = (SwitchPreferenceCompat) findPreference("prefUseWifiFTM");
            useFtmPreference.setEnabled(false);
        }

        // Metadata preference button listener
        findPreference("prefMetadata").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MetadataFragment metadataDialog = new MetadataFragment(MainActivity.getPerson(), MainActivity.getComment(), new MetadataFragment.ResultListener() {
                    @Override public void onCommit(String person, String comment) {
                        MainActivity.setPerson(person);
                        MainActivity.setComment(comment);

                        // Pop the MetadataFragment off the stack
                        getFragmentManager().popBackStack();
                    }

                    @Override public void onClose() { getFragmentManager().popBackStack(); }
                });

                getFragmentManager().beginTransaction()
                        .add(metadataDialog, "metadata")
                        .addToBackStack(null)
                        .commit();

                return true;
            }
        });

        // Recordings preference button listener
        findPreference("prefRecordings").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final DataFolder d = new DataFolder(MainActivity.getAppContext(), "sensorOutFiles");

                RecordingsFragment view = RecordingsFragment.newInstance(d.getFolder().listFiles());

                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(android.R.id.content, view, "recordings")
                        .addToBackStack(null)
                        .commit();

                return true;
            }
        });
    }
}
