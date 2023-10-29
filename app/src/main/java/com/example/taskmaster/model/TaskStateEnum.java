package com.example.taskmaster.model;

import androidx.annotation.NonNull;

public enum TaskStateEnum {
    NEW("New"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In progrress"),
    COMPLETE("Cpmplete");

    private final String taskStateText;

    TaskStateEnum(String taskStateText){
        this.taskStateText=taskStateText;
    }

    public String getTaskStateText() {
        return taskStateText;
    }

    public static TaskStateEnum formString(String possibleTaskStateText){
        for(TaskStateEnum task :TaskStateEnum.values()){
            if (task.taskStateText.equals(possibleTaskStateText)){
                return task;
            }
        }
        return null;
    }
    @NonNull
    @Override
    public String toString() {
        if(taskStateText==null)
            return "";

        return taskStateText ;

    }
}

