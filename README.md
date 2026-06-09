# Keshav General Store Billing

Offline-first Android billing app for Keshav General Store.

## MVP Features

- Android native Kotlin app.
- Camera barcode scanning.
- Manual barcode fallback.
- Local product database design with Indian kirana starter products.
- Unknown barcode add-product flow.
- Simple non-GST bill.
- Bill history.
- PDF receipt writer.
- WhatsApp/share intent support.
- Bluetooth ESC/POS thermal printer client.

## Build APK

The repository includes a GitHub Actions workflow at `.github/workflows/build-apk.yml`.

After pushing to GitHub:

1. Open the repository on GitHub.
2. Go to **Actions**.
3. Open **Build Android APK**.
4. Run the workflow or wait for the push build.
5. Download the `keshav-billing-debug-apk` artifact.
6. Install `app-debug.apk` on an Android phone after allowing installs from unknown sources.

This debug APK is for testing only.
