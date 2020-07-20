package com.develop.devtask.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.develop.devtask.R;
import com.develop.devtask.model.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class RepositoriesAdapter extends RecyclerView.Adapter<RepositoriesAdapter.RepositoryViewHolder> {

    private final onItemClick onItemClick;
    private List<Repository> repositoryList = new ArrayList<>();

    public interface onItemClick {
        void onItemClick(Repository repository, int i, LinearLayout layout);
    }

    public RepositoriesAdapter(onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setList(List<Repository> repositoryList) {
        this.repositoryList = repositoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RepositoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_repository, parent, false);

        return new RepositoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RepositoryViewHolder holder, int position) {

        Repository repository = repositoryList.get(position);

        holder.bind(repository, position, onItemClick);

    }

    @Override
    public int getItemCount() {
        return repositoryList.size();
    }

    public class RepositoryViewHolder extends RecyclerView.ViewHolder {

        private TextView tvRepoName;
        private TextView tvRepoDesc;
        private TextView tvRepoOwnerName;
        private LinearLayout lyContainer;
        private TextView tvForkStatus;

        RepositoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRepoName = itemView.findViewById(R.id.tv_repo_name);
            tvRepoDesc = itemView.findViewById(R.id.tv_repo_desc);
            tvRepoOwnerName = itemView.findViewById(R.id.tv_repo_owner_name);
            lyContainer = itemView.findViewById(R.id.ly_container);
            tvForkStatus = itemView.findViewById(R.id.tv_fork_status);


        }

        void bind(final Repository repository, int position, final onItemClick onItemClick) {

            tvRepoName.setText(repository.getName());
            tvRepoDesc.setText(repository.getDescription());
            tvRepoOwnerName.setText(repository.getLogin());
            tvForkStatus.setText(" "+repository.getFork());

            if (repository.getFork()){
                tvForkStatus.setBackground(itemView.getResources().getDrawable(R.drawable.background_fork_false));
            }else {
                tvForkStatus.setBackground(itemView.getResources().getDrawable(R.drawable.background_fork_true));
            }

            lyContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClick.onItemClick(repository, position, lyContainer);
                    return false;
                }
            });


        }

    }

    public void setFilter(ArrayList<Repository>newRepoList)
    {
        repositoryList=new ArrayList<>();
        repositoryList.addAll(newRepoList);
        notifyDataSetChanged();
    }

}
