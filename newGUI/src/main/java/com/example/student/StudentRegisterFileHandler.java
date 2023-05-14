package com.example.student;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class StudentRegisterFileHandler {
    //private static final String FILENAME = "student_register.txt";
    private static String filename;
    public static final String DELIMITER = ",";

    public ConcurrentHashMap<Integer, Student> load(Path filePath) throws IOException {
        try (Stream<String> stream = Files.lines(filePath)) {
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

    public static void save(ConcurrentHashMap<Integer, Student> register, Path filePath) throws IOException {
        if (filePath == null || filePath.toString().isEmpty()) {
            if (filename != null && !filename.isEmpty()) {
                filePath = Paths.get(filename);
            } else {
                filePath = Paths.get("new_student_register.txt");
            }
        }

        Stream<String> newLines = register.values().stream()
                .map(student -> String.format("%d,%s,%s,%s,%d",
                        student.getId(), student.getName(), student.getCourse(),
                        student.getModule(), student.getMarks()));

        // Pass StandardOpenOption.APPEND to append to the file instead of overwriting it
        //Files.write(filePath, newLines.toList(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        // Pass StandardOpenOption.TRUNCATE_EXISTING to overwrite to the file instead of appending
        Files.write(filePath, newLines.toList(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
//    public static void save(ConcurrentHashMap<Integer, Student> register) throws IOException {
//
//
//        Stream<String> newLines = register.values().stream()
//                .map(student -> String.format("%d,%s,%s,%s,%d",
//                        student.getId(), student.getName(), student.getCourse(),
//                        student.getModule(), student.getMarks()));
//
//        Files.write(Paths.get(FILENAME), newLines.toList());
//    }

//    public ConcurrentHashMap<Integer, Student> load(String fileName) throws IOException {
//        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
//            ConcurrentHashMap<Integer, Student> register = stream
//                    .map(line -> line.split(DELIMITER))
//                    .collect(
//                            ConcurrentHashMap::new,
//                            (map, tokens) -> {
//                                int id = Integer.parseInt(tokens[0].trim());
//                                String name = tokens[1].trim();
//                                String course = tokens[2].trim();
//                                String module = tokens[3].trim();
//                                int marks = Integer.parseInt(tokens[4].trim());
//                                Student student = new Student(id, name, course, module, marks);
//                                map.put(id, student);
//                            },
//                            ConcurrentHashMap::putAll
//                    );
//            return register;
//        }
//    }
}