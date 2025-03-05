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

        holder.chipFiltro.setOnCloseIconClickListener(v -> {
            // Remove o filtro da lista
            filtros.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, filtros.size());

            // Notifica o listener sobre a remoção do filtro
            listener.onFiltroRemoved(filtro);

            // Verifica se a lista de filtros está vazia
            if (filtros.isEmpty()) {
                listener.onTodosFiltrosRemovidos();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filtros.size();
    }

    public void setFiltros(List<String> filtros) {
        this.filtros = filtros;
        notifyDataSetChanged();
    }

    public static class FiltroViewHolder extends RecyclerView.ViewHolder {
        Chip chipFiltro;

        public FiltroViewHolder(@NonNull View itemView) {
            super(itemView);
            chipFiltro = itemView.findViewById(R.id.chipFiltro);
        }
    }

    public interface OnFiltroRemovedListener {
        void onFiltroRemoved(String filtro); // Chamado quando um filtro é removido
        void onTodosFiltrosRemovidos(); // Chamado quando todos os filtros são removidos
    }
}