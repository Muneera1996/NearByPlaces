package com.example.nearby.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.nearby.Model.Result;
import com.example.nearby.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class NearByAdapter extends RecyclerView.Adapter<NearByAdapter.ViewHolder> {
    // Class variables for the List that holds task data and the Context
    private List<Result> places;
    private Context mContext;
    private static final String TAG = NearByAdapter.class.getSimpleName();

    final private NearByAdapter.ItemClickListener mItemClickListener;

    public NearByAdapter(Context context, NearByAdapter.ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }


    @Override
    public NearByAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_places, parent, false);

        return new NearByAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NearByAdapter.ViewHolder holder, final int position) {
        // Determine the values of the wanted data
        final Result list = places.get(position);

        holder.tvTitle.setText(list.getName());


    }
    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        Log.e(TAG,Integer.toString(places.size()));
        if (places == null) {
            return 0;
        }
        return places.size();
    }

    public List<Result> getResults() {
        return places;
    }

    /**
     * When data changes, this method updates the list of places
     * and notifies the adapter to use the new values on it
     */
    public void setResults(List<Result> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    // Inner class for creating ViewHolders
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imgViewCover;
        private final TextView tvTitle;
        private final TextView tvAuthorAndPublishedAt;
        private final TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgViewCover=(ImageView) itemView.findViewById(R.id.imgViewCover);
            tvTitle=(TextView) itemView.findViewById(R.id.tvTitle);
            tvAuthorAndPublishedAt=(TextView) itemView.findViewById(R.id.tvAuthorAndPublishedAt);
            tvDescription=(TextView) itemView.findViewById(R.id.tvDescription);
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Result result = places.get(getAdapterPosition());
            mItemClickListener.onItemClickListener(result);
        }
    }
    public interface ItemClickListener {
        void onItemClickListener(Result result);
    }


}


