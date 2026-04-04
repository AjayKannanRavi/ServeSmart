# Quick Reference: Hotel Registration Fix

## Problem
```
❌ Error: Duplicate entry 'manager' for key 'UKn5ib031h2ipdsj507srabt3kf'
❌ Error: Duplicate entry 'kitchen' for key 'UKn5ib031h2ipdsj507srabt3kf'
```

When registering Hotel 2 after Hotel 1, registration failed because:
- Both hotels tried to create staff with username "manager" and "kitchen"
- Master staff table had global unique constraint on username column
- Multiple restaurants couldn't share same staff username

---

## Solution

### 1️⃣ Auto-Generate Unique Usernames
```java
// MANAGER: Always unique pattern per restaurant
String managerUsername = "mgr_" + saved.getId() + "_admin";  // e.g., mgr_5_admin

// KITCHEN: Always unique pattern per restaurant  
String kitchenUsername = "kch_" + saved.getId() + "_chef";   // e.g., kch_5_chef
```

### 2️⃣ Return Credentials to Frontend
```java
// Build response with generated credentials
return new HotelRegistrationResponse(
    saved.getId(),
    saved.getName(),
    credentials  // Contains all usernames & passwords
);
```

### 3️⃣ Display Credentials to Owner
Success page shows:
- Owner username (from email)
- Manager username (auto-generated)
- Kitchen username (auto-generated)
- All passwords

### 4️⃣ Drop Global Constraint
```sql
ALTER TABLE staff DROP INDEX UKn5ib031h2ipdsj507srabt3kf;
-- Keep composite constraint: (username, restaurant_id)
```

---

## Result

✅ Hotel 1: `mgr_1_admin`, `kch_1_chef`  
✅ Hotel 2: `mgr_2_admin`, `kch_2_chef`  
✅ Hotel 3: `mgr_3_admin`, `kch_3_chef`  

**No collisions!** Each restaurant has unique staff usernames.

---

## Testing

### Test Registration Flow
```bash
# 1. Start backend
java -jar backend/target/backend-0.0.1-SNAPSHOT.jar

# 2. Register Hotel 1 via frontend - SUCCESS
# Response includes: mgr_N_admin, kch_N_chef credentials

# 3. Register Hotel 2 via frontend - SUCCESS (no error!)
# Response includes: mgr_M_admin, kch_M_chef credentials (different N & M)

# 4. Log in with each hotel's manager account - SUCCESS
```

---

## Files Changed

| File | What Changed |
|------|--------------|
| `SaasService.java` | Username generation logic |
| `SaasController.java` | Return type (Restaurant → HotelRegistrationResponse) |
| `HotelRegistrationResponse.java` | NEW DTO |
| `HotelRegistration.jsx` | Display generated credentials |

---

## API Response Example

### Request
```json
POST /api/saas/hotels
{
  "restaurant": { "name": "Restaurant XYZ", ... },
  "adminPassword": "secret",
  "kitchenPassword": "secret"
}
```

### Response ✅
```json
{
  "hotelId": 42,
  "hotelName": "Restaurant XYZ",
  "generatedCredentials": {
    "hotelId": "42",
    "ownerUsername": "owner@email.com",
    "ownerPassword": "secret",
    "managerUsername": "mgr_42_admin",
    "managerPassword": "secret",
    "kitchenUsername": "kch_42_chef",
    "kitchenPassword": "secret"
  }
}
```

---

## Key Insight

The composite key `(username, restaurant_id)` already allows different restaurants to use the same username. We just needed to:
1. **Stop using static usernames** like "manager"
2. **Generate unique ones** that incorporate restaurant ID
3. **Drop the global constraint** that was preventing this

---

## Status: DEPLOYED ✅

Backend: Running (Java 24, Spring Boot 3.4.2)  
Database: Master constraint dropped  
Frontend: Updated to display credentials  
Build: Jar created and tested  

**Ready for production registration testing** 🚀
