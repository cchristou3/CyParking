package io.github.cchristou3.CyParking.ui.parking.lots.register;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.pojo.form.operator.RegisterLotFormState;
import io.github.cchristou3.CyParking.data.repository.DefaultOperatorRepository;
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
    // Subject under test
    private RegisterLotViewModel registerLotViewModel;

    @Before
    public void setUp() {
        registerLotViewModel = new RegisterLotViewModel(new DefaultOperatorRepository());
    }

    @Test
    public void getRegisterLotFormState_returnsNonNull() {
        assertThat(registerLotViewModel.getRegisterLotFormState(), is(not(nullValue())));
    }


    @Test
    public void lotRegistrationDataChanged_setNewValidValueToAllFields_returnsValidForm() throws InterruptedException {
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
}