package com.example.taskmaster.model;
import androidx.room.Entity;

@Entity
public class Task {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    private String title;
    private String body;
    private TaskState state;

    public Task(String title, String body, TaskState state) {
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

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
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

