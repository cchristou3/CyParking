package io.github.cchristou3.CyParking.data.pojo;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the {@link SnapshotState} class.
 */
public class SnapshotStateTest {

    @Test
    public void snapshotState_initializeWithInitialDataRetrievalState_returnsTheSameState() {
        // Given
        byte state = SnapshotState.INITIAL_DATA_RETRIEVAL;
        // When
        SnapshotState snapshotState = new SnapshotState(state);
        // Then
        Assert.assertEquals(state, snapshotState.getState());
    }

    @Test
    public void snapshotState_initializeWithListeningToChangesState_returnsTheSameState() {
        // Given
        byte state = SnapshotState.LISTENING_TO_DATA_CHANGES;
        // When
        SnapshotState snapshotState = new SnapshotState(state);
        // Then
        Assert.assertEquals(state, snapshotState.getState());
    }

    @Test
    public void setState_listeningToChangesState_returns_returnsTheSameState() {
        // Given
        SnapshotState snapshotState = new SnapshotState(SnapshotState.INITIAL_DATA_RETRIEVAL);
        // When
        snapshotState.setState(SnapshotState.LISTENING_TO_DATA_CHANGES);
        // Then
        Assert.assertEquals(SnapshotState.LISTENING_TO_DATA_CHANGES, snapshotState.getState());
    }
}