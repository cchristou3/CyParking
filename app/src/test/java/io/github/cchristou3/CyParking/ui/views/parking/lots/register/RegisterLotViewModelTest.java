package io.github.cchristou3.CyParking.ui.views.parking.lots.register;

import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.apiClient.remote.repository.DefaultOperatorRepository;
import io.github.cchristou3.CyParking.data.pojo.form.operator.RegisterLotFormState;
import io.github.cchristou3.CyParking.ui.InstantTaskRuler;

import static io.github.cchristou3.CyParking.ui.LiveDataTestUtil.getOrAwaitValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link RegisterLotViewModel} class.
 */
@RunWith(AndroidJUnit4.class)
public class RegisterLotViewModelTest extends InstantTaskRuler {

    // Valid attributes
    String validOperatorMobileNumber = "99999999";
    Integer validLotCapacity = 30;
    String validLotName = "Name";
    LatLng validLotLatLng = new LatLng(1, 2);
    List<SlotOffer> validSlotOfferList = Collections
            .singletonList(SlotOffer.getRandomInstance(new Random()));
    // Invalid attributes
    String invalidOperatorMobileNumber = "";
    Integer invalidLotCapacity = 0;
    String invalidLotName = "";
    LatLng invalidLotLatLng = null;
    List<SlotOffer> invalidSlotOfferList = null;

    // Other
    Uri mockUri;

    // Subject under test
    private RegisterLotViewModel registerLotViewModel;

    @Before
    public void setUp() {
        registerLotViewModel = new RegisterLotViewModel(new DefaultOperatorRepository());
        mockUri = Mockito.mock(Uri.class);
    }

    @Test
    public void getRegisterLotFormState_returnsNonNull() {
        assertThat(registerLotViewModel.getRegisterLotFormState(), is(not(nullValue())));
    }


    @Test
    public void lotRegistrationDataChanged_setNewValidValueToAllFields_returnsValidForm() throws InterruptedException {
        // Ensure valid set up
        registerLotViewModel.lotRegistrationDataChanged(validOperatorMobileNumber, validLotCapacity, validLotName, validLotLatLng, validSlotOfferList);
        getOrAwaitValue(registerLotViewModel.getRegisterLotFormState());

        // Setting the mock uri first and triggering an update
        // as the Uri validation check is before the Slot offer check in lotRegistrationDataChanged
        registerLotViewModel.updateImageUri(mockUri);
        getOrAwaitValue(registerLotViewModel.getImageUriState());
        // When the user invokes the lotRegistrationDataChanged method
        registerLotViewModel.lotRegistrationDataChanged(
                validOperatorMobileNumber, validLotCapacity,
                validLotName, validLotLatLng, validSlotOfferList
        );

        // Then the livedata's values should have been updated
        assertThat(getOrAwaitValue(registerLotViewModel.getRegisterLotFormState()).isDataValid(),
                is(true));
    }

    @Test
    public void lotRegistrationDataChanged_operatorMobileError_returnsOperatorMobileErrorNonNullInvalidForm()
            throws InterruptedException {
        // When the user invokes the lotRegistrationDataChanged method
        registerLotViewModel.lotRegistrationDataChanged(
                invalidOperatorMobileNumber, validLotCapacity,
                validLotName, validLotLatLng, validSlotOfferList
        );
        // Then the livedata's form should be invalid and the mobile error not null
        RegisterLotFormState latestForm = getOrAwaitValue(registerLotViewModel.getRegisterLotFormState());
        assertThat(latestForm.isDataValid(), is(false));
        assertThat(latestForm.getMobileNumberError(), is(not(nullValue())));
    }

    @Test
    public void lotRegistrationDataChanged_lotCapacityError_returnsLotCapacityErrorNonNullInvalidForm()
            throws InterruptedException {
        // When the user invokes the lotRegistrationDataChanged method
        registerLotViewModel.lotRegistrationDataChanged(
                validOperatorMobileNumber, invalidLotCapacity,
                validLotName, validLotLatLng, validSlotOfferList
        );
        // Then the livedata's form should be invalid and the mobile error not null
        RegisterLotFormState latestForm = getOrAwaitValue(registerLotViewModel.getRegisterLotFormState());
        assertThat(latestForm.isDataValid(), is(false));
        assertThat(latestForm.getLotCapacityError(), is(not(nullValue())));
    }

    @Test
    public void lotRegistrationDataChanged_lotNameError_returnsLotNameErrorNonNullInvalidForm()
            throws InterruptedException {
        // When the user invokes the lotRegistrationDataChanged method
        registerLotViewModel.lotRegistrationDataChanged(
                validOperatorMobileNumber, validLotCapacity,
                invalidLotName, validLotLatLng, validSlotOfferList
        );
        // Then the livedata's form should be invalid and the mobile error not null
        RegisterLotFormState latestForm = getOrAwaitValue(registerLotViewModel.getRegisterLotFormState());
        assertThat(latestForm.isDataValid(), is(false));
        assertThat(latestForm.getLotNameError(), is(not(nullValue())));
    }

    @Test
    public void lotRegistrationDataChanged_lotLatLngError_returnsLotLatLngErrorNonNullInvalidForm()
            throws InterruptedException {
        // When the user invokes the lotRegistrationDataChanged method
        registerLotViewModel.lotRegistrationDataChanged(
                validOperatorMobileNumber, validLotCapacity,
                validLotName, invalidLotLatLng, validSlotOfferList
        );
        // Then the livedata's form should be invalid and the mobile error not null
        RegisterLotFormState latestForm = getOrAwaitValue(registerLotViewModel.getRegisterLotFormState());
        assertThat(latestForm.isDataValid(), is(false));
        assertThat(latestForm.getLatLngError(), is(not(nullValue())));
    }

    @Test
    public void lotRegistrationDataChanged_slotOfferListError_returnsSlotOfferListErrorNonNullInvalidForm()
            throws InterruptedException {
        // When the user invokes the lotRegistrationDataChanged method
        registerLotViewModel.lotRegistrationDataChanged(
                validOperatorMobileNumber, validLotCapacity,
                validLotName, validLotLatLng, invalidSlotOfferList
        );
        // Setting the mock uri first and triggering an update
        // as the Uri validation check is before the Slot offer check in lotRegistrationDataChanged
        registerLotViewModel.updateImageUri(mockUri);
        getOrAwaitValue(registerLotViewModel.getImageUriState());

        // Then the livedata's form should be invalid and the mobile error not null
        RegisterLotFormState latestForm = getOrAwaitValue(registerLotViewModel.getRegisterLotFormState());
        assertThat(latestForm.isDataValid(), is(false));
        assertThat(latestForm.getSlotOfferError(), is(not(nullValue())));
    }

    @Test
    public void updateSlotOfferList_setsNewValue() throws InterruptedException {
        // Given a new list got received
        List<SlotOffer> offers = new ArrayList<>();
        SlotOffer slotOffer = SlotOffer.getRandomInstance(new Random());
        offers.add(slotOffer);
        // When the updateSlotOfferList is called
        registerLotViewModel.updateSlotOfferList(offers);
        // Then both getSlotOfferListState and
        // getSlotOfferList should return the expected values
        assertThat(getOrAwaitValue(registerLotViewModel.getSlotOfferListState()),
                is(offers));
        assertThat(registerLotViewModel.getSlotOfferList().get(0),
                is(slotOffer));
    }

    @Test
    public void getSelectedSlotOfferArgumentsState_returnsNonNullInvalidFormInitially() throws InterruptedException {
        assertThat(getOrAwaitValue(registerLotViewModel.getSelectedSlotOfferArgumentsState()),
                is(not(nullValue())));
        assertThat(getOrAwaitValue(registerLotViewModel.getSelectedSlotOfferArgumentsState()).isDataValid(),
                is(not(true)));
    }

    @Test
    public void updateSelectedSlotOfferArguments_NullDurationNonNullPrice_returnsInvalidForm() throws InterruptedException {
        registerLotViewModel.updateSelectedSlotOfferArguments(null, 1.0f);
        assertThat(getOrAwaitValue(registerLotViewModel.getSelectedSlotOfferArgumentsState()).isDataValid(),
                is(not(true)));
    }

    @Test
    public void updateSelectedSlotOfferArguments_NonNullDurationNullPrice_returnsInvalidForm() throws InterruptedException {
        registerLotViewModel.updateSelectedSlotOfferArguments(1.0f, null);
        assertThat(getOrAwaitValue(registerLotViewModel.getSelectedSlotOfferArgumentsState()).isDataValid(),
                is(not(true)));
    }

    @Test
    public void updateSelectedSlotOfferArguments_NullDurationNullPrice_returnsInvalidForm() throws InterruptedException {
        registerLotViewModel.updateSelectedSlotOfferArguments(null, null);
        assertThat(getOrAwaitValue(registerLotViewModel.getSelectedSlotOfferArgumentsState()).isDataValid(),
                is(not(true)));
    }

    @Test
    public void updateSelectedSlotOfferArguments_NonNullDurationNonNullPrice_returnsInvalidForm() throws InterruptedException {
        registerLotViewModel.updateSelectedSlotOfferArguments(1.0f, 1.0f);
        assertThat(getOrAwaitValue(registerLotViewModel.getSelectedSlotOfferArgumentsState()).isDataValid(),
                is(true));
    }

    @Test
    public void updateImageUri_setsNewValue() throws InterruptedException {
        registerLotViewModel.lotRegistrationDataChanged(validOperatorMobileNumber, validLotCapacity, validLotName, validLotLatLng, validSlotOfferList);
        getOrAwaitValue(registerLotViewModel.getRegisterLotFormState());
        registerLotViewModel.updateImageUri(mockUri);
        assertThat(registerLotViewModel.getImageUri(),
                is(mockUri));
    }

    @Test
    public void updateImageUri_setsNullValue() throws InterruptedException {
        // Ensure valid set up
        registerLotViewModel.lotRegistrationDataChanged(validOperatorMobileNumber, validLotCapacity, validLotName, validLotLatLng, validSlotOfferList);
        getOrAwaitValue(registerLotViewModel.getRegisterLotFormState());

        registerLotViewModel.updateImageUri(null);
        assertThat(registerLotViewModel.getImageUri(),
                is(nullValue()));
    }

    @Test
    public void navigateBack_setsValueToNull() throws InterruptedException {
        registerLotViewModel.navigateBack();
        assertThat(getOrAwaitValue(registerLotViewModel.getNavigateBackState()),
                is(nullValue()));
    }
}