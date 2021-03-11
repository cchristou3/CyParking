package io.github.cchristou3.CyParking.ui.views.parking.slots.booking;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.PaymentSessionHelper;
import io.github.cchristou3.CyParking.data.repository.BookingRepository;

/**
 * Purpose: <p>ViewModel provider factory to instantiate BookingViewModel.
 * Required given BookingViewModel has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 2.0 11/03/21
 */
public class BookingViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    private final BookingFragment mFragment;

    public BookingViewModelFactory(@NonNull BookingFragment bookingFragment) {
        this.mFragment = bookingFragment;
    }

    /**
     * Creates a new instance of the given {@code Class}.
     * <p>
     *
     * @param modelClass a {@code Class} whose instance is requested
     * @return a newly created ViewModel
     */
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BookingViewModel.class)) {
            return (T) new BookingViewModel(new BookingRepository(),
                    new PaymentSessionHelper(new PaymentSessionHelper.UiPaymentSessionListener() {
                        @Override
                        public void onPaymentMethodSelected(@NotNull String paymentMethodDetails) {
                            mFragment.getBookingViewModel().updatePaymentMethodState(paymentMethodDetails);
                        }

                        @Override
                        public void onCommunicatingStateChanged(boolean isCommunicating) {
                            if (isCommunicating)
                                mFragment.getGlobalStateViewModel().showLoadingBar();
                            else mFragment.getGlobalStateViewModel().hideLoadingBar();
                        }

                        @Override
                        public void onError(int errorCode, @NotNull String errorMessage) {
                            mFragment.showAlert(errorMessage);
                        }
                    }));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}