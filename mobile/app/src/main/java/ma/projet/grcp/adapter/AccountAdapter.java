package ma.projet.grcp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import ma.projet.grpc.stubs.Compte;
import ma.projet.grcp.R;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<Compte> accountList;

    public AccountAdapter(List<Compte> accountList) {
        this.accountList = accountList;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Compte account = accountList.get(position);

        // Configurer le texte pour l'ID du compte
        holder.accountIdText.setText("N°: " + account.getId());

        // Formater et afficher le solde
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        holder.soldeText.setText(formatter.format(account.getSolde()));

        // Afficher le type de compte en majuscules
        holder.typeText.setText(account.getType().toString().toUpperCase());

        // Configurer la date de création
        holder.dateText.setText("Créé le " + account.getDateCreation());
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView accountIdText, soldeText, typeText, dateText;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);

            // Lier les vues par leurs IDs
            accountIdText = itemView.findViewById(R.id.accountIdText);
            soldeText = itemView.findViewById(R.id.soldeText);
            typeText = itemView.findViewById(R.id.typeText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }
}
