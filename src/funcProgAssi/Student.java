package funcProgAssi;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ahmed
 *	University register for students using the functional programming paradigm.
 */

// The 'Student' class represents a single student in the student registry.
/*The final keyword is used to make the instance variables immutable. The constructor initializes the instance variables and no setters are provided for them. This ensures that once a Student object is created, its state cannot be changed.*/ 
public final class Student {
	   // The ID, name, course, module and marks of each student object
	   private final int id;
	   private final String name;
	   private final String course;
	   private final String module;
	   private final int marks;
	   
	    
	   // Constructor to initialize a new Student object.
	   public Student(int id, String name, String course, String module, int marks) {
	       this.id = id;
	       this.name = name;
	       this.course = course;
	       this.module = module;
	       this.marks = marks;
	   }
	    
	   //Getter methods for the Student object variables.
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
	   
	   //Converts objects to string and also allows formatting of the string
	   /*Tabular format*/
	   @Override
	   public String toString() {
//		   //Defines a format string that will provide the width and alignment of each field
//	       String format = "| %-10s | %-20s | %-20s | %-25s | %-5s |\n";
//	       //Defines the divider that will separate each row
//	       String divider = "+------------+----------------------+----------------------+---------------------------+-------+\n";
//	       //Create a new StringBuilder to build the table string
//	       StringBuilder sb = new StringBuilder();
//	       //Adds the divider
//	       sb.append(divider);
//	       //Adds the headers to the table
//	       sb.append(String.format(format, "Student ID", "Name", "Course", "Module", "Marks"));
//	       // Add the divider again to separate the header from the data rows
//	       sb.append(divider);
//	       //Add the rows of data to the SB using the format string and the values 
//	       sb.append(String.format(format, id, name, course, module, marks));
//	       //Finally add the divider again to complete the table and return the stringbuilder
//	       sb.append(divider);
//	       return sb.toString();
		   
		  /*attempt using functional programming paradigm*/
		// Defines a format string that will provide the width and alignment of each field
		    String format = "| %-10s | %-20s | %-20s | %-25s | %-5s |\n";
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
}