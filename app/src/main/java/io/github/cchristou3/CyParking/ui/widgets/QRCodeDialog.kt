package io.github.cchristou3.CyParking.ui.widgets

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.github.cchristou3.CyParking.R

/**
 * Purpose: Initializes a custom [AlertDialog.Builder] to display
 * the given message in its QR Code form.
 *
 * @author Charalambos Christou
 * @since 25/02/21
 */
class QRCodeDialog(context: Context, parent: ConstraintLayout, message: String) {

    private var dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)

    companion object {
        // How much space the dialog should occupy from its parent view
        private const val PARENT_TO_DIALOG_RATIO = 0.9f
    }

    /**
     * Here we initialize and set up the builder to ensure that its dialog will
     * host our custom view.
     */
    init {
        // inflate our custom view
        val qrCodeView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_qr_code, null)
        val imageView = qrCodeView.findViewById<ImageView>(R.id.dialog_qr_code_iv)
        // Generate QR Code
        val barcodeEncoder = BarcodeEncoder()
        var bitmap: Bitmap? = null
        try {
            bitmap = barcodeEncoder.encodeBitmap(message, BarcodeFormat.QR_CODE,
                    // Passing only the width to create a perfect square
                    (parent.measuredWidth * PARENT_TO_DIALOG_RATIO).toInt(),
                    (parent.measuredWidth * PARENT_TO_DIALOG_RATIO).toInt()
            )
        } catch (e: WriterException) {
            // TODO: 25/02/2021 Display some kind of error bitmap
        }
        // Display it on the image view
        imageView.setImageBitmap(bitmap)
        // Set the dialog builder's view to our custom view
        dialogBuilder.setView(qrCodeView)
    }

    /**
     * Displays the builder's dialog that was
     * previously initialized in [init].
     */
    fun show() {
        // Show the dialog
        dialogBuilder.show()
    }

}