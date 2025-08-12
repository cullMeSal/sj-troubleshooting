# JWT Asymmetric Key Authentication Implementation

This document explains the implementation of asymmetric key JWT authentication in your Spring Boot application.

## Overview

The application has been updated from symmetric key (HMAC) JWT authentication to asymmetric key (RSA) JWT authentication. This provides better security and allows for key rotation without affecting the entire system.

## Key Changes Made

### 1. New Classes Created

- **`RsaKeyManager`**: Manages RSA key pair loading and caching
- **`RsaKeyGenerator`**: Utility class to generate new RSA key pairs
- **`TokenManagerTest`**: Test class for the new implementation

### 2. Updated Classes

- **`TokenManager`**: Changed from HS256 (HMAC) to RS256 (RSA) algorithm

## How It Works

1. **Token Generation**: JWT tokens are signed using the private key (RS256 algorithm)
2. **Token Validation**: JWT tokens are verified using the public key
3. **Key Management**: Keys are loaded from `application.properties` and cached for performance

## Configuration

The RSA keys are configured in `src/main/resources/application.properties`:

```properties
jwt.private.key=\
  -----BEGIN PRIVATE KEY-----\n\
  [Your private key content]\n\
  -----END PRIVATE KEY-----\n

jwt.public.key=\
  -----BEGIN PUBLIC KEY-----\n\
  [Your public key content]\n\
  -----END PUBLIC KEY-----\n
```

## Security Benefits

1. **Asymmetric Cryptography**: Private key for signing, public key for verification
2. **Key Separation**: Private key stays secure, public key can be shared
3. **Algorithm Upgrade**: From HS256 to RS256 (more secure)
4. **Key Rotation**: Can rotate keys without affecting existing tokens

## Usage

### Generating New Keys

If you need to generate new RSA keys, run the `RsaKeyGenerator` main method:

```bash
./gradlew run --args="sj.sj_troubleshooting.security.RsaKeyGenerator"
```

### Token Generation

```java
@Autowired
private TokenManager tokenManager;

String token = tokenManager.generateJwtToken(userDetails);
```

### Token Validation

```java
boolean isValid = tokenManager.validateJwtToken(token, userDetails);
```

## Testing

Run the tests to verify the implementation:

```bash
./gradlew test
```

## Dependencies

The following JWT dependencies are required (already in your `build.gradle`):

```gradle
implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'
```

## Migration Notes

- The old `secret` property in `application.properties` is no longer used
- JWT algorithm changed from HS256 to RS256
- Token format remains the same, only the signing mechanism changed
- Existing tokens signed with the old system will not be valid

## Troubleshooting

1. **Key Loading Issues**: Ensure keys are properly formatted in `application.properties`
2. **Token Validation Failures**: Verify the public key matches the private key used for signing
3. **Performance**: Keys are cached after first load for better performance

## Security Considerations

1. **Private Key Security**: Keep the private key secure and never expose it
2. **Key Rotation**: Implement a key rotation strategy for production environments
3. **Key Storage**: Consider using a secure key management service for production
4. **Algorithm**: RS256 is more secure than HS256 and is recommended for production use
