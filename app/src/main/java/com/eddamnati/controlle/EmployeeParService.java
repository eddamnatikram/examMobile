package com.eddamnati.controlle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EmployeeParService extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployeAdapter adapter;
    private List<Employe> employeeList = new ArrayList<>();
    private static final String TAG = "employeeList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_par_service);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmployeAdapter(employeeList);
        recyclerView.setAdapter(adapter);

        fetchEmployeeList();
    }

    private void fetchEmployeeList() {
        String employeeUrl = "http://192.168.126.39:8082/api/v1/employee"; // Remplacez par votre URL de service

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                employeeUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Type type = new TypeToken<List<Employe>>() {}.getType();
                        employeeList = new Gson().fromJson(response.toString(), type);
                        adapter.setEmployeeList(employeeList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Erreur lors de la récupération des employés", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(request);
    }
}
