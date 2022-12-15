import java.util.ArrayList;
import java.util.Objects;

public class EpicTask extends Task {
    protected ArrayList<Integer> subTasksIds;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        EpicTask epicTask = (EpicTask) obj;
        return Objects.equals(subTasksIds, epicTask.subTasksIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksIds);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasks=" + subTasksIds +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }
}
