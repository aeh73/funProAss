package com.example.student;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Student {

    private final int id;
    private final String name;
    private final String course;
    private final String module;
    private final int marks;

    public Student(int id, String name, String course, String module, int marks) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.module = module;
        this.marks = marks;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }

    public String getModule() {
        return module;
    }

    public int getMarks() {
        return marks;
    }

    //@Override
    public String toString1() {
        String format = "| %-20s | %-20s | %-20s | %-20s | %-15s |\n";
        // Defines the divider that will separate each row
        String divider = "+------------+----------------------+----------------------+---------------------------+-------+\n";

        // Builds the table rows as a stream of strings
        String rows = Stream.of(
                String.format(format, "Student ID", "Name", "Course", "Module", "Marks"),
                divider,
                String.format(format, id, name, course, module, marks),
                divider
        ).collect(Collectors.joining());

        // Wraps the rows with the divider and returns the resulting string
        return divider + rows;// + divider;
    }

    public static Student fromString1(String str) {
        String[] tokens = str.split(StudentRegisterFileHandler.DELIMITER);
        int id = Integer.parseInt(tokens[0].trim());
        String name = tokens[1].trim();
        String course = tokens[2].trim();
        String module = tokens[3].trim();
        int marks = Integer.parseInt(tokens[4].trim());
        return new Student(id, name, course, module, marks);
    }

    public static String toString2(Student student) {
        return String.format("%d%s%s%s%d", student.getId(), StudentRegisterFileHandler.DELIMITER, student.getName(), StudentRegisterFileHandler.DELIMITER,
                student.getCourse(), StudentRegisterFileHandler.DELIMITER, student.getModule(), StudentRegisterFileHandler.DELIMITER,
                student.getMarks());
    }
}
