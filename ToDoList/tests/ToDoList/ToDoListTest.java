package ToDoList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.*;

import java.io.BufferedReader;
import java.util.GregorianCalendar;


import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

public class ToDoListTest {
    /**
     * tests only those of the methods which implements any logic;
     * doesn't test getters, setters, equals, toStrings and so on !!!
     */

    private static final String testFileName = "test_file.json";
    private static File file;

    @Before
    public void setUp() throws IOException {
        file = new File(testFileName);
        if (!file.exists()) assertTrue(file.createNewFile());
        assertTrue(file.setWritable(true));
        assertTrue(file.setReadable(true));
    }

    @After
    public void tearDown() {
        assertTrue(!file.exists() || file.delete());
    }


    /**
     * Tests load method with a case when there is one record in the file
     */
    @Test
    public void TestLoad1() throws Exception {
        PrintWriter writer = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file), UTF_8)), true);
        writer.println("[{\"toDo\":\"Call my mom\",\"doBefore\":{\"year\":2019,\"month\":5,\"dayOfMonth\":21,\"hourOfDay\":14,\"minute\":0,\"second\":0},\"completed\":false,\"read\":false}]");
        writer.close();

        ToDoList actual = new ToDoList();
        actual.load(testFileName);
        ToDoList expected = new ToDoList();
        expected.addCase(new Case("Call my mom", new GregorianCalendar(2019, GregorianCalendar.JUNE, 21, 14, 0)));
        assertEquals(expected, actual);
    }


    /**
     * Tests load method with a case when there is two records in the file
     */
    @Test
    public void TestLoad2() throws Exception {
        PrintWriter writer = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file), UTF_8)), true);

        writer.println("[{\"toDo\":\"Call my mom\",\"doBefore\":{\"year\":2019,\"month\":5,\"dayOfMonth\":21,\"hourOfDay\":14,\"minute\":0,\"second\":0},\"completed\":false,\"read\":false}," +
                "{\"toDo\":\"Go swimming\",\"doBefore\":{\"year\":2019,\"month\":6,\"dayOfMonth\":5,\"hourOfDay\":1,\"minute\":1,\"second\":0},\"completed\":false,\"read\":false}]");
        writer.close();

        ToDoList actual = new ToDoList();
        actual.load(testFileName);
        ToDoList expected = new ToDoList();
        expected.addCase(new Case("Call my mom", new GregorianCalendar(2019, GregorianCalendar.JUNE, 21, 14, 0)));
        expected.addCase(new Case("Go swimming", new GregorianCalendar(2019, GregorianCalendar.JULY, 5, 1, 1)));

        assertEquals(expected, actual);
    }

    /**
     * Tests load method with a case when the file doesn't exist
     */
    @Test(expected = IOException.class)
    public void TestLoad3() throws Exception {
        if (!file.exists() || file.delete()) {
            ToDoList list = new ToDoList();
            list.load(testFileName);
        }
        else fail();
    }

    /**
     * Tests load method with a case when instead of a collections notation there goes a objects notation in the file
     */
    @Test(expected = IllegalStateException.class)
    public void TestLoad4() throws Exception {
        PrintWriter writer = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file), UTF_8)), true);
        writer.println("{\"toDo\":\"Kiss my nose\",\"created\":\"May 18, 2019 3:14:26 PM\",\"doBefore\":\"Feb 1, 2019 12:00:00 AM\",\"completed\":false,\"read\":false},{\"toDo\":\"Drive my nose\",\"created\":\"May 18, 2019 3:14:26 PM\",\"doBefore\":\"Jun 12, 2019 12:00:00 AM\",\"completed\":false,\"read\":false},{\"toDo\":\"Wash my car\",\"created\":\"May 18, 2019 3:14:26 PM\",\"doBefore\":\"Jan 24, 2019 12:00:00 AM\",\"completed\":false,\"read\":false},{\"toDo\":\"Kiss my car\",\"created\":\"May 18, 2019 3:14:26 PM\",\"doBefore\":\"Jul 20, 2019 12:00:00 AM\",\"completed\":false,\"read\":false},{\"toDo\":\"Wash my nose\",\"created\":\"May 18, 2019 3:14:26 PM\",\"doBefore\":\"Jun 26, 2019 12:00:00 AM\",\"completed\":false,\"read\":false},{\"toDo\":\"Wash my house\",\"created\":\"May 18, 2019 3:14:26 PM\",\"doBefore\":\"Oct 3, 2019 12:00:00 AM\",\"completed\":false,\"read\":false},{\"toDo\":\"Wash my nose\",\"created\":\"May 18, 2019 3:14:26 PM\",\"doBefore\":\"Jun 1, 2019 12:00:00 AM\",\"completed\":false,\"read\":false}");
        writer.close();

        ToDoList list = new ToDoList();
        list.load(testFileName);
    }

    /**
     * Tests load method with a case when there is abracadabra in file =)
     */
    @Test(expected = IOException.class)
    public void TestLoad5() throws Exception {
        PrintWriter writer = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file), UTF_8)), true);
        writer.println("abracadabra");
        writer.close();

        ToDoList list = new ToDoList();
        list.load(testFileName);
    }

    /**
     * Tests save method with a case when there is one record in the file
     */
    @Test
    public void TestSave1() throws Exception {
        ToDoList list = new ToDoList();
        list.addCase(new Case("Call my mom", new GregorianCalendar(2019, GregorianCalendar.JUNE, 21, 14, 0)));
        list.save(testFileName);

        BufferedReader reader = new java.io.BufferedReader(new BufferedReader(
                new InputStreamReader(new FileInputStream(file))));
        String expected = "[{\"toDo\":\"Call my mom\",\"doBefore\":{\"year\":2019,\"month\":5,\"dayOfMonth\":21,\"hourOfDay\":14,\"minute\":0,\"second\":0},\"completed\":false,\"read\":false}]";
        String actual = reader.readLine();

        assertEquals(expected, actual);
    }

    /**
     * Tests save method with a case when there is two records in the file
     */
    @Test
    public void TestSave2() throws Exception {
        ToDoList list = new ToDoList();
        list.addCase(new Case("Call my mom", new GregorianCalendar(2019, GregorianCalendar.JUNE, 21, 14, 0)));
        list.addCase(new Case("Go swimming", new GregorianCalendar(2019, GregorianCalendar.JULY, 5, 1, 1)));
        list.save(testFileName);

        File file = new File(testFileName);
        BufferedReader reader = new java.io.BufferedReader(new BufferedReader(
                new InputStreamReader(new FileInputStream(file))));
        String expected = "[{\"toDo\":\"Call my mom\",\"doBefore\":{\"year\":2019,\"month\":5,\"dayOfMonth\":21,\"hourOfDay\":14,\"minute\":0,\"second\":0},\"completed\":false,\"read\":false}," +
                "{\"toDo\":\"Go swimming\",\"doBefore\":{\"year\":2019,\"month\":6,\"dayOfMonth\":5,\"hourOfDay\":1,\"minute\":1,\"second\":0},\"completed\":false,\"read\":false}]";
        String actual = reader.readLine();

        assertEquals(expected, actual);
    }

    /**
     * Tests save method with a case when the file is not available to write to
     */
    @Test(expected = IOException.class)
    public void TestSave3() throws Exception {
        ToDoList list = new ToDoList();
        list.addCase(new Case("Call my mom", new GregorianCalendar(2019, GregorianCalendar.JUNE, 21, 14, 0)));
        list.addCase(new Case("Go swimming", new GregorianCalendar(2019, GregorianCalendar.JULY, 5, 1, 1)));

        assertTrue(file.setReadOnly());
        list.save(testFileName);
        assertTrue(file.setWritable(true));


        BufferedReader reader = new java.io.BufferedReader(new BufferedReader(
                new InputStreamReader(new FileInputStream(file))));
        String expected = "[{\"toDo\":\"Call my mom\",\"doBefore\":{\"year\":2019,\"month\":5,\"dayOfMonth\":21,\"hourOfDay\":14,\"minute\":0,\"second\":0},\"completed\":false,\"read\":false}," +
                "{\"toDo\":\"Go swimming\",\"doBefore\":{\"year\":2019,\"month\":6,\"dayOfMonth\":5,\"hourOfDay\":1,\"minute\":1,\"second\":0},\"completed\":false,\"read\":false}]";
        String actual = reader.readLine();

        assertEquals(expected, actual);
    }
}