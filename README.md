 🎓 ClubHub – University Club Management Platform

 📌 Overview
**ClubHub** is a full-stack web application designed to digitalize and simplify university club management.  
It provides **role-based access** for students, club admins, university admins, and general users.  

This project was developed as part of my training and gave me **hands-on experience** in real-world software development and deployment.


👥 User Roles & Features

🧑‍🎓 Students
- Register and create profiles  
- Join clubs and participate  
- Interact through posts and comments  

 🏛️ University Admins
- Monitor and officially manage all clubs  
- Approve/reject new clubs  
- Ensure smooth club operations  
 👨‍💼 Club Admins
- Manage club members  
- Post updates, events, and announcements  
- Handle club activities
    
 🌍 General Users
- Browse and explore clubs publicly  
- View club details, events, and posts  


🛠️ Tech Stack
- **Backend:** Java, Spring Boot  
- **Frontend:** HTML, CSS, JavaScript, Thymeleaf  
- **Database:** PostgreSQL  
- **Tools & Deployment:** Git, GitHub, DSi Local Server  

📂 Project Structure
ClubHub/
│── src/ # Source code
│ ├── main/java/... # Backend (Spring Boot code)
│ ├── main/resources # Templates, static files, application config
│── pom.xml # Maven dependencies
│── README.md # Documentation

---

## 🚀 Installation & Setup

 1️⃣ Clone the repository
```bash
git clone https://github.com/bipasha42/Clubhubs.git
cd Clubhubs
 
2️⃣ Configure Database
Install PostgreSQL

Create a database (e.g., clubhub_db)

Update application.properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/clubhub_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
3️⃣ Build & Run
mvn clean install
mvn spring-boot:run
4️⃣ Access the app

👉 Open: http://localhost:8080

<img width="1920" height="1080" alt="Screenshot (171)" src="https://github.com/user-attachments/assets/1d5af577-4c5d-4fae-a351-9cbb69d7035f" />
<img width="1920" height="1080" alt="Screenshot (172)" src="https://github.com/user-attachments/assets/e795f359-2cf1-4b39-8d7c-7c0f17401cea" />

<img width="1920" height="1080" alt="Screenshot (173)" src="https://github.com/user-attachments/assets/4dd374f9-f45f-4d5e-b1ea-5ce6f7257d0e" />
<img width="1920" height="1080" alt="Screenshot (174)" src="https://github.com/user-attachments/assets/c3513975-c115-437c-969e-c55bbff78281" />

🧩 Development Process
We followed a structured Software Development Lifecycle (SDLC):

Requirement Analysis

System Design

Backend Development (Spring Boot, PostgreSQL)

Frontend Development (Thymeleaf, HTML, CSS, JavaScript)

Deployment on local server

🤝 Contributing

Contributions are welcome! To contribute:

Fork the repository

Create a new branch (feature-xyz)

Commit your changes

Push to your branch

Submit a Pull Request

