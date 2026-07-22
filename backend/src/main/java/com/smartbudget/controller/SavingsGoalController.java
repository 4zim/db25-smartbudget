package com.smartbudget.controller;

// ============================================================
// TICKET-F061/F062 (Day 6, Sprint 5) — Savings Goal REST Controller
// ============================================================
//
// WHAT: This controller exposes HTTP endpoints for savings goals.
//       A savings goal tracks progress toward a financial target
//       (e.g., "Save £5000 for holiday by December 2026").
//       The key feature is the "contribute" endpoint — adding money to a goal.
//
// WHY:  Savings goals are a core feature of any personal finance app.
//       This controller demonstrates a business operation (contribute)
//       beyond simple CRUD — it modifies an existing resource's state.
//
// REFERENCE: See CategoryController.java for the basic pattern.
// ============================================================
public class SavingsGoalController {

    // -------------------------------------------------------
    // TODO TICKET-F061: Step 1 — Add class-level annotations
    // -------------------------------------------------------
    // WHAT: @RestController marks this as an HTTP handler with automatic JSON conversion.
    //       @RequestMapping("/api/goals") sets the base URL path for all endpoints.
    //
    // HOW:  Add @RestController and @RequestMapping("/api/goals") above the class.
    //       Import from org.springframework.web.bind.annotation.
    //
    // WHY:  The URL path "/api/goals" is shorter than "/api/savings-goals" for convenience.
    //       All endpoint methods below will be relative to this base path.
    //
    // OBSERVE: After adding, restart the app and check the Spring boot logs
    //          for "Mapped ... /api/goals" to confirm registration.

    // -------------------------------------------------------
    // TODO TICKET-F061: Step 2 — Inject dependencies
    // -------------------------------------------------------
    // WHAT: This controller needs either a SavingsGoalRepository (simple)
    //       or a SavingsGoalService (better — with validation).
    //
    // HOW:  Declare a private final field for your dependency.
    //       Create a constructor that accepts it. Spring auto-injects.
    //       START with the repository for quick results,
    //       then REFACTOR to use the service once SavingsGoalService is built.
    //
    // WHY:  Starting with the repository is faster — you see results immediately.
    //       Refactoring to the service later teaches the value of the service layer:
    //       the controller stays simple, and validation lives in one place.
    //
    // OBSERVE: The app should boot without errors if the injected bean exists.

    // -------------------------------------------------------
    // TODO TICKET-F061: Step 3 — GET /api/goals/user/{userId}
    // -------------------------------------------------------
    // WHAT: Returns all savings goals belonging to a specific user.
    //       @PathVariable extracts {userId} from the URL.
    //
    // HOW:  Create a method with @GetMapping("/user/{userId}").
    //       Accept a Long @PathVariable parameter.
    //       Delegate to the repository's findByUser_UserId() method
    //       (you must first add this method to SavingsGoalRepository).
    //
    // WHY:  Each user has their own goals. The frontend's SavingsGoals page
    //       calls this endpoint to show only the current user's goals.
    //
    // OBSERVE: Call GET /api/goals/user/1 — should return goals for user 1 (from seed data).
    //          Call GET /api/goals/user/999 — should return an empty array.
    //
    // REQUIRES: A custom query method findByUser_UserId() in SavingsGoalRepository.

    // -------------------------------------------------------
    // TODO TICKET-F061: Step 4 — POST /api/goals (create a new goal)
    // -------------------------------------------------------
    // WHAT: Creates a new savings goal from a JSON request body.
    //       Returns HTTP 201 Created.
    //
    // HOW:  Create a method with @PostMapping and @ResponseStatus(HttpStatus.CREATED).
    //       Accept a SavingsGoal annotated with @RequestBody.
    //       Save it using the repository or service.
    //
    // WHY:  Users need to create goals like "Emergency Fund — £10,000 by June 2027."
    //       The initial currentAmount should be 0 (or whatever the user sets).
    //
    // OBSERVE: POST a new goal via Postman with name, targetAmount, and deadline.
    //          Check the H2 console — the new row should appear in the savings_goals table.

    // -------------------------------------------------------
    // TODO TICKET-F062: Step 5 — PUT /api/goals/{id}/contribute
    // -------------------------------------------------------
    // WHAT: Adds money to an existing savings goal.
    //       This is NOT a simple update — it's a business operation that
    //       ADDS to the current amount rather than replacing it.
    //
    // HOW:  Create a method with @PutMapping("/{id}/contribute").
    //       Accept the goal ID as @PathVariable and the contribution amount
    //       from the request body. You need a way to receive just the amount:
    //         Option A: Accept a Map and extract the "amount" key
    //         Option B: Create a small inner class with an amount field
    //       Then:
    //         1. Find the goal by ID (throw 404 if not found)
    //         2. Add the contribution to the current amount using BigDecimal.add()
    //         3. Save and return the updated goal
    //
    // WHY:  This demonstrates a domain-specific operation beyond basic CRUD.
    //       In real apps, you'd also record the contribution as a separate transaction,
    //       check if the goal is already met, and perhaps send a notification.
    //
    // OBSERVE: Find a goal with currentAmount = 500, targetAmount = 5000.
    //          PUT /api/goals/{id}/contribute with {"amount": 100}.
    //          GET the goal again — currentAmount should now be 600.
    //          Try contributing a negative amount — what happens?
    //          (If using SavingsGoalService, it should reject with HTTP 400.)
}
