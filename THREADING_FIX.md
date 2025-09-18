# Threading Issue Fix

## Problem
The app was crashing with the error:
```
java.lang.IllegalStateException: Method addObserver must be called on the main thread
```

This occurred because `BiometricPrompt.authenticate()` was being called from a background coroutine (IO dispatcher) instead of the main thread.

## Root Cause
- `BiometricPrompt` requires UI operations to be performed on the main thread
- The `authenticate()` method internally creates a `BiometricFragment` which needs to be attached to the activity's lifecycle
- Fragment lifecycle operations must happen on the main thread

## Solution Applied

### Before (Problematic):
```kotlin
CoroutineScope(Dispatchers.IO).launch {
    // ... credential check ...
    biometricPrompt.authenticate(promptInfo) // ❌ Called on background thread
}
```

### After (Fixed):
```kotlin
CoroutineScope(Dispatchers.IO).launch {
    // ... credential check ...
    CoroutineScope(Dispatchers.Main).launch {
        biometricPrompt.authenticate(promptInfo) // ✅ Called on main thread
    }
}
```

## Changes Made

### 1. authenticateWithBiometric()
- Added `CoroutineScope(Dispatchers.Main).launch` wrapper
- Ensures BiometricPrompt operations happen on main thread
- Maintains credential checking on IO thread for performance

### 2. createPasskey()
- Wrapped entire BiometricPrompt creation and authentication in main thread coroutine
- Ensures all UI-related operations happen on main thread

## Technical Details

### Why This Happens:
- `BiometricPrompt` internally uses `FragmentManager` to add a `BiometricFragment`
- Fragment lifecycle operations are restricted to main thread for thread safety
- Android's lifecycle system enforces this restriction

### Why The Fix Works:
- `Dispatchers.Main` ensures the coroutine runs on the main thread
- All UI operations (including Fragment operations) happen on the correct thread
- Background work (credential storage) still happens on IO thread for performance

## Best Practices

### For BiometricPrompt:
1. **Always call on main thread** - Use `Dispatchers.Main` for BiometricPrompt operations
2. **Keep credential checks on IO** - Database operations can stay on background thread
3. **Use proper coroutine scopes** - Don't mix UI and background operations

### For Fragment Operations:
1. **Main thread only** - Any Fragment lifecycle operations must be on main thread
2. **Use withContext()** - For switching between dispatchers in suspend functions
3. **Test on different devices** - Threading issues can be device-specific

## Testing
The fix has been tested and verified:
- ✅ Build compiles successfully
- ✅ No more threading exceptions
- ✅ Biometric authentication works properly
- ✅ Passkey setup dialog functions correctly

The app now properly handles threading for all biometric operations!
