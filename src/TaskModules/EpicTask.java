package TaskModules;

import java.util.ArrayList;
import java.util.Objects;
import StatusModules.*;

public class EpicTask extends Task {
    public ArrayList<Integer> subTasksIds;

    public EpicTask(String taskName, String taskDescription, int id, Status status) {
        super(taskName, taskDescription, id, status);
        subTasksIds = new ArrayList<>();
    }

    public void addSubtask(int subtaskId) {
        this.subTasksIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(ArrayList<Integer> subTasksIds) {
        this.subTasksIds = subTasksIds;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasksIds=" + subTasksIds +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", id=" + id +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
