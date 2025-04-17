import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.*;

@Entity
@Table(name = "students")
class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int age;

    public Student() {}

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters & setters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', age=" + age + '}';
    }
}

public class HibernateStudentApp {

    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        try {
            // Setup Hibernate
            Configuration cfg = new Configuration();
            cfg.configure("hibernate.cfg.xml");
            cfg.addAnnotatedClass(Student.class);
            sessionFactory = cfg.buildSessionFactory();

            // Perform CRUD operations
            Student student = new Student("John Doe", 20);
            saveStudent(student);

            Student fetched = getStudent(student.getId());
            System.out.println("Fetched: " + fetched);

            fetched.setAge(21);
            updateStudent(fetched);

            deleteStudent(fetched.getId());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sessionFactory != null) sessionFactory.close();
        }
    }

    public static void saveStudent(Student student) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.save(student);
            tx.commit();
            System.out.println("Saved: " + student);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public static Student getStudent(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Student.class, id);
        }
    }

    public static void updateStudent(Student student) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.update(student);
            tx.commit();
            System.out.println("Updated: " + student);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public static void deleteStudent(int id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Student student = session.get(Student.class, id);
            if (student != null) {
                session.delete(student);
                System.out.println("Deleted student with id " + id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}

