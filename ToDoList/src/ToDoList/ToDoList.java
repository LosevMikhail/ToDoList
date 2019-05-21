package ToDoList;

import java.io.*;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ToDoList {
    private List<Case> listOfCases;

    public ToDoList() {
        createList();
    }

    public ToDoList(ToDoList other) {
        if (other.listOfCases == null) {
            this.listOfCases = new ArrayList<>();
        } else {
            this.listOfCases = new ArrayList<>(other.listOfCases.size());
            for (Case aCase: other.listOfCases) {
                this.listOfCases.add(new Case(aCase));
            }
        }
    }

    public void createList() {
        listOfCases = new ArrayList<Case>();
    }

    public int size() {
        return listOfCases == null ? 0 : listOfCases.size();
    }
    public void addCase(Case e) {
        listOfCases.add(e);
    }

    public void complete(int number) {
        if (number < listOfCases.size()) listOfCases.get(number).complete();
        else System.out.println("You have no such a case");
    }
    public void read(int number) {
        if (number < listOfCases.size()) listOfCases.get(number).read();
        else System.out.println("You have no such a case");
    }

    public void removeCase(int index) {
        if (index < listOfCases.size())
            listOfCases.remove(index);
    }

    public void filter(boolean isDone) {
        if (listOfCases == null) return;
        int i = 0;
        for (Case toDo: listOfCases) {
            i++;
            if (toDo.getCompleted() == isDone)
                System.out.println(i + ")\t" + toDo);
        }
    }

    public void save(String fileName) throws IOException {
        JsonWriter jsonWriter = new JsonWriter(new FileWriter(fileName));
        Gson gson = new Gson();
        jsonWriter.beginArray();
        for (Case elem: listOfCases) {
            jsonWriter.setLenient(true);
            jsonWriter.jsonValue(gson.toJson(elem));
        }
        jsonWriter.endArray();
        jsonWriter.flush();
        jsonWriter.close(); // it is enough to close the stream once, even if there are 2 writers using it
    }

    public void load(String fileName) throws IOException, IllegalStateException {
        List<Case> loadedCases = new ArrayList<>();

        File file = new File(fileName);
        if (!file.exists()) {
            throw new IOException("The file doesn't exist");
        }
        if (file.length() == 0) {
            throw new IllegalStateException("The file is empty");
        }

        JsonReader reader = new JsonReader(new FileReader(file));
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        reader.beginArray();
        while (reader.hasNext()) {
            JsonElement element = parser.parse(reader);
            JsonObject object = element.getAsJsonObject();
            Case c = gson.fromJson(object, Case.class);
            loadedCases.add(c);
        }
        reader.endArray();
        reader.close();

        listOfCases = loadedCases; // if failed, not loosing old listOfCases
    }

    public void outputCases() {
        if (listOfCases == null) return;
        int i = 0;
        for (Case toDo: listOfCases)
            System.out.println(++i + ")\t" + toDo);
    }


    private boolean equals(ToDoList other) {
        if (this == other) return true;
        if (this.listOfCases.size() != other.listOfCases.size()) return  false;
        for (int i = 0; i < listOfCases.size(); i++) {
            if (!this.listOfCases.get(i).equals(other.listOfCases.get(i))) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == this.getClass() && equals((ToDoList)other);
    }


}
