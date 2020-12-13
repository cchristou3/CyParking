package io.github.cchristou3.CyParking.view.data.pojo.user;

/**
 * Purpose: <p>Base class of FormState sub-classes</p>
 * All common attributes are encapsulated here
 * to avoid code duplication.
 * A FormState object has only one attribute,
 * whether or not it is in a valid state.
 *
 * @author Charalambos Christou
 * @version 2.0 11/12/20
 */
public class FormState {

    private final boolean isDataValid;

    /**
     * Initialize {@link FormState#isDataValid} with the
     * specified argument.
     *
     * @param isDataValid The state of the form
     */
    public FormState(boolean isDataValid) {
        this.isDataValid = isDataValid;
    }

    /**
     * @return True if valid. Otherwise, false
     */
    public boolean isDataValid() {
        return isDataValid;
    }
}
