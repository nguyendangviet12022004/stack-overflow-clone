# Running Mail Service Tests

## Prerequisites
- Java 21 installed
- Maven or use the included Maven wrapper (./mvnw)
- RabbitMQ not required (tests use mocks)
- Mail server not required (tests use mocks)

## Quick Start

### Run All Tests
```bash
cd mail
./mvnw test
```

### Run Tests with Coverage Report
```bash
cd mail
./mvnw clean test jacoco:report
```
Coverage report will be available at: `target/site/jacoco/index.html`

### Run Specific Test Class
```bash
# Run MailServiceImpTest only
./mvnw test -Dtest=MailServiceImpTest

# Run AmqpConfigTest only
./mvnw test -Dtest=AmqpConfigTest

# Run all integration tests
./mvnw test -Dtest=*IntegrationTest
```

### Run Tests with Specific Profile
```bash
./mvnw test -Dspring.profiles.active=test
```

### Run Tests in Debug Mode
```bash
./mvnw test -Dmaven.surefire.debug
```
Then attach your debugger to port 5005

## Test Structure

```
mail/src/test/java/com/sukhoi/mail/
├── MailApplicationTests.java (116 lines, 15 tests)
├── config/
│   └── AmqpConfigTest.java (122 lines, 13 tests)
├── constant/
│   └── AmqpConstantsTest.java (117 lines, 16 tests)
├── controller/
│   └── TestControllerTest.java (195 lines, 17 tests)
├── dto/message/
│   └── ActivateAccountRequestTest.java (196 lines, 17 tests)
├── integration/
│   └── MailServiceIntegrationTest.java (101 lines, 7 tests)
├── message/
│   └── MessageReceiverTest.java (224 lines, 14 tests)
└── service/impl/
    └── MailServiceImpTest.java (265 lines, 17 tests)
```

**Total**: 1,336 lines of test code, ~116 test cases

## Test Resources

```
mail/src/test/resources/
└── application-test.yaml
```

## Common Issues

### Issue: Tests fail due to RabbitMQ connection
**Solution**: Tests should use mocks. Ensure `@MockBean` is used and `@TestPropertySource` disables Eureka:
```java
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false"
})
```

### Issue: Port conflicts
**Solution**: Tests use MockMvc and don't start actual server. If you see port conflicts, check for other running instances.

### Issue: Java version mismatch
**Solution**: Ensure Java 21 is installed:
```bash
java -version
# Should show version 21
```

## Continuous Integration

### GitHub Actions Example
```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Run tests
        run: cd mail && ./mvnw test
```

### GitLab CI Example
```yaml
test:
  image: eclipse-temurin:21-jdk
  script:
    - cd mail
    - ./mvnw test
  artifacts:
    reports:
      junit: mail/target/surefire-reports/TEST-*.xml
```

## IDE Integration

### IntelliJ IDEA
1. Right-click on `mail/src/test/java`
2. Select "Run 'All Tests'"
3. Or use keyboard shortcut: Ctrl+Shift+F10 (Windows/Linux) or Cmd+Shift+R (Mac)

### VS Code
1. Install "Test Runner for Java" extension
2. Click the test icon in the sidebar
3. Select "Run All Tests"

### Eclipse
1. Right-click on project
2. Run As > JUnit Test

## Test Execution Time

Expected execution times (approximate):
- Unit tests: 5-10 seconds
- Integration tests: 10-15 seconds
- All tests: 15-25 seconds

## Viewing Results

### Console Output
Test results are displayed in the console with:
- ✓ Green checkmarks for passing tests
- ✗ Red X for failing tests
- Stack traces for failures

### HTML Report
After running with `./mvnw test`, view the report:
```bash
open target/surefire-reports/index.html
```

### Coverage Report
After running with coverage:
```bash
open target/site/jacoco/index.html
```

## Tips

1. **Run tests before committing**: `./mvnw test`
2. **Check coverage regularly**: Aim for >80% coverage
3. **Run integration tests separately**: `-Dtest=*IntegrationTest`
4. **Use test filtering**: `-Dtest=*ServiceTest` to run only service tests
5. **Parallel execution**: Add to pom.xml for faster tests:
   ```xml
   <configuration>
       <parallel>classes</parallel>
       <threadCount>4</threadCount>
   </configuration>
   ```

## Troubleshooting

### All tests fail with "Cannot load ApplicationContext"
- Check application-test.yaml exists
- Verify all @MockBean dependencies are properly mocked
- Ensure test classpath includes main sources

### Specific test fails intermittently
- Check for test interdependencies
- Verify proper setup/teardown in @BeforeEach/@AfterEach
- Look for static state or shared mutable objects

### OutOfMemoryError
- Increase heap size: `export MAVEN_OPTS="-Xmx1024m"`
- Or in pom.xml surefire configuration

## Next Steps

After running tests successfully:
1. Review coverage report
2. Add tests for any uncovered code
3. Consider adding mutation testing with PITest
4. Set up CI/CD pipeline
5. Configure test coverage thresholds