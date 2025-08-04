# Customer Database Management Lab

A comprehensive containerized lab environment for testing customer database operations with automated change tracking via Liquibase. This lab demonstrates a modern full-stack application with PostgreSQL, Spring Boot, and Vue.js.

## 🎯 Lab Objectives

This lab helps you explore:
- **Database Management**: PostgreSQL with automated schema migrations
- **Change Tracking**: Complete audit trail of all database operations  
- **API Development**: RESTful services with Spring Boot
- **Frontend Development**: Modern web interface with Vue.js
- **DevOps Practices**: Container orchestration with Docker Compose
- **Schema Evolution**: Liquibase for version-controlled database changes

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Vue.js        │    │   Spring Boot   │    │   PostgreSQL    │
│   Frontend      │───▶│   Backend       │───▶│   Database      │
│   (Port 3000)   │    │   (Port 8080)   │    │   (Port 5432)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
        │                       │                       │
        │                       ▼                       │
        │               ┌─────────────────┐              │
        │               │   Liquibase     │              │
        │               │   Schema Mgmt   │              │
        │               └─────────────────┘              │
        │                                                │
        └─────────────── Audit Trail ────────────────────┘
```

## ✨ Features

### Customer Management
- **CRUD Operations**: Create, read, update, and delete customer records
- **Smart Search**: Filter by name, email, phone, or address
- **Pagination**: Handle large datasets efficiently
- **Form Validation**: Client and server-side validation

### Audit Trail
- **Automatic Tracking**: Every change is logged automatically via database triggers
- **Change History**: View complete history for any customer
- **Action Types**: Track INSERT, UPDATE, and DELETE operations
- **JSON Storage**: Before/after values stored as JSONB for flexible querying

### User Experience
- **Responsive Design**: Works on desktop, tablet, and mobile
- **Real-time Updates**: Changes reflect immediately across the interface
- **Intuitive Navigation**: Clean, modern interface built with Bootstrap 5
- **Error Handling**: Comprehensive error messages and validation feedback

### Development Features
- **Hot Reload**: Frontend and backend support development hot reload
- **Database Migrations**: Version-controlled schema changes with Liquibase
- **Sample Data**: Pre-loaded test customers for immediate experimentation
- **Comprehensive Logging**: Detailed logs for debugging and monitoring

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose installed
- Ports 3000, 5432, and 8080 available

### Option 1: One-Command Startup
```bash
# Clone and start (replace with your setup method)
cd db_gui
./start.sh start
```

### Option 2: Manual Startup
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f
```

### Verification
```bash
# Run automated tests
./test.sh

# Check service status
./start.sh status
```

## 🌐 Access Points

| Service | URL | Purpose |
|---------|-----|---------|
| **Frontend** | http://localhost:3000 | Main user interface |
| **Backend API** | http://localhost:8080/api | REST API endpoints |
| **Database** | localhost:5432 | PostgreSQL (postgres/postgres) |

## 📊 Sample Data

The lab includes 5 pre-loaded customers:
- John Doe (john.doe@example.com)
- Jane Smith (jane.smith@example.com)
- Bob Johnson (bob.johnson@example.com)
- Alice Williams (alice.williams@example.com)
- Charlie Brown (charlie.brown@example.com)

## 🔧 Key Technologies

### Backend Stack
- **Spring Boot 3.2**: Modern Java framework
- **Spring Data JPA**: Database abstraction layer
- **Liquibase**: Database version control
- **PostgreSQL**: Production-grade database
- **Maven**: Dependency management

### Frontend Stack
- **Vue.js 3**: Progressive JavaScript framework
- **Vue Router**: Client-side routing
- **Vuex**: State management
- **Bootstrap 5**: UI framework
- **Axios**: HTTP client

### Infrastructure
- **Docker**: Containerization
- **Docker Compose**: Service orchestration
- **Nginx**: Frontend web server

## 📝 Common Tasks

### Adding a New Customer
1. Navigate to http://localhost:3000
2. Click "Add Customer"
3. Fill out the form
4. Click "Create Customer"
5. View the audit log to see the change recorded

### Viewing Change History
1. Go to any customer detail page
2. Click "View History" or navigate to "Audit Log"
3. See all changes with before/after values
4. Filter by action type (INSERT/UPDATE/DELETE)

### Database Schema Changes
1. Create new Liquibase changeset in `backend/src/main/resources/db/changelog/`
2. Add reference to `db.changelog-master.xml`
3. Restart backend: `docker-compose restart backend`
4. Changes are applied automatically

### API Testing
```bash
# List customers
curl http://localhost:8080/api/customers

# Create customer  
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@example.com"}'

# Search customers
curl "http://localhost:8080/api/customers/search?firstName=John"
```

## 🛠️ Management Commands

```bash
# Service management
./start.sh start     # Start all services
./start.sh stop      # Stop all services  
./start.sh restart   # Restart all services
./start.sh logs      # View all logs
./start.sh status    # Check service status

# Specific service logs
./start.sh logs backend
./start.sh logs frontend
./start.sh logs postgres

# Testing and cleanup
./test.sh           # Run automated tests
./start.sh clean    # Remove all containers and data
```

## 🔍 Monitoring & Debugging

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
```

### Database Access
```bash
# Connect to database
docker exec -it customer_db psql -U postgres -d customer_db

# View customers
SELECT * FROM customers;

# View audit trail
SELECT * FROM audit.customer_audit ORDER BY changed_at DESC;
```

### Health Checks
```bash
# Backend health
curl http://localhost:8080/api/customers

# Frontend health
curl http://localhost:3000

# Database health
docker exec customer_db pg_isready -U postgres
```

## 📚 Learning Exercises

### Beginner
1. **Basic CRUD**: Create, edit, and delete customers via the web interface
2. **Search & Filter**: Try different search combinations
3. **Audit Exploration**: View change history for different customers

### Intermediate  
4. **API Usage**: Test all REST endpoints with curl or Postman
5. **Schema Changes**: Add a new customer field using Liquibase
6. **Validation**: Test form validation by entering invalid data

### Advanced
7. **Custom Features**: Add new search filters or customer fields
8. **Performance**: Test with large datasets and optimize queries
9. **Integration**: Connect external tools or build additional services

## 🎓 Educational Value

This lab demonstrates:

### Database Concepts
- **Relational Design**: Proper table structure and relationships
- **Indexing**: Performance optimization for searches
- **Triggers**: Automated audit trail generation
- **JSONB**: Flexible data storage for audit records

### Application Architecture
- **REST APIs**: RESTful service design principles  
- **MVC Pattern**: Clear separation of concerns
- **State Management**: Frontend state handling
- **Error Handling**: Graceful error management

### DevOps Practices
- **Containerization**: Application packaging and deployment
- **Database Migrations**: Version-controlled schema evolution
- **Configuration Management**: Environment-specific settings
- **Monitoring**: Logging and health checks

## 🛡️ Security Notes

⚠️ **This is a lab environment for educational purposes.**

For production use, implement:
- Authentication and authorization
- Input sanitization and validation
- HTTPS/TLS encryption  
- Database access controls
- Audit log protection
- Rate limiting

## 🤝 Contributing

This lab is designed for learning and experimentation. Feel free to:
- Add new features
- Improve the UI/UX
- Optimize performance
- Add tests
- Enhance documentation

## 📄 Additional Documentation

- [Development Guide](DEVELOPMENT.md) - Detailed technical documentation
- [API Documentation](http://localhost:8080/swagger-ui.html) - Interactive API docs (when running)
- [Database Schema](backend/src/main/resources/db/changelog/) - Liquibase changesets

## 🎉 Next Steps

Once you're comfortable with the basics:

1. **Extend the Schema**: Add customer categories, notes, or contact preferences
2. **Advanced Queries**: Implement complex search and reporting features  
3. **Integration**: Connect with external APIs or services
4. **Microservices**: Split into multiple specialized services
5. **Mobile App**: Build a mobile interface using the same API
6. **Analytics**: Add reporting and dashboard capabilities

---

**Happy Learning!** 🚀

This lab provides a solid foundation for understanding modern web application development, database management, and DevOps practices.
