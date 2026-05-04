# Foreign Key Constraint Violation Fix

## Problem
When attempting to delete a user, a `DataIntegrityViolationException` was thrown with the following error:
```
Cannot delete or update a parent row: a foreign key constraint fails 
(`mvc_proje_db`.`tickets`, CONSTRAINT `FK82g8fdsirhylasg83h4ivwvs2` 
FOREIGN KEY (`RequesterId`) REFERENCES `users` (`user_id`))
```

## Root Cause
The database has a foreign key constraint linking the `tickets` table to the `users` table through two columns:
- `RequesterId` - references the user who created the ticket
- `AssignedTechnicianId` - references the technician assigned to the ticket

When attempting to delete a user, the database prevented deletion because there were existing tickets referencing that user. The application layer was not handling this cascading deletion.

## Solution
Implemented a cascading deletion strategy at the application layer with admin confirmation warnings:

### Changes Made

#### 1. **TicketDAO.java** - Added new methods
Added `countTicketsByUserId(Long userId)` and `deleteTicketsByUserId(Long userId)` methods:
- `countTicketsByUserId`: Counts all tickets where the user is the requester or assigned technician
- `deleteTicketsByUserId`: Deletes all tickets where the user is the requester or assigned technician
- Uses efficient HQL queries for performance

#### 2. **TicketService.java** - Added service method
Added `countTicketsByUserId(Long userId)` method to expose ticket counting functionality.

#### 3. **UserService.java** - Updated deleteUser method
Updated `deleteUser(long id)` to:
- Inject the new `TicketDAO` dependency
- First delete all related tickets using the new method
- Then delete the user
- All operations are within a `@Transactional` block for atomicity
- Added logging for audit trail

#### 4. **AdminController.java** - Added confirmation system
Updated `deleteUser(long id, boolean confirm)` endpoint to:
- Check ticket count before deletion
- If tickets exist and `confirm=false`, return confirmation required message
- If `confirm=true` or no tickets exist, proceed with deletion
- Return appropriate Turkish language messages
- Provide `confirmUrl` for easy confirmation

## How It Works

### Two-Step Deletion Process

1. **First Request**: `DELETE /admin/users/{id}`
   - System checks for related tickets
   - If tickets exist, returns confirmation prompt
   - If no tickets, proceeds with deletion

2. **Confirmation Request**: `DELETE /admin/users/{id}?confirm=true`
   - Bypasses confirmation check
   - Deletes all related tickets and user
   - Returns success with deletion summary

## API Response Examples

### User with tickets - First attempt:
```json
{
  "confirmation_required": true,
  "message": "Bu kullanıcıya ait 5 adet ticket bulunmaktadır. Silmek istediğinizden emin misiniz?",
  "ticketCount": 5,
  "userId": 123,
  "confirmUrl": "/admin/users/123?confirm=true"
}
```

### User with tickets - After confirmation:
```json
{
  "message": "User deleted successfully.",
  "deletedTickets": 5,
  "warning": "Bu kullanıcıya ait 5 adet ticket silindi."
}
```

### User without tickets:
```json
{
  "message": "User deleted successfully.",
  "deletedTickets": 0
}
```

## Frontend Integration

The API is designed for easy frontend integration:

```javascript
// First attempt to delete
fetch('/admin/users/123', { method: 'DELETE' })
  .then(response => response.json())
  .then(data => {
    if (data.confirmation_required) {
      // Show confirmation dialog
      if (confirm(data.message)) {
        // User confirmed, make second request
        return fetch(data.confirmUrl, { method: 'DELETE' });
      }
    } else {
      // No confirmation needed, show success
      console.log(data.message);
    }
  });
```

## Benefits

- ✅ Resolves the foreign key constraint violation
- ✅ Maintains data integrity by handling cascading deletes
- ✅ All operations are atomic (transactional)
- ✅ Provides audit trail through logging
- ✅ **NEW**: Admin confirmation prevents accidental deletions
- ✅ Turkish language support for better UX
- ✅ Efficient single-query deletion strategy
- ✅ Easy frontend integration with confirmation dialogs

## Testing

To test the confirmation system:

1. Create a user with tickets
2. Call `DELETE /admin/users/{id}` - should return confirmation_required
3. Call `DELETE /admin/users/{id}?confirm=true` - should delete user and tickets
4. Test with user having no tickets - should delete immediately

## Alternative Approaches Considered

1. **Database Cascade Delete**: Could configure the database with `ON DELETE CASCADE` or `ON DELETE SET NULL`
   - Rejected because business logic should be in application layer
   - Harder to audit when cascades happen at DB level

2. **Set to NULL instead of delete**: Modify foreign keys to nullable
   - Rejected because foreign keys are defined as NOT NULL (nullable=false)
   - Would leave orphaned tickets without requesters

3. **Archive instead of delete**: Move deleted data to archive tables
   - Could be implemented separately if needed for compliance

4. **Separate endpoints**: Use different endpoints for check vs delete
   - Rejected in favor of query parameter approach for simplicity

## Related Code

- `AdminController.deleteUser()` - Main endpoint with confirmation logic
- `UserService.deleteUser()` - Performs cascading deletion
- `TicketService.countTicketsByUserId()` - Counts related tickets
- `TicketDAO.deleteTicketsByUserId()` - Deletes related tickets
- `TicketDAO.countTicketsByUserId()` - Counts related tickets