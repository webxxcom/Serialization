package main.java.storage;

public interface CsvSerializable {
    /**
     * Method to return a {@link String} of all class' fields separated
     * by comma in such a precedence that method {@link #fromFields(String[])}
     * then obtains these values and initializes an object in accordance with them.
     * <p>
     * For example, class Person:
     * <pre><code>
     *class Person implements CsvSerializable{
     *  String name;
     *  int id;
     *
     *  public Person(String name, int id){...}
     *
     *  &#64;Override
     *  public String getCsvString() {
     *      return name + ',' + age;
     *  }
     *</code></pre>
     * @return {@link String} containing all the class' fields separated
     * by a comma to work with {@link #fromFields(String[])} method
     */
    String getCsvString();

    /**
     * Initializes the {@link CsvSerializable} interface by calling this overloaded method
     *
     * @param vals an array of String representations of class' fields obtained
     *             via {@link #getCsvString()}.
     * @return an object initialized by fields specified in {@link #getCsvString()}
     * @throws IllegalCsvFormat if there is an error in parsing the field
     */
    CsvSerializable fromFields(String[] vals) throws IllegalCsvFormat;

    /**
     * Check the number of fields to correspond to the number of fields in class' constructor
     * @param vals an array of string fields
     * @param n expected number of fields
     * @throws IllegalCsvFormat if {@code vals.length != n}
     */
    static void checkNumberOfFields(String[] vals, int n) throws IllegalCsvFormat {
        if(vals.length != n) {
            throw new IllegalCsvFormat("Invalid number of fields to instantiate an object");
        }
    }
}
