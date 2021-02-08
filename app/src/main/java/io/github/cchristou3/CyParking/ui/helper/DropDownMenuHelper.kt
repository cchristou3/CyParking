package io.github.cchristou3.CyParking.ui.helper

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ListAdapter
import com.google.android.material.textfield.TextInputLayout
import io.github.cchristou3.CyParking.R

class DropDownMenuHelper {


    companion object {

        val TAG: String? = DropDownMenuHelper::class.qualifiedName

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> setUpSlotOfferDropDownMenu(
                context: Context, textInputLayout: TextInputLayout,
                array: Array<T>, itemHandler: ItemHandler<T>
        ) {
            if (textInputLayout.editText !is AutoCompleteTextView) return

            val autoCompleteTextView = textInputLayout.editText as AutoCompleteTextView

            autoCompleteTextView // Add an on item selected listener
                    .onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                /**
                 *
                 * Callback method to be invoked when an item in this view has been
                 * selected.
                 *
                 * @param parent The AdapterView where the selection happened
                 * @param view The view within the AdapterView that was clicked
                 * @param position The position of the view in the adapter
                 * @param id The row id of the item that is selected
                 */
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // Cast the selected object to a T object
                    val item = itemHandler.castItem(autoCompleteTextView.adapter, position)
                    itemHandler.onItemSelected(item)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) { /* ignore */
                }

            }

            // Initialize an ArrayAdapter
            val arrayAdapter = ArrayAdapter(context, R.layout.slot_offer_drop_down_item, array)
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
         */
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        private fun <T> setDropDownMenuDefaultItem(
                autoCompleteTextView: AutoCompleteTextView, itemHandler: ItemHandler<T>
        ) {
            autoCompleteTextView.setOnFocusChangeListener { _, _ ->

                Log.d(TAG, "setDropDownMenuDefaultItem: ")
                val item = itemHandler.castItem(autoCompleteTextView.adapter, 0)
                autoCompleteTextView.setText(
                        item.toString(), false
                )
                itemHandler.onItemSelected(item)
                autoCompleteTextView.onFocusChangeListener = null // remove listener
            }
        }
    }


    interface ItemHandler<T> {
        fun castItem(parent: ListAdapter, position: Int): T
        fun onItemSelected(item: T)
    }
}