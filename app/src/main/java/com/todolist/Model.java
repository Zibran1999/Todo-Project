package com.todolist;

public class Model {
    String taskType,task,taskTime,taskDate;

    public Model(String taskType, String task, String taskTime, String taskDate) {
        this.taskType = taskType;
        this.task = task;
        this.taskTime = taskTime;
        this.taskDate = taskDate;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }
}
