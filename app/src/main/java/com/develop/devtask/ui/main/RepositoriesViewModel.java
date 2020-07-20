package com.develop.devtask.ui.main;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.develop.devtask.database.AppExecutors;
import com.develop.devtask.database.RepoDatabase;
import com.develop.devtask.model.Repository;
import com.develop.devtask.service.ApiRequest;
import com.develop.devtask.utils.AppConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class RepositoriesViewModel extends ViewModel {

    public MutableLiveData<List<Repository>> mutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> mutableLivePagination = new MutableLiveData<>();

    ApiRequest apiRequest;
    List<Repository> repositoryList;
    private RepoDatabase mDb;
    private URL repoUrl;

    public void getRepositories(Context context, int page) {

       init(context);
        apiRequest.createGetRequest(buildUrl(page), Priority.IMMEDIATE, new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length()>0){
                        mutableLivePagination.setValue(true);
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Repository repository = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), Repository.class);
                        repository.setLogin(jsonArray.getJSONObject(i).getJSONObject("owner").getString("login"));
                        repositoryList.add(repository);

                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                mDb.repoDao().insertRepo(repository);
                            }
                        });

                    }
                    mutableLiveData.postValue(repositoryList);

                } catch (JSONException e) {
                    Timber.e(e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(ANError error) throws JSONException {
                Timber.e(error.getErrorBody());
                mutableLiveData = new MutableLiveData<>();
                mutableLiveData.setValue(null);

            }
        });
    }

    private void init(Context context) {
        mDb = RepoDatabase.getInstance(context);
        repositoryList = new ArrayList<>();
        mutableLiveData = new MutableLiveData<>();
        mutableLivePagination = new MutableLiveData<>();
        apiRequest = ApiRequest.getInstance(context);
    }

    private String buildUrl(int page){
        Uri builtUri;

            builtUri = Uri.parse(AppConstants.repoUrl)
                    .buildUpon()
                    .appendQueryParameter("per_page", "10")
                    .appendQueryParameter("page", String.valueOf(page))
                    .build();

        try {
             repoUrl = new URL(builtUri.toString());
        } catch (
    MalformedURLException e) {
            e.printStackTrace();
        }

        return repoUrl.toString();
    }
}
