# Keshav General Store Android Billing Design

Date: 2026-06-09

## Goal

Build an offline-first native Android billing app for Keshav General Store. The app is optimized for kirana/general-store counter billing where the shop owner may not have a dedicated barcode scanner. The phone camera acts as the scanner, and common Bluetooth thermal printers are supported in the first version.

## MVP Scope

The first version includes:

- Native Android app built with Kotlin.
- Offline local product database.
- Camera barcode scanning.
- Indian kirana/general-store starter product data.
- Fast add-product flow for unknown barcodes.
- Simple non-GST bill format.
- Local bill history.
- PDF bill generation.
- WhatsApp sharing of generated bill PDFs.
- Bluetooth thermal printer support for common ESC/POS 58mm and 80mm printers.

The first version does not include:

- GST invoice filing.
- Cloud sync.
- Multi-store management.
- iOS, desktop, or web app builds.
- A complete universal product database.

## Product Database Strategy

The app ships with starter data for common Indian kirana/general-store products. This data is only a starting point. The app must not assume it knows every barcode.

When a barcode is scanned:

- If the barcode exists locally, the product is added to the cart.
- If the barcode is unknown, the app opens Add Product with the scanned barcode pre-filled.
- After the product is saved, future scans use the local record.

This makes the app practical in real stores where product data is incomplete, regional, or changes frequently.

## Core Screens

### Billing Screen

The primary counter screen. It includes product search, scan button, cart items, quantity controls, totals, payment mode, and actions to save, print, generate PDF, and share on WhatsApp.

### Camera Scan Screen

Uses the phone camera to scan barcodes. Duplicate scan events are debounced so the same product is not added repeatedly while the camera is pointed at the same barcode.

### Add/Edit Product Screen

Fields:

- Barcode
- Product name
- Category
- Selling price
- Purchase price
- Stock quantity
- Unit type
- Low-stock alert quantity

### Products Screen

Allows searching, editing, deleting, and reviewing products. Search supports barcode, product name, and category.

### Bill Preview Screen

Shows the simple receipt before or after saving. Actions include save PDF, share on WhatsApp, and print.

### Printer Settings Screen

Allows selecting a paired Bluetooth printer, choosing 58mm or 80mm paper width, and sending a test print.

### Bill History Screen

Lists saved bills with search and detail view. Old bills can be reprinted or reshared without reducing stock again.

## Architecture

The app uses native Android Kotlin with Jetpack Compose.

Core technology choices:

- UI: Jetpack Compose
- Local database: Room SQLite
- Barcode scanning: CameraX with Google ML Kit Barcode Scanning
- PDF generation: Android PDF APIs
- WhatsApp sharing: Android share intent using generated PDF files
- Bluetooth printing: ESC/POS command generation sent over Bluetooth
- App pattern: MVVM with repositories and focused use-case classes

## Data Model

Main tables:

- `products`
- `bills`
- `bill_items`
- `settings`
- `printer_devices`

`products` stores the barcode, name, category, prices, stock, unit, and low-stock alert threshold.

`bills` stores bill metadata: bill number, date/time, total, payment mode, optional customer details, and receipt status.

`bill_items` stores the products, quantities, prices, and line totals used in each bill. This preserves historical bills even if product prices change later.

`settings` stores local shop settings such as shop name, address, receipt width, and rounding preferences.

`printer_devices` stores remembered Bluetooth printer details and selected paper width.

## Billing Behavior

- Product search must be fast with thousands of products.
- Barcode scan adds a known product to the cart immediately.
- Unknown barcode opens Add Product.
- Quantity can be changed in cart.
- Stock is reduced only when a bill is saved.
- Reprinting or resharing a saved bill does not change stock.
- Bills remain available offline in local history.

## Receipt Behavior

The receipt is a simple non-GST shop bill. It includes:

- Keshav General Store name
- Date/time
- Bill number
- Item name
- Quantity
- Price
- Line total
- Grand total
- Payment mode
- Optional customer name/mobile

PDF and thermal receipts use the same bill data but different rendering formats.

## Printer Behavior

The MVP supports common Bluetooth ESC/POS thermal printers. The app does not copy platform drivers. It sends standard receipt commands directly to selected Bluetooth printers.

Printer settings include:

- Select paired Bluetooth printer
- Paper width: 58mm or 80mm
- Test print
- Reprint from bill history

If no printer is connected, billing still works through PDF and WhatsApp sharing.

## Performance Requirements

- Billing screen should stay responsive during scan and search.
- Product lookup by barcode should be indexed.
- Product search should avoid blocking the main thread.
- PDF and print generation should run outside the UI-critical path.
- Duplicate camera scan events should be debounced.
- Cart state should be lightweight and local to the billing flow until saved.

## Testing Requirements

Initial test coverage should focus on:

- Product barcode lookup.
- Unknown barcode add flow.
- Bill total calculation.
- Stock reduction on save.
- No stock reduction on reprint.
- PDF receipt data correctness.
- ESC/POS receipt command generation.

Manual device testing should cover:

- Camera barcode scan.
- PDF generation.
- WhatsApp share intent.
- Bluetooth printer selection.
- Test print.
- Reprint saved bill.
