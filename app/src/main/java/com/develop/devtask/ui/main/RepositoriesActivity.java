package com.develop.devtask.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.develop.devtask.R;
import com.develop.devtask.database.AppExecutors;
import com.develop.devtask.database.RepoDatabase;
import com.develop.devtask.model.Repository;
import com.develop.devtask.utils.EndlessRecyclerViewScrollListener;
import com.develop.devtask.utils.Utilites;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class RepositoriesActivity extends AppCompatActivity implements View.OnClickListener, PullRefreshLayout.OnRefreshListener {

    private EditText tvSearch;
    private ImageView imgSearch;
    private ShimmerRecyclerView rvRepositories;
    private TextView tvName;
    private TextView tvGoToUrl;
    private TextView tvCancel;
    private PullRefreshLayout swipeRefreshLayout;

    private LinearLayoutManager layoutManager;
    private RepositoriesAdapter repositoriesAdapter;
    private RepositoriesViewModel viewModel;
    private List<Repository> repositoryList;
    private RepoDatabase mDb;
    private Boolean havePagination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(RepositoriesViewModel.class);

        initViews();

        setUpRecyclerViewRepositories();

        if (Utilites.isNetworkActive(getApplicationContext())) {
            getRepositories();
        } else {
            displayCashedData();
        }

        Utilites.scheduleJob(this);
    }

    private void getRepositories() {
        viewModel.getRepositories(this, 1);
        viewModel.mutableLivePagination.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                havePagination = aBoolean;
            }
        });
        viewModel.mutableLiveData.observe(this, new Observer<List<Repository>>() {
            @Override
            public void onChanged(List<Repository> repositories) {
                if (repositories != null) {
                    repositoryList = repositories;
                    repositoriesAdapter.setList(repositories);
                    rvRepositories.hideShimmerAdapter();
                    swipeRefreshLayout.setRefreshing(false);

                    rvRepositories.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            if (havePagination) {
                                loadMoreRepos(page + 1);
                            }
                        }
                    });

                }
            }
        });
    }

    private void loadMoreRepos(int i) {
        viewModel.getRepositories(this, i);
        viewModel.mutableLiveData.observe(this, new Observer<List<Repository>>() {
            @Override
            public void onChanged(List<Repository> repositories) {
                repositoryList.addAll(repositories);
                repositoriesAdapter.setList(repositoryList);
            }
        });
    }

    private void setUpRecyclerViewRepositories() {

        repositoriesAdapter = new RepositoriesAdapter(new RepositoriesAdapter.onItemClick() {
            @Override
            public void onItemClick(Repository repository, int i, LinearLayout layout) {
                showBottomSheetGoToUrl(repository);
            }
        });

        rvRepositories.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(RepositoriesActivity.this);
        rvRepositories.setLayoutManager(layoutManager);
        rvRepositories.setAdapter(repositoriesAdapter);
        rvRepositories.showShimmerAdapter();

    }

    private void initViews() {
        tvSearch = findViewById(R.id.tv_search);
        imgSearch = findViewById(R.id.img_search);
        rvRepositories = findViewById(R.id.rv_repositories);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        repositoryList = new ArrayList<>();

        imgSearch.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_search:
                search(tvSearch.getText().toString());
                break;

        }
    }

    private void showBottomSheetGoToUrl(Repository repository) {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(RepositoriesActivity.this);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_repository_bottom_sheet, null);
        mBottomSheetDialog.setContentView(sheetView);
        ((View) sheetView.getParent()).setBackgroundColor(Color.TRANSPARENT);


        tvName = sheetView.findViewById(R.id.tv_name);
        tvGoToUrl = sheetView.findViewById(R.id.tv_go_to_url);
        tvCancel = sheetView.findViewById(R.id.tv_cancel);

        tvName.append(" " + repository.getName());

        tvGoToUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRepoURL(repository.getHtmlUrl());
                mBottomSheetDialog.dismiss();
            }
        });


        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });

        mBottomSheetDialog.show();

    }

    public void openRepoURL(String inURL) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
        startActivity(browse);
    }

    public void search(String textSearch) {
        textSearch = textSearch.toLowerCase();
        ArrayList<Repository> newRepositoryList = new ArrayList<>();
        if (repositoryList != null) {
            for (Repository repository : repositoryList) {
                if (repository.getName() != null) {
                    String name = repository.getName().toLowerCase();
                    if (name.contains(textSearch)) {
                        newRepositoryList.add(repository);

                    }
                }
            }
            repositoriesAdapter.setFilter(newRepositoryList);
        }

    }

    @Override
    public void onRefresh() {
        if (Utilites.isNetworkActive(getApplicationContext())) {
            rvRepositories.showShimmerAdapter();
            getRepositories();
        }
        swipeRefreshLayout.setRefreshing(false);

    }

    private void displayCashedData() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb = RepoDatabase.getInstance(RepositoriesActivity.this);
                repositoryList = mDb.repoDao().getRepo();
                repositoriesAdapter.setList(repositoryList);
                rvRepositories.hideShimmerAdapter();
            }
        });
    }
}
