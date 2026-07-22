package com.smartbudget.controller;

// ============================================================
// TICKET-F060 (Day 6, Sprint 5) — User REST Controller
// ============================================================
//
// WHAT: This controller exposes HTTP endpoints for managing users.
//       It follows the same pattern as CategoryController (your reference).
//       Users are the "owners" of transactions and savings goals.
//
// WHY:  The frontend needs to know which users exist (for dropdowns, etc.)
//       and the backend needs to create users before they can add transactions.
//       Without a User endpoint, you can't associate transactions with people.
//
// REFERENCE: See CategoryController.java — same pattern, different entity.
// ============================================================
public class UserController {

    // -------------------------------------------------------
    // TODO TICKET-F060: Step 1 — Add class-level annotations
    // -------------------------------------------------------
    // WHAT: @RestController tells Spring this class handles HTTP requests
    //       and automatically converts return values to JSON.
    //       @RequestMapping("/api/users") sets the base URL for all endpoints.
    //
    // HOW:  Add @RestController and @RequestMapping("/api/users") above the class.
    //       Import from org.springframework.web.bind.annotation.
    //
    // WHY:  Without these annotations, Spring ignores this class entirely.
    //       The URL path "/api/users" follows REST naming conventions:
    //       plural nouns, lowercase, prefixed with /api.
    //
    // OBSERVE: After adding, restart the app. The Spring boot log should show
    //          "Mapped ... /api/users" — confirming the endpoint is registered.

    // -------------------------------------------------------
    // TODO TICKET-F060: Step 2 — Inject UserRepository
    // -------------------------------------------------------
    // WHAT: Constructor injection — Spring provides the UserRepository
    //       instance when creating this controller.
    //
    // HOW:  Declare a private final field of type UserRepository.
    //       Create a constructor that accepts UserRepository and assigns it.
    //       Compare with CategoryController — exact same pattern.
    //
    // WHY:  The controller needs the repository to fetch/save user data.
    //       Constructor injection makes the dependency explicit and testable.
    //
    // OBSERVE: If UserRepository is missing or not annotated with @Repository,
    //          the app won't start — Spring tells you exactly what bean is missing.

    // -------------------------------------------------------
    // TODO TICKET-F060: Step 3 — GET /api/users (list all)
    // -------------------------------------------------------
    // WHAT: Returns all users as a JSON array.
    //       This is used by the frontend for user selection dropdowns.
    //
    // HOW:  Create a method annotated with @GetMapping that returns
    //       a List of User entities. Delegate to repo.findAll().
    //
    // WHY:  The simplest possible endpoint — just return everything.
    //       Good starting point before adding filtering or pagination.
    //
    // OBSERVE: Visit http://localhost:8080/api/users in your browser.
    //          You should see JSON with the 5 seed users from data.sql.

    // -------------------------------------------------------
    // TODO TICKET-F060: Step 4 — POST /api/users (create)
    // -------------------------------------------------------
    // WHAT: Creates a new user from a JSON request body.
    //       Returns HTTP 201 Created with the saved user (including generated ID).
    //
    // HOW:  Create a method annotated with @PostMapping and @ResponseStatus(HttpStatus.CREATED).
    //       Accept a User parameter annotated with @RequestBody.
    //       Delegate to repo.save(user).
    //       LATER: Add @Valid before @RequestBody to enable Bean Validation
    //       (@NotBlank, @Email annotations on the User entity fields).
    //
    // WHY:  @RequestBody deserializes JSON → Java object. Without it, the body is ignored.
    //       @ResponseStatus(HttpStatus.CREATED) returns 201 instead of the default 200.
    //
    // OBSERVE: POST a JSON body with name and email using Postman.
    //          Check the response — it should include the auto-generated userId.
    //          POST the same email twice — what happens? (Hint: look at @Column(unique = true))

    // -------------------------------------------------------
    // TODO TICKET-F060: Step 5 — GET /api/users/{id} (get by ID)
    // -------------------------------------------------------
    // WHAT: Returns a single user by their ID.
    //       If the user doesn't exist, returns HTTP 404 Not Found.
    //
    // HOW:  Create a method with @GetMapping("/{id}").
    //       Accept a Long parameter annotated with @PathVariable.
    //       Use repo.findById(id).orElseThrow() to either return the user
    //       or throw ResourceNotFoundException.
    //       orElseThrow() is Java's Optional method — it returns the value
    //       if present, or throws the exception you provide if empty.
    //
    // WHY:  findById() returns Optional<User>, not User directly.
    //       This forces you to handle the "not found" case explicitly.
    //       Without orElseThrow(), you'd need if/else checks for null.
    //
    // OBSERVE: Call GET /api/users/1 — should return Alice's data.
    //          Call GET /api/users/999 — should return HTTP 404 with an error message.
    //          (The 404 response format is controlled by GlobalExceptionHandler.)
}
