package TaskModules;

import java.util.ArrayList;
import java.util.Objects;

public class EpicTask extends Task {
    public ArrayList<Integer> subTasksIds;

    public EpicTask(String taskName, String taskDescription, int id, String taskStatus) {
        super(taskName, taskDescription, id, taskStatus);
        this.subTasksIds = new ArrayList<>();
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

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        EpicTask epicTask = (EpicTask) obj;
        return Objects.equals(subTasksIds, epicTask.subTasksIds);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksIds);
    }

    public String toString() {
        return "TaskModules.EpicTask{" +
                "subTasks=" + subTasksIds +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }
}
