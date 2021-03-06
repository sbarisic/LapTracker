package hr.vub.laptracker;

import android.content.Context;
import android.view.*;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TrackViewAdapter extends RecyclerView.Adapter<TrackViewAdapter.ViewHolder> {

    private List<Track> mData;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    TrackViewAdapter(Context context, List<Track> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    public void updateData(List<Track> data) {
        mData = data;
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_track_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Track curTrack = mData.get(position);

        int minutes = (curTrack.best_time_ms / 1000) / 60;
        float seconds = (curTrack.best_time_ms / 1000.0f) % 60;

        holder.titleView.setText(curTrack.track_id);
        holder.timeView.setText(String.format("%d:%.2fs", minutes, seconds));
        holder.distView.setText(String.format("%d m", curTrack.distance));
        holder.avgSpeedView.setText(String.format("%d km/h", curTrack.avg_speed));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleView;
        TextView timeView;
        TextView distView;
        TextView avgSpeedView;

        ViewHolder(View itemView) {
            super(itemView);

            titleView = itemView.findViewById(R.id.tvTitle);
            timeView = itemView.findViewById(R.id.tvTime);

            distView = itemView.findViewById(R.id.tvDist);
            avgSpeedView = itemView.findViewById(R.id.tvAvgSpeed);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Track getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
