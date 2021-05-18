package de.fhws.indoor.sensorreadout.sensors;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.DrawableRes;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.fhws.indoor.sensorreadout.R;

/**
 * Created by toni on 10/01/18.
 * Extended by Markus on 19/06/19
 */

public class PedestrianActivityButton extends LinearLayout {

    private final LinearLayout innerBtn;
    private boolean isActive = false;
    private final PedestrianActivity activity;

    public PedestrianActivityButton(Context context, PedestrianActivity activity, @DrawableRes int imageId) {
        super(context);
        inflate(getContext(), R.layout.pedestrian_activity_button, this);
        this.activity = activity;
        // setup ui
        ImageView imageView = (ImageView)this.findViewById(R.id.activityButtonImage);
        imageView.setImageResource(imageId);
        TextView textView = (TextView)this.findViewById(R.id.activityButtonText);
        textView.setText(activity.toString());
        innerBtn = (LinearLayout)getChildAt(0);
        setActivity(false);
    }

    public void setActivity(boolean active) {
        this.isActive = active;
        innerBtn.setBackgroundColor(Color.parseColor(
                (isActive) ? "#F9D737" : "#B2B2B2"
        ));
    }

    public PedestrianActivity getPedestrianActivity() { return this.activity; }
}
