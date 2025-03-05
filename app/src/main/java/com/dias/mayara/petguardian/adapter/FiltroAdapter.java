package com.dias.mayara.petguardian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dias.mayara.petguardian.R;
import com.google.android.material.chip.Chip;

import java.util.List;

public class FiltroAdapter extends RecyclerView.Adapter<FiltroAdapter.FiltroViewHolder> {

    private List<String> filtros;
    private OnFiltroRemovedListener listener;

    public FiltroAdapter(List<String> filtros, OnFiltroRemovedListener listener) {
        this.filtros = filtros;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FiltroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filtro, parent, false);
        return new FiltroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FiltroViewHolder holder, int position) {
        String filtro = filtros.get(position);
        holder.chipFiltro.setText(filtro);

        // Remove o filtro quando o ícone de fechar é clicado
        holder.chipFiltro.setOnCloseIconClickListener(v -> {
            filtros.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, filtros.size());
            listener.onFiltroRemoved(filtro);
        });
    }

    @Override
    public int getItemCount() {
        return filtros.size();
    }

    public static class FiltroViewHolder extends RecyclerView.ViewHolder {
        Chip chipFiltro;

        public FiltroViewHolder(@NonNull View itemView) {
            super(itemView);
            chipFiltro = itemView.findViewById(R.id.chipFiltro);
        }
    }

    public interface OnFiltroRemovedListener {
        void onFiltroRemoved(String filtro);
    }
}