package com.itgnostic.test4sandbox.utils;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RestApiUtilsTest {
    private static final String[] PARAMS_BAD = {null, "", "&&&", "one ", "\t5", "5, one", "5,one&"};
    private static final String PARAMS_OK_1 = "one";
    private static final String PARAMS_OK_2 = "5";
    private static final String PARAMS_OK_3 = "5,one";
    private static final String PARAMS_OK_4 = "5,one,one";

    private static final String[] PARAMS_INT_BAD = {null, "", "-5", " 5", "5.2", "5o", "5, 5", "5,s", "5&", "5,5?", "5,-5"};
    private static final String PARAMS_INT_OK_1 = "5";
    private static final String PARAMS_INT_OK_2 = "5,5,5";
    private static final String PARAMS_INT_OK_3 = "5,6";
    private static final String PARAMS_INT_OK_4 = "5,6,5";


    @Test
    public void getParamsAsArrayTest() {
        String[] badValue = new String[0];
        String[] oneValue = {"one"};
        String[] oneValue2 = {"5"};
        String[] lotValues = {"5","one"};
        String[] lotValuesNotUniq = {"5","one","one"};

        for (String badParam : PARAMS_BAD)
            assertArrayEquals(badValue, RestApiUtils.getParamsAsArray(badParam, true));

        assertArrayEquals(oneValue, RestApiUtils.getParamsAsArray(PARAMS_OK_1, true));
        assertArrayEquals(oneValue2, RestApiUtils.getParamsAsArray(PARAMS_OK_2, true));
        assertArrayEquals(lotValues, RestApiUtils.getParamsAsArray(PARAMS_OK_3, true));
        assertArrayEquals(lotValues, RestApiUtils.getParamsAsArray(PARAMS_OK_4, true));
        assertArrayEquals(lotValuesNotUniq, RestApiUtils.getParamsAsArray(PARAMS_OK_4, false));
    }

    @Test
    public void getIntParamsAsArrayTest() {
        long[] badValue = new long[0];
        long[] oneValue = {5};
        long[] lotValues = {5,6};
        long[] lotValuesNotUniq1 = {5,5,5};
        long[] lotValuesNotUniq2 = {5,6,5};

        for (String badParam : PARAMS_INT_BAD)
            assertArrayEquals(badValue, RestApiUtils.getLongParamsAsArray(badParam, true));

        assertArrayEquals(oneValue, RestApiUtils.getLongParamsAsArray(PARAMS_INT_OK_1, true));
        assertArrayEquals(oneValue, RestApiUtils.getLongParamsAsArray(PARAMS_INT_OK_2, true));
        assertArrayEquals(lotValues, RestApiUtils.getLongParamsAsArray(PARAMS_INT_OK_3, true));
        assertArrayEquals(lotValues, RestApiUtils.getLongParamsAsArray(PARAMS_INT_OK_4, true));

        assertArrayEquals(lotValuesNotUniq1, RestApiUtils.getLongParamsAsArray(PARAMS_INT_OK_2, false));
        assertArrayEquals(lotValuesNotUniq2, RestApiUtils.getLongParamsAsArray(PARAMS_INT_OK_4, false));
    }
}
