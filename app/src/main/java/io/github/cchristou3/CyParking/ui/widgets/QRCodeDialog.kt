package io.github.cchristou3.CyParking.ui.widgets

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.github.cchristou3.CyParking.databinding.DialogQrCodeBinding

/**
 * Purpose: Initializes a custom [AlertDialog.Builder] to display
 * the given message in its QR Code form.
 *
 * @author Charalambos Christou
 * @since 2.0  29/03/21
 */
class QRCodeDialog(context: Context, parent: ConstraintLayout, message: String) {

    private var mDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)

    private lateinit var mDialog: AlertDialog

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
        val qrCodeViewBinding = DialogQrCodeBinding.inflate(LayoutInflater.from(context), null, false)
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
        qrCodeViewBinding.dialogQrCodeIv.setImageBitmap(bitmap)
        // Set the dialog builder's view to our custom view
        mDialogBuilder.setView(qrCodeViewBinding.root)
    }

    /**
     * Displays the builder's dialog that was
     * previously initialized in [init].
     */
    fun show() {
        // Show the dialog
        mDialog = mDialogBuilder.show()
    }

    /**
     * Hides the previously created dialog.
     */
    fun dismiss() {
        mDialog.dismiss()
    }
}