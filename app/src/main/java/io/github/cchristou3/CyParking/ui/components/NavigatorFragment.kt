package io.github.cchristou3.CyParking.ui.components

import androidx.navigation.NavDirections
import androidx.viewbinding.ViewBinding
import io.github.cchristou3.CyParking.data.interfaces.Navigable

/**
 * Modularize logic related to navigation.
 *
 * @param <T> Any type that implements the {@link ViewBinding} interface.
 * @author Charalambos Christou
 * @version 1.0 30/08/21
 */
abstract class NavigatorFragment<T : ViewBinding> : BaseFragment<T>(), Navigable {

    open fun navigateTo(directions: NavDirections) {
        getNavController(requireActivity()).navigate(directions)
    }

    open fun goBack() {
        goBack(requireActivity())
    }
}