package com.example.gomind.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gomind.R;
import com.example.gomind.model.Leader;

import java.util.List;

public class LeadersAdapter extends RecyclerView.Adapter<LeadersAdapter.ViewHolder> {
    private List<Leader> leaders;

    public LeadersAdapter(List<Leader> leaders) {
        this.leaders = leaders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leader_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Leader leader = leaders.get(position);

        // Установить значения для элемента
        holder.tvScore.setText(String.valueOf(leader.getPosition()));
        holder.tvName.setText(leader.getNickname());
        holder.tvRank.setText(String.valueOf(leader.getPoints()));

        // Изменение фона для первого элемента
        if (position == 0) {
            holder.tvScore.setBackgroundResource(R.drawable.click); // Заменяем цвет счета
            holder.tvRank.setBackgroundResource(R.drawable.click2);
        } else {
            holder.tvScore.setBackgroundResource(R.drawable.noclick);
            holder.tvRank.setBackgroundResource(R.drawable.noclick2);
        }
    }

    @Override
    public int getItemCount() {
        return leaders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvScore, tvName, tvRank;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvName = itemView.findViewById(R.id.tvName);
            tvRank = itemView.findViewById(R.id.tvRank);
        }
    }
}