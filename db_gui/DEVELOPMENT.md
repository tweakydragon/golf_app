# Customer Database Management Lab - Development Guide

## Overview

This lab environment demonstrates a complete customer database management system with:

- **PostgreSQL Database**: Customer data with audit trail
- **Spring Boot Backend**: REST API with Liquibase integration
- **Vue.js Frontend**: Modern web interface
- **Docker Compose**: Containerized deployment

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Vue.js        │    │   Spring Boot   │    │   PostgreSQL    │
│   Frontend      │───▶│   Backend       │───▶│   Database      │
│   (Port 3000)   │    │   (Port 8080)   │    │   (Port 5432)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   Liquibase     │
                       │   Schema Mgmt   │
                       └─────────────────┘
```

## Key Features

### Frontend (Vue.js)
- Customer listing with search and pagination
- Customer creation and editing forms
- Audit log viewer
- Responsive design with Bootstrap 5
- Real-time form validation

### Backend (Spring Boot)
- RESTful API design
- JPA/Hibernate for database access
- Liquibase for schema management
- Automatic audit trail generation
- Input validation and error handling

### Database (PostgreSQL)
- Customer table with indexes
- Audit table with triggers
- Sample data for testing
- JSONB storage for change tracking

## API Endpoints

### Customer Management
- `GET /api/customers` - List customers (paginated)
- `GET /api/customers/{id}` - Get customer by ID
- `POST /api/customers` - Create new customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer

### Search & Filtering
- `GET /api/customers/search` - Search customers by criteria
- `GET /api/customers/search/name` - Search by name

### Audit Trail
- `GET /api/customers/{id}/audit` - Get customer audit history
- `GET /api/customers/audit/recent` - Get recent changes
- `GET /api/customers/audit/action/{action}` - Get changes by action

## Database Schema

### Customers Table
```sql
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(255),
    address VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Audit Table
```sql
CREATE TABLE audit.customer_audit (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,
    changed_by VARCHAR(100) NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    old_values JSONB,
    new_values JSONB
);
```

## Development Workflow

### 1. Local Development

#### Backend Development
```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend Development
```bash
cd frontend
npm install
npm run serve
```

#### Database Access
```bash
psql -h localhost -p 5432 -U postgres -d customer_db
```

### 2. Container Development
```bash
# Start all services
./start.sh start

# View logs
./start.sh logs

# Stop services
./start.sh stop
```

### 3. Database Changes

All schema changes should be made through Liquibase changesets:

1. Create new changeset file in `backend/src/main/resources/db/changelog/`
2. Add reference to `db.changelog-master.xml`
3. Restart backend service

Example changeset:
```xml
<changeSet id="005-add-customer-status" author="developer">
    <addColumn tableName="customers">
        <column name="status" type="VARCHAR(20)" defaultValue="ACTIVE">
            <constraints nullable="false"/>
        </column>
    </addColumn>
</changeSet>
```

## Testing Data

The system includes 5 sample customers:

1. John Doe - john.doe@example.com
2. Jane Smith - jane.smith@example.com  
3. Bob Johnson - bob.johnson@example.com
4. Alice Williams - alice.williams@example.com
5. Charlie Brown - charlie.brown@example.com

## Common Tasks

### Add New Customer Field

1. **Update Entity**: Add field to `Customer.java`
2. **Database**: Create Liquibase changeset
3. **API**: Update DTOs and validation
4. **Frontend**: Update forms and views

### View Audit Trail

1. Access frontend at http://localhost:3000
2. Navigate to "Audit Log" in the navigation
3. Filter by action type or view all changes
4. Click "View Details" for complete change information

### Search Customers

1. Use the search form on the main customer list
2. Search by first name, last name, email, or phone
3. Clear filters to show all customers
4. Use pagination controls for large result sets

## Troubleshooting

### Backend Won't Start
- Check if port 8080 is available
- Verify database connection
- Review backend logs: `./start.sh logs backend`

### Frontend Won't Load
- Check if port 3000 is available
- Verify backend API is running
- Review frontend logs: `./start.sh logs frontend`

### Database Connection Issues
- Check if PostgreSQL is running
- Verify connection parameters
- Review database logs: `./start.sh logs postgres`

### Liquibase Migration Fails
- Check changeset syntax
- Verify database state
- Review backend startup logs

## Performance Considerations

### Database
- Indexes on frequently searched columns
- Pagination for large result sets
- JSONB for flexible audit data

### Backend
- Connection pooling
- Query optimization
- Caching for read-heavy operations

### Frontend
- Lazy loading of audit data
- Debounced search inputs
- Pagination controls

## Security Notes

⚠️ **This is a lab environment for testing purposes only.**

For production use, consider:
- Authentication and authorization
- Input sanitization
- SQL injection prevention
- HTTPS/TLS encryption
- Database access controls
- Audit log protection

## Extension Ideas

### Additional Features
- Customer categories/tags
- Import/export functionality
- Advanced reporting
- Email integration
- Phone number formatting
- Address validation

### Technical Improvements
- Redis caching layer
- Elasticsearch for full-text search
- Microservices architecture
- Event-driven updates
- GraphQL API
- Mobile app support
