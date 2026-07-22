package com.smartbudget.service;

// ============================================================
// TICKET-F040 to F043 (Day 4, Sprint 3) — Unit Tests with JUnit 5 + Mockito
// ============================================================
//
// WHAT: Unit tests verify that your code works CORRECTLY.
//       These tests check TransactionService WITHOUT a real database.
//       Instead, Mockito creates "mock" (fake) repositories that return
//       whatever you tell them to — no database needed, no network, no I/O.
//
// WHY:  Without tests, you only know your code works when you manually click
//       through the app. Tests automate this — they run in seconds and catch
//       bugs BEFORE they reach production. In professional development,
//       you write tests alongside (or even BEFORE) the code.
//
// KEY CONCEPTS:
//   @ExtendWith(MockitoExtension.class) → Enables Mockito in JUnit 5
//   @Mock → Creates a fake version of a class (no real database calls)
//   @BeforeEach → Runs before EVERY test method (fresh setup each time)
//   when(...).thenReturn(...) → "When someone calls this method, return this value"
//   verify(...) → "Check that this method was actually called"
//   assertEquals(expected, actual) → "These two values must be equal"
//   assertThrows(Exception.class, () -> ...) → "This code must throw this exception"
//
// TEST PATTERN: Arrange → Act → Assert
//   Arrange: Set up mock behavior (when...thenReturn)
//   Act:     Call the method you're testing
//   Assert:  Check the result + verify interactions
//
// ============================================================
public class TransactionServiceTest {

    // -------------------------------------------------------
    // TODO TICKET-F043: Step 1 — Add test class annotations
    // -------------------------------------------------------
    // WHAT: @ExtendWith(MockitoExtension.class) tells JUnit 5 to activate Mockito.
    //       Without it, @Mock annotations are ignored and mocks are null.
    //
    // HOW:  Add @ExtendWith(MockitoExtension.class) above the class declaration.
    //       You will need to import:
    //         org.junit.jupiter.api.extension.ExtendWith
    //         org.mockito.junit.jupiter.MockitoExtension
    //
    // WHY:  JUnit is extensible — @ExtendWith plugs in additional functionality.
    //       MockitoExtension scans the class for @Mock fields and creates fake objects.
    //
    // OBSERVE: Without this annotation, all tests will fail with NullPointerException
    //          because the mock fields won't be initialized.

    // -------------------------------------------------------
    // TODO TICKET-F043: Step 2 — Declare mock fields
    // -------------------------------------------------------
    // WHAT: @Mock creates a fake version of a repository.
    //       When you call a method on a mock (e.g., transactionRepo.findAll()),
    //       it returns null/empty by default UNLESS you tell it otherwise with when().
    //
    // HOW:  Declare three fields annotated with @Mock:
    //         - TransactionRepository (for transaction CRUD)
    //         - UserRepository (for user lookups during create)
    //         - CategoryRepository (for category lookups during create)
    //       Also declare a TransactionService field (NOT mocked — this is the real class).
    //       Import @Mock from org.mockito.Mock.
    //
    // WHY:  Mocking repositories means your tests don't need a database.
    //       Tests run in milliseconds instead of seconds. They never fail because
    //       "the database is down" — they only fail if YOUR code is wrong.
    //
    // OBSERVE: After declaring, verify the test class compiles without errors.

    // -------------------------------------------------------
    // TODO TICKET-F043: Step 3 — Initialize service with mocks in @BeforeEach
    // -------------------------------------------------------
    // WHAT: @BeforeEach runs the annotated method before EVERY test.
    //       This ensures each test starts with a fresh service instance.
    //
    // HOW:  Create a void method annotated with @BeforeEach.
    //       Inside, create a new TransactionService and pass all three mocks
    //       to its constructor: new TransactionService(transactionRepo, userRepo, categoryRepo).
    //       NOTE: This only works AFTER TransactionService has the 3-parameter constructor
    //       (TICKET-F063, Step 1).
    //
    // WHY:  Each test must be independent. If test A modifies the service state,
    //       test B should NOT be affected. Creating a fresh instance ensures isolation.
    //
    // OBSERVE: Run the test class — @BeforeEach should execute before each test method.

    // -------------------------------------------------------
    // TODO TICKET-F040: Step 4 — Write testInsertTransaction
    // -------------------------------------------------------
    // WHAT: Tests that creating a valid transaction works correctly.
    //       This is the "happy path" test — everything is valid.
    //
    // HOW:  Follow the Arrange → Act → Assert pattern:
    //       ARRANGE:
    //         - Create a User object and set its userId
    //         - Create a Category object and set its categoryId
    //         - Tell the mocks what to return:
    //           when(userRepo.findById(1L)).thenReturn(Optional.of(mockUser))
    //           when(categoryRepo.findById(1L)).thenReturn(Optional.of(mockCategory))
    //           when(transactionRepo.save(any())).thenAnswer(i -> i.getArgument(0))
    //           (thenAnswer returns whatever was passed to save — simulating "save and return")
    //       ACT:
    //         - Call service.create() with valid parameters (userId, categoryId, amount, date, description, type)
    //       ASSERT:
    //         - assertNotNull(result) — verify something was returned
    //         - assertEquals(expectedAmount, result.getAmount()) — verify the amount is correct
    //         - verify(transactionRepo, times(1)).save(any()) — verify save was called exactly once
    //
    // WHY:  This tests the core business logic: does create() actually create a transaction?
    //       verify() ensures the service actually called repo.save() — not just returning null.
    //
    // OBSERVE: The test should PASS (green). If it fails, check your service's create() method.

    // -------------------------------------------------------
    // TODO TICKET-F041: Step 5 — Write testGetByUserId
    // -------------------------------------------------------
    // WHAT: Tests that getting transactions by user ID returns the correct list.
    //
    // HOW:  ARRANGE:
    //         - Create 2 Transaction objects with different IDs
    //         - Tell the mock: when(transactionRepo.findByUser_UserIdOrderByTxnDateDesc(1L))
    //           .thenReturn(Arrays.asList(t1, t2))
    //       ACT:
    //         - Call service.getByUserId(1L)
    //       ASSERT:
    //         - assertEquals(2, result.size()) — verify 2 transactions returned
    //         - verify the repository method was called with the correct userId
    //
    // WHY:  This verifies the service correctly delegates to the repository.
    //       It also confirms the method exists and is wired properly.
    //
    // OBSERVE: Should pass if your service's getByUserId() delegates correctly.

    // -------------------------------------------------------
    // TODO TICKET-F042: Step 6 — Write testDeleteTransaction
    // -------------------------------------------------------
    // WHAT: Tests that deleting a transaction calls the repository's deleteById.
    //
    // HOW:  ARRANGE:
    //         - Tell the mock: when(transactionRepo.existsById(1L)).thenReturn(true)
    //           (The service should check existence before deleting)
    //       ACT:
    //         - Call service.delete(1L)
    //       ASSERT:
    //         - verify(transactionRepo, times(1)).deleteById(1L)
    //           "Verify that deleteById was called exactly once with ID 1"
    //
    // WHY:  Delete is a destructive operation. This test ensures the service
    //       actually delegates to the repository and doesn't silently skip the delete.
    //       The existsById check ensures the service throws 404 for missing IDs.
    //
    // OBSERVE: Should pass. Try removing the existsById mock — the service should throw 404.

    // -------------------------------------------------------
    // TODO TICKET-F043: Step 7 — Write testCreateTransaction_invalidAmount
    // -------------------------------------------------------
    // WHAT: Tests that creating a transaction with a NEGATIVE amount is rejected.
    //       This is a "negative test" — testing that bad input fails correctly.
    //
    // HOW:  Use assertThrows to verify an exception is thrown:
    //         assertThrows(InvalidTransactionException.class, () ->
    //             service.create(1L, 1L, negativeAmount, date, description, type)
    //         )
    //       Then verify that save was NEVER called:
    //         verify(transactionRepo, never()).save(any())
    //       The "never()" verification is crucial — if validation fails,
    //       the invalid data must NOT reach the database.
    //
    // WHY:  Validation tests are as important as happy-path tests.
    //       They prove your service rejects bad data BEFORE it hits the database.
    //       In financial applications, saving a -£50 transaction would corrupt the books.
    //
    // OBSERVE: Should pass if your service validates amount > 0 and throws
    //          InvalidTransactionException for negative amounts.
    //          If this test fails, add the validation to TransactionService.create().
}
