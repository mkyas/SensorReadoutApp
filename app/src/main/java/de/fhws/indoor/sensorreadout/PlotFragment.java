package de.fhws.indoor.sensorreadout;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYPlot;

import de.fhws.indoor.sensorreadout.sensors.SensorType;

/**
 A fragment which hosts the plot view
 * @author Elias (https://github.com/Zatrac)
 */
public class PlotFragment extends Fragment {
    private static final int HISTORY_SIZE = 1500;

    public Redrawer redrawer;

    private View plotView;
    private SimpleXYSeries xBuffer;
    private SimpleXYSeries yBuffer;
    private SimpleXYSeries zBuffer;
    private XYPlot plotClass;
    private SensorType currentSensorToPlot = SensorType.ACCELEROMETER;

    private Button btnAccelerometer;
    private Button btnLinearAccelerometer;
    private Button btnGravity;
    private Button btnGyroscope;
    private Button btnBarometer;
    private Button btnOrientation;
    private Button btnPlotBack;
    private ToggleButton btnPausePlot;

    public PlotFragment() {

    }

    public static PlotFragment newInstance(boolean isInitialized) {
        PlotFragment fragment = new PlotFragment();
        Bundle args = new Bundle();
        args.putBoolean("isInitialized", isInitialized);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnAccelerometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanPlotterData();
                currentSensorToPlot = SensorType.ACCELEROMETER;
                plotClass.setTitle("Accelerometer");
            }});
        btnLinearAccelerometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanPlotterData();
                currentSensorToPlot = SensorType.LINEAR_ACCELERATION;
                plotClass.setTitle("Linear Accelerometer");
            }});
        btnGravity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanPlotterData();
                currentSensorToPlot = SensorType.GRAVITY;
                plotClass.setTitle("Gravity");
            }});
        btnGyroscope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanPlotterData();
                currentSensorToPlot = SensorType.GYROSCOPE;
                plotClass.setTitle("Gyroscope");
            }});
        btnBarometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanPlotterData();
                currentSensorToPlot = SensorType.PRESSURE;
                plotClass.setTitle("Barometer");
            }});
        btnOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanPlotterData();
                currentSensorToPlot = SensorType.ORIENTATION_NEW;
                plotClass.setTitle("Orientation");
            }});
        btnPlotBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanPlotterData();
                getActivity().onBackPressed();
            }});
        btnPausePlot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btnPausePlot.setTextOn("Resume Plot");
                    redrawer.pause();
                }
                else{
                    btnPausePlot.setTextOn("Pause Plot");
                    redrawer.start();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        plotView = inflater.inflate(R.layout.fragment_plot, container, false);

        btnAccelerometer = plotView.findViewById(R.id.btnAccelerometer);
        btnLinearAccelerometer = plotView.findViewById(R.id.btnLinearAcc);
        btnGravity = plotView.findViewById(R.id.btnGravity);
        btnGyroscope = plotView.findViewById(R.id.btnGyroscope);
        btnBarometer = plotView.findViewById(R.id.btnBarometer);
        btnOrientation = plotView.findViewById(R.id.btnOrientation);

        btnPlotBack = plotView.findViewById(R.id.btnBackPlot);
        btnPausePlot = plotView.findViewById(R.id.btnPausePlot);

        plotClass = plotView.findViewById(R.id.plot);

        xBuffer = new SimpleXYSeries("X");
        xBuffer.useImplicitXVals();
        yBuffer = new SimpleXYSeries("Y");
        yBuffer.useImplicitXVals();
        zBuffer = new SimpleXYSeries("Z");
        zBuffer.useImplicitXVals();

        plotClass.addSeries(xBuffer, new LineAndPointFormatter(
                Color.rgb(100, 100, 200), null, null, null));
        plotClass.addSeries(yBuffer, new LineAndPointFormatter(
                Color.rgb(100, 200, 100), null, null, null));
        plotClass.addSeries(zBuffer, new LineAndPointFormatter(
                Color.rgb(200, 100, 100), null, null, null));
        plotClass.setDomainStepMode(StepMode.INCREMENT_BY_VAL);
        plotClass.setDomainStepValue(HISTORY_SIZE/10);
        plotClass.setLinesPerRangeLabel(3);
        plotClass.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);

        // If the application is already recording data, start the redrawer immediately
        if (getArguments().getBoolean("isInitialized")) {
            redrawer = new Redrawer(plotClass, 100, true);
        } else {
            redrawer = new Redrawer(plotClass, 100, false);
        }

        return plotView;
    }

    public void provideDataToPlotter(final SensorType sensorID, final String csv){

        if(currentSensorToPlot == sensorID && xBuffer != null && yBuffer != null && zBuffer != null){

            String[] data = csv.split(";");

            // get rid the oldest sample in history, where applicable
            // some sensors will only use one or two of the buffers
            if (xBuffer.size() > HISTORY_SIZE) {
                xBuffer.removeFirst();
            }
            if (yBuffer.size() > HISTORY_SIZE) {
                yBuffer.removeFirst();
            }
            if (zBuffer.size() > HISTORY_SIZE) {
                zBuffer.removeFirst();
            }

            if(data.length > 0){
                xBuffer.addLast(null, Float.parseFloat(data[0]));
            }

            if(data.length > 1){
                yBuffer.addLast(null, Float.parseFloat(data[1]));
            }

            if(data.length > 2){
                zBuffer.addLast(null, Float.parseFloat(data[2]));
            }
        }
    }

    public void cleanPlotterData(){
        xBuffer.clear();
        yBuffer.clear();
        zBuffer.clear();
    }
}