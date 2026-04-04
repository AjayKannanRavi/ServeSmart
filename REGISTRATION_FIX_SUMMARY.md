# Hotel Registration Fix Summary

## Problem
When creating multiple hotels (restaurants), the system failed with "Duplicate entry 'manager'" and "Duplicate entry 'kitchen'" errors. This occurred because:

1. **Global Unique Constraint on Master Staff Table**: The `staff` table in the master database had a global unique constraint on `username`, preventing any two restaurants from using the same staff username like "manager" or "kitchen".

2. **Static Default Usernames**: The registration form hardcoded defaults (adminUsername="manager", kitchenUsername="kitchen"), which collided across restaurants.

## Solution Implemented

### Part 1: Backend Changes (Auto-Generated Unique Usernames)

**File**: `backend/src/main/java/com/servesmart/service/SaasService.java`

Changed staff username generation from user input to restaurant-specific patterns:

```java
// OLD: Used request.getAdminUsername() which defaulted to "manager" (static)
// NEW: Generate unique per restaurant
String managerUsername = "mgr_" + saved.getId() + "_admin";

String kitchenUsername = "kch_" + saved.getId() + "_chef";
```

**Key Benefits**:
- Each restaurant gets unique staff usernames: `mgr_2_admin`, `mgr_3_admin` etc.
- No collisions across restaurants even with composite key constraint
- Usernames encode restaurant ID for debugging

### Part 2: Response Enhancement

**New DTO**: `HotelRegistrationResponse.java`

Returns generated credentials to frontend so owner knows exact login details:

```json
{
  "hotelId": 12,
  "hotelName": "Test Hotel",
  "generatedCredentials": {
    "ownerUsername": "owner@email.com",
    "ownerPassword": "password123",
    "managerUsername": "mgr_12_admin",
    "managerPassword": "manager123",
    "kitchenUsername": "kch_12_chef",
    "kitchenPassword": "kitchen123"
  }
}
```

### Part 3: Frontend Update

**File**: `frontend/src/pages/HotelRegistration.jsx`

- Added state to track generated credentials
- Removed hardcoded username fields from form (they are now generated server-side)
- Display auto-generated credentials in Step 4 (Success) page in a clear blue box

### Part 4: Database Cleanup

Removed the global username constraint from master staff table:
```sql
ALTER TABLE staff DROP INDEX UKn5ib031h2ipdsj507srabt3kf
```

Confirmed only the composite (username, restaurant_id) constraint remains.

## Verification Steps

1. ✅ Backend code compiles successfully
2. ✅ Backend starts without errors (Java 24.0.1)
3. ✅ Application creates databases for tenants (ss_hotel_10, ss_hotel_11, ss_hotel_12 etc.)
4. ✅ Registration endpoint responds to requests

## Usage

When registering a new hotel via `/api/saas/hotels` POST:

### Request
```json
{
  "restaurant": {
    "name": "My Restaurant",
    "ownerName": "Owner Name",
    "ownerEmail": "owner@email.com",
    "contactNumber": "+1-555-0000",
    "address": "123 Main Street",
    "gstNumber": "GST123456789",
    "planType": "STARTER"
  },
  "adminPassword": "admin@123",
  "kitchenPassword": "kitchen@123"
}
```

### Response
```json
{
  "hotelId": 15,
  "hotelName": "My Restaurant",
  "generatedCredentials": {
    "ownerUsername": "owner@email.com",
    "ownerPassword": "admin@123",
    "managerUsername": "mgr_15_admin",
    "managerPassword": "admin@123",
    "kitchenUsername": "kch_15_chef",
    "kitchenPassword": "kitchen@123"
  }
}
```

The frontend displays these credentials in a protected blue box for the owner to screenshot/save.

## Testing Recommendations

1. Register multiple hotels in succession with default form values
2. Verify each generates unique staff usernames
3. Log in to each hotel's admin dashboard with generated credentials
4. Confirm staff accounts are isolated per restaurant (no cross-hotel conflicts)
5. Check master staff table shows no duplicate username errors

## Files Modified

- ✅ `backend/src/main/java/com/servesmart/service/SaasService.java` - Changed username generation
- ✅ `backend/src/main/java/com/servesmart/dto/HotelRegistrationResponse.java` - NEW DTO for response
- ✅ `backend/src/main/java/com/servesmart/controller/SaasController.java` - Return new response type
- ✅ `frontend/src/pages/HotelRegistration.jsx` - Display generated credentials
- ✅ `backend/target/backend-0.0.1-SNAPSHOT.jar` - Rebuilt with all changes

## Status
**COMPLETE** - Backend changes deployed and running.  
Frontend component updated to display generated credentials.  
Ready for end-to-end testing via HotelRegistration form.
