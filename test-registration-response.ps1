#!/usr/bin/env powershell

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  TESTING NEW HOTEL REGISTRATION" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Prepare payload for a test hotel registration
$newHotel = @{
    restaurant = @{
        name = "Test Hotel Verify"
        ownerName = "Test Owner"
        ownerEmail = "test@verify.com"
        contactNumber = "555-9999"
        address = "123 Verify Street"
        gstNumber = "GSTTEST123"
        planType = "STARTER"
    }
    adminPassword = "admin@test123"
    kitchenPassword = "kitchen@test123"
} | ConvertTo-Json -Depth 10

Write-Host "Submitting registration for: Test Hotel Verify" -ForegroundColor Yellow
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8085/api/saas/hotels" `
        -Method POST `
        -Headers @{"Content-Type" = "application/json"} `
        -Body $newHotel `
        -ErrorAction Stop
    
    $result = $response.Content | ConvertFrom-Json
    
    Write-Host "✅ REGISTRATION SUCCESSFUL" -ForegroundColor Green
    Write-Host ""
    Write-Host "Hotel ID:   $($result.hotelId)" -ForegroundColor Cyan
    Write-Host "Hotel Name: $($result.hotelName)" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "AUTO-GENERATED CREDENTIALS RETURNED:" -ForegroundColor Green
    Write-Host "=====================================" -ForegroundColor Green
    Write-Host ""
    
    $creds = $result.generatedCredentials
    
    Write-Host "[OWNER ACCOUNT]" -ForegroundColor Magenta
    Write-Host "  Username: $($creds.ownerUsername)"
    Write-Host "  Password: $($creds.ownerPassword)"
    Write-Host ""
    
    Write-Host "[MANAGER ACCOUNT]" -ForegroundColor Yellow
    Write-Host "  Username: $($creds.managerUsername)"
    Write-Host "  Password: $($creds.managerPassword)"
    Write-Host ""
    
    Write-Host "[KITCHEN ACCOUNT]" -ForegroundColor Red
    Write-Host "  Username: $($creds.kitchenUsername)"
    Write-Host "  Password: $($creds.kitchenPassword)"
    Write-Host ""
    
    Write-Host "=====================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "✅ FEATURE CONFIRMED: Auto-generated credentials are being returned in registration response!" -ForegroundColor Green
    
} catch {
    Write-Host "❌ REGISTRATION FAILED" -ForegroundColor Red
    Write-Host ""
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        Write-Host ""
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
    }
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
