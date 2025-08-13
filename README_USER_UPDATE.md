# User Update Functionality with Asymmetric Keys

This document describes the new user update functionality that has been implemented to work with asymmetric RSA keys for enhanced security.

## Overview

The new update functionality allows users to update only their username and password while keeping their ID and email immutable. The system uses asymmetric RSA keys for JWT token signing and verification, providing enhanced security.

## New Endpoint

### PUT `/users/{id}/update`

Updates a user's username and/or password. Only the authenticated user can update their own information.

**URL Parameters:**
- `id` (Long): The ID of the user to update

**Request Body (JSON):**
```json
{
  "username": "newUsername",
  "password": "newPassword123"
}
```

**Notes:**
- Both fields are optional, but at least one must be provided
- Username cannot be empty if provided
- ID and email remain immutable

**Headers:**
- `Authorization: Bearer <JWT_TOKEN>` (required)

**Response:**
- `200 OK`: User updated successfully
- `400 Bad Request`: Validation error
- `403 Forbidden`: Access denied (trying to update another user's info)
- `404 Not Found`: User not found
- `500 Internal Server Error`: Server error

## Security Features

1. **Authentication Required**: Users must be authenticated with a valid JWT token
2. **Authorization**: Users can only update their own information
3. **Asymmetric Key Encryption**: JWT tokens are signed with RSA private key and verified with public key
4. **Password Security**: Passwords are encrypted using BCrypt with strength factor 4
5. **Input Validation**: Comprehensive validation of update requests

## Example Usage

### cURL Example
```bash
curl -X PUT \
  http://localhost:8080/users/1/update \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <your_jwt_token>' \
  -d '{
    "username": "newUsername",
    "password": "newSecurePassword123"
  }'
```

### JavaScript Example
```javascript
const response = await fetch('/users/1/update', {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${jwtToken}`
  },
  body: JSON.stringify({
    username: 'newUsername',
    password: 'newSecurePassword123'
  })
});

if (response.ok) {
  const updatedUser = await response.json();
  console.log('User updated:', updatedUser);
} else {
  const error = await response.text();
  console.error('Update failed:', error);
}
```

## Technical Implementation

### DTOs
- `UpdateUserDTO`: Contains only username and password fields

### Service Layer
- `updateUserWithAsymmetricKeys()`: Main update method with security validation
- `validateUpdateRequest()`: Validates update request data
- `encryptPasswordWithAsymmetricKey()`: Encrypts passwords securely

### Security Configuration
- RSA private/public key pair for JWT signing
- JWT encoder/decoder beans configured
- Authentication filter for JWT validation

## Testing

The functionality includes comprehensive unit tests covering:
- Successful updates
- Unauthorized access attempts
- User not found scenarios
- Invalid update requests

Run tests with:
```bash
./gradlew test
```

## Migration Notes

The old `updateUser()` method remains available for backward compatibility, but it's recommended to use the new `updateUserWithAsymmetricKeys()` method for enhanced security.

## Configuration

Ensure the following properties are set in `application.properties`:
```properties
jwt.private.key=<your_private_key>
jwt.public.key=<your_public_key>
tokenValidity=3600000
```

The RSA keys should be in PEM format and properly configured for JWT signing and verification.
