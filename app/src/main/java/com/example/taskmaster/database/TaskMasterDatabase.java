package com.example.taskmaster.database;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.taskmaster.dao.TaskDao;
import com.example.taskmaster.model.Task;

@Database(entities = {Task.class}, version = 2)
public abstract class TaskMasterDatabase extends Roomdatabase {
public abstract TaskDao taskDao();
}
