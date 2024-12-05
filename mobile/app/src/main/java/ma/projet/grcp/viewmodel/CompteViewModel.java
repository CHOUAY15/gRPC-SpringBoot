package ma.projet.grcp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ma.projet.grcp.repository.CompteRepository;
import ma.projet.grpc.stubs.Compte;
import ma.projet.grpc.stubs.SoldeStats;
import ma.projet.grpc.stubs.TypeCompte;

public class CompteViewModel extends ViewModel {

    private final CompteRepository repository;
    private final MutableLiveData<List<Compte>> comptesLiveData = new MutableLiveData<>();
    private final MutableLiveData<SoldeStats> statsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> successLiveData = new MutableLiveData<>();

    public CompteViewModel() {
        repository = new CompteRepository();
    }

    public LiveData<List<Compte>> getComptes() {
        repository.getAllComptes().observeForever(comptesLiveData::postValue);
        return comptesLiveData;
    }

    public LiveData<SoldeStats> getStats() {
        repository.getStats().observeForever(statsLiveData::postValue);
        return statsLiveData;
    }

    public LiveData<Boolean> addCompte(float solde, String dateCreation, TypeCompte type) {
        repository.addCompte(solde, dateCreation, type, successLiveData);
        return successLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.close();
    }
}
