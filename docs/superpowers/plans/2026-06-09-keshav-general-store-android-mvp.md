# Keshav General Store Android MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the first native Android MVP for offline Keshav General Store billing with camera barcode scanning, local products, simple bills, PDF/WhatsApp sharing, and Bluetooth ESC/POS printing.

**Architecture:** Create a Kotlin Android app using MVVM, Jetpack Compose, Room, CameraX/ML Kit, and small focused services for receipts and printing. Keep business logic testable in plain Kotlin where possible, with Android integrations behind narrow classes.

**Tech Stack:** Kotlin, Gradle Android plugin, Jetpack Compose, Room SQLite, CameraX, ML Kit Barcode Scanning, Android PdfDocument, Bluetooth APIs, JUnit.

---

## File Structure

- `settings.gradle.kts`: Gradle project settings.
- `build.gradle.kts`: Root Gradle plugin versions.
- `app/build.gradle.kts`: Android app dependencies and build config.
- `app/src/main/AndroidManifest.xml`: Permissions and app declaration.
- `app/src/main/java/com/keshavgeneralstore/billing/MainActivity.kt`: App entry point.
- `app/src/main/java/com/keshavgeneralstore/billing/KeshavBillingApp.kt`: Compose app shell.
- `app/src/main/java/com/keshavgeneralstore/billing/data/Product.kt`: Product model.
- `app/src/main/java/com/keshavgeneralstore/billing/data/Bill.kt`: Bill and bill item models.
- `app/src/main/java/com/keshavgeneralstore/billing/data/AppDatabase.kt`: Room database.
- `app/src/main/java/com/keshavgeneralstore/billing/data/ProductDao.kt`: Product queries.
- `app/src/main/java/com/keshavgeneralstore/billing/data/BillDao.kt`: Bill persistence queries.
- `app/src/main/java/com/keshavgeneralstore/billing/data/SeedProducts.kt`: Starter Indian kirana products.
- `app/src/main/java/com/keshavgeneralstore/billing/domain/CartModels.kt`: Cart state models.
- `app/src/main/java/com/keshavgeneralstore/billing/domain/BillingCalculator.kt`: Totals and bill item conversion.
- `app/src/main/java/com/keshavgeneralstore/billing/domain/StockUpdater.kt`: Stock reduction behavior.
- `app/src/main/java/com/keshavgeneralstore/billing/print/EscPosReceiptBuilder.kt`: ESC/POS receipt commands.
- `app/src/main/java/com/keshavgeneralstore/billing/print/BluetoothPrinterClient.kt`: Bluetooth output.
- `app/src/main/java/com/keshavgeneralstore/billing/receipt/PdfReceiptWriter.kt`: PDF receipt output.
- `app/src/main/java/com/keshavgeneralstore/billing/share/ShareBillIntentFactory.kt`: WhatsApp/share intent.
- `app/src/main/java/com/keshavgeneralstore/billing/ui/BillingViewModel.kt`: Billing screen state and actions.
- `app/src/main/java/com/keshavgeneralstore/billing/ui/screens/BillingScreen.kt`: Main billing UI.
- `app/src/main/java/com/keshavgeneralstore/billing/ui/screens/AddProductScreen.kt`: Product creation/edit UI.
- `app/src/main/java/com/keshavgeneralstore/billing/ui/screens/BillHistoryScreen.kt`: Saved bill UI.
- `app/src/main/java/com/keshavgeneralstore/billing/ui/screens/PrinterSettingsScreen.kt`: Printer selection/test UI.
- `app/src/main/java/com/keshavgeneralstore/billing/ui/screens/ScannerScreen.kt`: Camera scan UI.
- `app/src/test/java/com/keshavgeneralstore/billing/domain/BillingCalculatorTest.kt`: Billing math tests.
- `app/src/test/java/com/keshavgeneralstore/billing/domain/StockUpdaterTest.kt`: Stock update tests.
- `app/src/test/java/com/keshavgeneralstore/billing/print/EscPosReceiptBuilderTest.kt`: Printer command tests.

## Task 1: Scaffold Android Project

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/keshavgeneralstore/billing/MainActivity.kt`

- [ ] **Step 1: Create Gradle settings**

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "KeshavGeneralStoreBilling"
include(":app")
```

- [ ] **Step 2: Create root build file**

```kotlin
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.24" apply false
}
```

- [ ] **Step 3: Create app build file**

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.keshavgeneralstore.billing"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.keshavgeneralstore.billing"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
```

- [ ] **Step 4: Create manifest with required permissions**

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />

    <application
        android:allowBackup="true"
        android:label="Keshav Billing"
        android:theme="@style/Theme.KeshavBilling">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

- [ ] **Step 5: Create minimal Activity**

```kotlin
package com.keshavgeneralstore.billing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeshavBillingApp()
        }
    }
}
```

- [ ] **Step 6: Run build**

Run: `.\gradlew.bat :app:assembleDebug`
Expected: debug APK builds successfully after dependencies download.

## Task 2: Domain Models and Billing Math

**Files:**
- Create: `app/src/main/java/com/keshavgeneralstore/billing/data/Product.kt`
- Create: `app/src/main/java/com/keshavgeneralstore/billing/data/Bill.kt`
- Create: `app/src/main/java/com/keshavgeneralstore/billing/domain/CartModels.kt`
- Create: `app/src/main/java/com/keshavgeneralstore/billing/domain/BillingCalculator.kt`
- Create: `app/src/test/java/com/keshavgeneralstore/billing/domain/BillingCalculatorTest.kt`

- [ ] **Step 1: Write billing calculator tests**

```kotlin
package com.keshavgeneralstore.billing.domain

import com.keshavgeneralstore.billing.data.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class BillingCalculatorTest {
    @Test
    fun calculatesLineTotalsAndGrandTotal() {
        val product = Product(
            id = 1,
            barcode = "890100",
            name = "Parle-G 250g",
            category = "Biscuits",
            sellingPricePaise = 3000,
            purchasePricePaise = 2500,
            stockQuantity = 10.0,
            unit = "pcs",
            lowStockAlertQuantity = 2.0
        )
        val cart = listOf(CartItem(product = product, quantity = 3.0))

        val result = BillingCalculator.calculate(cart)

        assertEquals(9000, result.totalPaise)
        assertEquals(1, result.items.size)
        assertEquals(9000, result.items.first().lineTotalPaise)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat :app:testDebugUnitTest --tests "*BillingCalculatorTest"`
Expected: FAIL because models are not created yet.

- [ ] **Step 3: Implement product and bill models**

Use paise as `Long` for money to avoid floating point price bugs.

- [ ] **Step 4: Implement calculator**

Calculate line totals as `sellingPricePaise * quantity`, rounded to nearest paise.

- [ ] **Step 5: Run test**

Run: `.\gradlew.bat :app:testDebugUnitTest --tests "*BillingCalculatorTest"`
Expected: PASS.

## Task 3: Room Database and Seed Products

**Files:**
- Create: `app/src/main/java/com/keshavgeneralstore/billing/data/ProductDao.kt`
- Create: `app/src/main/java/com/keshavgeneralstore/billing/data/BillDao.kt`
- Create: `app/src/main/java/com/keshavgeneralstore/billing/data/AppDatabase.kt`
- Create: `app/src/main/java/com/keshavgeneralstore/billing/data/SeedProducts.kt`

- [ ] **Step 1: Add Room annotations to data models**

Use `@Entity`, `@PrimaryKey(autoGenerate = true)`, and indexes on `barcode`, `name`, and `category`.

- [ ] **Step 2: Create ProductDao**

Include lookup by barcode, search by query, insert/update, and list.

- [ ] **Step 3: Create BillDao**

Include transaction insert for bill and bill items.

- [ ] **Step 4: Create AppDatabase**

Expose `productDao()` and `billDao()`.

- [ ] **Step 5: Create SeedProducts**

Include common kirana items such as Parle-G, Maggi, Amul milk, Tata salt, Surf Excel, Rin, Good Day, Kurkure, Haldiram namkeen, Colgate, Closeup, Lifebuoy, Dettol soap, Fortune oil, Aashirvaad atta, India Gate rice, Red Label tea, Nescafe, Dairy Milk, and common loose-item entries like sugar, rice, dal, and wheat flour.

## Task 4: Stock Update Rules

**Files:**
- Create: `app/src/main/java/com/keshavgeneralstore/billing/domain/StockUpdater.kt`
- Create: `app/src/test/java/com/keshavgeneralstore/billing/domain/StockUpdaterTest.kt`

- [ ] **Step 1: Write test for saving bill once**

Verify stock is reduced by bill item quantity.

- [ ] **Step 2: Write test for reprint behavior**

Verify stock is not reduced by reprint.

- [ ] **Step 3: Implement StockUpdater**

Expose a function that converts current stock and saved bill items to updated product stock only during bill save.

- [ ] **Step 4: Run tests**

Run: `.\gradlew.bat :app:testDebugUnitTest --tests "*StockUpdaterTest"`
Expected: PASS.

## Task 5: ESC/POS Receipt Builder

**Files:**
- Create: `app/src/main/java/com/keshavgeneralstore/billing/print/EscPosReceiptBuilder.kt`
- Create: `app/src/test/java/com/keshavgeneralstore/billing/print/EscPosReceiptBuilderTest.kt`

- [ ] **Step 1: Write test for receipt text**

Verify receipt bytes contain shop name, item name, quantity, and total.

- [ ] **Step 2: Implement builder**

Generate ESC/POS bytes for 58mm and 80mm formats using text commands, line separators, and cutter command where supported.

- [ ] **Step 3: Run tests**

Run: `.\gradlew.bat :app:testDebugUnitTest --tests "*EscPosReceiptBuilderTest"`
Expected: PASS.

## Task 6: Compose Billing UI

**Files:**
- Create: `app/src/main/java/com/keshavgeneralstore/billing/KeshavBillingApp.kt`
- Create: `app/src/main/java/com/keshavgeneralstore/billing/ui/BillingViewModel.kt`
- Create: `app/src/main/java/com/keshavgeneralstore/billing/ui/screens/BillingScreen.kt`
- Create: `app/src/main/res/values/styles.xml`

- [ ] **Step 1: Create app theme style**

Use a Material theme compatible with Compose.

- [ ] **Step 2: Create KeshavBillingApp**

Show the billing screen as the first screen.

- [ ] **Step 3: Create BillingViewModel seed state**

Load starter products into in-memory state first, then connect Room after UI is stable.

- [ ] **Step 4: Create BillingScreen**

Include search, scan button, product list, cart, quantity controls, total, PDF, WhatsApp, and Print buttons.

- [ ] **Step 5: Run build**

Run: `.\gradlew.bat :app:assembleDebug`
Expected: PASS.

## Task 7: PDF and WhatsApp Sharing

**Files:**
- Create: `app/src/main/java/com/keshavgeneralstore/billing/receipt/PdfReceiptWriter.kt`
- Create: `app/src/main/java/com/keshavgeneralstore/billing/share/ShareBillIntentFactory.kt`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Implement PDF writer**

Use `PdfDocument` to write receipt data to app cache.

- [ ] **Step 2: Add FileProvider**

Configure a cache-path FileProvider for PDF sharing.

- [ ] **Step 3: Implement share intent**

Use `Intent.ACTION_SEND` with MIME type `application/pdf`.

- [ ] **Step 4: Wire buttons**

PDF generates file; WhatsApp/share opens Android chooser.

## Task 8: Barcode Scanner Screen

**Files:**
- Create: `app/src/main/java/com/keshavgeneralstore/billing/ui/screens/ScannerScreen.kt`
- Modify: `app/src/main/java/com/keshavgeneralstore/billing/KeshavBillingApp.kt`
- Modify: `app/src/main/java/com/keshavgeneralstore/billing/ui/BillingViewModel.kt`

- [ ] **Step 1: Add camera permission flow**

Request camera permission before showing preview.

- [ ] **Step 2: Implement CameraX preview**

Bind preview and image analysis to lifecycle.

- [ ] **Step 3: Implement ML Kit barcode analyzer**

Send unique barcode values to the ViewModel.

- [ ] **Step 4: Add debounce**

Ignore the same barcode for two seconds after a successful scan.

- [ ] **Step 5: Wire known/unknown behavior**

Known barcode adds to cart; unknown barcode opens Add Product with barcode prefilled.

## Task 9: Bluetooth Printer Client

**Files:**
- Create: `app/src/main/java/com/keshavgeneralstore/billing/print/BluetoothPrinterClient.kt`
- Create: `app/src/main/java/com/keshavgeneralstore/billing/ui/screens/PrinterSettingsScreen.kt`
- Modify: `app/src/main/java/com/keshavgeneralstore/billing/KeshavBillingApp.kt`

- [ ] **Step 1: List bonded devices**

Use `BluetoothAdapter.bondedDevices` when Bluetooth permissions are granted.

- [ ] **Step 2: Implement socket write**

Connect to selected device using the serial port UUID `00001101-0000-1000-8000-00805F9B34FB`.

- [ ] **Step 3: Add test print**

Use `EscPosReceiptBuilder` to send a short test receipt.

- [ ] **Step 4: Wire bill print**

Print the current or saved receipt from billing and history screens.

## Task 10: Bill History and Final Verification

**Files:**
- Create: `app/src/main/java/com/keshavgeneralstore/billing/ui/screens/BillHistoryScreen.kt`
- Modify: `app/src/main/java/com/keshavgeneralstore/billing/KeshavBillingApp.kt`
- Modify: `app/src/main/java/com/keshavgeneralstore/billing/ui/BillingViewModel.kt`

- [ ] **Step 1: Persist saved bills**

Save bill and bill items to Room.

- [ ] **Step 2: Show bill history**

List bills by newest first.

- [ ] **Step 3: Add reprint and reshare**

Use saved bill data only; do not reduce stock.

- [ ] **Step 4: Run unit tests**

Run: `.\gradlew.bat :app:testDebugUnitTest`
Expected: PASS.

- [ ] **Step 5: Run debug build**

Run: `.\gradlew.bat :app:assembleDebug`
Expected: PASS.

## Self-Review

Spec coverage:

- Android-first native app: covered by Tasks 1 and 6.
- Offline local product database: covered by Task 3.
- Camera barcode scanning: covered by Task 8.
- Indian starter product data: covered by Task 3.
- Unknown product add flow: covered by Tasks 6 and 8.
- Simple non-GST bill: covered by Tasks 2, 5, and 7.
- PDF and WhatsApp: covered by Task 7.
- Bluetooth ESC/POS printing: covered by Tasks 5 and 9.
- Bill history and no stock reduction on reprint: covered by Tasks 4 and 10.

No placeholders remain in the plan. The MVP stays offline and does not include cloud sync, iOS, desktop, GST, or universal product data.
