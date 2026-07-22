package com.smartbudget.console;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// ============================================================
// TICKET-F016 to F019 (Day 2, Sprint 1) — Console Menu Application
// ============================================================
//
// WHAT: This is a standalone Java program (NOT a Spring class).
//       It uses a while loop + Scanner to create an interactive text menu.
//       Run this class directly in your IDE: right-click → Run 'Main.main()'
//
// WHY:  Before building REST APIs (Day 5-6), you practice Java basics:
//       ArrayList, Scanner input, loops, String.format, if-else validation.
//       This is your first runnable code in the SmartBudget project.
//
// ============================================================
public class Main {
    public static void main(String[] args) {

        // -------------------------------------------------------
        // TODO TICKET-F017: Step 1 — Create sample data
        // -------------------------------------------------------
        // WHAT: An ArrayList is a resizable list that can grow/shrink dynamically.
        //       Unlike arrays (fixed size), ArrayList lets you add/remove items anytime.
        //
        // HOW:  Create an ArrayList and add 10+ hardcoded "transactions" to it.
        //       Since you haven't built the Transaction class yet, use a simple approach:
        //       each transaction can be a String[] with fields: id, description, type, amount, date.
        //       Add items using the .add() method.
        //
        // WHY:  This gives you data to display and filter before the database is connected.
        //
        // OBSERVE: After creating the list, print its .size() to verify — should be 10+.

        // -------------------------------------------------------
        // TODO TICKET-F016: Step 2 — Build the menu loop
        // -------------------------------------------------------
        // WHAT: A while loop with a Scanner creates a text-based menu.
        //       Scanner reads keyboard input from the user.
        //       The loop runs until the user chooses "Exit".
        //
        // HOW:  Create a Scanner object for System.in.
        //       Use a boolean variable (e.g., "running = true") to control the loop.
        //       Inside the loop:
        //         1. Print the menu options (1. List, 2. Add, 3. Summary, 4. Exit)
        //         2. Read the user's choice with scanner.nextInt()
        //         3. IMPORTANT: call scanner.nextLine() after nextInt() to consume the leftover newline
        //         4. Use a switch statement to handle each option
        //         5. Option 4 sets running = false to exit the loop
        //
        // WHY:  This teaches control flow (while, switch) and user input handling.
        //       The nextLine() trick after nextInt() is a common Java gotcha —
        //       without it, the next Scanner read skips input unexpectedly.
        //
        // OBSERVE: Run the program. You should see the menu. Type 1, 2, 3, 4.
        //          Each option should do something different. Typing 4 should exit.

        // -------------------------------------------------------
        // TODO TICKET-F018: Step 3 — Formatted output with printf
        // -------------------------------------------------------
        // WHAT: System.out.printf() lets you format output in aligned columns.
        //       Format specifiers control width and alignment:
        //         %-15s = left-aligned string, 15 chars wide
        //         %10.2f = right-aligned decimal, 10 chars wide, 2 decimal places
        //         %n = newline (platform-independent)
        //
        // HOW:  When the user picks "1. List Transactions":
        //         Print a header row with column names (ID, Description, Type, Amount, Date)
        //         Print a separator line using "-".repeat(55)
        //         Loop through the ArrayList and print each transaction using printf
        //
        // WHY:  Formatted output makes data readable. Without alignment,
        //       columns don't line up and the output looks messy.
        //
        // OBSERVE: The output should look like a clean table with aligned columns.
        //          All amounts should have exactly 2 decimal places.

        // -------------------------------------------------------
        // TODO TICKET-F019: Step 4 — Input validation
        // -------------------------------------------------------
        // WHAT: Validation means checking user input BEFORE using it.
        //       Invalid input should produce a clear error message, not crash the program.
        //
        // HOW:  When the user picks "2. Add Transaction":
        //         Read amount and date from the Scanner.
        //         Check Rule 1: Amount must be > 0.
        //           Parse the input to BigDecimal, then use compareTo(BigDecimal.ZERO) to check.
        //           If invalid, print an error message and skip the add.
        //         Check Rule 2: Date must not be in the future.
        //           Parse the input to LocalDate, then use isAfter(LocalDate.now()) to check.
        //           If invalid, print an error message and skip the add.
        //
        // WHY:  Real applications never trust user input. A user might type "-500" as amount
        //       or "2030-01-01" as date. Your code must handle this gracefully.
        //       Later (Day 3), you'll move this validation INTO the BaseTransaction constructor.
        //
        // OBSERVE: Try adding a transaction with amount = -10. It should print an error.
        //          Try a future date. It should also print an error.
        //          Try valid data — it should add successfully and appear in the list.

        System.out.println("SmartBudget Console - implement me! (TICKET-F016)");
    }
}
