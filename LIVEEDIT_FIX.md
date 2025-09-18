# LiveEdit Compatibility Fix

## Issue
The app was experiencing crashes when using biometric authentication with Android Studio's LiveEdit feature enabled. The error was:
```
java.lang.IllegalArgumentException: Unhandled superclass: androidx/biometric/BiometricPrompt$AuthenticationCallback
```

## Solution
The issue has been resolved by:

1. **Extracting BiometricPrompt callbacks** into separate private methods
2. **Avoiding anonymous inner classes** for BiometricPrompt.AuthenticationCallback
3. **Using factory methods** to create callback instances

## If Issues Persist

If you still experience LiveEdit-related crashes with biometric authentication, you can:

### Option 1: Disable LiveEdit
1. Go to **File > Settings** (or **Android Studio > Preferences** on Mac)
2. Navigate to **Build, Execution, Deployment > LiveEdit**
3. Uncheck **Enable LiveEdit**
4. Restart Android Studio

### Option 2: Disable LiveEdit for Specific Files
Add this annotation to the top of PasskeyService.kt:
```kotlin
@file:Suppress("LiveEdit")
```

### Option 3: Use Release Build
Run the app using a release build instead of debug:
```bash
./gradlew assembleRelease
```

## Technical Details

The issue occurs because LiveEdit has trouble with:
- Anonymous inner classes extending Android framework classes
- BiometricPrompt.AuthenticationCallback implementations
- Complex inheritance hierarchies in coroutine contexts

The fix separates the callback creation from the coroutine context, making it more compatible with LiveEdit's bytecode manipulation.
