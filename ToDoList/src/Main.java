import ToDoList.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class CommandToken {
    private final int EMPTY = 0;
    private final int LOAD = 1;
    private final int SAVE = 2;
    private final int HELP = 3;
    private final int COMPLETE = 4;
    private final int FILTER = 5;
    private final int ADD = 6;
    private final int EXIT = 7;
    private final int INVALID = 8;
    private final int REMOVE = 9;
    private final int CREATE = 10;
    private final int READ = 11;

    private int command;
    private Vector<Object> args;
    private String fileName = "todo-list.json";

    private Date getDate(String dateStr) {
        Date date = null;

        try {
            // Convert string into Date
            date = new SimpleDateFormat("d.M.y H:m").parse(dateStr);
            System.out.println(date);

        } catch (ParseException exception) {
            System.out.println("Wrong date format \t ¯\\_(ツ)_/¯");
            System.out.println(exception.getMessage());
        }
        return date;
    }

    CommandToken() {
        command = EMPTY;
        args = new Vector<>();
    }


    private void parse(String commandString) {
        args = new Vector<>();
        String[] commandSpit = commandString.split(" ");
        if (commandString.equals("")) {
            command = EMPTY;
        } else {
            Pattern pattern;
            Matcher matcher;
            switch (commandSpit[0]) {
                case "create":
                    command = commandSpit.length == 1 ? CREATE : INVALID;
                    break;
                case "load":
                    command = LOAD;
                    switch (commandSpit.length) {
                        case 1:
                            break;
                        case 2:
                            args.add(commandSpit[1]);
                            break;
                        default:
                            command = INVALID;
                            break;
                    }
                    break;
                case "save":
                    command = SAVE;
                    switch (commandSpit.length) {
                        case 1:
                            break;
                        case 2:
                            args.add(commandSpit[1]);
                            break;
                        default:
                            command = INVALID;
                            break;
                    }
                    break;

                case "add":
                    command = ADD;
                    pattern = Pattern.compile("(\\w+) +\"(.+)\" +(\\d+)\\.(\\d+)\\.(\\d+) +(\\d+):(\\d+)*");
                    // command + "case" + date
                    matcher = pattern.matcher(commandString);
                    if (matcher.matches()) {        // 2 args
                        args.add(matcher.group(2));             // case, String
                        try {
                            int day = Integer.parseInt(matcher.group(3));
                            int month = Integer.parseInt(matcher.group(4)) - 1;
                            int year = Integer.parseInt(matcher.group(5));
                            int hour = Integer.parseInt(matcher.group(6));
                            int minute = Integer.parseInt(matcher.group(7));
                            args.add(new GregorianCalendar(year, month, day, hour, minute));
                        }
                        catch (NumberFormatException exception) {
                            System.out.println("Invalid command format: wrong date format");
                            command = INVALID;
                        }
                    } else {
                        command = INVALID;
                    }
                    break;
                case "remove":
                    command = REMOVE;
                    pattern = Pattern.compile("(\\w+) +(\\d+)");    // command + number
                    matcher = pattern.matcher(commandString);
                    if (matcher.matches()) {
                        args.add(Integer.parseInt(matcher.group(2)) - 1); // parse number
                    } else {
                        command = INVALID;
                    }
                    break;

                case "complete":
                    command = COMPLETE;
                    if (commandSpit.length == 2) {
                        pattern = Pattern.compile("(\\w+) +(\\d+)");    // command + number
                        matcher = pattern.matcher(commandString);
                        if (matcher.matches()) {
                            args.add(Integer.parseInt(matcher.group(2)) - 1); // parse number
                        } else {
                            command = INVALID;
                        }
                    } else {
                        command = INVALID;
                    }
                    break;
                case "read":
                    command = READ;
                    if (commandSpit.length == 2) {
                        pattern = Pattern.compile("(\\w+) +(\\d+)");    // command + number
                        matcher = pattern.matcher(commandString);
                        if (matcher.matches()) {
                            args.add(Integer.parseInt(matcher.group(2)) - 1); // parse number
                        } else {
                            command = INVALID;
                        }
                    } else {
                        command = INVALID;
                    }
                    break;
                case "filter":
                    if (commandSpit.length == 1) {
                        command = FILTER;
                    } else {
                        command = INVALID;
                    }
                    break;

                case "help":
                    if (commandSpit.length == 1) {
                        command = HELP;
                    } else {
                        command = INVALID;
                    }
                    break;
                case "exit":
                    if (commandSpit.length == 1) {
                        command = EXIT;
                    } else {
                        command = INVALID;
                    }
                    break;

                default:
                    command = INVALID;
                    break;
            }
        }
    }

    void execute(String commandString, ToDoList list) {
        parse(commandString);
        switch (command) {
            case EMPTY:
                System.out.println("Empty command. Type 'help' for commands...");
                break;
            case INVALID:
                System.out.println("Invalid command. Type 'help' for commands...");
                break;
            case EXIT:
                System.exit(0);
                break;
            case HELP:
                System.out.println(
                        "load - load from default file \n" +
                                "load file.json - load from file.json \n" +
                                "add \"new case\" 1.01.2019 18.00 - add new case \n" +
                                "exit - exit \n" +
                                "save - save to default file \n" +
                                "save file.json - save to file.json \n" +
                                "complete 14 - mark case #14 as completed\n" +
                                "remove 1 - mark case #1 as completed\n" +
                                "read 92 - mark case #92 as read\n" +
                                "filter - show uncompleted cases first of the list\n" +
                                "create - create new list");
                break;
            case LOAD: // trouble
                if (args.size() < 2) {
                    try {
                        list.load(args.size() == 0 ? fileName : (String)args.get(0));
                        System.out.println("Loaded successfully");
                    } // handle exceptions here because ToDoList doesn't write to "stdout"
                    catch(IOException exception){
                        // JsonIOException extends IOException
                        System.out.println(exception.getMessage() + "\t ¯\\_(ツ)_/¯");
                    }
                    catch (IllegalStateException exception) {
                        if (exception.getMessage().equals("Not a JSON Object: null")) {
                            System.out.println("Invalid file format\t ¯\\_(ツ)_/¯");
                        } else if (exception.getMessage().equals("The file is empty")) {
                            System.out.println("The file is empty\t ¯\\_(ツ)_/¯");
                        }
                    }
                    catch (OutOfMemoryError error) { // handle situation when the file is too big
                        System.out.println("Failed loading: the file is too big. If you want to process it, start the JVM with a larger heap.\n");
                    }
                } else {
                    System.out.println("Invalid command. Type 'help' for commands...");
                }
                list.outputCases();
                break;
            case SAVE:
                if (args.size() < 2) {
                    try {
                        list.save(args.size() == 1 ? (String) args.get(0) : fileName);
                        System.out.println("Saved successfully");
                    }
                    catch (IOException exception) {
                        System.out.println("Failed saving: " + exception.getMessage());
                    }
                }
                break;
            case COMPLETE:
                list.complete((int)args.get(0));
                list.outputCases();
                break;
            case READ:
                list.read((int)args.get(0));
                list.outputCases();
                break;
            case REMOVE:
                list.removeCase((int)args.get(0));
                list.outputCases();
                break;
            case FILTER:
                list.filter(true);
                break;
            case ADD:
                list.addCase(new Case((String)args.get(0), (Calendar) args.get(1)));
                list.outputCases();
                break;
            case CREATE:
                list.createList();
                System.out.println("A list created successfully");
                break;
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ToDoList list;
        Scanner scanner = new Scanner(System.in);
        list = new ToDoList();

        System.out.println("Type help for commands...");
        String command;
        do {
            command = scanner.nextLine();
            CommandToken commandToken = new CommandToken();
            commandToken.execute(command, list);
        } while (!command.equals("exit")); //
    }
}
