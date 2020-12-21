package io.github.cchristou3.CyParking.ui.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import static io.github.cchristou3.CyParking.MainHostActivity.TAG;

public class PhoneEditText extends androidx.appcompat.widget.AppCompatEditText implements TextWatcher {

    private int latestEditableLength = 0;

    /**
     * The following three constructors call the corresponding constructor of the Base class.
     * These ones also add a TextWatcher listener to the current EditText object, which is later
     * removed with the invocation of the {@link #finalize()} method.
     *
     * @see androidx.appcompat.widget.AppCompatEditText
     */
    public PhoneEditText(@NonNull Context context) {
        super(context);
        this.addTextChangedListener(this);
    }

    public PhoneEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.addTextChangedListener(this);
    }

    public PhoneEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.addTextChangedListener(this);
    }

//    /**
//     * Return the text that the view is displaying. If an editable text has not been set yet, this
//     * will return null. Any spaces are omitted.
//     */
//    @Nullable
//    @Override
//    public Editable getText() {
//        if (super.getText() == null || super.getText().length() == 0) return super.getText();
//        final String currentText = super.getText().toString();
//        return new SpannableStringBuilder(currentText.replace(" ", ""));
//    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object.
     * A subclass overrides the {@code finalize} method to dispose of
     * system resources or to perform other cleanup.
     */
    @Override
    protected void finalize() throws Throwable {
        this.removeTextChangedListener(this);
        super.finalize();
    }

    /**
     * This method is called to notify you that, somewhere within
     * <code>s</code>, the text has been changed.
     *
     * @param s The EditText's editable object.
     */
    @Override
    public void afterTextChanged(@NotNull Editable s) {
        Log.d(TAG, "PhoneEditText afterTextChanged");
        if (s.length() < latestEditableLength) {
            latestEditableLength = s.length();
            if (latestEditableLength == 2 || latestEditableLength == 5 || latestEditableLength == 8) {
                s.replace(latestEditableLength - 1, latestEditableLength, "");
            }
            return;
        }
        int a = s.length();
        int b = latestEditableLength;

        String number = s.toString();
        int length = number.length();
        if (length == 2 || length == 5 || length == 8) {
            s.insert(length, " ");//append(" ");
        }
        latestEditableLength = s.length();
    }


    /**
     * Ignored
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * Ignored
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
