package com.example.newgui;

import com.example.student.Student;
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

import static com.example.student.StudentRegister.studentRegister;


public class HelloController implements Initializable {
//    ObservableList courseList = FXCollections.observableArrayList(
//            "Mathematics","Physics","Engineering","Computer Science");
//    ObservableList modulesList = FXCollections.observableArrayList(
//            "Programming 1","Programming 2","Data Structures","Calculus 1","Linear Algebra","Probability","Statistics","Electromagnetism","Thermodynamics","Methods in Mechanics");
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
        loadCourseModule();
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
    private void clearChoiceBox() {
        // add any other ChoiceBox instances to clear here
        modules.getSelectionModel().clearSelection();
        courses.getSelectionModel().clearSelection();
    }
    public void handleClearDisplay() {
        listView.getItems().clear();
        clearInputs();
        clearChoiceBox();
    }

    private void loadCourseModule(){
        // Set up the options for the first choice box
        List<String> courseOptions = Arrays.asList("Mathematics","Physics","Engineering","Computer Science");
        courses.getItems().addAll(courseOptions);

        // Set up the options for the second choice box
        List<String> moduleOptions1 = Arrays.asList("Calculus 1", "Linear Algebra", "Probability", "Statistics");
        List<String> moduleOptions2 = Arrays.asList("Mechanics", "Electromagnetism", "Thermodynamics");
        List<String> moduleOptions3 = Arrays.asList("Methods in Mechanics");
        List<String> moduleOptions4 = Arrays.asList("Programming 1", "Programming 2", "Data Structures", "Data Mining");
        optionsMap.put("Mathematics", moduleOptions1);
        optionsMap.put("Physics", moduleOptions2);
        optionsMap.put("Engineering", moduleOptions3);
        optionsMap.put("Computer Science", moduleOptions4);

        // Set up the event handler for the first choice box to determine the second
        courses.setOnAction(event -> {
            String selectedCourse = courses.getSelectionModel().getSelectedItem();
            List<String> secondOptions = optionsMap.get(selectedCourse);
            modules.getItems().clear(); // Clear existing options

            if (secondOptions != null) {
                modules.getItems().addAll(secondOptions); // Add new options
            }

            Predicate<Student> coursePredicate = studentRegister.getCbCoursePredicate(selectedCourse);
            List<Student> filteredStudents = studentRegister.getStudentsByPredicate(coursePredicate);
            listView.getItems().clear(); // Clear existing items
            listView.getItems().addAll(filteredStudents.toString());
        });
    }

    @FXML
    private void handleFileOpen() {
        // Show a file chooser dialog to let the user select a file
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Store the file path
                filePath = selectedFile.toPath();

                // Load the contents of the file into a ConcurrentHashMap
                StudentRegisterFileHandler fileHandler = new StudentRegisterFileHandler();
                ConcurrentHashMap<Integer, Student> registerData = fileHandler.load(filePath);

                // Clear the existing items in the list view and add the students from the register
                listView.getItems().clear();
                listView.getItems().addAll(registerData.values().stream()
                        .map(Student::toString1) // use the toString method of Student
                        .collect(Collectors.toList()));

                // Set the register to the new ConcurrentHashMap
                studentRegister.setRegister(registerData);

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
            Path filePath = file.toPath();

            // Save the register to the selected file
            studentRegister.saveFile(filePath);
        }
    }

    public void handleDisplayAll() {
        if (studentRegister.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Data Loaded");
            alert.setHeaderText(null);
            alert.setContentText("No students registered.");
            alert.showAndWait();
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("The student has been successfully added.");
            alert.showAndWait();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("ID and Marks must be numbers..");
            alert.showAndWait();
        } catch (IllegalArgumentException | IllegalStateException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Saving File");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while saving the student register to file.");
            alert.showAndWait();
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("The student has been successfully removed.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText(null);
                alert.setContentText("Student not found..");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid student ID..");
            alert.showAndWait();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error..");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No matching students found.");
                alert.setHeaderText(null);
                alert.setContentText("No matching students were found for the provided search criteria.");
                alert.showAndWait();
            } else {
                listView.getItems().clear();
                matchingStudents.forEach(student -> listView.getItems().add(student.toString1()));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Students found.");
                alert.setHeaderText(null);
                alert.setContentText("These are the matching students.");
                alert.showAndWait();
            }
        } catch (IllegalArgumentException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Search Criteria Required");
            alert.setHeaderText(null);
            alert.setContentText("Please provide at least one search criteria.");
            alert.showAndWait();
        }

    }
    @FXML
    private void handleTableView() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Path filePath = selectedFile.toPath();
            try {
                List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

                // Create the table columns
                TableColumn<Student, String> idColumn = new TableColumn<>("ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

                TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

                TableColumn<Student, String> courseColumn = new TableColumn<>("Course");
                courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));

                TableColumn<Student, String> moduleColumn = new TableColumn<>("Module");
                moduleColumn.setCellValueFactory(new PropertyValueFactory<>("module"));

                TableColumn<Student, String> marksColumn = new TableColumn<>("Marks");
                marksColumn.setCellValueFactory(new PropertyValueFactory<>("marks"));

                // Add the columns to the table view
                tblView.getColumns().setAll(idColumn, nameColumn, courseColumn, moduleColumn, marksColumn);

                // Create an observable list of students from the loaded lines
                ObservableList<Student> students = FXCollections.observableArrayList();
                for (String line : lines) {
                    String[] values = line.split(",");
                    Student student = new Student(Integer.parseInt(values[0]), values[1], values[2], values[3], Integer.parseInt(values[4]));
                    students.add(student);
                }

                // Set the observable list as the items of the table view
                tblView.setItems(students);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}