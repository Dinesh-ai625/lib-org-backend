package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.model.Book;
import com.example.demo.model.FineLog;
import com.example.demo.model.FineStatus;
import com.example.demo.model.Rack;
import com.example.demo.model.Role;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionStatus;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.FineLogRepository;
import com.example.demo.repository.RackRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RackRepository rackRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FineLogRepository fineLogRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("Seeding database with initial data...");

            // Create Users
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@liborg.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setFineBalance(BigDecimal.ZERO);
            userRepository.save(admin);

            User user = new User();
            user.setUsername("dinesh");
            user.setFirstName("Dinesh");
            user.setLastName("Kumar");
            user.setEmail("dinesh@example.com");
            user.setPasswordHash(passwordEncoder.encode("password123"));
            user.setRole(Role.USER);
            user.setFineBalance(new BigDecimal("5.00"));
            user.setBio("Avid reader and sci-fi enthusiast.");
            user.setPhotoUrl("https://ui-avatars.com/api/?name=Dinesh+Kumar&background=random");
            userRepository.save(user);

            // Create Racks
            Rack rack = new Rack();
            rack.setSection("Sci-Fi");
            rack.setColumnNumber(1);
            rackRepository.save(rack);

            Rack rack2 = new Rack();
            rack2.setSection("Fantasy");
            rack2.setColumnNumber(2);
            rackRepository.save(rack2);

            Rack rack3 = new Rack();
            rack3.setSection("Classic Literature");
            rack3.setColumnNumber(3);
            rackRepository.save(rack3);

            Rack rack4 = new Rack();
            rack4.setSection("Mystery & Thriller");
            rack4.setColumnNumber(4);
            rackRepository.save(rack4);

            Rack rack5 = new Rack();
            rack5.setSection("Biography");
            rack5.setColumnNumber(5);
            rackRepository.save(rack5);

            Rack rack6 = new Rack();
            rack6.setSection("History & Politics");
            rack6.setColumnNumber(6);
            rackRepository.save(rack6);

            Rack rack7 = new Rack();
            rack7.setSection("Self Help");
            rack7.setColumnNumber(7);
            rackRepository.save(rack7);

            Rack rack8 = new Rack();
            rack8.setSection("Children's Books");
            rack8.setColumnNumber(8);
            rackRepository.save(rack8);

            // Create Books
            int pos = 0;
            Book book1 = new Book();
            book1.setTitle("Dune");
            book1.setAuthor("Frank Herbert");
            book1.setIsbn("9780441172719");
            book1.setGenre("Science Fiction");
            book1.setCategory("Fiction");
            book1.setTotalCopies(5);
            book1.setAvailableCopies(4);
            book1.setRack(rack);
            book1.setPosition(pos++);
            book1.setCoverUrl("https://covers.openlibrary.org/b/isbn/9780441172719-L.jpg");
            book1.setDescription("Set on the desert planet Arrakis...");
            bookRepository.save(book1);

            Book book2 = new Book();
            book2.setTitle("The Hobbit");
            book2.setAuthor("J.R.R. Tolkien");
            book2.setIsbn("9780547928227");
            book2.setGenre("Fantasy");
            book2.setCategory("Fiction");
            book2.setTotalCopies(3);
            book2.setAvailableCopies(3);
            book2.setRack(rack2);
            book2.setPosition(0);
            book2.setCoverUrl("https://covers.openlibrary.org/b/isbn/9780547928227-L.jpg");
            bookRepository.save(book2);

            Book book3 = new Book();
            book3.setTitle("1984");
            book3.setAuthor("George Orwell");
            book3.setIsbn("9780451524935");
            book3.setGenre("Dystopian");
            book3.setCategory("Fiction");
            book3.setTotalCopies(2);
            book3.setAvailableCopies(0);
            book3.setRack(rack);
            book3.setPosition(pos++);
            book3.setCoverUrl("https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg");
            bookRepository.save(book3);

            Book book4 = new Book();
            book4.setTitle("The Great Gatsby");
            book4.setAuthor("F. Scott Fitzgerald");
            book4.setIsbn("9780743273565");
            book4.setGenre("Tragedy");
            book4.setCategory("Fiction");
            book4.setTotalCopies(4);
            book4.setAvailableCopies(4);
            book4.setRack(rack);
            book4.setPosition(pos++);
            book4.setCoverUrl("https://covers.openlibrary.org/b/isbn/9780743273565-L.jpg");
            bookRepository.save(book4);

            Book book5 = new Book();
            book5.setTitle("To Kill a Mockingbird");
            book5.setAuthor("Harper Lee");
            book5.setIsbn("9780060935467");
            book5.setGenre("Southern Gothic");
            book5.setCategory("Fiction");
            book5.setTotalCopies(6);
            book5.setAvailableCopies(5);
            book5.setRack(rack2);
            book5.setPosition(1);
            book5.setCoverUrl("https://covers.openlibrary.org/b/isbn/9780060935467-L.jpg");
            bookRepository.save(book5);

            // Add more books to every rack
            String[][] extraBooks = {
                {"Foundation", "Isaac Asimov", "9780553293357", "Sci-Fi", "1"}, // rack 1
                {"The Name of the Wind", "Patrick Rothfuss", "9780756404741", "Fantasy", "2"}, // rack 2
                {"The Great Gatsby", "F. Scott Fitzgerald", "9780743273566", "Classics", "3"}, // rack 3
                {"The Silent Patient", "Alex Michaelides", "9781250301697", "Mystery", "4"}, // rack 4
                {"Steve Jobs", "Walter Isaacson", "9781451648539", "Biography", "5"}, // rack 5
                {"Sapiens", "Yuval Noah Harari", "9780062316097", "History", "6"}, // rack 6
                {"Atomic Habits", "James Clear", "9780735211292", "Self Help", "7"}, // rack 7
                {"Harry Potter", "J.K. Rowling", "9780590353427", "Children", "8"}, // rack 8
                {"Neuromancer", "William Gibson", "9780441569595", "Sci-Fi", "1"},
                {"The Way of Kings", "Brandon Sanderson", "9780765326355", "Fantasy", "2"},
                {"Ulysses", "James Joyce", "9780141184432", "Classics", "3"},
                {"Gone Girl", "Gillian Flynn", "9780307588371", "Mystery", "4"},
                {"Becoming", "Michelle Obama", "9781524763138", "Biography", "5"},
                {"The Silk Roads", "Peter Frankopan", "9781101912379", "History", "6"},
                {"Deep Work", "Cal Newport", "9781455586691", "Self Help", "7"},
                {"Matilda", "Roald Dahl", "9780141365466", "Children", "8"}
            };

            Rack[] allRacks = {rack, rack2, rack3, rack4, rack5, rack6, rack7, rack8};
            for (int i = 0; i < extraBooks.length; i++) {
                Book b = new Book();
                b.setTitle(extraBooks[i][0]);
                b.setAuthor(extraBooks[i][1]);
                b.setIsbn(extraBooks[i][2]);
                b.setGenre(extraBooks[i][3]);
                b.setCategory("Fiction");
                b.setTotalCopies(3);
                b.setAvailableCopies(3);
                int rIdx = Integer.parseInt(extraBooks[i][4]) - 1;
                b.setRack(allRacks[rIdx]);
                b.setPosition(i / 8 + 5); // Rough unique position
                b.setCoverUrl("https://covers.openlibrary.org/b/isbn/" + extraBooks[i][2] + "-L.jpg");
                bookRepository.save(b);
            }

            // Create a Transaction (Reading History)
            Transaction tx = new Transaction();
            tx.setUser(user);
            tx.setBook(book1);
            tx.setIssueDate(LocalDate.now().minusDays(20));
            tx.setDueDate(LocalDate.now().minusDays(6));
            tx.setStatus(TransactionStatus.BORROWED);
            transactionRepository.save(tx);

            // Create a Fine Log
            FineLog fine = new FineLog();
            fine.setUser(user);
            fine.setAmount(new BigDecimal("5.00"));
            fine.setReason("Overdue book: Dune");
            fine.setStatus(FineStatus.PENDING);
            fineLogRepository.save(fine);

            System.out.println("Seeding complete! You can login as:");
            System.out.println("Admin -> admin / admin123");
            System.out.println("User -> dinesh / password123");
        }
    }
}
