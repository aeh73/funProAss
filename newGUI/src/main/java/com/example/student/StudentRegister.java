package com.example.student;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentRegister {

    private final ConcurrentHashMap<Integer, Student> register = new ConcurrentHashMap<>();

    /*constructor to be used for file I/O*/
    public StudentRegister() {
    }

    public boolean isEmpty() {
        return register.isEmpty();
    }

    public ConcurrentHashMap<Integer, Student> getRegister() {
        return new ConcurrentHashMap<>(register);
    }

    public void saveFile(Path filePath) throws IOException {
        StudentRegisterFileHandler handler = new StudentRegisterFileHandler();
        handler.save(register, filePath);
    }

    public void load(Path filePath) throws IOException {
        StudentRegisterFileHandler handler = new StudentRegisterFileHandler();
        ConcurrentHashMap<Integer, Student> newRegister = handler.load(filePath);
        this.register.clear();
        this.register.putAll(newRegister);
    }

    public void addStudent(Student student) {
        Optional.ofNullable(student)
                .filter(s -> s.getId() > 0
                        && s.getName() != null && !s.getName().isEmpty()
                        && s.getCourse() != null && !s.getCourse().isEmpty()
                        && s.getModule() != null && !s.getModule().isEmpty()
                        && s.getMarks() >= 0 && s.getMarks() <= 100)
                .ifPresent(s -> register.put(s.getId(), s));
    }
//    public void addStudent(Student student) {
//        Optional.ofNullable(student)
//                .filter(s -> s.getId() > 0
//                        && s.getName() != null && !s.getName().isEmpty()
//                        && s.getCourse() != null && !s.getCourse().isEmpty()
//                        && s.getModule() != null && !s.getModule().isEmpty()
//                        && s.getMarks() >= 0 && s.getMarks() <= 100)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid student data.."));
//        register.entrySet()
//                .stream()
//                .filter(entry -> entry.getKey() == student.getId())
//                .findAny()
//                .ifPresent(entry -> {
//                    throw new IllegalStateException("Student ID already in use..");
//                });
//        register.put(student.getId(), student);
//        // saveFile();
//    }
    public boolean exists(int id) {
        return register.containsKey(id);
    }



    public Student removeStudent(int id) {
        Optional<Student> student = Optional.ofNullable(register.get(id));
        student.orElseThrow(() -> new IllegalArgumentException("Student not found.."));
        register.remove(id);
        return student.get();
    }

    public List<String> getAllStudents() {
        return register.values().stream().map(Student::toString1).collect(Collectors.toList());
    }

    public List<Student> getStudentsByPredicate(Predicate<Student> predicate) {
        return register.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    public List<Student> getSortedByMarksAsc() {
        return register.values().stream()
                .sorted(Comparator.comparingInt(Student::getMarks))
                .collect(Collectors.toList());
    }

    public List<Student> getSortedByMarksDesc() {
        return register.values().stream()
                .sorted(Comparator.comparingInt(Student::getMarks).reversed())
                .collect(Collectors.toList());
    }

    public List<Student> getSortedByNameAsc() {
        return register.values().stream()
                .sorted(Comparator.comparing(Student::getName))
                .collect(Collectors.toList());
    }

    public List<Student> getSortedByNameDesc() {
        return register.values().stream()
                .sorted(Comparator.comparing(Student::getName).reversed())
                .collect(Collectors.toList());
    }


    public Predicate<Student> getIdPredicate(String idText) {
        return s -> idText.isEmpty() || Integer.toString(s.getId()).equals(idText);
    }

    public Predicate<Student> getNamePredicate(String nameText) {
        return s -> nameText.isEmpty() || s.getName().toLowerCase().startsWith(nameText);
    }

    public Predicate<Student> getCoursePredicate(String courseText) {
        return s -> courseText.isEmpty() || s.getCourse().toLowerCase().startsWith(courseText);
    }

    public Predicate<Student> getModulePredicate(String moduleText) {
        return s -> moduleText.isEmpty() || s.getModule().toLowerCase().startsWith(moduleText);
    }

    public Predicate<Student> getMarksPredicate(String marksText) {
        return s -> marksText.isEmpty() || Integer.toString(s.getMarks()).equals(marksText);
    }

    public Predicate<Student> getCombinedPredicate(String idText, String nameText, String courseText
                                                    , String moduleText, String marksText) {
        List<Predicate<Student>> predicates = new ArrayList<>();
        predicates.add(getIdPredicate(idText));
        predicates.add(getNamePredicate(nameText));
        predicates.add(getCoursePredicate(courseText));
        predicates.add(getModulePredicate(moduleText));
        predicates.add(getMarksPredicate(marksText));
        return predicates.stream().reduce(Predicate::and).orElse(student -> true);
    }

    public double calculateAverageMark() {
        return register.values().stream()
                .mapToDouble(Student::getMarks) // map each student to its marks
                .average() // calculate the average
                .orElse(0.0); // return 0.0 if there are no students
    }

    public List<Student> getStudentsWhoPassed() {
        return register.values().stream()
                .filter(student -> student.getMarks() >= 40)
                .collect(Collectors.toList());
    }

    public List<Student> getStudentsWhoFailed() {
        return register.values().stream()
                .filter(student -> student.getMarks() < 40)
                .collect(Collectors.toList());
    }

}