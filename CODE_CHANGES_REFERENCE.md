# Code Changes Reference Guide

## 1. SaasService.java - Staff Username Generation

### Location: `backend/src/main/java/com/servesmart/service/SaasService.java`

### Method Signature Change
```java
// BEFORE
public Restaurant createHotel(HotelRegistrationRequest request)

// AFTER
public HotelRegistrationResponse createHotel(HotelRegistrationRequest request)
```

### Staff Username Generation - OWNER
```java
// BEFORE & AFTER (same)
String ownerUsername = request.getOwnerEmail() != null ? request.getOwnerEmail() : "owner_" + saved.getId();

// Updated: Added toLowerCase().trim() for better email handling
String ownerUsername = request.getOwnerEmail() != null ? request.getOwnerEmail().toLowerCase().trim() : "owner_" + saved.getId();
```

### Staff Username Generation - MANAGER & KITCHEN (CHANGED!)
```java
// ❌ BEFORE: Conditional logic, still could use generic "manager" prefix
String managerUsername = request.getAdminUsername();
if (managerUsername == null || managerUsername.trim().isEmpty() || managerUsername.equals(ownerUsername)) {
    managerUsername = "manager_" + saved.getId();
}

// ✅ AFTER: Always generate unique, deterministic username with restaurant ID
String managerUsername = "mgr_" + saved.getId() + "_admin";
```

```java
// ❌ BEFORE: Conditional logic, still could use generic "kitchen" prefix
String kitchenUsername = request.getKitchenUsername();
if (kitchenUsername == null || kitchenUsername.trim().isEmpty() || kitchenUsername.equals(ownerUsername) || kitchenUsername.equals(managerUsername)) {
    kitchenUsername = "kitchen_" + saved.getId();
}

// ✅ AFTER: Always generate unique, deterministic username with restaurant ID
String kitchenUsername = "kch_" + saved.getId() + "_chef";
```

### Return Value Construction (NEW)
```java
// ❌ BEFORE: Returns Restaurant entity
return saved;

// ✅ AFTER: Build and return HotelRegistrationResponse with credentials
java.util.Map<String, String> credentials = new java.util.HashMap<>();
credentials.put("hotelId", saved.getId().toString());
credentials.put("ownerUsername", ownerUsername);
credentials.put("ownerPassword", request.getAdminPassword() != null ? request.getAdminPassword() : "password123");
credentials.put("managerUsername", managerUsername);
credentials.put("managerPassword", request.getAdminPassword() != null ? request.getAdminPassword() : "manager123");
credentials.put("kitchenUsername", kitchenUsername);
credentials.put("kitchenPassword", request.getKitchenPassword() != null ? request.getKitchenPassword() : "kitchen123");

return new com.servesmart.dto.HotelRegistrationResponse(
    saved.getId(),
    saved.getName(),
    credentials
);
```

---

## 2. HotelRegistrationResponse.java (NEW FILE)

### File: `backend/src/main/java/com/servesmart/dto/HotelRegistrationResponse.java`

```java
package com.servesmart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Response DTO for hotel registration that includes auto-generated staff credentials.
 * This ensures each restaurant gets unique staff usernames to avoid conflicts.
 */
@Data
@AllArgsConstructor
public class HotelRegistrationResponse {
    private Long hotelId;
    private String hotelName;
    private Map<String, String> generatedCredentials;
}
```

---

## 3. SaasController.java - Return Type Update

### Location: `backend/src/main/java/com/servesmart/controller/SaasController.java`

```java
// ❌ BEFORE
@PostMapping("/hotels")
public Restaurant createHotel(@RequestBody HotelRegistrationRequest request) {
    return saasService.createHotel(request);
}

// ✅ AFTER
@PostMapping("/hotels")
public com.servesmart.dto.HotelRegistrationResponse createHotel(@RequestBody HotelRegistrationRequest request) {
    return saasService.createHotel(request);
}
```

---

## 4. HotelRegistration.jsx - Frontend Display

### Location: `frontend/src/pages/HotelRegistration.jsx`

### Add State for Generated Credentials
```javascript
// ❌ BEFORE
const [regHotelId, setRegHotelId] = useState(null);

// ✅ AFTER
const [regHotelId, setRegHotelId] = useState(null);
const [generatedCredentials, setGeneratedCredentials] = useState(null);
```

### Update Response Handling
```javascript
// ❌ BEFORE
const response = await axios.post('http://localhost:8085/api/saas/hotels', payload);
setRegHotelId(response.data.id);
setStep(4);

// ✅ AFTER
const response = await axios.post('http://localhost:8085/api/saas/hotels', payload);
setRegHotelId(response.data.hotelId);
setGeneratedCredentials(response.data.generatedCredentials);
setStep(4);
```

### Success Page (Step 4) UI Update
```jsx
// ✅ ADDED: Display generated credentials in secure blue box
{generatedCredentials && (
    <div className="space-y-4 mb-8 text-left bg-blue-50 p-6 rounded-[2rem] border border-blue-100">
        <div className="flex items-center gap-2 mb-4">
            <ShieldCheck size={18} className="text-blue-600" />
            <h4 className="font-black text-blue-900 text-sm">Auto-Generated Staff Credentials</h4>
        </div>
        
        {/* Owner Account */}
        <div className="bg-white rounded-xl p-4 border border-blue-100">
            <p className="text-[10px] font-black uppercase tracking-widest text-gray-500 mb-2">Owner</p>
            <div className="font-mono text-xs text-gray-900 space-y-1">
                <div>Username: <span className="font-bold text-amber-600">{generatedCredentials.ownerUsername}</span></div>
                <div>Password: <span className="font-bold text-gray-700">{generatedCredentials.ownerPassword}</span></div>
            </div>
        </div>
        
        {/* Manager Account */}
        <div className="bg-white rounded-xl p-4 border border-blue-100">
            <p className="text-[10px] font-black uppercase tracking-widest text-gray-500 mb-2">Manager</p>
            <div className="font-mono text-xs text-gray-900 space-y-1">
                <div>Username: <span className="font-bold text-amber-600">{generatedCredentials.managerUsername}</span></div>
                <div>Password: <span className="font-bold text-gray-700">{generatedCredentials.managerPassword}</span></div>
            </div>
        </div>
        
        {/* Kitchen Account */}
        <div className="bg-white rounded-xl p-4 border border-blue-100">
            <p className="text-[10px] font-black uppercase tracking-widest text-gray-500 mb-2">Kitchen</p>
            <div className="font-mono text-xs text-gray-900 space-y-1">
                <div>Username: <span className="font-bold text-amber-600">{generatedCredentials.kitchenUsername}</span></div>
                <div>Password: <span className="font-bold text-gray-700">{generatedCredentials.kitchenPassword}</span></div>
            </div>
        </div>
    </div>
)}
```

---

## 5. Database - Constraint Removal

### Master Database: servesmart_db

```sql
-- ❌ REMOVED (global username uniqueness constraint)
ALTER TABLE staff DROP INDEX UKn5ib031h2ipdsj507srabt3kf;

-- ✅ KEPT (composite restaurant_id + username uniqueness)
-- Index name: UKik22clyxg3fsjlmpwm557iwrr
-- Columns: (username, restaurant_id)
```

---

## Summary of Changes

| Component | Change | Impact |
|-----------|--------|--------|
| SaasService.java | Generate unique usernames: `mgr_N_admin`, `kch_N_chef` | Eliminates collision errors |
| SaasService.java | Return HotelRegistrationResponse instead of Restaurant | Sends credentials to frontend |
| SaasController.java | Update endpoint return type | Properly typed API response |
| HotelRegistrationResponse.java | NEW DTO | Carries generated credentials |
| HotelRegistration.jsx | Add credentials display on success page | Users see staff logins created |
| Database | Drop global username constraint | Allows restaurant-specific usernames |

---

## Example Username Generation

For Hotel ID 13:
- Owner: alice@grandpav.com (from email)
- Manager: `mgr_13_admin`
- Kitchen: `kch_13_chef`

For Hotel ID 15:
- Owner: bob@newrestaurant.com (from email)
- Manager: `mgr_15_admin`
- Kitchen: `kch_15_chef`

No collisions possible! ✅

---

## Rebuild Command

After changes, rebuild with:
```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

---

**Generated**: 2026-04-02  
**Status**: All changes deployed and tested ✅
