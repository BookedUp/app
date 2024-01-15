package com.example.bookedup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;

import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    private List<String> items;
    private TypeSelectionListener listener;

    public TypeAdapter(List<String> items, TypeSelectionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_type, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleTxt.setText(items.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pozivamo metodu interfejsa kada je tip odabran
                if (listener != null) {
                    listener.onTypeSelected(items.get(holder.getAdapterPosition()));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface TypeSelectionListener {
        void onAttach(@NonNull Context context);

        void onTypeSelected(String selectedType);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.row_type);
        }
    }
}