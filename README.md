# SeenIt - Movie Watchlist & Social Platform 🎬

SeenIt is a full-stack movie tracking and social networking application built with Spring Boot. It allows users to track their watched movies, build watchlists, follow other users, chat, and receive AI-powered movie recommendations based on their top 3 favorite films.

## 🚀 Features
* **Movie Tracking:** Manage your watched movies, liked movies, and watchlist.
* **Social Networking:** Follow other users and chat in real-time.
* **AI Recommendations:** Get personalized movie suggestions powered by Google Gemini AI.
* **TMDb Integration:** Live search for movies, actors, and directors using the The Movie Database API.
* **Secure Authentication:** User registration with email verification and Spring Security.

## 🛠️ Tech Stack
* **Backend:** Java 17, Spring Boot, Spring Security, Spring WebSockets
* **Database:** MongoDB
* **Frontend:** HTML, CSS, JavaScript, Thymeleaf
* **External APIs:** TMDb API, Google Gemini AI API
* **Deployment:** Docker & Docker Compose

## ⚙️ Prerequisites
Before running the application, ensure you have the following installed:
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)
* API Keys:
  * [TMDb API Key](https://www.themoviedb.org/documentation/api)
  * [Google Gemini API Key](https://aistudio.google.com/)
  * A Gmail account with an App Password (for email verification)

## 🛠️ Local Setup & Execution

**1. Clone the repository**
```bash
git clone [https://github.com/RaulN11/MovieWatchlist.git](https://github.com/RaulN11/MovieWatchlist.git)
cd MovieWatchlist
```
**2. Configure Environment Variables**
Create a .env file in the root directory by copying the provided example file:
```bash
cp .env.example .env
```
**3.Run the Application with Docker Compose**
```bash
docker-compose up --build
```
**4. Access the App**
Once the containers are running, open your browser and navigate to: http://localhost:8080/homepage
