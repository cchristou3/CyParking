package io.github.cchristou3.CyParking.ui.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

/**
 * Purpose: provide a more clean way of inputting a mobile number.
 * It automatically adds spacing to make the mobile number appear
 * as such as 99 99 99 99.
 *
 * @author Charalambos Christou
 * @version 2.0 17/01/21
 */
public final class PhoneEditText extends androidx.appcompat.widget.AppCompatEditText implements TextWatcher {

    private int mLatestEditableLength = 0;

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
     * @param updatedEditable The EditText's editable object.
     */
    @Override
    public void afterTextChanged(@NotNull Editable updatedEditable) {
        // If the received Editable has shorter length than the previously stored one
        // then the user is using backspace to remove characters.
        if (updatedEditable.length() < mLatestEditableLength) {
            mLatestEditableLength = updatedEditable.length();
            // If the cursor is pointing in positions where there is space, then
            // remove the space at that position.
            if (mLatestEditableLength == 2 || mLatestEditableLength == 5 || mLatestEditableLength == 8) {
                updatedEditable.replace(mLatestEditableLength - 1, mLatestEditableLength, "");
            }
            // and terminate the method.
            return;
        }

        // Evey two digits at a white space.
        int length = updatedEditable.toString().length();
        if (length == 2 || length == 5 || length == 8) {
            updatedEditable.insert(length, " ");
        }
        // Keep track of the editable's length
        mLatestEditableLength = updatedEditable.length();
    }

    /**
     * Access the current text and remove any white spaces,
     * then return it.
     *
     * @return The editText's text represented as a string without any white spaces.
     */
    @NotNull
    public String getNonSpacedText() {
        if (super.getText() != null)
            return super.getText().toString().replace(" ", "");
        else
            return "";
    }

    /**
     * Unused {@link TextWatcher} methods
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Ignore */ }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { /* Ignore */ }
}
