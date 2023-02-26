package funcProgAssi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class StudentRegisterFileHandler {
    private static final String FILENAME = "student_register.txt";
    private static final String DELIMITER = ",";
    
    public ConcurrentHashMap<Integer, Student> load(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            ConcurrentHashMap<Integer, Student> register = stream
                    .map(line -> line.split(DELIMITER))
                    .collect(
                            ConcurrentHashMap::new,
                            (map, tokens) -> {
                                int id = Integer.parseInt(tokens[0].trim());
                                String name = tokens[1].trim();
                                String course = tokens[2].trim();
                                String module = tokens[3].trim();
                                int marks = Integer.parseInt(tokens[4].trim());
                                Student student = new Student(id, name, course, module, marks);
                                map.put(id, student);
                            },
                            ConcurrentHashMap::putAll
                    );
            return register;
        }
    }
//    public void load(Map<Integer, Student> register, String filename) throws IOException {
//        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
//            stream.map(line -> line.split(DELIMITER))
//                  .forEach(tokens -> {
//                      int id = Integer.parseInt(tokens[0].trim());
//                      String name = tokens[1].trim();
//                      String course = tokens[2].trim();
//                      String module = tokens[3].trim();
//                      int marks = Integer.parseInt(tokens[4].trim());
//                      Student student = new Student(id, name, course, module, marks);
//                      register.put(id, student);
//                  });
//        }
//    }
    public static void save(ConcurrentHashMap<Integer, Student> register) throws IOException {
    	/*Had issues re-putting the whole hashmap back into the textfile on saving - although the addStudent method had exception handling for
    	 * ID's already in use, the save method will not be using it and will just save the whole hashmap again*/
        // Read the existing content of the file into memory
        List<String> existingLines = Files.readAllLines(Paths.get(FILENAME));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME, true))) {
            // Filter out the student records that are already present in the file
            Stream<String> newLines = register.values().stream()
                    .map(student -> String.format("%d,%s,%s,%s,%d", 
                            student.getId(), student.getName(), student.getCourse(), 
                            student.getModule(), student.getMarks()))
                    .filter(line -> !existingLines.contains(line));

            // Append the new lines to the file
            newLines.forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    
    
//    public static void save(Map<Integer, Student> students) throws IOException {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
//            for (Student student : students.values()) {
//                writer.write(String.format("%d%s%s%s%s%d%n",
//                    student.getId(), DELIMITER, student.getName(), DELIMITER,
//                    student.getCourse(), DELIMITER, student.getModule(),
//                    student.getMarks()));
//            }
//        }
//    }
}
