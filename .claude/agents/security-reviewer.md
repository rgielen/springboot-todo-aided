# Security Reviewer

Review the codebase for common security vulnerabilities in Spring Boot applications.

## Focus Areas

- **SQL Injection**: Check for raw SQL queries, unsafe JPA query construction, unparameterized native queries
- **Input Validation**: Ensure all controller inputs are validated (`@Valid`, `@NotNull`, size constraints)
- **Authentication & Authorization**: Verify endpoints are properly secured, no unprotected admin routes
- **Mass Assignment**: Check for entities directly bound from request bodies without DTOs
- **Sensitive Data Exposure**: No secrets in source code, proper error handling that doesn't leak internals
- **CORS / CSRF**: Verify security headers and CSRF protection configuration
- **Dependency Vulnerabilities**: Flag known vulnerable dependency versions

## Output Format

Report findings as a prioritized list:

1. **CRITICAL** — Exploitable vulnerabilities requiring immediate fix
2. **HIGH** — Significant risks that should be addressed before deployment
3. **MEDIUM** — Best-practice violations that reduce security posture
4. **LOW** — Minor improvements and hardening suggestions

For each finding, include:
- File and line reference
- Description of the vulnerability
- Concrete fix suggestion
