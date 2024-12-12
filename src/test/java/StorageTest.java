import main.java.storage.*;
import org.junit.Test;
import test.java.Person;

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

    private static final String filesRoot = "src\\test\\resources\\";
    private final Square sq1 = new Square(20);
    private final Square sq2 = new Square(4.4);
    private final Square sq3 = new Square(0);
    private final Rectangle rc1 = new Rectangle(10, 20);
    private final Rectangle rc2 = new Rectangle(0, 0);
    private final Person ps1 = new Person("Person 1", 10);
    private final Person ps2 = new Person("Person 2", 1000);

    @Test
    public void EmptyClassStorageCollectionTest() throws IOException {
     final String path = filesRoot + "emptyData";
        System.out.println(Square.class.getName());

        Storage sg = new Storage();
        List<CsvSerializable> ls = sg.load(path);
        assertThrows(NullPointerException.class, ()
                -> sg.save(null, path));

        assertTrue(ls.isEmpty());

        try(FileInputStream os = new FileInputStream(path)){
            assertEquals( "The empty file after saving nothing must be left empty",
                    0, os.available());
        }
    }

    @Test
    public void EmptyFileTest() throws IOException {
        Storage sg = new Storage();
        List<CsvSerializable> ls = sg.load(filesRoot + "emptyData");
        assertTrue(ls.isEmpty());
    }

    @Test
    public void SaveEmptyListTest() throws IOException {
        final String fileName = filesRoot + "emptyListData";
        List<CsvSerializable> expected = new ArrayList<>();  // Empty list

        Storage sg = new Storage();
        sg.save(expected, fileName);

        // Ensure the file is empty after saving an empty list
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            assertEquals("The file should be empty when saving an empty list", 0, fileInputStream.available());
        }

        // Load the file and check if it's still an empty list
        List<CsvSerializable> loadedList = sg.load(fileName);
        assertTrue("The loaded list should be empty", loadedList.isEmpty());
    }

    @Test
    public void SaveAndLoadSingleObjectTest() throws IOException {
        final String fileName = filesRoot + "singleObjectData";
        List<CsvSerializable> expected = List.of(sq1);  // Single object (Square)

        Storage sg = new Storage();
        sg.save(expected, fileName);

        List<CsvSerializable> loadedList = sg.load(fileName);
        assertEquals("The loaded list should contain exactly one object", 1, loadedList.size());
        assertEquals("The loaded object should match the saved object", sq1, loadedList.get(0));
    }

    @Test
    public void SaveAndLoadMultipleObjectTypesTest() throws IOException {
        final String fileName = filesRoot + "multipleTypesData";
        List<CsvSerializable> expected = List.of(sq1, rc1, ps1);  // Square, Rectangle, Person

        Storage sg = new Storage();
        sg.save(expected, fileName);

        List<CsvSerializable> loadedList = sg.load(fileName);
        assertEquals("The loaded list should contain exactly 3 objects", 3, loadedList.size());
        assertTrue("The loaded list should contain a Square", loadedList.contains(sq1));
        assertTrue("The loaded list should contain a Rectangle", loadedList.contains(rc1));
        assertTrue("The loaded list should contain a Person", loadedList.contains(ps1));
    }

    @Test
    public void SaveAndLoadWithMixedObjectTypesTest() throws IOException {
        final String fileName = filesRoot + "mixedTypesData";
        List<CsvSerializable> expected = List.of(sq1, ps1, rc2, sq2, ps2, sq3, rc1);  // Square, Person, Rectangle

        Storage sg = new Storage();
        sg.save(expected, fileName);

        List<CsvSerializable> loadedList = sg.load(fileName);
        assertEquals("The loaded list should contain exactly 7 objects", expected.size(), loadedList.size());
        assertTrue("The loaded list should contain a Square", loadedList.contains(sq1));
        assertTrue("The loaded list should contain a Person", loadedList.contains(ps1));
        assertTrue("The loaded list should contain a Rectangle", loadedList.contains(rc2));
    }

    @Test
    public void SaveAndLoadSpecialCharactersTest() throws IOException {
        final String fileName = filesRoot + "specialCharsData";
        Person personWithSpecialChars = new Person("John-Doe", 30);

        List<CsvSerializable> expected = List.of(personWithSpecialChars);

        Storage sg = new Storage();
        sg.save(expected, fileName);

        List<CsvSerializable> loadedList = sg.load(fileName);
        assertEquals("The loaded list should contain exactly one object", 1, loadedList.size());
        assertEquals("The loaded object should match the saved object", personWithSpecialChars, loadedList.get(0));
    }

    @Test
    public void SaveAndLoadWithNullFieldsTest() throws IOException {
        final String fileName = filesRoot + "nullFieldsData";
        Person personWithNullName = new Person(null, 25);  // Name is null

        List<CsvSerializable> expected = List.of(personWithNullName);

        Storage sg = new Storage();
        sg.save(expected, fileName);

        List<CsvSerializable> loadedList = sg.load(fileName);
        assertEquals("The loaded list should contain exactly one object", 1, loadedList.size());
        assertEquals("The loaded object should match the saved object", personWithNullName, loadedList.get(0));
    }

    @Test
    public void ThrowsExceptionForMissingFieldsTest() throws IOException{
        final String fileName = filesRoot + "missingFieldsData";

        // Create a malformed CSV string with missing fields
        String malformedData = Person.class.getPackageName() + ",Person,Roman";  // Missing age
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(malformedData.getBytes());
        }

        Storage sg = new Storage();
        assertThrows(IllegalCsvFormat.class, () -> sg.load(fileName));
    }

    @Test
    public void ThrowsExceptionForIncorrectFileFormat() throws IOException {
        final String fileName = filesRoot + "incorrectFormatData";

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            String malformedData = ",StorageTest$Square,20,20\n,StorageTest$Rectangle,data,20";
            outputStream.write(malformedData.getBytes());
        }

        Storage sg = new Storage();
        assertThrows(IllegalCsvFormat.class, () -> sg.load(fileName));
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
        final String fileName = filesRoot + "noParameterLessConstructor";

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(",StorageTest$1Point,10,10".getBytes());
        }

        Storage sg = new Storage();
        assertThrows(NoParameterlessConstructorException.class, () -> sg.load(fileName));
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
        final String fileName = filesRoot + "noParameterLessConstructor";

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(",StorageTest$1Point,10,10".getBytes());
        }

        Storage sg = new Storage();
        assertThrows(NoParameterlessConstructorException.class, () -> sg.load(fileName));
    }

    @Test
    public void ThrowsExceptionForInvalidPackage() throws IOException{
        final String fileName = filesRoot + "invalidPackage";

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(",Square,10".getBytes());
        }

        Storage sg = new Storage();
        assertThrows(IllegalArgumentException.class, () -> sg.load(fileName));
    }

    @Test
    public void ThrowsExceptionForInvalidClassName() throws IOException{
        final String fileName = filesRoot + "invalidName";

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(",Squaare,10".getBytes());
        }

        Storage sg = new Storage();
        assertThrows(IllegalArgumentException.class, () -> sg.load(fileName));
    }
}
