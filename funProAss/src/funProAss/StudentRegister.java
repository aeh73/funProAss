package funProAss;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ahmed
 *	University register for students using the functional programming paradigm.
 *	The system, as a minimum, should allow the developer to:
	a)	Add a new student.
	b)	Remove an existing student.
	c)	Query existing students by their name, ID, course, and module.
	d)	Perform queries of interest (examples are for reference only, further own ideas are encouraged):
			-	Get all students on a given module and sort entries in descending order, based on marks.
			-	Get all students on a given course whose names match a given text.
			-	Get all students whose names begin with a given letter.
    e)  Additions and additional queries:
    		-   ConcurrentHashMap for thread safety and the ability to allow multiple users to use the system at the same time. --
    		-   Add a file which will hold the map that will be loaded at the start of the program and save when the program is shut - Data persistence.
    		-   Queries for average mark, top 10%(maybe top quartile), bottom 10%(maybe bottom quartile) 
    		-	Add a method to update an existing student
    		
    improvements needed/would be nice:
     	- exception handling in methods that require it(needed) --
     	- implement a feature to calculate+display statistics about the register, average marks for each course/module, number of students per course/module, number of students close to failing etc.(would be nice)
     	- improve the toString method to display the student register in a user-friendly format, such as a table.(needed) - check apache commons text
     	- implement a database to store the register instead of a concurrent map(would be nice) - might end up querying using sql rather than functional programming paradigm
     	- add tests for each part of the system(needed)
     	- File watcher for textfile modifications
     	
    RECHECK COMMENTS AS CODE IS CHANGING
    REMOVE REDUNDANT COMMENTS
 */

public class StudentRegister {
	// Use a concurrent hash map to store students to ensure thread safety i.e. multiple users/threads can access or modify the system effectively.
	protected static ConcurrentHashMap<Integer, Student> register = new ConcurrentHashMap<>();
	protected static StudentRegister studentRegister = new StudentRegister(register);
	protected static final String FILENAME = "student_register.txt";
	protected static StudentRegisterFileHandler fileHandler;
	
	/*Had to create a getRegister method as alot of my methods are void and have no return types, had issues using methods on primitives*/
//    public static ConcurrentHashMap<Integer, Student> getRegister() {
//        return register;
//    }
    /*Method to create studentregister objects with no arguments - used in testing*/
//    public StudentRegister() { 	
//            this.register = register;     
//	}	
    
	/*constructor to be used for file I/O*/
	public StudentRegister(ConcurrentHashMap<Integer, Student> register) {
        StudentRegister.register = register;
    }
	
	public void saveFile() {
		//ADD the save method after a student has been added so when next
        //query is loaded the new student will be part of it
        try {
			StudentRegisterFileHandler.save(register);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void loadFile() {
		fileHandler = new StudentRegisterFileHandler();
        try {
            register = fileHandler.load(FILENAME); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return register;
	}

	/*String instance of the filename - change filename in 1 place to maintain code*/
	//private static final String FILENAME = "student_register.txt";
	// a) Method to add a new student to the registry
	/* 1. The Optional class is used to handle the null values of the student object - using an optional type represents the presence or absence of a value 
	 * which is a safer way to handle nulls as we force the code to handle the absense of a value explicitly. 
	 * We check if the student is not null and if it has a valid ID and name. If any of the conditions are not met, 
	 * then an IllegalArgumentException is thrown with the message "Invalid input".
	 * 2. The entrySet() method is used to get a Set view of the entries in the register map. Then, we use the stream() method to create a stream of the entries, 
	 * and the filter() method to filter the entries where the key is equal to the student ID. We use the findAny() method to get any entry that 
	 * satisfies the filter condition. If an entry is found, then an IllegalStateException is thrown with the message "Student ID already in use".
	 * IF both conditions are met then the new student is added to the register map using the put method with its ID as the key and the student object as the value.
	 * NOTE: this method allows multiple students with the same first and last name but not same ID.*/
    public void addStudent(Student student) {
    	 Optional.ofNullable(student)
         		.filter(s -> s.getId() > 0 
         			 && s.getName() != null && !s.getName().isEmpty() 
                     && s.getCourse() != null && !s.getCourse().isEmpty() 
                     && s.getModule() != null && !s.getModule().isEmpty() 
                     && s.getMarks() >= 0 && s.getMarks() <= 100)
         		.orElseThrow(() -> new IllegalArgumentException("Invalid student data.."));
    	register.entrySet()
    			.stream()
    			.filter(entry -> entry.getKey() == student.getId())
    			.findAny()
    			.ifPresent(entry -> {
    				throw new IllegalStateException("Student ID already in use..");
    			});
        register.put(student.getId(), student);
        saveFile();
    }
	
    // b) Method to remove a student from the registry
    /* This method uses the 'computeIfPresent' method of ConcurrentHashMap which takes an int (the key) as an argument and computes its value based on the hash, 
     * if the key exists we remove the key and its associated value by passing a lambda expression that returns null effectively removing the key-value pair*/
    public Student removeStudent(int id) {
    	Optional<Student> student = Optional.ofNullable(register.get(id));
        student.orElseThrow(() -> new IllegalArgumentException("Student not found.."));
        register.remove(id);
        saveFile();
        return student.get();
    	
    }
    
    // Method to get a stream of all students in the register - returns a list of student objects
    public Stream<Student> getAllStudents() {
        return register.values().stream();
        		
        					
    }
    //Method to print all of the students in the register
    public void printAllStudents() {
    	register.values().stream().forEach(student -> System.out.println(student));
    }
    
    

    //c)	Query existing students by their name, ID, course, and module.
	/*
	 * getStudentByName method takes a string 'name' as an argument and returns a list of student objects that match the given string.
	 * - Using values() and stream() we can get a stream of all the value instances of the student class from the list
	 * - It can then be filtered using the filter method and lambda expression 'student ->'
	 * - student.getName().equalsIgnoreCase(name) returns true for any student object whose name matches the given string while ignoring case
	 * - The filtered list is then collected to a new list using the collect method and collectors.toList collector
	*/
    public List<Student> getStudentByName(String name) {
       	return register.values().stream()
        			.filter(student -> student.getName().equalsIgnoreCase(name))
        			.collect(Collectors.toList()); 			
    }
    

    
    public void getStudentsByName3(String name) {
    	List<Student> matchingStudents = 
    			register.values().stream()
    			.filter(student -> student.getName().equalsIgnoreCase(name))
    	        .collect(Collectors.toList());
    	matchingStudents.forEach(student -> System.out.println(student));
    }
    
    /*
	 * Same as getStudentByName except it returns a list of students starting with the given string/character
	*/
    public List<Student> getStudentsByNameStartingWith(String letter) {
        return register.values().stream()
        		.filter(student -> student.getName().startsWith(letter))
        		.collect(Collectors.toList());
    }
    
    /*
	 * Similar to the getStudentByName method except it takes an int as an argument and returns a student object that match the given int
	*/
    public Student getStudentById(int id) {
    	return register.values().stream()
                .filter(student -> student.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    /*
	 * Same as getStudentByName except it gets filtered by course
	*/
    public List<Student> getStudentByCourse(String course) {
    	return register.values().stream()
    			.filter(student -> student.getCourse().equalsIgnoreCase(course))
    			.collect(Collectors.toList());
    }
    
    /*
	 * Same as getStudentByName except it gets filtered by module
	*/
    public List<Student> getStudentByModule(String module) {
    	return register.values().stream()
    			.filter(student -> student.getModule().equalsIgnoreCase(module))
    			.collect(Collectors.toList());
    }
    
    public List<Student> getStudentsByModuleAndSortByMarksDescending(String module) {
        return register.values().stream()
                .filter(student -> student.getModule().equalsIgnoreCase(module))
                .sorted((student1, student2) -> student2.getMarks() - student1.getMarks())
                .collect(Collectors.toList());
    }
    
    
    public static void main(String[] args) {
//    	StudentRegister register = new StudentRegister();
//        Student student1 = new Student(1, "John Doe", "Computer Science", "Software Engineering", 85);
//        register.addStudent(student1);
//        Student student2 = new Student(2, "Jane Doe", "Computer Science", "Artificial Intelligence", 90);
//        register.addStudent(student2);
//        Student student3 = new Student(3, "Jim Smith", "Computer Science", "Data Science", 95);
//        register.addStudent(student3);
//        Student student4 = new Student(4, "Jenny Johnson", "Computer Science", "Software Engineering", 80);
//        register.addStudent(student4);
//        Student student5 = new Student(5, "Jim Brown", "Computer Science", "Artificial Intelligence", 85);
//        register.addStudent(student5);
//        Student student6 = new Student(6, "Jane Lee", "Computer Science", "Data Science", 90);
//        register.addStudent(student6);
//        Student student7 = new Student(7, "Jim Clark", "Computer Science", "Software Engineering", 95);
//        register.addStudent(student7);
//        Student student8 = new Student(8, "Jenny Martinez", "Computer Science", "Artificial Intelligence", 80);
//        register.addStudent(student8);
//        Student student9 = new Student(9, "Jim Wilson", "Computer Science", "Data Science", 85);
//        register.addStudent(student9);
//        Student student10 = new Student(10, "Jane Phillips", "Computer Science", "Software Engineering", 90);
//        register.addStudent(student10);
//        Student student11 = new Student(11, "John Doe2", "Engineering", "Mechanical Engineering", 75);
//        register.addStudent(student11);
//        Student student12 = new Student(12, "Jane Doe2", "Engineering", "Methods in Mechanics", 95);
//        register.addStudent(student12);
//        Student student13 = new Student(13, "Jim Smith2", "Engineering", "Engineering Design", 70);
//        register.addStudent(student13);
//        Student student14 = new Student(14, "Jenny Johnson2", "Engineering", "Engineering Design", 95);
//        register.addStudent(student14);
//        Student student15 = new Student(15, "Jim Brown2", "Engineering", "Methods in Mechanics", 90);
//        register.addStudent(student15);
    	
//    	ConcurrentHashMap<Integer, Student> register = new ConcurrentHashMap<>();
//    	StudentRegisterFileHandler fileHandler = new StudentRegisterFileHandler();
//    	try {
//			fileHandler.load("student_register.txt");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    	/*Create a new StudentRegister object using the loaded register*/
    	
    	//StudentRegister studentRegister = new StudentRegister(register);
    	
//    	ConcurrentHashMap<Integer, Student>
//    	
//    	register = new ConcurrentHashMap<Integer, Student>();
//		try {
//			StudentRegisterFileHandler studentRegisterFileHandler = new StudentRegisterFileHandler();
//			studentRegisterFileHandler.load(FILENAME);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	StudentRegister studentRegister = new StudentRegister(register);
    	
    	
//    	ConcurrentHashMap<Integer, Student> register = new ConcurrentHashMap<Integer, Student>();
//		try {
//			register = new StudentRegisterFileHandler().load(FILENAME);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	StudentRegister studentRegister = new StudentRegister(register);
//        
        /*Get a list of all students*/
    	loadFile();
    	//StudentRegister studentRegister = new StudentRegister(register);
    	//studentRegister.printAllStudents();
  
    	//List<Student> studentList2 = studentRegister.getAllStudents().collect(Collectors.toList());
       // System.out.println(studentList2);
        
        //Student student55 = new Student(55,"Jim Douglas", "Engineering", "Methods in Mechanics", 81);
        //studentRegister.addStudent(student55);
        
        //studentRegister.removeStudent(55);
        //List<Student> studentList = studentRegister.getAllStudents().collect(Collectors.toList());
        //System.out.println(studentList);
    	//studentRegister.printAllStudents();
    	
    	//Student student9095 = new Student(60,"Jim Douglas", "Engineering", "Methods in Mechanics", 81);
        //studentRegister.addStudent(student9095);
    	//studentRegister.removeStudent(60);
    	studentRegister.getStudentById(51);
        
    	
    	studentRegister.printAllStudents();
        
        
        /*Get a list of student with the given name*/
        //List<Student> studentList = studentRegister.getStudentByName("Frank Lee");
        //System.out.println(studentList);
        
        /*Get a list of all students starting with a given character or characters*/
        //List<Student> studentList = studentRegister.getStudentsByNameStartingWith("J");
        //System.out.println(studentList);
        
        /*Get a list of all students with a given ID - will always return 1..*/
        //Student studentList3 = studentRegister.getStudentById(3);
        //System.out.println(studentList3);
        //System.out.println(studentRegister.getStudentById(4));
        
        /*Get a list of all students on a given course*/
        //List<Student> studentList = studentRegister.getStudentByCourse("Computer Science");
        //System.out.println(studentList);
        
        /*Get a list of all students on a given module*/
        //List<Student> studentList = studentRegister.getStudentByModule("Data Structures");
        //System.out.println(studentList);
        
        /*Get a list of all students on a given module and sort by marks*/
        //List<Student> studentList = studentRegister.getStudentsByModuleAndSortByMarksDescending("Data Structures");
        //System.out.println(studentList);
        
       
      
        
	}
}