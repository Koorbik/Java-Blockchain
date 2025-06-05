# Java Blockchain Simulation

## Description

This project implements a blockchain in Java, focusing on the challenges of mining, transactions, and maintaining a decentralized ledger. It includes core functionalities such as block creation, proof-of-work, transaction validation, and client interaction. The project provides a simulation of miners competing to add blocks to the chain while handling transactions securely and efficiently.

## Features

- **Blockchain Core:** Implements a decentralized blockchain structure.
- **Mining:** Proof-of-work algorithm for mining blocks with adjustable difficulty.
- **Transactions:** Validates and processes transactions between clients.
- **Clients:** Allows clients to create transactions and participate in mining.
- **Multithreading:** Utilizes multithreading to simulate real-world mining competition.
- **Dynamic Difficulty Adjustment:** Adjusts mining difficulty based on block generation time.

## Installation

### Prerequisites

- Java 17 or later
- Maven 3.6 or later

### Steps

1. Clone the repository:

   ```console
   git clone https://github.com/Koorbik/Java-Blockchain.git
   cd Java-Blockchain
   ```
   
2. Build the project
    ```console
    ./mvnw clean install 
    ```
3. Run the project:
    ```console
    java -jar target/BlockchainWithJava-1.0-SNAPSHOT.jar 
    ```

## Usage

### Simulate Blockchain Activity

#### Run BlockchainApp

Execute the `BlockchainApp` class to simulate mining and transaction processing. Miners will compete to add blocks to the chain.

#### Adjust Parameters

Modify the `BlockchainApp` class to:

- Set the target number of blocks.
- Adjust mining difficulty.
- Add custom transactions.

