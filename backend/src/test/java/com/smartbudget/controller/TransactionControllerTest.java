package com.smartbudget.controller;

// ============================================================
// TICKET-F064 to F066 (Day 6, Sprint 5) — Integration Tests with MockMvc
// ============================================================
//
// WHAT: Integration tests verify the FULL stack end-to-end:
//         HTTP request → Controller → Service → Repository → Database → Response
//       Unlike unit tests (TransactionServiceTest) which mock everything,
//       these tests start the REAL Spring Boot application with an H2 database
//       and send actual HTTP requests.
//
// WHY:  Unit tests verify individual classes. Integration tests verify that
//       all the pieces work TOGETHER. A controller might be wired correctly
//       but return the wrong JSON format — only an integration test catches that.
//
// KEY CONCEPTS:
//   @SpringBootTest → Starts the FULL Spring Boot application (all beans, all config)
//   @AutoConfigureMockMvc → Creates a MockMvc tool for sending HTTP requests
//   MockMvc → Simulates HTTP calls without a real web server (much faster than Postman)
//   mockMvc.perform() → Sends a request (GET, POST, PUT, DELETE)
//   .andExpect() → Checks the response (status code, JSON fields, content type)
//   jsonPath("$.fieldName") → Extracts values from JSON using dot notation
//
// NOTE: The seed data from data.sql loads automatically into H2 before tests run,
//       so you have real test data available without setup.
//
// PREREQUISITES: TransactionController must be fully implemented (TICKET-F056 to F059)
//                with @RestController, @RequestMapping, and all endpoint methods.
// ============================================================
public class TransactionControllerTest {

    // -------------------------------------------------------
    // TODO TICKET-F064: Step 1 — Add class annotations and inject MockMvc
    // -------------------------------------------------------
    // WHAT: @SpringBootTest loads the entire Spring application context.
    //       @AutoConfigureMockMvc creates a MockMvc instance you can use
    //       to send HTTP requests without a real server.
    //       @Autowired injects the MockMvc instance Spring created.
    //
    // HOW:  Add @SpringBootTest and @AutoConfigureMockMvc above the class.
    //       Declare a MockMvc field annotated with @Autowired.
    //       Import:
    //         org.springframework.boot.test.context.SpringBootTest
    //         org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
    //         org.springframework.beans.factory.annotation.Autowired
    //         org.springframework.test.web.servlet.MockMvc
    //
    // WHY:  MockMvc is faster than starting a real server + using Postman.
    //       Tests run in the build pipeline (mvn test) without any manual intervention.
    //
    // OBSERVE: The test class should compile. Running it may take a few seconds
    //          because Spring starts the full application context.

    // -------------------------------------------------------
    // TODO TICKET-F064: Step 2 — Write testCreateTransaction_validInput
    // -------------------------------------------------------
    // WHAT: Sends a POST request with valid JSON and verifies a 201 response.
    //       This tests the full round-trip: JSON → Controller → Service → DB → JSON.
    //
    // HOW:  Create a test method annotated with @Test (from org.junit.jupiter.api.Test).
    //       Build a JSON string with valid transaction fields:
    //         user (with userId), category (with categoryId), amount, txnDate, description, type
    //       Use mockMvc.perform() to send the request:
    //         mockMvc.perform(post("/api/transactions")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(jsonString))
    //       Chain .andExpect() calls to verify:
    //         status().isCreated() → HTTP 201
    //         jsonPath("$.txnId").exists() → the response has an auto-generated ID
    //         jsonPath("$.amount").value(expectedAmount) → the amount matches
    //       Import static methods from:
    //         org.springframework.test.web.servlet.request.MockMvcRequestBuilders (post, get, delete)
    //         org.springframework.test.web.servlet.result.MockMvcResultMatchers (status, jsonPath, content)
    //         org.springframework.http.MediaType
    //
    // WHY:  This proves the endpoint actually creates a record in the database.
    //       If the JSON is malformed or a field is missing, this test catches it.
    //
    // OBSERVE: Should pass when TransactionController POST is properly implemented.
    //          If it returns 404 → the endpoint isn't mapped (check @RequestMapping).
    //          If it returns 400 → the JSON format might be wrong.
    //          If it returns 500 → check the server logs for the actual error.

    // -------------------------------------------------------
    // TODO TICKET-F065: Step 3 — Write testCreateTransaction_negativeAmount
    // -------------------------------------------------------
    // WHAT: Sends a POST request with a NEGATIVE amount and verifies HTTP 400.
    //       This tests that validation correctly rejects bad input.
    //
    // HOW:  Same pattern as Step 2, but use a negative amount in the JSON.
    //       Expect status().isBadRequest() (HTTP 400) instead of isCreated().
    //       NOTE: This test only works AFTER:
    //         1. Adding @Valid before @RequestBody in the controller's POST method
    //         2. Adding @DecimalMin on the amount field in the Transaction entity
    //         3. Implementing GlobalExceptionHandler to catch validation exceptions
    //
    // WHY:  Proving that bad input is rejected is just as important as proving
    //       good input works. In a financial app, a -£50 transaction could cause
    //       incorrect balances. Validation is a security boundary.
    //
    // OBSERVE: If this returns 201 instead of 400, validation isn't active.
    //          Check: Does the controller have @Valid? Does the entity have @DecimalMin?

    // -------------------------------------------------------
    // TODO TICKET-F066: Step 4 — Write testGetTransactionsByUser
    // -------------------------------------------------------
    // WHAT: Sends a GET request to /api/transactions/user/1 and verifies
    //       the response is a JSON array with at least one transaction.
    //
    // HOW:  Use mockMvc.perform(get("/api/transactions/user/1")).
    //       Chain .andExpect() calls to verify:
    //         status().isOk() → HTTP 200
    //         content().contentType(MediaType.APPLICATION_JSON) → response is JSON
    //         jsonPath("$").isArray() → the root response is an array
    //         jsonPath("$.length()").value(greaterThan(0)) → array is not empty
    //       The greaterThan matcher comes from org.hamcrest.Matchers.
    //
    // WHY:  This verifies the GET endpoint works end-to-end and that
    //       seed data (from data.sql) is being loaded correctly.
    //       If the array is empty, either data.sql isn't running or the query is wrong.
    //
    // OBSERVE: Should pass if:
    //          1. TransactionController has GET /api/transactions/user/{userId}
    //          2. TransactionRepository has findByUser_UserIdOrderByTxnDateDesc()
    //          3. data.sql seeds transactions for user 1
}
