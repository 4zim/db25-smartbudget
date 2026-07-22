package com.smartbudget.repository;

import com.smartbudget.entity.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ============================================================
// TICKET-F061 (Day 6, Sprint 5) — Savings Goal Repository
// ============================================================
//
// WHAT: A JPA Repository for the SavingsGoal entity.
//       Like all JPA repositories, you get findAll(), findById(), save(),
//       deleteById() for FREE by extending JpaRepository.
//       Your task is to add a custom query to find goals by user.
//
// WHY:  Each user has their own savings goals. The SavingsGoalController's
//       GET /api/goals/user/{userId} endpoint needs a way to fetch
//       only that user's goals — not every goal in the database.
//
// HOW IT WORKS:
//       JpaRepository<SavingsGoal, Long> means:
//         SavingsGoal = the @Entity class (maps to savings_goals table)
//         Long        = the type of the primary key (goalId is Long)
//
// ============================================================
@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    // -------------------------------------------------------
    // TODO TICKET-F061: Add findByUser_UserId()
    // -------------------------------------------------------
    // WHAT: Finds all savings goals belonging to a specific user.
    //       The underscore in "User_UserId" navigates a relationship:
    //         SavingsGoal → user (the @ManyToOne field) → userId (the ID field)
    //       Spring generates: SELECT * FROM savings_goals WHERE user_id = ?
    //
    // HOW:  Declare a method that accepts a Long parameter (the userId)
    //       and returns a List of SavingsGoal entities.
    //       Method name: findByUser_UserId
    //       You will need to import java.util.List.
    //
    // WHY:  Without this, the controller would need to call findAll() and then
    //       filter in Java — inefficient and wasteful when the database can do it.
    //       The underscore convention (User_UserId) is Spring Data JPA's way of
    //       traversing entity relationships in method names.
    //
    // OBSERVE: After adding, call GET /api/goals/user/1 from Postman.
    //          You should see only user 1's goals (from the seed data).
    //          Call GET /api/goals/user/999 → should return an empty JSON array [].
    //
    // USED BY: SavingsGoalController GET /api/goals/user/{userId}
    //          SavingsGoalService.getByUserId()
}
