package com.example.newgui;

import com.example.student.Student;
import com.example.student.StudentRegister;
import com.example.student.StudentRegisterFileHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketOption;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//import static com.example.student.StudentRegister.studentRegister;


public class HelloController implements Initializable {
    private StudentRegister studentRegister = new StudentRegister();
    @FXML
    private ChoiceBox<String> courses, modules;
    // Map to store the secondary choicebox options to the first choicebox input
    private Map<String, List<String>> optionsMap = new HashMap<>();
    @FXML
    private ListView<String> listView;
    @FXML
    private TableView<Student> tblView;
    @FXML
    private TextField idField, nameField, courseField, moduleField, marksField;
    @FXML
    private Path filePath;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
//        loadCourse();
//        loadModule();
//        loadCourseModule();
    }
    /*Clears TextField inputs*/
    public void clearInputs(){
        // add any other TextField instances to clear here
        idField.clear();
        nameField.clear();
        courseField.clear();
        moduleField.clear();
        marksField.clear();
    }

    public void handleClearDisplay() {
        listView.getItems().clear();
        clearInputs();
    }

    public void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @FXML
    private void handleFileOpen() {
        // Show a file chooser dialog to let the user select a file
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Store the file path
                filePath = selectedFile.toPath();

                // Load the contents of the file into a ConcurrentHashMap
                studentRegister.load(filePath);

                // Clear the existing items in the list view and add the students from the register
                listView.getItems().clear();
                listView.getItems().addAll(studentRegister.getRegister().values().stream()
                        .map(Student::toString1) // use the toString method of Student
                        .collect(Collectors.toList()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void handleFileSave() throws IOException {
        // Show a file chooser dialog to the user and get file destination
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");

        // Add a file extension filter for .txt files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show the save file dialog and get the selected file
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            // Convert the file to a Path object
            filePath = file.toPath();

            // Save the register to the selected file
            studentRegister.saveFile(filePath);
        }
    }


    public void handleDisplayAll() {
        if (studentRegister.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "No Data Loaded", null, "No students registered.");
            listView.getItems().clear();
        } else {
            listView.getItems().clear();
            listView.getItems().addAll(studentRegister.getAllStudents());
        }
    }

    @FXML
    private void handleAddStudent() {
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            String course = courseField.getText();
            String module = moduleField.getText();
            int marks = Integer.parseInt(marksField.getText());

            Student student = new Student(id, name, course, module, marks);
            studentRegister.addStudent(student);
            listView.getItems().add(student.toString1());

            // Save the updated student register to file
            studentRegister.saveFile(filePath);
            clearInputs();
            showAlert(Alert.AlertType.ERROR, "Success", null, "The student has been successfully added.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", null, "ID and Marks must be numbers..");
        } catch (IllegalArgumentException | IllegalStateException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", null, e.getMessage());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error Saving File", null, "An error occurred while saving the student register to file.");
        }
    }

    @FXML
    public void handleRemoveStudent(){
        try {
            int id = Integer.parseInt(idField.getText());
            Student removedStudent = studentRegister.removeStudent(id);
            if (removedStudent != null) {
                // Update the list view
                listView.getItems().remove(removedStudent.toString1());

                // Save the updated student register to file
                studentRegister.saveFile(filePath);
                clearInputs();
                showAlert(Alert.AlertType.ERROR, "Success", null, "The student has been successfully removed.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", null, "Student not found..");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", null, "Please enter a valid student ID..");
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error..", null, e.getMessage());
        }
    }
    @FXML
    private void handleSearch() {
        String idText = idField.getText();
        String nameText = nameField.getText().toLowerCase();
        String courseText = courseField.getText().toLowerCase();
        String moduleText = moduleField.getText().toLowerCase();
        String marksText = marksField.getText();

        // create the combined predicate from TextField inputs
        Predicate<Student> combinedPredicate = studentRegister.getCombinedPredicate(
                idText,
                nameText,
                courseText,
                moduleText,
                marksText
        );

        try {
            List<Student> matchingStudents = studentRegister.getStudentsByPredicate(combinedPredicate);
            if (matchingStudents.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "No matching students found.", null, "No matching students were found for the provided search criteria.");
            } else {
                listView.getItems().clear();
                matchingStudents.forEach(student -> listView.getItems().add(student.toString1()));
                showAlert(Alert.AlertType.ERROR, "Students found.", null, "These are the matching students.");
            }
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "Search Criteria Required", null, "Please provide at least one search criteria.");
        }
    }

    @FXML
    private void handleAverageMarks() {
        // Calculate the average mark
        double averageMark = studentRegister.calculateAverageMark();
        // Display the average in a popup box
        showAlert(Alert.AlertType.INFORMATION, "Average Mark", null, "The average mark is: " + averageMark);
    }

    @FXML
    public void handleSortByMarksAsc() {
        List<Student> sortedStudents = studentRegister.getSortedByMarksAsc();
        listView.getItems().clear();
        listView.getItems().addAll(sortedStudents.stream().map(Student::toString1).collect(Collectors.toList()));
    }

    @FXML
    public void handleSortByMarksDesc() {
        List<Student> sortedStudents = studentRegister.getSortedByMarksDesc();
        listView.getItems().clear();
        listView.getItems().addAll(sortedStudents.stream().map(Student::toString1).collect(Collectors.toList()));
    }

    @FXML
    public void handleSortByNameAsc() {
        List<Student> sortedStudents = studentRegister.getSortedByNameAsc();
        listView.getItems().clear();
        listView.getItems().addAll(sortedStudents.stream().map(Student::toString1).collect(Collectors.toList()));
    }

    @FXML
    public void handleSortByNameDesc() {
        List<Student> sortedStudents = studentRegister.getSortedByNameDesc();
        listView.getItems().clear();
        listView.getItems().addAll(sortedStudents.stream().map(Student::toString1).collect(Collectors.toList()));
    }
}