package io.github.cchristou3.CyParking.ui.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.LayoutRes
import com.google.android.material.textfield.TextInputLayout
import io.github.cchristou3.CyParking.R

/**
 * Purpose: Initialize and set up the drop-down menus
 * that consist of [TextInputLayout] - [AutoCompleteTextView]'s.
 *
 * @author Charalambos Christou
 * @version 4.0 27/03/21
 */
class DropDownMenuHelper {

    companion object {

        val TAG: String? = DropDownMenuHelper::class.qualifiedName

        /**
         * Initialize the AutoCompleteTextView's values. Also, hook it up with
         * an [AdapterView.OnItemSelectedListener].
         * Whenever an item gets selected, trigger the
         * [ItemHandler.onItemSelected] method.
         *
         * @param context the context to make use of.
         * @param textInputLayout the input layout to set up as a drop-down-menu widget.
         * @param array a collection of [T] items.
         * @param itemHandler An interface that allows the manipulation of selected items.
         */
        @JvmStatic
        fun <T> setUpSlotOfferDropDownMenu(
                context: Context, textInputLayout: TextInputLayout,
                array: Array<T?>, itemHandler: ItemHandler<T>
        ) {
            if (textInputLayout.editText !is AutoCompleteTextView) return

            val autoCompleteTextView = textInputLayout.editText as AutoCompleteTextView

            autoCompleteTextView // Add an on item click listener
                    .setOnItemClickListener { _, _, position, _ -> // Cast the selected object to a T object
                        val item = itemHandler.castItem(autoCompleteTextView.adapter, position)
                        itemHandler.onItemSelected(item)
                    }

            // Initialize an ArrayAdapter
            val arrayAdapter = DropDownItemAdapter<T>(context, R.layout.slot_offer_drop_down_item, array, itemHandler)
            // bind the autoCompleteTextView with the above adapter
            autoCompleteTextView.setAdapter(arrayAdapter)
            // Set its default value
            setDropDownMenuDefaultItem(autoCompleteTextView, itemHandler)
        }

        /**
         * Set an [android.view.View.OnFocusChangeListener] to the given
         * [AutoCompleteTextView] instance.
         * On-focus-changed: sets both the text of the {@link AutoCompleteTextView}
         * instance and the state's value, responsible for the item,
         * with the first [T] object of the adapter's array.
         * purpose: ensures that a slot offer is picked, even if the user dismisses
         * the drop-down menu.
         *
         * @param autoCompleteTextView The [AutoCompleteTextView] instance to be attached the
         *                             onFocusChangeListener.
         * @param itemHandler An interface that allows the manipulation of selected items.
         */
        @JvmStatic
        private fun <T> setDropDownMenuDefaultItem(
                autoCompleteTextView: AutoCompleteTextView, itemHandler: ItemHandler<T>
        ) {
            autoCompleteTextView.setOnFocusChangeListener { _, _ ->
                // Access the selected item
                val item = itemHandler.castItem(autoCompleteTextView.adapter, 0)
                autoCompleteTextView.setText( // set the text of the view to
                        // the given item's content
                        itemHandler.onOutput(item), false
                )
                itemHandler.onItemSelected(item)
                autoCompleteTextView.onFocusChangeListener = null // remove listener
            }
        }

        /**
         * Perform any necessary clean up on the given [TextInputLayout]'s
         * [AutoCompleteTextView] child.
         *
         * @param textInputLayout an input layout that acts as a drop-down-menu.
         */
        @JvmStatic
        fun cleanUp(textInputLayout: TextInputLayout) {
            textInputLayout.isFocusable = false
            val autoCompleteTextView = textInputLayout.editText
            if (autoCompleteTextView is AutoCompleteTextView) {
                autoCompleteTextView.setAdapter(null)
                autoCompleteTextView.onItemClickListener = null
                autoCompleteTextView.isClickable = false
            }
        }
    }

    /**
     * Provide an interface to handle on item selected events.
     */
    interface ItemHandler<T> {
        /**
         * Access the adapter's [Any] instance at the given [position]
         * and cast it to the type of [T].
         *
         * @param parent the parent [ListAdapter] instance.
         * @param position the position of the selected item.
         */
        fun castItem(parent: ListAdapter, position: Int): T

        /**
         * Callback triggered whenever an item gets selected.
         *
         * @param item The selected item.
         */
        fun onItemSelected(item: T)

        /**
         * Callback triggered for each item in the adapter, to represent it in the View.
         */
        fun onOutput(item: T): String
    }

    class DropDownItemAdapter<T>(context: Context, @LayoutRes resource: Int, objects: Array<T?>, private val itemHandler: ItemHandler<T>)
        : ArrayAdapter<T>(context, resource, objects) {

        /**
         * Inflates the view if not already inflated and update its content using the [itemHandler].
         */
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if (view == null) {
                view = LayoutInflater.from(parent.context).inflate(R.layout.slot_offer_drop_down_item, parent, false)
            }
            if (view is TextView) {
                getItem(position)?.let { view.text = itemHandler.onOutput(it) }
            }
            return view!!
        }
    }
}