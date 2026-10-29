package com.placementassist.institute.modules.questionbank;

import com.placementassist.institute.common.enums.QuestionDifficulty;
import com.placementassist.institute.modules.companydrive.Drive;
import com.placementassist.institute.modules.companydrive.DriveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds at least 15 realistic campus interview questions across technical domains.
 */
@Component
@Order(4)
@ConditionalOnBean(name = "entityManagerFactory")
public class QuestionSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(QuestionSeedRunner.class);

    private final QuestionRepository questionRepository;
    private final DriveRepository driveRepository;

    public QuestionSeedRunner(QuestionRepository questionRepository, DriveRepository driveRepository) {
        this.questionRepository = questionRepository;
        this.driveRepository = driveRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (questionRepository.count() >= 15) {
            return;
        }

        List<Drive> drives = driveRepository.findAll();
        Drive d1 = !drives.isEmpty() ? drives.get(0) : null;
        Drive d2 = drives.size() > 1 ? drives.get(1) : null;

        log.info("Seeding 15 realistic technical and interview questions...");

        Object[][] questions = {
                {"Explain the difference between HashMap and ConcurrentHashMap in Java.",
                 "HashMap is non-synchronized, not thread-safe, and allows one null key. ConcurrentHashMap is thread-safe using bucket-level / segment locking (synchronized blocks on bin heads in Java 8+), does not allow null keys or values, and offers high concurrent throughput.",
                 "SDE", "TECHNICAL", QuestionDifficulty.MEDIUM, "Java, Concurrency, Collections", 2024, d1},

                {"What are ACID properties in Database Management Systems?",
                 "ACID stands for: Atomicity (all or nothing execution of transaction), Consistency (database transitions from one valid state to another), Isolation (concurrent transactions execute independently without interference), and Durability (committed changes survive system crashes).",
                 "SDE", "TECHNICAL", QuestionDifficulty.EASY, "DBMS, SQL, Transactions", 2024, d1},

                {"How does the Garbage Collector work in Java?",
                 "The JVM Garbage Collector reclaims memory from objects no longer reachable from GC roots. It uses generational collection dividing the heap into Young Generation (Eden, S0, S1) and Old/Tenured Generation. Common collectors include G1GC, ZGC, and ParallelGC.",
                 "SDE", "TECHNICAL", QuestionDifficulty.MEDIUM, "Java, JVM, Memory", 2024, d2},

                {"Describe the Two-Pointer approach to detect a cycle in a Linked List (Floyd's Cycle-Finding Algorithm).",
                 "Use two pointers: slow moving 1 node per step, fast moving 2 nodes per step. If a cycle exists, fast will eventually meet slow inside the loop in O(N) time and O(1) auxiliary space.",
                 "SDE", "TECHNICAL", QuestionDifficulty.EASY, "DSA, LinkedList", 2023, d1},

                {"What is the difference between TCP and UDP?",
                 "TCP is connection-oriented, reliable with error checking, sequencing, and flow control (three-way handshake). UDP is connectionless, lightweight, and fast, used for real-time video streaming, gaming, and DNS where occasional dropped packets are acceptable.",
                 "Network Engineer", "TECHNICAL", QuestionDifficulty.EASY, "Networking, Protocols", 2023, d2},

                {"How do you design a URL Shortener like bit.ly?",
                 "Key components: API Gateway, Shortening Service, Database (Key-Value or SQL), and Cache (Redis). Use Base62 encoding on an auto-incrementing ID or MD5 hash. Cache top 20% URLs for fast redirect (HTTP 301 or 302). Store longUrl, shortCode, and createdAt with an index on shortCode.",
                 "SDE", "TECHNICAL", QuestionDifficulty.HARD, "System Design, Microservices, Redis", 2024, d1},

                {"Explain the difference between Process and Thread in Operating Systems.",
                 "A process is an executing instance of a program with its own dedicated memory address space, file handles, and resources. A thread is a lightweight unit of execution within a process that shares the parent process's memory (heap and code) but has its own stack and registers.",
                 "SDE", "TECHNICAL", QuestionDifficulty.EASY, "OS, Multithreading", 2023, d2},

                {"What is Normalization in SQL? Differentiate between 2NF and 3NF.",
                 "Normalization organizes database schemas to reduce redundancy and avoid update anomalies. 2NF requires 1NF and no partial dependencies (every non-key attribute is fully dependent on the primary key). 3NF requires 2NF and no transitive dependencies.",
                 "Data Analyst", "TECHNICAL", QuestionDifficulty.MEDIUM, "DBMS, SQL, Normalization", 2024, null},

                {"Implement LRU (Least Recently Used) Cache data structure.",
                 "Combine a Doubly Linked List with a HashMap. The HashMap provides O(1) lookup to list nodes. The Doubly Linked List maintains access order: recent items move to head, oldest items evicted from tail in O(1) time.",
                 "SDE", "TECHNICAL", QuestionDifficulty.HARD, "DSA, Design, DoublyLinkedList", 2024, d1},

                {"What are Virtual Functions and Dynamic Polymorphism in C++ / OOP?",
                 "A virtual function declared with 'virtual' in base class tells the compiler to resolve calls at runtime via a vtable (virtual method table) based on the actual object type rather than the pointer type, achieving dynamic/runtime polymorphism.",
                 "Software Engineer", "TECHNICAL", QuestionDifficulty.MEDIUM, "C++, OOP, Polymorphism", 2023, null},

                {"What is the CAP Theorem in Distributed Systems?",
                 "In a distributed data store, you can only guarantee two out of three: Consistency (every read receives the most recent write), Availability (every non-failing node returns a response), and Partition Tolerance (system continues functioning despite network drops between nodes).",
                 "Cloud Associate", "TECHNICAL", QuestionDifficulty.HARD, "System Design, Cloud, Distributed", 2024, d2},

                {"Tell me about a challenging project and how you handled conflict in your team.",
                 "Use the STAR method: Situation (project context), Task (what was required), Action (active listening, finding objective data, dividing tasks based on strengths), and Result (successful project delivery on time).",
                 "Any", "MANAGERIAL", QuestionDifficulty.EASY, "HR, Behavioral, STAR", 2024, null},

                {"What is Indexing in databases and what is B+ Tree?",
                 "An index is an auxiliary data structure that accelerates query lookups. B+ Trees keep keys in sorted order with data pointers only at leaf nodes linked together as a doubly linked list, enabling fast point queries and efficient range queries.",
                 "SDE", "TECHNICAL", QuestionDifficulty.MEDIUM, "DBMS, Indexes, B-Tree", 2024, d1},

                {"How does HTTPS encrypt client-server communication?",
                 "HTTPS uses TLS/SSL. Handshake uses asymmetric encryption (RSA/ECC) to authenticate the server via certificate and securely exchange a symmetric session key. Subsequent HTTP traffic is encrypted using fast symmetric encryption (AES).",
                 "Security Analyst", "TECHNICAL", QuestionDifficulty.MEDIUM, "Security, Networking, SSL", 2023, null},

                {"Find the maximum subarray sum in O(N) time (Kadane's Algorithm).",
                 "Maintain current_sum and max_sum. Iterate through elements: current_sum = max(num, current_sum + num); max_sum = max(max_sum, current_sum). Returns the contiguous subarray with maximum sum in linear time.",
                 "SDE", "TECHNICAL", QuestionDifficulty.EASY, "DSA, Dynamic Programming, Arrays", 2024, d1}
        };

        for (Object[] q : questions) {
            Question question = Question.builder()
                    .question((String) q[0])
                    .answer((String) q[1])
                    .role((String) q[2])
                    .roundType((String) q[3])
                    .difficulty((QuestionDifficulty) q[4])
                    .tags((String) q[5])
                    .askedYear((Integer) q[6])
                    .drive((Drive) q[7])
                    .build();
            questionRepository.save(question);
        }

        log.info("Seeded 15 interview questions successfully.");
    }
}
