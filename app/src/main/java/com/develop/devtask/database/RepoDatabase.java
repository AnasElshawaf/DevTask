package com.develop.devtask.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.develop.devtask.model.Repository;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
@Database(entities = Repository.class, version = 2)
public abstract class RepoDatabase extends RoomDatabase {

    private static RepoDatabase instance;

    public abstract RepoDao repoDao();

    public static synchronized RepoDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context, RepoDatabase.class, "repo_database")
                    .fallbackToDestructiveMigration().build();
        }

        return instance;
    }
}
