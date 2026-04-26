# Sari-Sync

Sari-Sync is a modern, offline-first Android application designed specifically for managing sari-sari stores (neighbourhood convenience stores in the Philippines). Built with Kotlin and Jetpack Compose, the app provides an intuitive, bilingual interface to help store owners track inventory, monitor credit (utang), and analyse daily sales performance.

## Google Development Tools \& Technologies

To meet project requirements and ensure a robust, modern architecture, Sari-Sync heavily integrates several official Google development tools and libraries:

* **Google ML Kit (On-Device Text Recognition):** Utilised for offline Optical Character Recognition (OCR). This allows store owners to quickly scan product labels using their device camera, extracting text to speed up the process of adding new inventory items without requiring an internet connection.
* **Android Jetpack (Architecture Components):**

  * **Jetpack Compose:** The modern, declarative UI toolkit used to build the entire bilingual user interface (Material Design 3).
  * **Room Database:** An abstraction layer over SQLite used for offline data persistence, ensuring that inventory, sales, and credit records are safely stored on the device.
  * **ViewModel \& Lifecycle:** Used to manage UI-related data in a lifecycle-conscious way, surviving configuration changes like screen rotations.
* **Kotlin Symbol Processing (KSP):** A powerful Google-developed API used to parse Kotlin code directly, significantly speeding up build times when generating boilerplate code for the Room database.
* **Android Studio \& Gradle:** The official Integrated Development Environment (IDE) and build system provided by Google, used to compile, test, and package the application.

## Core Features

Sari-Sync offers a comprehensive suite of tools tailored to the unique needs of micro-retailers:

### Inventory Management

The app provides a robust inventory system where users can easily add, edit, and track products across various categories such as Drinks, Food, Cooking, Household, and Snacks. A built-in search function allows for quick item retrieval. By leveraging Google ML Kit, the app provides OCR-assisted product entry. The system automatically categorises stock levels, highlighting items that are running low or completely out of stock.

### Credit (Utang) Tracking

Managing customer credit is a core challenge for sari-sari stores, and Sari-Sync addresses this with a dedicated ledger. Store owners can record new debts, log payments, and view the outstanding balance for each customer. The app features an intelligent payer classification system that categorises customers based on their payment history—identifying them as Good, Average, or Bad payers depending on how quickly they settle their accounts.

### Dashboard and Analytics

A dynamic dashboard provides a high-level overview of the store's financial health. It displays key metrics including total revenue, cost, net profit, and profit margins. Users can toggle between daily, weekly, and monthly views to analyse trends. The dashboard features visual charts for revenue versus cost and daily profit tracking. It also highlights top-selling items, overall inventory health, and the customers with the highest outstanding credit, empowering owners to make data-driven decisions.

## Technical Architecture

The application is built using modern Android development practices and libraries:

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3)
* **Architecture:** Model-View-ViewModel (MVVM)
* **Local Database:** Room Database for robust, offline data persistence
* **Asynchronous Programming:** Kotlin Coroutines and StateFlow for reactive UI updates
* **Machine Learning:** Google ML Kit for on-device text recognition

### Data Models

The Room database manages three primary entities:

1. **InventoryItem:** Stores product details including name, category, price, cost price, and current stock levels.
2. **CreditTransaction:** Logs customer debts and payments to calculate net balances.
3. **SalesRecord:** Captures historical sales data to support dashboard analytics and stock depletion predictions.

## Getting Started

To build and run the Sari-Sync application locally, follow these steps:

1. Clone the repository to your local machine.
2. Open the project in Android Studio (ensure you have the latest version supporting Kotlin 2.0+ and Jetpack Compose).
3. Sync the project with Gradle files to download all necessary dependencies.
4. Build and run the application on an emulator or a physical Android device running API level 24 (Android 7.0) or higher.

The app includes a `PrepopulateCallback` that automatically seeds the database with sample inventory items, credit transactions, and sales records upon first launch, allowing you to explore the features immediately.

## Contributing

Contributions to Sari-Sync are welcome. Please ensure that any pull requests align with the existing MVVM architecture and utilise Jetpack Compose for UI modifications.

## License

This project is currently unlicensed. Please contact the repository owner for permissions regarding use, modification, or distribution.


## Team

•	Cobby N. Baldonado – Frontend Developer
•	James Carlo F. Fernando - Backend Developer
•	Edrian C. Necesario – Lead Developer
•	Adrianne Quinn Emmanuel C. Rosalan – Full-Stack Developer



