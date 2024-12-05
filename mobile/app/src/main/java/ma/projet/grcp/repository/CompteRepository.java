package ma.projet.grcp.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ma.projet.grpc.stubs.Compte;
import ma.projet.grpc.stubs.CompteRequest;
import ma.projet.grpc.stubs.CompteServiceGrpc;
import ma.projet.grpc.stubs.GetAllComptesRequest;
import ma.projet.grpc.stubs.GetAllComptesResponse;
import ma.projet.grpc.stubs.GetTotalSoldeRequest;
import ma.projet.grpc.stubs.GetTotalSoldeResponse;
import ma.projet.grpc.stubs.SaveCompteRequest;
import ma.projet.grpc.stubs.SaveCompteResponse;
import ma.projet.grpc.stubs.SoldeStats;
import ma.projet.grpc.stubs.TypeCompte;

public class CompteRepository {

    private static final String TAG = "CompteRepository";
    private static final String SERVER_ADDRESS = "10.0.2.2";
    private static final int SERVER_PORT = 9090;

    private final ManagedChannel channel;

    public CompteRepository() {
        channel = ManagedChannelBuilder
                .forAddress(SERVER_ADDRESS, SERVER_PORT)
                .usePlaintext()
                .build();
    }

    public MutableLiveData<List<Compte>> getAllComptes() {
        MutableLiveData<List<Compte>> comptesLiveData = new MutableLiveData<>();
        new Thread(() -> {
            try {
                CompteServiceGrpc.CompteServiceBlockingStub stub = CompteServiceGrpc.newBlockingStub(channel);
                GetAllComptesRequest request = GetAllComptesRequest.newBuilder().build();
                GetAllComptesResponse response = stub.allComptes(request);
                comptesLiveData.postValue(response.getComptesList());
            } catch (Exception e) {
                Log.e(TAG, "Error fetching accounts", e);
                comptesLiveData.postValue(new ArrayList<>());
            }
        }).start();
        return comptesLiveData;
    }

    public MutableLiveData<SoldeStats> getStats() {
        MutableLiveData<SoldeStats> statsLiveData = new MutableLiveData<>();
        new Thread(() -> {
            try {
                CompteServiceGrpc.CompteServiceBlockingStub stub = CompteServiceGrpc.newBlockingStub(channel);
                GetTotalSoldeRequest request = GetTotalSoldeRequest.newBuilder().build();
                GetTotalSoldeResponse response = stub.totalSolde(request);
                statsLiveData.postValue(response.getStats());
            } catch (Exception e) {
                Log.e(TAG, "Error fetching stats", e);
            }
        }).start();
        return statsLiveData;
    }

    public void addCompte(float solde, String dateCreation, TypeCompte type, MutableLiveData<Boolean> successLiveData) {
        new Thread(() -> {
            try {
                CompteServiceGrpc.CompteServiceBlockingStub stub = CompteServiceGrpc.newBlockingStub(channel);
                SaveCompteRequest request = SaveCompteRequest.newBuilder()
                        .setCompte(CompteRequest.newBuilder()
                                .setSolde(solde)
                                .setDateCreation(dateCreation)
                                .setType(type)
                                .build())
                        .build();
                stub.saveCompte(request);
                successLiveData.postValue(true);
            } catch (Exception e) {
                Log.e(TAG, "Error adding account", e);
                successLiveData.postValue(false);
            }
        }).start();
    }

    public void close() {
        if (!channel.isShutdown()) {
            channel.shutdown();
        }
    }
}
