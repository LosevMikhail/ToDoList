package ToDoList;

import java.util.Calendar;

public class Case {
    private String toDo;
    private Calendar doBefore;
    private boolean completed;
    private boolean read;

    public Case(String toDo, Calendar doBefore) {
        this.toDo = toDo;
        this.doBefore = doBefore;
        completed = false;
        read = false;
    }

    public Case(Case other) {
        this.toDo = other.toDo;
        this.doBefore = (Calendar) other.doBefore.clone();
        this.completed = other.completed;
        this.read = other.read;
    }

    void complete() {
        completed = true;
    }
    boolean getCompleted() {
        return completed;
    }

    void read() {
        read = true;
    }

    private boolean equals(Case other) {
        return toDo.equals(other.toDo) && doBefore.equals(other.doBefore) &&
                completed == other.completed && read == other.read;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == this.getClass() && equals((Case)other);
    }

    @Override
    public String toString() {
        return toDo + ";\t\t\tdo before: " + doBefore.getTime() + "\t\t" +
                (completed ? "✔" : "✗") + " \t" + (read ? "" : "new");
    }
}

