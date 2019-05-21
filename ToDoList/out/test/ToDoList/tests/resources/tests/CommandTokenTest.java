import ToDoList.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.GregorianCalendar;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class CommandTokenTest {
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    private CommandToken  commandToken = new CommandToken();
    private ToDoList list = new ToDoList();
    private String fileName = "test-todo-list.json";

    @Before
    public void setUp() {
        System.setOut(new PrintStream(output));
    }

    @After
    public void tearDown() {
        System.setOut(null);
    }

    /**
     * Tests command 'create'
     */
    @Test
    public void TestExecute1() {
        commandToken.execute("create", list);
        ToDoList expected = new ToDoList();
        expected.createList();
        assertEquals(expected, list);
        assertEquals("A list created successfully\n", output.toString());
    }

    /**
     * Tests an invalid command
     */
    @Test
    public void TestExecute2() {
        commandToken.execute("create abracadabra", list);
        ToDoList expected = new ToDoList();
        assertEquals(expected, list);
        assertEquals("Invalid command. Type 'help' for commands...\n", output.toString());
    }


    /**
     * Tests an empty command
     */
    @Test
    public void TestExecute3() {
        commandToken.execute("", list);
        ToDoList expected = new ToDoList();
        assertEquals(expected, list);
        assertEquals("Empty command. Type 'help' for commands...\n", output.toString());
    }

    /**
     * Tests command 'load' with no argument
     */
    @Test
    public void TestExecute4() throws IOException {
        File file = new File("todo-list.json");
        if (!file.exists()) assertTrue(file.createNewFile());
        PrintWriter writer = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file), UTF_8)), true);
        writer.println("[{\"toDo\":\"Call my mom\",\"doBefore\":{\"year\":2019,\"month\":5,\"dayOfMonth\":21,\"hourOfDay\":14,\"minute\":0,\"second\":0},\"completed\":false,\"read\":false}," +
                "{\"toDo\":\"Go swimming\",\"doBefore\":{\"year\":2019,\"month\":6,\"dayOfMonth\":5,\"hourOfDay\":1,\"minute\":1,\"second\":0},\"completed\":false,\"read\":false}]");
        commandToken.execute("load", list);
        ToDoList expected = new ToDoList();
        expected.load("todo-list.json");
        assertEquals(expected, list);
        assertEquals("Loaded successfully\n" +
                        "1)\tCall my mom;\t\t\tdo before: Fri Jun 21 14:00:00 MSK 2019\t\t✗ \tnew\n" +
                        "2)\tGo swimming;\t\t\tdo before: Fri Jul 05 01:01:00 MSK 2019\t\t✗ \tnew\n",
                output.toString());
        assertTrue(file.delete());
    }

    /**
     * Tests command 'load' with an argument
     */
    @Test
    public void TestExecute5() {
        File file = new File("test-todo-list.json");
        if (file.exists()) assertTrue(file.delete());
        commandToken.execute("load test-todo-list.json", list);
        ToDoList expected = new ToDoList();
        try {
            expected.load("test-todo-list.json");
            fail(); // if the exception haven't been thrown
        }
        catch (IOException exception) {
            assertEquals(exception.getMessage() + "\t ¯\\_(ツ)_/¯\n", output.toString());
        }
        assertEquals(expected, list);
    }

    /**
     * Tests command 'load' with empty file
     */
    @Test
    public void TestExecute6() throws IOException {
        File file = new File(fileName);
        if (!file.exists()) assertTrue(file.createNewFile());
        commandToken.execute("load " + fileName, list);
        PrintWriter writer = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file), UTF_8)), true);
        writer.println("{\"toDo\":\"Call my mom\",\"doBefore\":{\"year\":2019,\"month\":5,\"dayOfMonth\":21,\"hourOfDay\":14,\"minute\":0,\"second\":0},\"completed\":false,\"read\":false}");
        ToDoList expected = new ToDoList();
        try {
            expected.load(fileName);
            fail(); // if the exception haven't been thrown
        }
        catch (IllegalStateException exception) {
            assertEquals("The file is empty\t ¯\\_(ツ)_/¯\n", output.toString());
        }
        assertEquals(expected, list);
        assertTrue(file.delete());
    }

    /**
     * Tests command 'help'
     */
    @Test
    public void TestExecute7() {
        ToDoList expected = new ToDoList();
        commandToken.execute("help", list);

        assertEquals("load - load from default file \n" +
                "load file.json - load from file.json \n" +
                "add \"new case\" 1.01.2019 18.00 - add new case \n" +
                "exit - exit \n" +
                "save - save to default file \n" +
                "save file.json - save to file.json \n" +
                "complete 14 - mark case #14 as completed\n" +
                "remove 1 - mark case #1 as completed\n" +
                "read 92 - mark case #92 as read\n" +
                "filter - show uncompleted cases first of the list\n" +
                "create - create new list\n", output.toString());
        assertEquals(expected, list);
    }

    /**
     * Tests command 'remove'
     */
    @Test
    public void TestExecute8() {
        ToDoList expected = new ToDoList();
        expected.addCase(new Case("Water my flowers", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 7, 30)));
        list = new ToDoList(expected);
        expected.removeCase(0);

        commandToken.execute("remove 1", list);

        assertEquals("", output.toString());
        assertEquals(expected, list);
    }

    /**
     * Tests command 'complete'
     */
    @Test
    public void TestExecute9() {
        ToDoList expected = new ToDoList();
        expected.addCase(new Case("Water my flowers", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 7, 30)));
        list = new ToDoList(expected);
        expected.complete(0);

        commandToken.execute("complete 1", list);

        assertEquals("1)\tWater my flowers;\t\t\tdo before: Thu May 23 07:30:00 MSK 2019\t\t✔ \tnew\n", output.toString());
        assertEquals(expected, list);
    }

    /**
     * Tests command 'read'
     */
    @Test
    public void TestExecute10() {
        ToDoList expected = new ToDoList();
        expected.addCase(new Case("Water my flowers", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 7, 30)));
        list = new ToDoList(expected);
        expected.read(0);

        commandToken.execute("read 1", list);

        assertEquals("1)\tWater my flowers;\t\t\tdo before: Thu May 23 07:30:00 MSK 2019\t\t✗ \t\n", output.toString());
        assertEquals(expected, list);
    }

    /**
     * Tests command 'add'
     */
    @Test
    public void TestExecute11() {
        ToDoList expected = new ToDoList();
        expected.addCase(new Case("Water my flowers", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 7, 30)));
        list = new ToDoList(expected);

        expected.addCase(new Case("Have some fun", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 17, 45)));

        commandToken.execute("add \"Have some fun\" 23.05.2019 17:45", list);

        assertEquals("1)\tWater my flowers;\t\t\tdo before: Thu May 23 07:30:00 MSK 2019\t\t✗ \tnew\n" +
                "2)\tHave some fun;\t\t\tdo before: Thu May 23 17:45:00 MSK 2019\t\t✗ \tnew\n", output.toString());
        assertEquals(expected, list);
    }

    /**
     * Tests command 'save' without an argument
     */
    @Test
    public void TestExecute12() throws IOException {
        list.addCase(new Case("Water my flowers", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 7, 30)));
        list.addCase(new Case("Have some fun", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 17, 45)));

        commandToken.execute("save", list);
        //ToDoList expected = new ToDoList();
        list.save("todo-list.json");

        File file = new File("todo-list.json");
        BufferedReader reader = new java.io.BufferedReader(new BufferedReader(
                new InputStreamReader(new FileInputStream(file))));
        String expected = "[{\"toDo\":\"Water my flowers\",\"doBefore\":{\"year\":2019,\"month\":4,\"dayOfMonth\":23,\"hourOfDay\":7,\"minute\":30,\"second\":0},\"completed\":false,\"read\":false}," +
                "{\"toDo\":\"Have some fun\",\"doBefore\":{\"year\":2019,\"month\":4,\"dayOfMonth\":23,\"hourOfDay\":17,\"minute\":45,\"second\":0},\"completed\":false,\"read\":false}]";
        String actual = reader.readLine();


        assertEquals(expected, actual);
        assertEquals("Saved successfully\n", output.toString());
        assertTrue(file.delete());
    }

    /**
     * Tests command 'save' with an argument
     */
    @Test
    public void TestExecute13() throws IOException {
        list.addCase(new Case("Water my flowers", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 7, 30)));
        list.addCase(new Case("Have some fun", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 17, 45)));

        commandToken.execute("save " + fileName, list);
        //ToDoList expected = new ToDoList();
        list.save(fileName);

        File file = new File(fileName);
        BufferedReader reader = new java.io.BufferedReader(new BufferedReader(
                new InputStreamReader(new FileInputStream(file))));
        String expected = "[{\"toDo\":\"Water my flowers\",\"doBefore\":{\"year\":2019,\"month\":4,\"dayOfMonth\":23,\"hourOfDay\":7,\"minute\":30,\"second\":0},\"completed\":false,\"read\":false}," +
                "{\"toDo\":\"Have some fun\",\"doBefore\":{\"year\":2019,\"month\":4,\"dayOfMonth\":23,\"hourOfDay\":17,\"minute\":45,\"second\":0},\"completed\":false,\"read\":false}]";
        String actual = reader.readLine();


        assertEquals(expected, actual);
        assertEquals("Saved successfully\n", output.toString());
        assertTrue(file.delete());
    }

    /**
     * Tests command 'save' with a case when the file is not available to write to
     */
    @Test
    public void TestExecute14() throws IOException {
        list.addCase(new Case("Water my flowers", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 7, 30)));
        list.addCase(new Case("Have some fun", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 17, 45)));


        File file = new File("todo-list.json");
        if (!file.exists()) assertTrue(file.createNewFile());
        assertTrue(file.setReadOnly());
        commandToken.execute("save", list);
        try {
            list.save("todo-list.json");
            fail();
        }
        catch (IOException exception) {
            assertEquals("Failed saving: " + exception.getMessage() + "\n", output.toString());
        }

        assertTrue(file.setWritable(true));
        assertTrue(file.delete());
    }

    /**
     * Tests command 'filter'
     */
    @Test
    public void TestExecute15() {
        list.addCase(new Case("Water my flowers", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 7, 30)));
        list.addCase(new Case("Have some fun", new GregorianCalendar(2019, GregorianCalendar.MAY, 23, 17, 45)));
        list.addCase(new Case("Call my mom", new GregorianCalendar(2019, GregorianCalendar.JUNE, 21, 14, 0)));
        list.addCase(new Case("Go swimming", new GregorianCalendar(2019, GregorianCalendar.JULY, 5, 1, 1)));
        ToDoList expected = new ToDoList(list);

        expected.filter(true);

        ByteArrayOutputStream actualOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(actualOutput));
        commandToken.execute("filter", list);
        assertEquals(output.toString(), actualOutput.toString());
    }
}