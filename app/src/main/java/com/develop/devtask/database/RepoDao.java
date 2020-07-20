package com.develop.devtask.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
import com.develop.devtask.model.Repository;

import java.util.List;


@Dao
public interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRepo(Repository repository);

    @Delete
    void deleteRepo(Repository repository);

    @Query("SELECT * FROM repo_table")
    List<Repository> getRepo();

}
