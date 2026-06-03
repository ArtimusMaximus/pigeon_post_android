package com.wgu.d424;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.wgu.d424.utils.SecurityUtils;

import org.junit.Test;

public class SecurityUtilsAndroidTest {

    @Test
    public void invalidEmail_returnsFalse() {
        boolean result = SecurityUtils.isValidEmail("invalid@@email.com");
        assertFalse(result);
    }

    @Test
    public void validEmail_returnsTrue() {
        boolean result = SecurityUtils.isValidEmail("abc@aol.com");
        assertTrue(result);
    }

    @Test
    public void validFourDigitPin_returnsTrue() {
        boolean result = SecurityUtils.isValidFourDigitPin("1234");
        assertTrue(result);
    }
}