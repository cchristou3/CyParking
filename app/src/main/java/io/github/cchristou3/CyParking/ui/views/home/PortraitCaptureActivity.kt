package io.github.cchristou3.CyParking.ui.views.home

import com.journeyapps.barcodescanner.CaptureActivity

/**
 * Purpose: used when scanning QRCode via [IntentIntegrator.initiateScan].
 * Ensures that the scanner is in portrait mode.
 *
 * @author Charalambos Christou
 * @version 1.0 24/02/21
 */
internal class PortraitCaptureActivity : CaptureActivity()