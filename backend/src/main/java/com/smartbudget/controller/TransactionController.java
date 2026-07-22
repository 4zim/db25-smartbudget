package com.smartbudget.controller;

// ============================================================
// TICKET-F056 to F059, F102 (Day 6, Sprint 5 + Day 9) — Transaction REST Controller
// ============================================================
//
// WHAT: A REST Controller is the entry point for HTTP requests.
//       When the React frontend (or Postman) calls GET /api/transactions,
//       Spring routes that request to a method in THIS class.
//       The controller should ONLY handle HTTP concerns — it delegates
//       business logic to the service layer.
//
// WHY:  Separation of concerns. The controller knows about HTTP (status codes,
//       request bodies, path variables). The service knows about business rules
//       (validation, calculations). The repository knows about the database.
//       This 3-layer architecture (Controller → Service → Repository) is the
//       standard pattern in Spring Boot applications.
//
// PREREQUISITES (complete these first):
//   - TICKET-F048: @Entity Transaction (provided in entity/)
//   - TICKET-F051: TransactionRepository with custom queries
//   - TICKET-F063: TransactionService with business logic
//
// REFERENCE: See CategoryController.java for a working example.
// ============================================================
public class TransactionController {

    // -------------------------------------------------------
    // TODO TICKET-F056: Step 1 — Add class-level annotations
    // -------------------------------------------------------
    // WHAT: @RestController is a combination of two annotations:
    //         @Controller (marks this class as an HTTP handler)
    //         @ResponseBody (automatically converts return values to JSON)
    //       @RequestMapping("/api/transactions") sets the base URL path.
    //       ALL methods in this class will be relative to /api/transactions.
    //
    // HOW:  Add @RestController above the class declaration.
    //       Add @RequestMapping("/api/transactions") below it.
    //       You will need to import from org.springframework.web.bind.annotation.
    //
    // WHY:  Without @RestController, Spring does not know this class handles HTTP.
    //       Without @RequestMapping, Spring does not know which URL path to use.
    //
    // OBSERVE: After adding, restart the app. In the Spring boot logs, you should see
    //          a line like "Mapped ... /api/transactions" — that confirms Spring found it.

    // -------------------------------------------------------
    // TODO TICKET-F063: Step 2 — Inject TransactionService
    // -------------------------------------------------------
    // WHAT: Dependency Injection (DI) means Spring automatically provides
    //       the objects this class needs. Instead of doing "new TransactionService()",
    //       you declare a constructor parameter, and Spring passes the instance.
    //
    // HOW:  Declare a private final field of type TransactionService.
    //       Create a constructor that accepts TransactionService and assigns it.
    //       Spring sees the constructor, finds the @Service bean, and passes it in.
    //
    //       ALTERNATIVE (simpler start): Inject TransactionRepository directly
    //       instead of the service, if you haven't built TransactionService yet.
    //
    // WHY:  Constructor injection is the recommended way in Spring because:
    //       1. Fields can be "final" (immutable after construction)
    //       2. Makes dependencies explicit and testable
    //       3. Spring guarantees the dependency exists at startup
    //
    // OBSERVE: If the service/repository doesn't exist as a Spring bean,
    //          the app will FAIL to start with a clear error: "No qualifying bean".

    // -------------------------------------------------------
    // TODO TICKET-F056: Step 3 — GET /api/transactions (list all)
    // -------------------------------------------------------
    // WHAT: A GET endpoint that returns ALL transactions as a JSON array.
    //       @GetMapping maps HTTP GET requests to this method.
    //       The return value (a List) is automatically converted to JSON.
    //
    // HOW:  Create a public method that returns a List of Transaction entities.
    //       Annotate it with @GetMapping (no path — uses the class-level /api/transactions).
    //       Inside, delegate to the service's getAll() method (or repo.findAll()).
    //
    // WHY:  This is the most basic CRUD endpoint — Read all records.
    //       The frontend's TransactionList page calls this to populate the table.
    //
    // OBSERVE: Start the app, then visit http://localhost:8080/api/transactions in a browser.
    //          You should see a JSON array of all transactions from the seed data.
    //          If you see an empty array [], check that data.sql is loading.

    // -------------------------------------------------------
    // TODO TICKET-F057: Step 4 — POST /api/transactions (create)
    // -------------------------------------------------------
    // WHAT: A POST endpoint that creates a new transaction.
    //       @PostMapping maps HTTP POST requests to this method.
    //       @RequestBody tells Spring to parse the JSON request body
    //       into a Transaction object automatically (deserialization).
    //       @ResponseStatus(HttpStatus.CREATED) returns HTTP 201 instead of 200.
    //
    // HOW:  Create a public method that accepts a Transaction parameter
    //       annotated with @RequestBody. Return the saved entity.
    //       Annotate the method with @PostMapping and @ResponseStatus(HttpStatus.CREATED).
    //       Delegate to the service's create() method (which validates the data).
    //
    // WHY:  HTTP 201 Created is the correct status for "new resource created."
    //       @RequestBody is essential — without it, the JSON body is ignored
    //       and all fields would be null.
    //
    // OBSERVE: Use Postman to POST a JSON body to /api/transactions.
    //          You should get a 201 response with the saved transaction (including its new ID).
    //          Try an invalid amount (negative) — if the service validates, you'll get 400.

    // -------------------------------------------------------
    // TODO TICKET-F058: Step 5 — GET /api/transactions/user/{userId}
    // -------------------------------------------------------
    // WHAT: A GET endpoint that returns transactions for ONE specific user.
    //       @PathVariable extracts the {userId} from the URL path.
    //       For example: GET /api/transactions/user/1 → userId = 1
    //
    // HOW:  Create a method with @GetMapping("/user/{userId}").
    //       Accept a Long parameter annotated with @PathVariable.
    //       Delegate to the service's getByUserId() method (or a repo custom query).
    //
    // WHY:  The frontend needs user-specific data. A dashboard showing "Alice's transactions"
    //       calls this endpoint, not the "get all" endpoint.
    //
    // OBSERVE: Call GET /api/transactions/user/1 — you should see only user 1's transactions.
    //          Call GET /api/transactions/user/999 — you should get an empty array (not an error).
    //
    // REQUIRES: TICKET-F051 — the custom query findByUser_UserIdOrderByTxnDateDesc()
    //           must exist in TransactionRepository.

    // -------------------------------------------------------
    // TODO TICKET-F059: Step 6 — DELETE /api/transactions/{id}
    // -------------------------------------------------------
    // WHAT: A DELETE endpoint that removes a transaction by its ID.
    //       @DeleteMapping("/{id}") maps HTTP DELETE to this method.
    //       @ResponseStatus(HttpStatus.NO_CONTENT) returns HTTP 204 — "success, no body."
    //
    // HOW:  Create a void method with @DeleteMapping("/{id}").
    //       Accept a Long @PathVariable. Delegate to the service's delete() method.
    //       The service should check if the ID exists and throw 404 if not.
    //
    // WHY:  HTTP 204 No Content is the standard response for successful deletes.
    //       There's nothing to return — the resource is gone.
    //
    // OBSERVE: Delete a transaction, then call GET /api/transactions — it should be gone.
    //          Try deleting a non-existent ID — if the service throws ResourceNotFoundException,
    //          GlobalExceptionHandler returns HTTP 404.

    // -------------------------------------------------------
    // TODO TICKET-F102 (Day 9): Step 7 — PUT /api/transactions/{id} (update)
    // -------------------------------------------------------
    // WHAT: A PUT endpoint that updates an existing transaction.
    //       PUT means "replace the entire resource with this new data."
    //       @PutMapping("/{id}") maps HTTP PUT to this method.
    //
    // HOW:  Create a method with @PutMapping("/{id}").
    //       Accept the id as @PathVariable and the updated data as @RequestBody.
    //       Delegate to the service's update() method, which should:
    //         1. Find the existing transaction (throw 404 if missing)
    //         2. Update the fields
    //         3. Save and return the updated entity
    //
    // WHY:  Without an update endpoint, users can only delete and re-create transactions.
    //       PUT is idempotent — calling it multiple times with the same data produces the same result.
    //
    // OBSERVE: Update a transaction's amount from 100 to 200.
    //          GET the same transaction — the amount should be 200.
}
