package ma.projet.grcp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ma.projet.grcp.adapter.AccountAdapter;
import ma.projet.grcp.viewmodel.CompteViewModel;
import ma.projet.grpc.stubs.TypeCompte;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private CompteViewModel viewModel;

    private TextView totalCountText, totalSumText, averageText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(CompteViewModel.class);

        recyclerView = findViewById(R.id.comptesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalCountText = findViewById(R.id.totalCountText);
        totalSumText = findViewById(R.id.totalSumText);
        averageText = findViewById(R.id.averageText);

        Button btn = findViewById(R.id.addCompteButton);
        btn.setOnClickListener(v -> showAddAccountDialog());

        observeData();
    }

    private void observeData() {
        viewModel.getComptes().observe(this, comptes -> {
            AccountAdapter adapter = new AccountAdapter(comptes);
            recyclerView.setAdapter(adapter);
        });

        viewModel.getStats().observe(this, stats -> {
            if (stats != null) {
                totalCountText.setText("Total Accounts: " + stats.getCount());
                Log.d("test",stats.getCount()+"");
                totalSumText.setText("Total Balance: " + stats.getSum() + "€");
                averageText.setText("Average Balance: " + stats.getAverage() + "€");
            }
        });

    }

    private void showAddAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_account, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        TextInputEditText etSolde = dialogView.findViewById(R.id.soldeInput);
        RadioGroup typeGroup = dialogView.findViewById(R.id.typeGroup);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String soldeStr = etSolde.getText().toString().trim();
            int selectedId = typeGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
            String typeStr = selectedRadioButton.getText().toString();

            if (soldeStr.isEmpty() || typeStr.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                float solde = Float.parseFloat(soldeStr);
                TypeCompte typeCompte = typeStr.equals("Courant") ? TypeCompte.COURANT : TypeCompte.EPARGNE;

                viewModel.addCompte(solde, getCurrentDate(), typeCompte).observe(this, success -> {
                    if (success != null && success) {
                        Toast.makeText(this, "Account added successfully", Toast.LENGTH_SHORT).show();
                        observeData();
                    } else {
                        Toast.makeText(this, "Failed to add account", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();
            }
        });
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());
    }
}
