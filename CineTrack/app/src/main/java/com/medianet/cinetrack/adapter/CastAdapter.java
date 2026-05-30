package com.medianet.cinetrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.medianet.cinetrack.R;
import java.util.ArrayList;
import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<String> castList = new ArrayList<>();

    public void setCast(List<String> cast) {
        this.castList = cast;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cast, parent, false);
        return new CastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        holder.textActorName.setText(castList.get(position));
    }

    @Override
    public int getItemCount() { return castList.size(); }

    static class CastViewHolder extends RecyclerView.ViewHolder {
        TextView textActorName;
        CastViewHolder(View v) {
            super(v);
            textActorName = v.findViewById(R.id.textActorName);
        }
    }
}