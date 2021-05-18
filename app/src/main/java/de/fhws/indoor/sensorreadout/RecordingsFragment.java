package de.fhws.indoor.sensorreadout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Elias (https://github.com/Zatrac)
 */
public class RecordingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "Files";

    private File[] files;

    private RecyclerView recyclerView;
    private View recordingsView;


    public RecordingsFragment() {

    }

    public static RecordingsFragment newInstance(File[] files) {
        RecordingsFragment fragment = new RecordingsFragment();
        Bundle args = new Bundle();
        args.putSerializable("Files", files);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            files = (File[]) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        recordingsView = inflater.inflate(R.layout.fragment_recordings, container, false);

        ArrayList<Recording> recordings = new ArrayList<Recording>();
        RecordAdapter adapter = new RecordAdapter(R.layout.recording_item, recordings);

        recyclerView = (RecyclerView) recordingsView.findViewById(R.id.recordingRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(adapter.itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        for (File file : files) {
            recordings.add(new Recording(file));
        }

        return recordingsView;
    }
}

class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private final int ItemLayout;
    static private ArrayList<Recording> Recordings;

    public RecordAdapter(int layoutId, ArrayList<Recording> recordings) {
        ItemLayout = layoutId;
        Recordings = recordings;
    }

    @Override
    public int getItemCount() {
        return Recordings == null ? 0 : Recordings.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(ItemLayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        holder.FileName.setText(Recordings.get(listPosition).getFileName());
        holder.Description.setText(Recordings.get(listPosition).getLastModified().toString());
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // When swiped right, the recording is removed and deleted
            Recordings.get(viewHolder.getAdapterPosition()).Delete();
            Recordings.remove(viewHolder.getAdapterPosition());
            notifyDataSetChanged();
        }
    };

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView FileName;
        public TextView Description;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            FileName = (TextView) itemView.findViewById(R.id.FileName);
            Description = (TextView) itemView.findViewById(R.id.Description);
        }

        @Override
        public void onClick(View view) {
            // Share the clicked file
            Recordings.get(getLayoutPosition()).Share();
        }
    }
}