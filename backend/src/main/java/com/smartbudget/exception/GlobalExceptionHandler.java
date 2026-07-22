package com.smartbudget.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// ============================================================
// TICKET-F065 (Day 6, Sprint 5) — Global Exception Handler
// ============================================================
//
// WHAT: @RestControllerAdvice is a Spring annotation that intercepts exceptions
//       thrown by ANY controller in the application. Without it, Spring returns
//       ugly HTML error pages. With it, you return clean JSON error responses
//       that the React frontend can parse and display to the user.
//
//       Each @ExceptionHandler method handles one type of exception.
//       When that exception is thrown anywhere in the app, Spring automatically
//       calls the matching handler method instead of crashing.
//
// WHY:  The React frontend expects JSON responses, not HTML.
//       If a user sends amount = -50, the frontend should display
//       "Amount must be greater than zero" — not a raw Java stack trace.
//       This is essential for a good user experience.
//
// ============================================================
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------
    // TODO TICKET-F065: Step 1 — Handle ResourceNotFoundException → HTTP 404
    // -------------------------------------------------------
    // WHAT: When a service throws ResourceNotFoundException (e.g., transaction not found),
    //       this handler catches it and returns HTTP 404 with a JSON body.
    //
    // HOW:  Create a public method annotated with @ExceptionHandler(ResourceNotFoundException.class).
    //       The method receives the exception as a parameter.
    //       Build a Map with keys: "timestamp", "status" (404), "error" ("Not Found"), "message" (from exception).
    //       Return a ResponseEntity with status NOT_FOUND and the Map as the body.
    //
    // WHY:  HTTP 404 clearly tells the frontend: "This resource doesn't exist."
    //       The React app can then show a "Not found" message instead of a generic error.
    //
    // OBSERVE: After implementing, build a controller endpoint that throws ResourceNotFoundException.
    //          Call it with Postman — you should see a clean JSON response with status 404,
    //          not an HTML error page.

    // -------------------------------------------------------
    // TODO TICKET-F065: Step 2 — Handle InvalidTransactionException → HTTP 400
    // -------------------------------------------------------
    // WHAT: When validation fails (amount <= 0, date in future), this handler
    //       catches InvalidTransactionException and returns HTTP 400 "Bad Request".
    //
    // HOW:  Same pattern as Step 1, but use @ExceptionHandler(InvalidTransactionException.class).
    //       Return ResponseEntity with status BAD_REQUEST (400).
    //       Include the exception's message in the response body.
    //
    // WHY:  HTTP 400 tells the frontend: "Your input is invalid."
    //       The frontend can show the specific validation message to the user.
    //
    // OBSERVE: POST a transaction with amount = -10 via Postman.
    //          Response should be 400 with message "Amount must be greater than zero."

    // -------------------------------------------------------
    // TODO TICKET-F065: Step 3 — Handle MethodArgumentNotValidException → HTTP 400 + field errors
    // -------------------------------------------------------
    // WHAT: This exception is thrown when @Valid fails on a @RequestBody.
    //       For example, if the User entity has @NotBlank on "name" and the request
    //       sends name = "", Spring throws MethodArgumentNotValidException.
    //       Unlike the other handlers, this one includes FIELD-LEVEL errors.
    //
    // HOW:  Create a handler for MethodArgumentNotValidException.
    //       Call ex.getBindingResult().getFieldErrors() to get all field errors.
    //       Build a Map where each key is the field name and value is the error message.
    //       Return it in the response body with status 400.
    //
    // WHY:  The React form needs to know WHICH field is invalid to highlight it in red.
    //       A generic "validation failed" message isn't helpful — the user needs to know
    //       "email: Valid email required" and "name: Name is required" specifically.
    //
    // OBSERVE: POST to /api/users with empty name and invalid email via Postman.
    //          Response should be 400 with fieldErrors like:
    //          { "name": "Name is required", "email": "Valid email required" }

    // -------------------------------------------------------
    // TODO TICKET-F065: Step 4 — Create a private helper method
    // -------------------------------------------------------
    // WHAT: Steps 1 and 2 build the same Map structure — just with different status codes.
    //       A private helper method avoids repeating that logic.
    //
    // HOW:  Create a private method that accepts HttpStatus and a message String.
    //       Inside, build the Map (timestamp, status, error, message) and return a ResponseEntity.
    //       Call this helper from both Step 1 and Step 2 handlers.
    //
    // WHY:  This is the DRY principle applied to your exception handler.
    //       If you later change the error format (e.g., add a "path" field),
    //       you change it in one place, not two.
}
