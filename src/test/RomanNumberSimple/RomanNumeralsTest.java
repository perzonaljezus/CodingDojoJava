package RomanNumberSimple;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RomanNumeralsTest {
    RomanNumberSimple.RomanNumerals r = null;

    @Before
    public void setUp() throws Exception {
        r = new RomanNumberSimple.RomanNumerals();
    }

    // Acceptance Tests
    @Test
    public void test1990toMDCCCCLXXXX() throws Exception {
        assertEquals("MDCCCCLXXXX", r.convertArabic(1990));
    }

    @Test
    public void test2008toMMVIII() throws Exception {
        assertEquals("MMVIII", r.convertArabic(2008));
    }

    // unit tests
    @Test
    public void test1toI() throws Exception {
        assertEquals("I", r.convertArabic(1));
    }
    @Test
    public void test2toII() throws Exception {
        assertEquals("II", r.convertArabic(2));
    }
    @Test
    public void test3toIII() throws Exception {
        assertEquals("III", r.convertArabic(3));
    }
    @Test
    public void test4toIIII() throws Exception {
        assertEquals("IIII", r.convertArabic(4));
    }
    @Test
    public void test5toV() throws Exception {
        assertEquals("V", r.convertArabic(5));
    }
    @Test
    public void test6toVI() throws Exception {
        assertEquals("VI", r.convertArabic(6));
    }
    @Test
    public void test7toVII() throws Exception {
        assertEquals("VII", r.convertArabic(7));
    }
    @Test
    public void test8toVIII() throws Exception {
        assertEquals("VIII", r.convertArabic(8));
    }
    @Test
    public void test9toIX() throws Exception {
        assertEquals("VIIII", r.convertArabic(9));
    }
    @Test
    public void test10toX() throws Exception {
        assertEquals("X", r.convertArabic(10));
    }

}