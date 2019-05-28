package com.example.loghi.a3o_media_player;

import android.content.Context;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LoginUnitTest {
    private static final String FAKE_STRING = "Login was successful";


    @Test
    public void check_email() {

        LoginActivity myObjectUnderTest = new LoginActivity();

        // ...when the string is returned from the object under test...
        boolean result = myObjectUnderTest.isEmailValid("abcd#gmail.com");

        // ...then the result should be the expected one.
        assertEquals("Is email valid",false,result);
    }
}
