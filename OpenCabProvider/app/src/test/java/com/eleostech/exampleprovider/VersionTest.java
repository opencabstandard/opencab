package com.eleostech.exampleprovider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.opencabstandard.provider.Version;

public class VersionTest {
    @Test
    public void version_TestVersions() {
        Version a = new Version("1.1");
        Version b = new Version("1.1.1");
        assertTrue(a.compareTo(b) == -1); //// return -1 (a<b)
        assertFalse(a.equals(b));

        Version a1 = new Version("2.0");
        Version b1 = new Version("1.9.9");
        assertTrue(a1.compareTo(b1) == 1); // return 1 (a1>b1)
        assertFalse(a1.equals(b1));

        Version a2 = new Version("1.0");
        Version b2 = new Version("1");
        assertTrue(a2.compareTo(b2) == 0); // return 0 (a2=b2)
        assertTrue(a2.equals(b2));

        Version a3 = new Version("1");
        Version b3 = null;
        assertTrue(a3.compareTo(b3) == 1);// return 1 (a3>b3)
        assertFalse(a3.equals(b3));

        Version a4 = new Version("2.06");
        Version b4 = new Version("2.060");
        assertFalse(a4.equals(b4));   // return false

    }

}