# Project Structure: Quinpoint-Capital (Wealth Mangagement Project)
An integrated wealth management application built for **Quinpoint Capital**, designed to replace fragmented onboarding and portfolio tracking systems.  

Developed a **full-stack JavaFX prototype** using **Java**, **CSV data**, and **modular services**, providing a seamless onboarding flow, portfolio insights, performance analytics, and secure messaging — all in one unified platform.  

This functional demo illustrates how a single, cohesive platform can streamline operations, enhance client experience, and modernize outdated workflows.
  
## Project Screenshot
![image](https://github.com/user-attachments/assets/4d47b221-88a8-4142-811f-764ce883180a)

## 📌 Features by page

### Portfolio
- Displays **total portfolio value**, daily returns, and performance trends.  
- Visualized asset breakdown via **PieChart** (Stocks, Bonds, Real Estate, Cash).  
- Interactive **LineChart** showing value growth over time.  
- "Add Funds" button triggers a toast notification (future integration placeholder).

### Performance
- Shows performance metrics:  
  - 1 Week, 1 Month, 1 Year returns.  
  - Risk analysis and **Sharpe Ratio**.  
- Future-ready for data-driven expansion (CSV or API-based metrics).

### Transactions
- Lists credits and debits for each authenticated client.  
- Displays totals for deposits and withdrawals.  
- Includes GDPR compliance note for secure record handling.

### Settings
- Editable client name and email with **live save confirmation**.  
- Uses `AuthContext` for secure session-level data updates.  
- Simple toast-based feedback for instant UI interaction

### Messaging
- Secure client–advisor communication channel with timestamped messages.
- Emoji support and instant UI updates on message send.
- Messages stored in CSV format with encrypted persistence for GDPR compliance.

### Technologies
- **Java 17** – Core backend logic.  
- **JavaFX** – Modern desktop UI and charting.  
- **CSV Data Services** – Lightweight data persistence via `CsvLoaderService` and `PortfolioService`.  
- **BigDecimal**, **NumberFormat** – Accurate currency handling.  
- **Canvas API** – Captures digital signatures on login.  
- **JavaFX Charts** – PieChart and LineChart for financial visualization.  
- **FileChooser API** – ID upload during onboarding.  

## Learning Outcomes
- Designed and implemented a **modular JavaFX architecture** separating model, service, and UI layers.  
- Built a **secure authentication system** using a singleton `AuthContext`.  
- Practiced **data visualization** for financial metrics and portfolio tracking.  
- Learned **event-driven JavaFX programming** for real-time UI interactivity.  
- Implemented **encrypted, GDPR-compliant message storage** in CSV.



