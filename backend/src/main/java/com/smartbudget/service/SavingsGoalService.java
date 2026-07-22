package com.smartbudget.service;

// ============================================================
// TICKET-F061/F062 (Day 6, Sprint 5) — Savings Goal Service
// ============================================================
//
// WHAT: This service handles business logic for savings goals.
//       After implementing, refactor SavingsGoalController to use this service
//       instead of calling the repository directly.
//
// WHY:  The controller should only handle HTTP concerns (request/response).
//       Business rules like "contribution must be > 0" belong in the service.
//       This separation makes your code testable — you can test service logic
//       WITHOUT starting a web server.
//
// ============================================================
public class SavingsGoalService {

    // -------------------------------------------------------
    // TODO TICKET-F061: Step 1 — Add @Service + inject repositories
    // -------------------------------------------------------
    // WHAT: Same pattern as TransactionService.
    //       @Service marks this as a Spring-managed component.
    //       Inject SavingsGoalRepository and UserRepository via constructor.
    //
    // HOW:  Add @Service annotation, declare two private final repository fields,
    //       create a constructor that accepts both. Spring auto-injects them.
    //
    // OBSERVE: Check Spring boot logs — you should see this service being created.

    // -------------------------------------------------------
    // TODO TICKET-F061: Step 2 — getByUserId(Long userId)
    // -------------------------------------------------------
    // WHAT: Returns all savings goals belonging to a specific user.
    //
    // HOW:  Delegate to the repository's findByUser_UserId() method.
    //       NOTE: You must first add this method to SavingsGoalRepository (TODO there).
    //
    // OBSERVE: Call GET /api/goals/user/1 — should return goals for user 1.

    // -------------------------------------------------------
    // TODO TICKET-F061: Step 3 — getById(Long id)
    // -------------------------------------------------------
    // WHAT: Returns a single goal by its ID, or throws 404 if not found.
    //
    // HOW:  Use repo.findById(id).orElseThrow() with ResourceNotFoundException.
    //       orElseThrow() is Java's Optional method: "if value exists, return it;
    //       if empty, throw the exception I provide."
    //
    // WHY:  Without orElseThrow(), findById() returns Optional<SavingsGoal>.
    //       You'd need to check .isPresent() manually — orElseThrow is cleaner.
    //
    // OBSERVE: Request a non-existent goal ID → should get HTTP 404 with clear message.

    // -------------------------------------------------------
    // TODO TICKET-F062: Step 4 — contribute(Long goalId, BigDecimal amount)
    // -------------------------------------------------------
    // WHAT: Adds money to a savings goal's current amount.
    //       Validates that the contribution is positive.
    //
    // HOW:  1. Validate: if amount is null or <= 0, throw InvalidTransactionException
    //       2. Get the goal using getById() (reuse your Step 3 method — throws 404 if missing)
    //       3. Add the amount to the goal's current amount using BigDecimal.add()
    //       4. Save and return the updated goal
    //
    // WHY:  Validation prevents nonsensical contributions like -£50.
    //       Reusing getById() follows DRY — the 404 logic is written once.
    //
    // OBSERVE: Contribute £100 to a goal with current_amount = 500.
    //          After the call, current_amount should be 600.
    //          Try contributing -10 → should get HTTP 400.

    // -------------------------------------------------------
    // TODO TICKET-F061: Step 5 — create(Long userId, String name, BigDecimal target, LocalDate deadline)
    // -------------------------------------------------------
    // WHAT: Creates a new savings goal for a user.
    //
    // HOW:  1. Look up the User by userId using UserRepository — throw 404 if not found
    //       2. Create a new SavingsGoal entity, set all fields
    //       3. Save and return it
    //
    // OBSERVE: POST a new goal → check H2 console → the row should appear in savings_goals table.
}
