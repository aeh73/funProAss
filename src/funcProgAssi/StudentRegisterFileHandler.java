package funcProgAssi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class StudentRegisterFileHandler {
    private static final String FILENAME = "student_register.txt";
    private static final String DELIMITER = ",";
    
    public ConcurrentHashMap<Integer, Student> load(String filename) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
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
//            stream.map(line -> line.split(","))
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
    
    public static void save(Map<Integer, Student> students) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            for (Student student : students.values()) {
                writer.write(String.format("%d%s%s%s%s%d%n",
                    student.getId(), DELIMITER, student.getName(), DELIMITER,
                    student.getCourse(), DELIMITER, student.getModule(),
                    student.getMarks()));
            }
        }
    }
}
