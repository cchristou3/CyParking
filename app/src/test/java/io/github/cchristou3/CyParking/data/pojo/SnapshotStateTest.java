package io.github.cchristou3.CyParking.data.pojo;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the {@link SnapshotState} class.
 */
public class SnapshotStateTest {

    @Test
    public void snapshotState_initialize_with_initial_data_retrieval_state() {
        // Given
        byte state = SnapshotState.INITIAL_DATA_RETRIEVAL;
        // When
        SnapshotState snapshotState = new SnapshotState(state);
        // Then
        Assert.assertEquals(state, snapshotState.getState());
    }

    @Test
    public void snapshotState_initialize_with_listening_to_changes_state() {
        // Given
        byte state = SnapshotState.LISTENING_TO_DATA_CHANGES;
        // When
        SnapshotState snapshotState = new SnapshotState(state);
        // Then
        Assert.assertEquals(state, snapshotState.getState());
    }

    @Test
    public void setState_with_listening_to_changes_state() {
        // Given
        SnapshotState snapshotState = new SnapshotState(SnapshotState.INITIAL_DATA_RETRIEVAL);
        // When
        snapshotState.setState(SnapshotState.LISTENING_TO_DATA_CHANGES);
        // Then
        Assert.assertEquals(SnapshotState.LISTENING_TO_DATA_CHANGES, snapshotState.getState());
    }
}