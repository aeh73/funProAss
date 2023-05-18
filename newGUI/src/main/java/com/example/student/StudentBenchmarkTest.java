package com.example.student;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StudentBenchmarkTest {
    private static final Random random = new Random();

    private static List<Student> generateStudents(int size) {
        return random.ints(size, 1, 100)
                .mapToObj(i -> new Student(i, "Student " + i, "Course " + i, "Module " + i, random.nextInt(100)))
                .collect(Collectors.toList());
    }

    private static Predicate<Student> Condition() {
        return student -> student.getMarks() >= 40
                && student.getName().contains("5")
                && student.getCourse().startsWith("Course")
                && student.getModule().endsWith("8");
    }

    public static long testStreamPerformance(int size) {
        List<Student> students = generateStudents(size);

        long start = System.currentTimeMillis();
        List<Student> filteredStudents = students.stream()
                .filter(Condition())
                .collect(Collectors.toList());
        long end = System.currentTimeMillis();

        return end - start;
    }

    public static long testParallelStreamPerformance(int size) {
        List<Student> students = generateStudents(size);

        long start = System.currentTimeMillis();
        List<Student> filteredStudents = students.parallelStream()
                .filter(Condition())
                .collect(Collectors.toList());
        long end = System.currentTimeMillis();

        return end - start;
    }
}
