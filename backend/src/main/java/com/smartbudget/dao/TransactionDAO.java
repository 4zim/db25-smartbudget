package com.smartbudget.dao;

import com.smartbudget.entity.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ============================================================
// TICKET-F036 to F039 (Day 4, Sprint 3) — Raw JDBC DAO
// ============================================================
//
// WHAT: A DAO (Data Access Object) is a class whose ONLY job is to talk to the database.
//       It translates between Java objects and SQL queries.
//       This DAO uses raw JDBC — later (Day 5), you'll see how Spring Data JPA
//       does the same thing with ZERO SQL code.
//
//       SECURITY RULE: ALWAYS use PreparedStatement — NEVER concatenate user input into SQL.
//       Bad:  "SELECT * FROM transactions WHERE user_id = " + userId   ← SQL INJECTION ATTACK!
//       Good: "SELECT * FROM transactions WHERE user_id = ?"           ← Safe, uses parameter binding
//
// WHY:  Understanding raw JDBC helps you appreciate what Spring Data JPA does automatically.
//       In interviews, you may be asked about JDBC even if you use JPA day-to-day.
//
// ============================================================
public class TransactionDAO {

    // -------------------------------------------------------
    // TODO TICKET-F036: Implement insert(Transaction t)
    // -------------------------------------------------------
    // WHAT: Inserts a new transaction record into the database.
    //       Uses a PreparedStatement with ? placeholders for safe parameter binding.
    //
    // HOW:  1. Write the SQL INSERT statement with 6 placeholders (?)
    //          for: user_id, category_id, amount, txn_date, description, type
    //       2. Get a Connection from DatabaseConnection.getConnection()
    //       3. Create a PreparedStatement from the connection using conn.prepareStatement(sql)
    //       4. Set each parameter using ps.setLong(), ps.setBigDecimal(), ps.setDate(), ps.setString()
    //          IMPORTANT: ps.setDate() requires java.sql.Date, not java.time.LocalDate.
    //          Convert using: Date.valueOf(t.getTxnDate())
    //       5. Call ps.executeUpdate() to run the INSERT
    //       6. Use try-with-resources to auto-close Connection and PreparedStatement
    //
    // WHY:  PreparedStatement prevents SQL injection by separating SQL code from data.
    //       The database treats ? values as data, never as SQL commands.
    //       try-with-resources ensures connections are closed even if an exception occurs.
    //
    // OBSERVE: After implementing, call insert() with a valid Transaction object.
    //          Then call getAll() — your new record should appear in the list.

    // -------------------------------------------------------
    // TODO TICKET-F037: Implement getAll() → List<Transaction>
    // -------------------------------------------------------
    // WHAT: Retrieves ALL transactions from the database and returns them as a Java List.
    //       This is a "read" operation — it doesn't modify data.
    //
    // HOW:  1. Write the SQL: SELECT * FROM transactions ORDER BY txn_date DESC
    //       2. Get a Connection, create a Statement (not PreparedStatement — no parameters needed)
    //       3. Call stmt.executeQuery(sql) to get a ResultSet
    //       4. Loop through the ResultSet with while(rs.next())
    //       5. For each row, create a Transaction object and populate its fields from the ResultSet
    //          using rs.getLong("txn_id"), rs.getBigDecimal("amount"), rs.getDate("txn_date").toLocalDate(), etc.
    //       6. Add each Transaction to an ArrayList
    //       7. Return the list
    //
    // WHY:  This is the most common database operation. The while(rs.next()) pattern
    //       is fundamental JDBC — rs.next() moves to the next row, returns false when done.
    //
    // OBSERVE: After implementing, call getAll() and print each transaction.
    //          You should see all records from the database.

    // -------------------------------------------------------
    // TODO TICKET-F038: Implement getByUserId(int userId) → List<Transaction>
    // -------------------------------------------------------
    // WHAT: Retrieves transactions for ONE specific user.
    //       Uses a PreparedStatement because the userId comes from user input.
    //
    // HOW:  Same pattern as getAll(), but:
    //       - Use a WHERE clause: WHERE user_id = ?
    //       - Use PreparedStatement (not Statement) because you have a parameter
    //       - Set the parameter: ps.setInt(1, userId)
    //       - The rest is identical: loop ResultSet, build Transaction objects, return list
    //
    // WHY:  This demonstrates the difference between Statement (no parameters)
    //       and PreparedStatement (with parameters). When user input is involved,
    //       ALWAYS use PreparedStatement.
    //
    // OBSERVE: Call getByUserId(1) — you should only see transactions for user 1.
    //          Call getByUserId(999) — you should get an empty list (no crash).

    // -------------------------------------------------------
    // TODO TICKET-F039: Implement delete(int txnId)
    // -------------------------------------------------------
    // WHAT: Deletes a single transaction by its ID.
    //       executeUpdate() returns the number of rows affected.
    //
    // HOW:  1. Write the SQL: DELETE FROM transactions WHERE txn_id = ?
    //       2. Use PreparedStatement, set the txnId parameter
    //       3. Call ps.executeUpdate() — it returns an int (rows deleted)
    //       4. If the return value is 0, no record was found with that ID — log a warning
    //
    // WHY:  Checking the return value of executeUpdate() is good practice.
    //       If 0 rows were affected, the ID didn't exist — your code should handle this gracefully.
    //
    // OBSERVE: Call delete() with a valid ID, then getAll() — the record should be gone.
    //          Call delete() with a non-existent ID — no crash, just a warning message.
}
