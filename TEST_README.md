# Authentication Test Suite

This document describes the comprehensive test suite for the login/register functionality in the SJ Troubleshooting application.

## Test Structure

The test suite is organized into several layers:

### 1. Unit Tests
- **AuthControllerTest**: Tests the REST endpoints for registration and login
- **UserServiceTest**: Tests the business logic for user authentication and registration
- **SecurityConfigTest**: Tests the security configuration and endpoint access
- **CustomAuthenticationProviderTest**: Tests the custom authentication provider
- **TokenManagerTest**: Tests JWT token generation and validation
- **GlobalExceptionHandlerTest**: Tests exception handling for authentication errors

### 2. Integration Tests
- **AuthIntegrationTest**: End-to-end tests for the complete authentication flow

### 3. Test Utilities
- **TestDataBuilder**: Utility class for creating test data

## Running the Tests

### Prerequisites
- Java 17 or higher
- Gradle 7.0 or higher

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Classes
```bash
./gradlew test --tests AuthControllerTest
./gradlew test --tests UserServiceTest
./gradlew test --tests SecurityConfigTest
```

### Run Tests with Coverage
```bash
./gradlew test jacocoTestReport
```

## Test Coverage

### Registration Tests
- ✅ Successful user registration
- ✅ Duplicate email handling
- ✅ Invalid email format validation
- ✅ Empty field handling
- ✅ Password encryption verification

### Login Tests
- ✅ Successful authentication
- ✅ Invalid credentials handling
- ✅ Non-existent user handling
- ✅ Invalid email format validation
- ✅ JWT token generation

### Security Tests
- ✅ Public endpoint accessibility
- ✅ Protected endpoint restrictions
- ✅ Authentication filter chain
- ✅ Custom authentication provider
- ✅ JWT token validation

### Exception Handling Tests
- ✅ Email unavailable exceptions
- ✅ Invalid email format exceptions
- ✅ Authentication exceptions
- ✅ Generic error handling

## Test Configuration

The tests use an in-memory H2 database with the following configuration:
- **Profile**: `test`
- **Database**: H2 in-memory
- **Security**: Mock security context
- **JWT**: Test secret key

## Key Test Scenarios

### Happy Path
1. User registers with valid credentials
2. User logs in with correct credentials
3. JWT token is generated and returned

### Error Scenarios
1. Registration with duplicate email
2. Registration with invalid email format
3. Login with non-existent user
4. Login with wrong password
5. Login with invalid email format

### Security Scenarios
1. Public endpoints accessible without authentication
2. Protected endpoints require authentication
3. JWT token validation
4. Custom authentication provider logic

## Test Data

Test data is generated using the `TestDataBuilder` utility class, which provides:
- Valid and invalid email formats
- Sample user entities
- Authentication request objects
- Registration DTOs

## Best Practices Implemented

1. **Arrange-Act-Assert Pattern**: Clear test structure
2. **Mocking**: External dependencies are properly mocked
3. **Test Isolation**: Each test is independent
4. **Comprehensive Coverage**: Edge cases and error scenarios
5. **Realistic Data**: Tests use realistic test data
6. **Security Testing**: Authentication and authorization scenarios

## Troubleshooting

### Common Issues

1. **Test Database Connection**: Ensure H2 dependency is included
2. **Security Context**: Some tests may require `@WithMockUser` annotation
3. **JWT Configuration**: Test JWT secret key must be configured

### Debug Mode
To run tests with debug logging:
```bash
./gradlew test -Dlogging.level.sj.sj_troubleshooting=DEBUG
```

## Adding New Tests

When adding new authentication features:

1. Add unit tests for the new functionality
2. Update integration tests if needed
3. Add test data builders if required
4. Ensure proper exception handling coverage
5. Update this documentation

## Performance Notes

- Unit tests run quickly (< 1 second each)
- Integration tests may take longer due to Spring context startup
- H2 in-memory database provides fast test execution
- MockMvc provides fast HTTP request simulation
