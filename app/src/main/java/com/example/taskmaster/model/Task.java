package com.example.taskmaster.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    private String title;
    private String body;
    private TaskStateEnum state;

    public Task(String title, String body, TaskStateEnum state) {
        this.title = title;
        this.body = body;
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public TaskStateEnum getState() {
        return state;
    }

    public void setState(TaskStateEnum state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", state=" + state +
                '}';
    }
}

