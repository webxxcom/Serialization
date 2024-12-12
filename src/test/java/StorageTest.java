package test.java;

import main.java.storage.*;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

public class StorageTest {

    public static class Rectangle implements CsvSerializable {
        private final double width;
        private final double height;

        public Rectangle(){
            width = height = 0;
        }

        public Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public String getCsvString() {
            return width + "," + height;
        }

        @Override
        public Rectangle fromFields(String[] values) {
            try {
                return new Rectangle(Double.parseDouble(values[0]), Double.parseDouble(values[1]));
            } catch (NumberFormatException ex){
                throw new IllegalCsvFormat(ex);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Rectangle rectangle = (Rectangle) obj;
            return Double.compare(rectangle.width, width) == 0 && Double.compare(rectangle.height, height) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(width, height);
        }

        @Override
        public String toString() {
            return "Rectangle{width=" + width + ", height=" + height + "}";
        }
    }

    public static class Square extends Rectangle {

        public Square(){
            super();
        }

        public Square(double side) {
            super(side, side);  // A square's width and height are the same
        }

        @Override
        public String getCsvString() {
            return "" + super.width;  // Inherited, but can customize if necessary
        }

        @Override
        public Square fromFields(String[] values) {
            try {
                return new Square(Double.parseDouble(values[0]));  // Only one value needed for side
            } catch(NumberFormatException ex){
                throw new IllegalCsvFormat(ex);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            if (!super.equals(obj)) return false;
            Square square = (Square) obj;
            return Double.compare(square.getWidth(), getWidth()) == 0;  // Only compare sides for equality
        }

        @Override
        public int hashCode() {
            return Objects.hash(getWidth());  // Square's side is the same as both width and height
        }

        @Override
        public String toString() {
            return "Square{side=" + getWidth() + "}";
        }

        public double getWidth() {
            return super.width;  // Since it's a square, width and height are the same
        }
    }

    private static final String TEST_RESOURCES = "src\\test\\resources\\";
    private static final String PACKAGE_NAME = StorageTest.class.getPackageName();
    private final Square sq1 = new Square(20);
    private final Square sq2 = new Square(4.4);
    private final Square sq3 = new Square(0);
    private final Rectangle rc1 = new Rectangle(10, 20);
    private final Rectangle rc2 = new Rectangle(0, 0);
    private final Person ps1 = new Person("Person 1", 10);
    private final Person ps2 = new Person("Person 2", 1000);

    @Test
    public void EmptyClassStorageCollectionTest() throws IOException {
        final String path = TEST_RESOURCES + "emptyData";
        CsvStorage sg = new CsvStorage(path);

        List<CsvSerializable> ls = sg.load();
        assertThrows(NullPointerException.class, ()
                -> sg.save(null));

        assertTrue(ls.isEmpty());

        try(FileInputStream os = new FileInputStream(path)){
            assertEquals( "The empty file after saving nothing must be left empty",
                    0, os.available());
        }
    }

    @Test
    public void EmptyFileTest() throws IOException {
        CsvStorage sg = new CsvStorage(TEST_RESOURCES + "emptyData");
        List<CsvSerializable> ls = sg.load();
        assertTrue(ls.isEmpty());
    }

    @Test
    public void SaveEmptyListTest() throws IOException {
        final String fileName = TEST_RESOURCES + "emptyListData";
        List<CsvSerializable> expected = new ArrayList<>();  // Empty list

        CsvStorage sg = new CsvStorage(fileName);
        sg.save(expected);

        // Ensure the file is empty after saving an empty list
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            assertEquals("The file should be empty when saving an empty list", 0, fileInputStream.available());
        }

        // Load the file and check if it's still an empty list
        List<CsvSerializable> loadedList = sg.load();
        assertTrue("The loaded list should be empty", loadedList.isEmpty());
    }

    @Test
    public void SaveAndLoadSingleObjectTest() throws IOException {
        final String fileName = TEST_RESOURCES + "singleObjectData";
        List<CsvSerializable> expected = List.of(sq1);  // Single object (Square)

        CsvStorage sg = new CsvStorage(fileName);
        sg.save(expected);

        List<CsvSerializable> loadedList = sg.load();
        assertEquals("The loaded list should contain exactly one object", 1, loadedList.size());
        assertEquals("The loaded object should match the saved object", sq1, loadedList.get(0));
    }

    @Test
    public void SaveAndLoadMultipleObjectTypesTest() throws IOException {
        final String fileName = TEST_RESOURCES + "multipleTypesData";
        List<CsvSerializable> expected = List.of(sq1, rc1, ps1);  // Square, Rectangle, Person

        CsvStorage sg = new CsvStorage(fileName);
        sg.save(expected);

        List<CsvSerializable> loadedList = sg.load();
        assertEquals("The loaded list should contain exactly 3 objects", 3, loadedList.size());
        assertTrue("The loaded list should contain a Square", loadedList.contains(sq1));
        assertTrue("The loaded list should contain a Rectangle", loadedList.contains(rc1));
        assertTrue("The loaded list should contain a Person", loadedList.contains(ps1));
    }

    @Test
    public void SaveAndLoadWithMixedObjectTypesTest() throws IOException {
        final String fileName = TEST_RESOURCES + "mixedTypesData";
        List<CsvSerializable> expected = List.of(sq1, ps1, rc2, sq2, ps2, sq3, rc1);  // Square, Person, Rectangle

        CsvStorage sg = new CsvStorage(fileName);
        sg.save(expected);

        List<CsvSerializable> loadedList = sg.load();
        assertEquals("The loaded list should contain exactly 7 objects", expected.size(), loadedList.size());
        assertTrue("The loaded list should contain a Square", loadedList.contains(sq1));
        assertTrue("The loaded list should contain a Person", loadedList.contains(ps1));
        assertTrue("The loaded list should contain a Rectangle", loadedList.contains(rc2));
    }

    @Test
    public void SaveAndLoadSpecialCharactersTest() throws IOException {
        final String fileName = TEST_RESOURCES + "specialCharsData";
        Person personWithSpecialChars = new Person("John-Doe", 30);

        List<CsvSerializable> expected = List.of(personWithSpecialChars);

        CsvStorage sg = new CsvStorage(fileName);
        sg.save(expected);

        List<CsvSerializable> loadedList = sg.load();
        assertEquals("The loaded list should contain exactly one object", 1, loadedList.size());
        assertEquals("The loaded object should match the saved object", personWithSpecialChars, loadedList.get(0));
    }

    @Test
    public void SaveAndLoadWithNullFieldsTest() throws IOException {
        final String fileName = TEST_RESOURCES + "nullFieldsData";
        Person personWithNullName = new Person(null, 25);  // Name is null

        List<CsvSerializable> expected = List.of(personWithNullName);

        CsvStorage sg = new CsvStorage(fileName);
        sg.save(expected);

        List<CsvSerializable> loadedList = sg.load();
        assertEquals("The loaded list should contain exactly one object", 1, loadedList.size());
        assertEquals("The loaded object should match the saved object", personWithNullName, loadedList.getFirst());
    }

    @Test
    public void ThrowsExceptionForMissingFieldsTest() throws IOException{
        final String fileName = TEST_RESOURCES + "missingFieldsData";

        // Create a malformed CSV string with missing fields
        String malformedData = Person.class.getPackageName() + ",Person,Roman";  // Missing age
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(malformedData.getBytes());
        }

        CsvStorage sg = new CsvStorage(fileName);
        assertThrows(IllegalCsvFormat.class, sg::load);
    }

    @Test
    public void ThrowsExceptionForIncorrectFileFormat() throws IOException {
        final String fileName = TEST_RESOURCES + "incorrectFormatData";

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write((PACKAGE_NAME + ",StorageTest$Square,20,20\n"+PACKAGE_NAME +",StorageTest$Rectangle,data,20").getBytes());
        }

        CsvStorage sg = new CsvStorage(fileName);
        assertThrows(IllegalCsvFormat.class, sg::load);
    }

    @Test
    public void ThrowsNoParameterlessConstructorException() throws IOException{
        class Point implements CsvSerializable{
            final int x, y;
            public Point(int x, int y) {this.x = x; this.y = y;}

            @Override
            public String getCsvString() {
                return x+","+y;
            }

            @Override
            public CsvSerializable fromFields(String[] vals) throws IllegalCsvFormat {
                return new Point(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]));
            }
        }
        final String fileName = TEST_RESOURCES + "noParameterLessConstructor";

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write((PACKAGE_NAME + ",StorageTest$1Point,10,10").getBytes());
        }

        CsvStorage sg = new CsvStorage(fileName);
        assertThrows(NoParameterlessConstructorException.class, sg::load);
    }

    @Test
    public void ThrowsExceptionForPrivateConstructor() throws IOException{
        class Book implements CsvSerializable{
            final int pages;
            final String name;

            private Book(){
                pages = 0;
                name = null;
            }

            @Override
            public String getCsvString() {
                return "";
            }

            @Override
            public CsvSerializable fromFields(String[] vals) throws IllegalCsvFormat {
                return null;
            }
        }
        final String fileName = TEST_RESOURCES + "noParameterLessConstructor";

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write((PACKAGE_NAME + ",StorageTest$1Book,10,10").getBytes());
        }

        CsvStorage sg = new CsvStorage(fileName);
        assertThrows(NoParameterlessConstructorException.class, sg::load);
    }

    @Test
    public void ThrowsExceptionForInvalidPackage() throws IOException{
        final String fileName = TEST_RESOURCES + "invalidPackage";

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(",Square,10".getBytes());
        }

        CsvStorage sg = new CsvStorage(fileName);
        assertThrows(IllegalArgumentException.class, sg::load);
    }

    @Test
    public void ThrowsExceptionForInvalidClassName() throws IOException{
        final String fileName = TEST_RESOURCES + "invalidName";

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(",Squaare,10".getBytes());
        }

        CsvStorage sg = new CsvStorage(fileName);
        assertThrows(IllegalArgumentException.class, sg::load);
    }
}
