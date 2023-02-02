package TaskModules;

import java.util.Objects;
import StatusModules.*;

public class Subtask extends Task {
    int epicTaskId;

    public Subtask(String taskName, String taskDescription, int id, Status status, int epicTaskId) {
        super(taskName, taskDescription, id, status);
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    public String toString() {
        return "SubTask{" +
                "epicTaskId=" + epicTaskId +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", id=" + id +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }
}
