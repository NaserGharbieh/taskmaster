package com.example.taskmaster.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.taskmaster.model.Task;

import java.util.List;

@DAO
public interface  TaskDao {
    @Insert
    public void insertAProduct(Task task);

    @Query("select * from Product")
    public List<Task> findAll();

    @Query("select * from Product ORDER BY name ASC")
    public List<Task> findAllSortedByName();

    @Query("select * from Product where id = :id")
    Product findByAnId(long id);
}
}
