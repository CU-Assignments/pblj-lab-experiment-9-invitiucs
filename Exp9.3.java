Create a banking system with Spring and Hibernate to manage money transfers using transactions.	- Configure Spring with Hibernate ORM.
- Create Account.java and Transaction.java entities.
- Use Hibernate Transaction Management.
- Ensure rollback on failure.
- Demonstrate both success and failure cases.

  code 
  import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.*;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.*;
import javax.persistence.*;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;
import java.util.*;

@Entity
class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String owner;
    private double balance;

    public Account() {}
    public Account(String owner, double balance) {
        this.owner = owner;
        this.balance = balance;
    }

    public int getId() { return id; }
    public String getOwner() { return owner; }
    public double getBalance() { return balance; }

    public void setOwner(String owner) { this.owner = owner; }
    public void setBalance(double balance) { this.balance = balance; }

    public String toString() {
        return "Account{id=" + id + ", owner='" + owner + "', balance=" + balance + "}";
    }
}

@Entity
class TransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int fromAccount;
    private int toAccount;
    private double amount;
    private Date timestamp;

    public TransactionLog() {}
    public TransactionLog(int fromAccount, int toAccount, double amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.timestamp = new Date();
    }
}

@Service
class BankService {
    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void transferMoney(int fromId, int toId, double amount) {
        Session session = sessionFactory.getCurrentSession();
        Account from = session.get(Account.class, fromId);
        Account to = session.get(Account.class, toId);

        if (from.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds for transfer!");
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        session.save(new TransactionLog(fromId, toId, amount));
        System.out.println("Transfer successful: $" + amount);
    }
}

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "")
class AppConfig {

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/bankdb");
        ds.setUsername("root");
        ds.setPassword("your_password");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sf = new LocalSessionFactoryBean();
        sf.setDataSource(dataSource());
        sf.setPackagesToScan("");
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        props.setProperty("hibernate.hbm2ddl.auto", "update");
        props.setProperty("show_sql", "true");
        sf.setHibernateProperties(props);
        return sf;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sf) {
        return new HibernateTransactionManager(sf);
    }
}

public class BankingApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        BankService service = context.getBean(BankService.class);
        SessionFactory sessionFactory = context.getBean(SessionFactory.class);
        Session session = sessionFactory.openSession();

        // Setup accounts
        session.beginTransaction();
        Account acc1 = new Account("Alice", 1000);
        Account acc2 = new Account("Bob", 500);
        session.save(acc1);
        session.save(acc2);
        session.getTransaction().commit();

        // Perform transactions
        try {
            service.transferMoney(acc1.getId(), acc2.getId(), 300); // Success
            service.transferMoney(acc1.getId(), acc2.getId(), 800); // Failure (rollback)
        } catch (Exception e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }

        session.close();
        context.close();
    }
}
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC 
    "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
    "http://hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/bankdb</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password">your_password</property>
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
        <property name="hibernate.hbm2ddl.auto">update</property>
        <property name="show_sql">true</property>
    </session-factory>
</hibernate-configuration>

