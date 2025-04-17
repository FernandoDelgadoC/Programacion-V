package com.fernando_delgado.symphonie.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fernando_delgado.symphonie.R;
import com.fernando_delgado.symphonie.model.Asistencia;

import java.util.List;

public class AsistenciaAdapter extends RecyclerView.Adapter<AsistenciaAdapter.AsistenciaViewHolder> {

    private List<Asistencia> listaAsistencias;

    // Constructor
    public AsistenciaAdapter(List<Asistencia> listaAsistencias) {
        this.listaAsistencias = listaAsistencias;
    }

    @Override
    public AsistenciaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asistencia, parent, false);
        return new AsistenciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AsistenciaViewHolder holder, int position) {
        Asistencia asistencia = listaAsistencias.get(position);
        holder.tvFecha.setText(asistencia.getFecha());
        holder.tvHora.setText(asistencia.getHora());
        holder.tvEstado.setText(asistencia.getEstado());  // Mostrar el estado

        // Cambiar el color del texto según el estado
        if ("Entrada".equals(asistencia.getEstado())) {
            holder.tvEstado.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));  // Verde
        } else if ("Salida".equals(asistencia.getEstado())) {
            holder.tvEstado.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));  // Rojo
        }
    }

    @Override
    public int getItemCount() {
        return listaAsistencias.size();
    }

    // ViewHolder que enlaza las vistas
    public static class AsistenciaViewHolder extends RecyclerView.ViewHolder {

        TextView tvFecha, tvHora, tvEstado;

        public AsistenciaViewHolder(View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.txtFecha);
            tvHora = itemView.findViewById(R.id.txtHora);
            tvEstado = itemView.findViewById(R.id.txtEstado);  // Agregar el TextView para mostrar el estado
        }
    }
}
