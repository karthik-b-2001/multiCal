# Multiple Calendar Manager (Java)

A robust, extensible calendar management system designed using SOLID principles. This application allows users to manage multiple calendars, schedule events, and perform complex analytics through both a Graphical User Interface (GUI) and a Command-Line Interface (CLI).

## 🛠 Features
* **Multiple Calendar Support:** Create, load, and switch between various calendar instances.
* **Event Management:** Schedule, edit, and remove events with conflict detection.
* **Hybrid Interface:** Fully functional Desktop GUI (Swing/JavaFX) and a scriptable Text Interface.
* **Analytics Dashboard:** (New in Part 4) Comprehensive metrics including:
    * Event distribution by subject, weekday, week, and month.
    * Busiest/Least busy day identification.
    * Online vs. In-person event ratios.

## 📊 Design Architecture
The project follows the **Model-View-Controller (MVC)** pattern to ensure high maintainability and extensibility.



* **Model:** Handles the logic for event conflicts, date math, and analytics calculations.
* **View:** Supports both `TextView` for console output and a `JFrame`-based GUI.
* **Controller:** Orchestrates user input from both interfaces to update the calendar state.

## 🚀 Getting Started
### Prerequisites
* Java JDK 11 or higher
* Gradle (for building)

### Installation
1. Clone the repository:
   ```bash
   git clone [https://github.com/your-username/Multiple-Calendar-Manager.git](https://github.com/your-username/Multiple-Calendar-Manager.git)
