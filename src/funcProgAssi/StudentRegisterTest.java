package funcProgAssi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

public class StudentRegisterTest {
	//Initialising register for testing
	private StudentRegister register;
	
	//Creating the register beforeeach test
    @BeforeEach
    public void setup() {
        register = new StudentRegister();
    }
    
    /*The ValidConstructor test method checks if a student object can be created successfully with valid constructor parameters. 
     *It uses the assertDoesNotThrow() method to ensure that no exceptions are thrown when a student object is created with valid parameters.*/
    @Test
    public void testValidConstructor() {
    	//Create 2 new student objects
    	Student student = new Student(1, "John", "Computer Science", "Programming", 85);
    	Student student2 = new Student(2, "Jane", "Mathematics", "Calculus", 95);
    	//Create a new studentregister object
        StudentRegister register = new StudentRegister();
        //Add the 2 students
        register.addStudent(student);
        register.addStudent(student2);
        //Add the to a list so i can use the .size() method to get the amount of students added
        List<Student> studentList = register.getAllStudents().collect(Collectors.toList());
        //assertEquals studentList size == 2
        assertEquals(2, studentList.size());
    }

    /*The InvalidConstructor test method checks if exceptions are thrown when a student object is created with invalid constructor parameters. 
     * assertThrows() method is used to ensure that an IllegalArgumentException is thrown when a student object is created with an invalid ID or null name.
	 * The functional programming paradigm is used here in the lambda expressions passed to the assertThrows() method. 
	 * The lambda expressions are used to specify the code that should be executed to trigger the exception. 
	 * The -> is the operator that separates the input arguments from the code that should be executed.
	*/
    @Test
    public void testInvalidConstructor() {
    	 StudentRegister register = new StudentRegister();
    	  assertThrows(IllegalArgumentException.class, () -> register.addStudent(new Student(0, "Jane", "Mathematics", "Calculus", 95)));
    	  assertThrows(IllegalArgumentException.class, () -> register.addStudent(new Student(2, null, "Physics", "Mechanics", 80)));
    }

    /*The DuplicateStudents test method checks if an IllegalStateException is thrown when a duplicate student is added to the student register. 
     * It uses the assertDoesNotThrow() method to add a student to the student register without throwing any exceptions.
     * It then uses the assertThrows() method to ensure that an IllegalStateException is thrown when a duplicate student is added to the register.
	 * lambda expressions passed to the assertDoesNotThrow() and assertThrows() methods. 
	 * The lambda specify the code that should be executed to add a student to the register or trigger the exception. 
	 */
    @Test
    public void testDuplicateStudents() {
    	Student student1 = new Student(1, "John", "Computer Science", "Programming", 85);
        Student student2 = new Student(1, "Jane", "Mathematics", "Calculus", 95);
        StudentRegister register = new StudentRegister();
        assertDoesNotThrow(() -> register.addStudent(student1));
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> register.addStudent(student2));
        assertEquals("Student ID already in use..", e.getMessage());
    }
    
    /*The RemoveStudent test method checks if a student has been removed by using my getAllStudents method from StudentRegister
     * This method collects all students into a list and the anyMatch method is used to check if the removed ID is still present
     * It also checks if an IllegalArgumentException occurs when trying to remove a studentID that does not exist*/
    @Test
    public void testRemoveStudent() {
    	Student student1 = new Student(1, "John Doe", "Computer Science", "Programming 101", 80);
    	Student student2 = new Student(2, "Jane Doe", "Computer Science", "Programming 201", 90);
    	Student student3 = new Student(3, "Bob Smith", "Mathematics", "Calculus 101", 75);
    	register.addStudent(student1);
        register.addStudent(student2);
        register.addStudent(student3);
        
        register.removeStudent(2);
        Assertions.assertFalse(register.getAllStudents().anyMatch(student -> student.getId() == 2));
        
        register.removeStudent(3);
        Assertions.assertFalse(register.getAllStudents().anyMatch(student -> student.getId() == 3));
        
        // Test removing a student that does not exist in the registry
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> register.removeStudent(4));
        assertEquals("Student not found..", ex.getMessage());
    }
    
}