package io.github.cchristou3.CyParking.ui;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;

/**
 * Required by all tests that have LiveData.
 *
 * @see InstantTaskExecutorRule
 */
public class InstantTaskRuler {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
}
