# Employee Task Management System

Spring Boot 3 (Java 17) + MySQL + JWT backend, React (Vite) frontend.

## Run
1. Install JDK 17+, Maven, Node 18+, MySQL 8.
2. Edit `backend/src/main/resources/application.properties` (set your MySQL password).
   The database `task_db` is created automatically. (`database/schema.sql` is optional reference.)
3. Backend:  `cd backend && mvn spring-boot:run`   -> http://localhost:8080
4. Frontend: `cd frontend && npm install && npm run dev` -> http://localhost:5173

## Demo accounts (seeded on first start)
- Admin:    admin@test.com / admin123
- Employee: john@test.com / john123

## API
- POST /api/auth/register, POST /api/auth/login
- GET /api/tasks  (admin: all, employee: own)
- POST /api/tasks?assignedTo={userId}   (admin)
- PUT /api/tasks/{id}/status?status=TODO|IN_PROGRESS|DONE
- DELETE /api/tasks/{id}   (admin)
- GET /api/tasks/employees (admin)

Change `app.jwt.secret` and the seeded passwords before any real deployment.
