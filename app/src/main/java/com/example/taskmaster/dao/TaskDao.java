package com.example.taskmaster.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.taskmaster.model.Task;

import java.util.List;

@Dao
public interface  TaskDao {
    @Insert
    public void insertATask(Task task);

    @Query("select * from Task")
    public List<Task> findAll();

    @Query("select * from Task ORDER BY title ASC")
    public List<Task> findAllSortedByTitle();

    @Query("select * from Task where id = :id")
    Task findByAnId(long id);
}

