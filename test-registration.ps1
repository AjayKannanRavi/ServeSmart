param(
    [string]$HotelName = "Test Hotel 3",
    [string]$OwnerEmail = "john@testhotel3.com"
)

$payload = @{
    restaurant = @{
        name = $HotelName
        ownerName = "John Tester"
        ownerEmail = $OwnerEmail
        contactNumber = "555-0003"
        address = "123 Test Street"
        gstNumber = "GST123456789"
        planType = "STARTER"
    }
    adminPassword = "admin@123"
    kitchenPassword = "kitchen@123"
} | ConvertTo-Json -Depth 10

Write-Host "Sending registration request..."
Write-Host "Payload: $payload`n`n"

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8085/api/saas/hotels" `
        -Method POST `
        -Headers @{"Content-Type"="application/json"} `
        -Body $payload `
        -ErrorAction Stop

    Write-Host "✅ Registration successful! Status: $($response.StatusCode)"
    Write-Host "`nResponse:"
    $responseJson = $response.Content | ConvertFrom-Json
    $responseJson | ConvertTo-Json -Depth 10
} catch {
    Write-Host "❌ Registration failed!"
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
        Write-Host "Response Body: $($_.Exception.Response | Get-Member)"
    }
}
