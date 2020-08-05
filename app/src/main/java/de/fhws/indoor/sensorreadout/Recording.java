package de.fhws.indoor.sensorreadout;

import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.Date;

public class Recording {
    private File RecordingFile;
    private String FileName;
    private Uri uri;
    private Date lastModified;

    public Recording(File file) {
        RecordingFile = file;
        FileName = file.getName();
        uri = FileProvider.getUriForFile(
            MainActivity.getAppContext(),
            MainActivity.getAppContext().getApplicationContext()
                    .getPackageName() + ".provider", file);
        lastModified = new Date(file.lastModified());
    }

    public String getFileName() {
        return FileName;
    }

    public Uri getUri() {
        return uri;
    }

    public Date getLastModified() { return lastModified; }

    public void Share() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/csv");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity.getAppContext().startActivity(shareIntent);
    }

    public boolean Delete() {
        return RecordingFile.delete();
    }
}
