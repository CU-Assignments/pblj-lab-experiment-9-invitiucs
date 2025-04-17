import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class SpringDIExample {

    // Course class
    static class Course {
        private String courseName;
        private int duration;

        public Course(String courseName, int duration) {
            this.courseName = courseName;
            this.duration = duration;
        }

        public String getCourseName() {
            return courseName;
        }

        public int getDuration() {
            return duration;
        }

        @Override
        public String toString() {
            return "Course Name: " + courseName + ", Duration: " + duration + " weeks";
        }
    }

    // Student class
    static class Student {
        private String name;
        private Course course;

        public Student(String name, Course course) {
            this.name = name;
            this.course = course;
        }

        public void printDetails() {
            System.out.println("Student Name: " + name);
            System.out.println(course.toString());
        }
    }

    // Spring Configuration
    @Configuration
    static class AppConfig {
        @Bean
        public Course course() {
            return new Course("Spring Framework", 8);
        }

        @Bean
        public Student student() {
            return new Student("Alice", course());
        }
    }

    // Main method
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Student student = context.getBean(Student.class);
        student.printDetails();
    }
}

