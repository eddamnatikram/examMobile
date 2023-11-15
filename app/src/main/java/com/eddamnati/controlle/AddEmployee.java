package com.eddamnati.controlle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddEmployee extends AppCompatActivity implements View.OnClickListener {

    private EditText nom;
    private EditText prenom;
    private EditText photo, date;
    private Spinner serviceSpinner;
    private Button add;
    private static final String TAG = "addEmployee";

    String insertUrl = "http://192.168.126.39:8082/api/v1/employee";
    private RequestQueue requestQueue;
    private List<Service> servicesList = new ArrayList<>();
    String servicesUrl = "http://192.168.126.39:8082/api/v1/service"; // Remplacez par votre URL de service
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        Toolbar toolbar = findViewById(R.id.AddEmployeeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AddEmployee");

        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        photo = findViewById(R.id.photo);
        date = findViewById(R.id.date);
        serviceSpinner = findViewById(R.id.serviceSpinner);
        add = findViewById(R.id.add);

        add.setOnClickListener(this);
        date.setOnClickListener(this);


        requestQueue = Volley.newRequestQueue(getApplicationContext());
        fetchServices();
    }

    private void fetchServices() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                servicesUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Convertir la réponse JSON en liste d'objets Service
                        Type type = new TypeToken<List<Service>>() {}.getType();
                        servicesList = new Gson().fromJson(response.toString(), type);

                        // Afficher les services dans le Spinner
                        setupServiceSpinner();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Erreur lors de la récupération des services", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(request);
    }

    // Retirez cette partie car vous récupérez déjà les services de l'URL
// Map<String, String> serviceMap = new HashMap<>();
// serviceMap.put("Service 1", "1");
// serviceMap.put("Service 2", "2");
// serviceMap.put("Service 3", "3");

// ...

    private void setupServiceSpinner() {
        List<String> serviceNames = new ArrayList<>();
        for (Service service : servicesList) {
            serviceNames.add(service.getNom());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, serviceNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSpinner.setAdapter(adapter);

        // Gérer la sélection d'un service dans le Spinner
        serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Récupérer l'ID du service sélectionné directement à partir de l'objet Service
                Service selectedService = servicesList.get(position);
                String serviceId = String.valueOf(selectedService.getId());
                // Vous pouvez utiliser serviceId comme nécessaire
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Ne rien faire ici
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v == add) {
            addEmployee();
        } else if (v == date) {
            showDatePickerDialog();
        }
    }

    private void addEmployee() {
        // Récupérer l'objet Service directement à partir du Spinner
        Service selectedService = servicesList.get(serviceSpinner.getSelectedItemPosition());
        String serviceId = String.valueOf(selectedService.getId());

        // Construire l'objet Employee
        Employe newEmployee = new Employe(
                0,  // L'ID sera généré côté serveur, donc 0 pour l'instant
                nom.getText().toString(),
                prenom.getText().toString(),
                date.getText().toString(),
                Long.parseLong(serviceId),
                photo.getText().toString()
        );

        // Convertir l'objet Employee en JSON
        JSONObject jsonEmployee = employeeToJson(newEmployee);

        // Envoyer la requête POST
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                insertUrl,
                jsonEmployee,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Employé ajouté avec succès!", Toast.LENGTH_SHORT).show();
                        finish(); // Fermer l'activité après l'ajout réussi
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Erreur lors de l'ajout de l'employé", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(request);
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private JSONObject employeeToJson(Employe employee) {
        JSONObject jsonEmployee = new JSONObject();
        try {
            jsonEmployee.put("nom", employee.getNom());
            jsonEmployee.put("prenom", employee.getPrenom());
            jsonEmployee.put("photo", employee.getPhoto());
            jsonEmployee.put("serviceId", employee.getServiceId());
            jsonEmployee.put("date", employee.getDateNaissance());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonEmployee;
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            EditText dateEditText = requireActivity().findViewById(R.id.date);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(calendar.getTime());

            dateEditText.setText(formattedDate);
        }
    }
}
