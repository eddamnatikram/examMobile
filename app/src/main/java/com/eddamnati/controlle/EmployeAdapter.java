package com.eddamnati.controlle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmployeAdapter extends RecyclerView.Adapter<EmployeAdapter.EmployeeViewHolder> {

    private List<Employe> employeeList;

    public EmployeAdapter(List<Employe> employeeList) {
        this.employeeList = employeeList;
    }

    public void setEmployeeList(List<Employe> employeeList) {
        this.employeeList = employeeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employe_item, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employe employee = employeeList.get(position);
        holder.bind(employee);
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView serviceTextView;
        private ImageView photoImageView;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            serviceTextView = itemView.findViewById(R.id.serviceTextView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }

        public void bind(Employe employee) {
            nameTextView.setText(employee.getNom() + " " + employee.getPrenom());
            serviceTextView.setText("Service: " + employee.getServiceId());
            // Utilisez une bibliothèque comme Picasso ou Glide pour charger l'image depuis l'URL
            // Exemple avec Picasso : Picasso.get().load(employee.getPhoto()).into(photoImageView);
        }
    }
}
