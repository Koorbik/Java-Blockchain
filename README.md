# Java Blockchain Simulation ⛓️

## 🚀 Description

This project implements a simplified blockchain in Java, simulating core functionalities such as cryptographic hashing, block creation, proof-of-work mining, transaction processing, and a decentralized ledger. It's designed to provide a hands-on understanding of fundamental blockchain concepts and the challenges involved in creating a distributed and secure system.

The simulation showcases miners competing to add blocks to the chain, clients generating and signing transactions, and dynamic adjustment of mining difficulty.

## ✨ Core Concepts Demonstrated

* **Decentralized Ledger:** A chain of blocks, each cryptographically linked to the previous one.
* **Block Structure:** Blocks containing an ID, timestamp, previous hash, its own hash (calculated based on content and a "magic number"), a list of transactions, miner information, and generation time.
* **Cryptographic Hashing:** Utilizes SHA-256 for generating secure block hashes, ensuring data integrity and linking blocks.
* **Proof-of-Work (PoW):** Miners expend computational effort to find a "magic number" that results in a block hash meeting a defined difficulty (e.g., starting with a certain number of zeros).
* **Transactions & Digital Signatures:**
    * Clients create transactions (sender, receiver, amount).
    * Transactions are digitally signed using RSA (SHA256withRSA) to ensure authenticity and integrity.
    * The blockchain validates these signatures and checks for sufficient funds before including transactions in a block.
* **Miner Rewards:** Miners are rewarded with virtual currency for successfully mining a block.
* **Dynamic Difficulty Adjustment:** The mining difficulty (N, the number of leading zeros required in a hash) is dynamically adjusted based on the time taken to mine previous blocks, aiming for a consistent block generation rate.
* **Multithreading:** Simulates concurrent mining activity from multiple miners and asynchronous transaction submissions to the blockchain.

## 🛠️ Features

* **Blockchain Core:** Manages the chain of blocks, validates new blocks, and handles transaction queuing.
* **Mining Simulation:** Miners run in separate threads, competing to solve the PoW puzzle.
* **Client Interaction:** Clients can generate key pairs (RSA) and create signed transactions.
* **Transaction Validation:** Includes checks for valid digital signatures and sufficient sender balance.
* **Configurable Simulation:** Key parameters like the target number of blocks and miner pool size can be adjusted in `BlockchainApp.java`.

## 💻 Technologies Used

* **Java 17**
* **Maven:** For project build and dependency management.
* **JUnit 5:** For unit testing.
* **Mockito:** For creating mock objects in tests.
* **Static Analysis Tools:**
    * **Checkstyle:** Enforces coding conventions (configured via `checkstyle.xml`).
    * **PMD:** Analyzes code for potential bugs, dead code, and suboptimal code.
    * **SpotBugs (with FindSecBugs plugin):** Identifies potential security vulnerabilities and other defects.
* **JaCoCo:** For measuring code coverage of tests.

## ⚙️ Setup and Installation

### Prerequisites

* Java Development Kit (JDK) 17 or later.
* Apache Maven 3.6 or later.

### Steps

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Koorbik/Java-Blockchain.git
    cd Java-Blockchain
    ```

2.  **Build the project:**
    This command will compile the code, run tests, and package the application.
    ```bash
    ./mvnw clean install
    ```
    (or `mvn clean install` if you have Maven installed globally and not using the wrapper)

## ▶️ Running the Simulation

Execute the `BlockchainApp` class to start the simulation:

```bash
java -jar target/BlockchainWithJava-1.0-SNAPSHOT.jar
```

This will initiate the blockchain, start multiple miner threads, and begin submitting transactions. You'll see blocks being mined and added to the chain, along with logs about difficulty adjustments and transaction processing.

## Configuration
You can modify simulation parameters in the `BlockchainApp.java` file:

* **TARGET_BLOCKS**: The total number of blocks to mine before the simulation stops.
* **MINER_POOL_SIZE**: The number of concurrent miner threads.

You can also add or modify the simulated transactions within the `simulateTransactions()` method.

## 🧪 Testing
This project includes a comprehensive suite of unit tests to ensure the correctness of individual components.

**Frameworks Used**: JUnit 5 and Mockito.

**Tested Classes**:
* `BlockTest.java`
* `BlockchainTest.java`
* `ClientTest.java`
* `MinerTest.java` (includes testing for interruption and behavior when the target is reached)
* `TransactionTest.java` (covers signature validation logic, including award transactions)

**Running Tests**:
Tests are automatically run during the `mvn clean install` build process. You can also run them explicitly:

```bash
./mvnw test
```
## Code Coverage

**JaCoCo** is used to generate code coverage reports. The project aims for a minimum of 80% line and branch coverage, with certain classes (like exceptions and the main application class) excluded from this target. You can find the report in *target/site/jacoco/index.html* after building the project.

---

## 📊 Code Quality

The project emphasizes high code quality through:

* **Checkstyle**: Ensures adherence to defined coding standards (Sun/Oracle conventions with some additions). Violations will fail the build.
* **PMD**: Detects common programming flaws.
* **SpotBugs with FindSecBugs**: Identifies potential bugs and security issues. The build checks for high-priority issues.

These tools are integrated into the Maven build lifecycle and run during the `verify` phase.
