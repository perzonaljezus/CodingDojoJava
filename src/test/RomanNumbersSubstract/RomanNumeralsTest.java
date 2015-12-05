package RomanNumbersSubstract;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RomanNumeralsTest {

    RomanNumerals romanNumerals;

    @Before
    public void setUp() throws Exception {
        romanNumerals = new RomanNumerals();
    }

    // Acceptance Tests

    @Test
    public void test3999toMMMCMXCIX() throws Exception { assertEquals("MMMCMXCIX", romanNumerals.convertArabicNumber(3999));}
    @Test
    public void test3888toMMMDCCCLXXXVIII() throws Exception { assertEquals("MMMDCCCLXXXVIII", romanNumerals.convertArabicNumber(3888));}
    @Test
    public void test207toCCVII() throws Exception { assertEquals("CCVII", romanNumerals.convertArabicNumber(207));}
    @Test
    public void test654toMLXVI() throws Exception { assertEquals("DCLIV", romanNumerals.convertArabicNumber(654));}
    @Test
    public void test1066toMLXVI() throws Exception { assertEquals("MLXVI", romanNumerals.convertArabicNumber(1066));}
    @Test
    public void test1954toMCMLIV() throws Exception { assertEquals("MCMLIV", romanNumerals.convertArabicNumber(1954));}
    @Test
    public void test1990toMCMXC() throws Exception { assertEquals("MCMXC", romanNumerals.convertArabicNumber(1990));}
    @Test
    public void test1984toMCMLXXXIV() throws Exception { assertEquals("MCMLXXXIV", romanNumerals.convertArabicNumber(1984));}
    @Test
    public void test2014toMMXIV() throws Exception { assertEquals("MMXIV", romanNumerals.convertArabicNumber(2014));}

    @Test
    public void test2008toMMVIII() throws Exception { assertEquals("MMVIII", romanNumerals.convertArabicNumber(2008));}
    @Test
    public void test1234toMCCXXXIV() throws Exception { assertEquals("MCCXXXIV", romanNumerals.convertArabicNumber(1234));}
    @Test
    public void test987toCMLXXXVII() throws Exception { assertEquals("CMLXXXVII", romanNumerals.convertArabicNumber(987));}
    @Test
    public void test27toXXVII() throws Exception { assertEquals("XXVII", romanNumerals.convertArabicNumber(27));}


    // unit tests

    @Test
    public void test1toI() throws Exception { assertEquals("I", romanNumerals.convertArabicNumber(1)); }
    @Test
    public void test2toII() throws Exception { assertEquals("II", romanNumerals.convertArabicNumber(2));}
    @Test
    public void test3toIII() throws Exception { assertEquals("III", romanNumerals.convertArabicNumber(3));}
    @Test
    public void test4toIV() throws Exception { assertEquals("IV", romanNumerals.convertArabicNumber(4));}
    @Test
    public void test5toV() throws Exception { assertEquals("V", romanNumerals.convertArabicNumber(5));}
    @Test
    public void test6toVI() throws Exception { assertEquals("VI", romanNumerals.convertArabicNumber(6));}
    @Test
    public void test7toVII() throws Exception { assertEquals("VII", romanNumerals.convertArabicNumber(7));}
    @Test
    public void test8toVIII() throws Exception { assertEquals("VIII", romanNumerals.convertArabicNumber(8));}
    @Test
    public void test9toIX() throws Exception { assertEquals("IX", romanNumerals.convertArabicNumber(9));}
    @Test
    public void test10toX() throws Exception { assertEquals("X", romanNumerals.convertArabicNumber(10));}


    // illegal values
    @Test(expected = IllegalArgumentException.class)
    public void test9987throwsException() throws Exception {
        assertEquals("SHOULDN'T ARRIVE HERE!", romanNumerals.convertArabicNumber(9987), "SHOULDN'T ARRIVE HERE!");
    }
}