package funcProgAssi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class StudentRegisterFileHandler {
	//declares immutable variable to store the studentdata
    private static final String FILENAME = "student_register.txt";

    //loadFromFile method takes concurrenthashmap of int keys and student object values as input parameters
    public static void loadFromFile(ConcurrentHashMap<Integer, Student> register) {
    	//try/catch bufferedreader object to read the file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
        	//reader.lines() reads the text from the file line-by-line as a stream<string> object
            reader.lines()
            		//As the text file will hold strings we need to parse each line to transform it into a student object
            		//this is done by applying the parseStudent method on each line of the file
                    .map(StudentRegisterFileHandler::parseStudent)
                    //Iterates over each student object in the stream and adds it to the register map with id as the key and student object as the value
                    .forEach(student -> register.put(student.getId(), student));
        } catch (IOException e) {
            // Handle exception - print an error message
            System.err.println("Error loading data from file: " + e.getMessage());
        }
    }

    //saveToFile method takes concurrenthashmap of int keys and student object values as input parameters
    //with 2 file handling exceptions - 1 for writing and 1 for saving the data
    public static void saveToFile(ConcurrentHashMap<Integer, Student> register) {
    	//try/catch bufferedwriter object to write the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
        	//gets the stream of all the student objects in the register map
            register.values().stream()
            		//As the text file will only be read by separating each field by commas and each object by a new line
    				//we will need to format each student object to transform it into a stream of formatted text
                    .map(StudentRegisterFileHandler::formatStudent)
                    //iterates over each formatted line in the stream and writes it to the file using the bufferedwriter object
                    //
                    .forEach(line -> {
                        try {
                            writer.write(line);
                            writer.newLine();
                        } catch (IOException e) {
                            // Handle exception - print an error message
                            System.err.println("Error writing data to file: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            // Handle exception - print an error message
            System.err.println("Error saving data to file: " + e.getMessage());
        }
    }

    /*This method takes a student object and returns a formatted string that represents the data in the object using the student class getters
     * The formatted string consists of the values of the Student object's fields separated by commas. This is the format used 
     * when writing the Student object to the file.
     * The use of String.format method in this method is an example of functional programming as it allows the code to be written in a more concise and modular way,
     *  as opposed to manually concatenating strings. It also allows for easy modification of the format of the string output without having to 
     *  modify each of the string concatenation operations.*/
    private static String formatStudent(Student student) {
    	//"%d,%s,%s,%s,%d" specifies there will be 5 different fields, d representing ints and s representing strings
        return String.format("%d,%s,%s,%s,%d", student.getId(), student.getName(),
                student.getCourse(), student.getModule(), student.getMarks());
    }

    /*The parseStudent method takes a line from the input file as a string and returns a Student object that represents the data in that line. 
     * The method first splits the line into an array of strings using the comma as the separator. 
     * Then it extracts the necessary information from the array and creates a new Student object using the constructor that takes 
     * an ID, name, course, module, and marks. This method demonstrates the use of functional programming by using the split method 
     * to split the line into an array of strings and then extracting the necessary information using array indexing.*/
    private static Student parseStudent(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0]);
        String name = parts[1];
        String course = parts[2];
        String module = parts[3];
        int marks = Integer.parseInt(parts[4]);
        return new Student(id, name, course, module, marks);
    }
}