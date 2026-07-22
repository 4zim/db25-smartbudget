package com.smartbudget.exception;

// ============================================================
// ResourceNotFoundException (Day 6, Sprint 5)
// ============================================================
//
// WHAT: Thrown when a database record cannot be found by its ID.
//       For example: user requests transaction #999, but it doesn't exist.
//       This exception is caught by GlobalExceptionHandler and converted
//       to an HTTP 404 "Not Found" response.
//
// WHY:  Without this, the app would return HTTP 500 "Internal Server Error"
//       when a record is missing — which is misleading. 404 clearly tells
//       the frontend: "The resource you asked for doesn't exist."
//
//       Compare:
//         InvalidTransactionException → HTTP 400 (Bad Request — your input is wrong)
//         ResourceNotFoundException   → HTTP 404 (Not Found — the item doesn't exist)
//
// ============================================================
public class ResourceNotFoundException extends RuntimeException {

    // -------------------------------------------------------
    // TODO: Add a constructor
    // -------------------------------------------------------
    // WHAT: Same pattern as InvalidTransactionException — accept a String message, call super(message).
    //
    // HOW:  One public constructor, one parameter (String message), one line: super(message).
    //
    // WHY:  Used in services like this:
    //         repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id))
    //       The message helps developers know WHICH record was missing and WHICH ID was requested.
    //
    // OBSERVE: You won't use this until Day 6 when you build the service layer.
    //          When you call GET /api/transactions/999 (non-existent), the response should be:
    //          HTTP 404 with body: { "status": 404, "error": "Not Found", "message": "Transaction not found: 999" }
}
