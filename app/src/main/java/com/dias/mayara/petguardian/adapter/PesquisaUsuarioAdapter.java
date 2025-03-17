package com.dias.mayara.petguardian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PesquisaUsuarioAdapter extends RecyclerView.Adapter<PesquisaUsuarioAdapter.UsuarioViewHolder> {

    private final List<Usuario> usuarioList;

    // Construtor para inicializar a lista de usuários
    public PesquisaUsuarioAdapter(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    // ViewHolder interno
    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNomeUsuario, textViewPesquisaCidadeUsuario, textViewPesquisaQuantidadePetsUsuario;
        public CircleImageView imageViewPesquisaFotoUsuario;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNomeUsuario = itemView.findViewById(R.id.textViewPesquisaNomeUsuario);
            textViewPesquisaCidadeUsuario = itemView.findViewById(R.id.textViewPesquisaCidadeUsuario);
            imageViewPesquisaFotoUsuario = itemView.findViewById(R.id.imageViewPesquisaFotoUsuario);
        }
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate do layout do item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuario_pesquisa_adapter, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        // Vincule os dados ao ViewHolder
        Usuario usuario = usuarioList.get(position);
        holder.textViewNomeUsuario.setText(usuario.getNomeUsuario());
        holder.textViewPesquisaCidadeUsuario.setText(usuario.getCidadeUsuario());

        Glide.with(holder.imageViewPesquisaFotoUsuario.getContext())
                .load(usuario.getCaminhoFotoUsuario()) // Aqui você insere a URL da imagem
                .placeholder(R.drawable.imagem_carregamento) // Imagem padrão enquanto carrega
                .error(R.drawable.profile_image) // Imagem em caso de erro
                .into(holder.imageViewPesquisaFotoUsuario);
    }

    @Override
    public int getItemCount() {
        return usuarioList.size(); // Retorna o número de itens na lista
    }
}
