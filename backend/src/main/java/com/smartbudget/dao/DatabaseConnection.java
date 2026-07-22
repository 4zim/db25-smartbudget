package com.smartbudget.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// ============================================================
// TICKET-F035 (Day 4, Sprint 3) — JDBC Connection Utility
// ============================================================
//
// WHAT: JDBC (Java Database Connectivity) is Java's standard API for talking
//       to databases. DriverManager.getConnection() creates a live connection
//       to PostgreSQL using a URL, username, and password.
//
//       This class is a UTILITY — it provides a static method that any DAO
//       class can call. Static means you call it as DatabaseConnection.getConnection()
//       without creating an object first.
//
// WHY:  Every DAO method (insert, getAll, delete) needs a database connection.
//       Without this utility, every method would repeat the same URL/user/password.
//       Centralizing it here follows DRY and makes it easy to change credentials.
//
// PREREQUISITES:
//   1. PostgreSQL must be running on localhost:5432
//   2. A database called "smartbudget" must exist
//   3. A user "sb_user" with password "sb_pass" must have access
//   Run in psql:  CREATE DATABASE smartbudget;
//                 CREATE USER sb_user WITH PASSWORD 'sb_pass';
//                 GRANT ALL PRIVILEGES ON DATABASE smartbudget TO sb_user;
//
// ============================================================
public class DatabaseConnection {
    private static final String URL      = "jdbc:postgresql://localhost:5432/smartbudget";
    private static final String USERNAME = "sb_user";
    private static final String PASSWORD = "sb_pass";

    // -------------------------------------------------------
    // TODO TICKET-F035: Implement getConnection()
    // -------------------------------------------------------
    // WHAT: A static method that returns a Connection object.
    //       The Connection represents a live session with the database.
    //       You MUST close it after use to avoid resource leaks.
    //
    // HOW:  Create a public static method called getConnection() that:
    //         - Returns type: java.sql.Connection
    //         - Throws: SQLException (checked exception — must be declared)
    //         - Body: calls DriverManager.getConnection(URL, USERNAME, PASSWORD) and returns the result
    //
    // WHY:  DriverManager is Java's built-in class for creating database connections.
    //       It reads the JDBC URL to determine which database driver to use (PostgreSQL in our case).
    //       The pom.xml already includes the PostgreSQL JDBC driver dependency.
    //
    // OBSERVE: After implementing, test it from a main() method:
    //          Call DatabaseConnection.getConnection() inside a try-with-resources block.
    //          If PostgreSQL is running, it should print "Connected!" without errors.
    //          If PostgreSQL is NOT running, you'll get a connection refused error.
}
