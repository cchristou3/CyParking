package io.github.cchristou3.CyParking.ui.home;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;

import io.github.cchristou3.CyParking.data.repository.operator.DefaultOperatorRepository;
import io.github.cchristou3.CyParking.data.repository.operator.OperatorRepository;

/**
 * Mock repository to substitute {@link DefaultOperatorRepository}
 * in {@link OperatorViewModelTest}.
 */
public class FakeOperatorRepository implements OperatorRepository {

    @NotNull
    @Override
    public Query observeParkingLot(String operatorId) {
        return Mockito.mock(Query.class);
    }

    @Override
    public void incrementAvailableSpacesOf(@NotNull DocumentReference lotReference) {

    }

    @Override
    public void decrementAvailableSpacesOf(@NotNull DocumentReference lotReference) {

    }
}
