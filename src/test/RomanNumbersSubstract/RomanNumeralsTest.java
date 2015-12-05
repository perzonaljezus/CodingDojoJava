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
    public void test3888toMMMDCCCLXXXVIII() throws Exception { assertEquals("MMMDCCCLXXXVIII", romanNumerals.convertArabicNumber(3888));}
    @Test
    public void test207toCCVII() throws Exception { assertEquals("CCVII", romanNumerals.convertArabicNumber(207));}
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



    // helper methods

    @Test
    public void testConvert2Thousands() {assertEquals("MM", romanNumerals.convertThousands(2));}

    @Test
    public void testConvert9Hundreds() { assertEquals("CM", romanNumerals.convertHundreds(9));}
    @Test
    public void testConvert8Hundreds() {
        assertEquals("DCCC", romanNumerals.convertHundreds(8));
    }
    @Test
    public void testConvert6Hundreds() {
        assertEquals("DC", romanNumerals.convertHundreds(6));
    }
    @Test
    public void testConvert5Hundreds() {
        assertEquals("D", romanNumerals.convertHundreds(5));
    }
    @Test
    public void testConvert4Hundreds() {
        assertEquals("CD", romanNumerals.convertHundreds(4));
    }
    @Test
    public void testConvert2Hundreds() {
        assertEquals("CC", romanNumerals.convertHundreds(2));
    }
    @Test
    public void testConvert1Hundreds() {
        assertEquals("C", romanNumerals.convertHundreds(1));
    }

    @Test
    public void testConvert9Tens() {
        assertEquals("XC", romanNumerals.convertTens(9));
    }
    @Test
    public void testConvert8Tens() {
        assertEquals("LXXX", romanNumerals.convertTens(8));
    }
    @Test
    public void testConvert7Tens() {
        assertEquals("LXX", romanNumerals.convertTens(7));
    }
    @Test
    public void testConvert5Tens() {
        assertEquals("L", romanNumerals.convertTens(5));
    }
    @Test
    public void testConvert4Tens() {
        assertEquals("XL", romanNumerals.convertTens(4));
    }
    @Test
    public void testConvert3Tens() {
        assertEquals("XXX", romanNumerals.convertTens(3));
    }
    @Test
    public void testConvert1Tens() {
        assertEquals("X", romanNumerals.convertTens(1));
    }


    @Test
    public void testConvert9Ones() {
        assertEquals("IX", romanNumerals.convertOnes(9));
    }
    @Test
    public void testConvert8Ones() {
        assertEquals("VIII", romanNumerals.convertOnes(8));
    }
    @Test
    public void testConvert7Ones() {
        assertEquals("VII", romanNumerals.convertOnes(7));
    }
    @Test
    public void testConvert5Ones() {
        assertEquals("V", romanNumerals.convertOnes(5));
    }
    @Test
    public void testConvert4Ones() {
        assertEquals("IV", romanNumerals.convertOnes(4));
    }
    @Test
    public void testConvert3Ones() {
        assertEquals("III", romanNumerals.convertOnes(3));
    }
    @Test
    public void testConvert1Ones() { assertEquals("I", romanNumerals.convertOnes(1));}

}