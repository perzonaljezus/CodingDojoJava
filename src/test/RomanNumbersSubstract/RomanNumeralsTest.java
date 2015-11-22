package RomanNumbersSubstract;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RomanNumeralsTest {
    RomanNumerals r = null;

    @Before
    public void setUp() throws Exception {
        r = new RomanNumerals();
    }

    // Acceptance Tests
    @Test
    public void test1990toMCMXC() throws Exception {
        assertEquals("MCMXC", r.convertArabicNumber(1990));
    }
    @Test
    public void test1984toMCMLXXXIV() throws Exception {
        assertEquals("MCMLXXXIV", r.convertArabicNumber(1984));
    }

    @Test
    public void test2008toMMVIII() throws Exception {
        assertEquals("MMVIII", r.convertArabicNumber(2008));
    }
    @Test
    public void test1234toMCCXXXIV() throws Exception {
        assertEquals("MCCXXXIV", r.convertArabicNumber(1234));
    }
    @Test
    public void test987toCMLXXXVII() throws Exception {
        assertEquals("CMLXXXVII", r.convertArabicNumber(987));
    }

    // unit tests

    // Roman numbers
    @Test
    public void test1toI() throws Exception {
        assertEquals("I", r.convertArabicNumber(1));
    }
    @Test
    public void test2toII() throws Exception {
        assertEquals("II", r.convertArabicNumber(2));
    }
    @Test
    public void test3toIII() throws Exception {
        assertEquals("III", r.convertArabicNumber(3));
    }
    @Test
    public void test4toIV() throws Exception {
        assertEquals("IV", r.convertArabicNumber(4));
    }
    @Test
    public void test5toV() throws Exception {
        assertEquals("V", r.convertArabicNumber(5));
    }
    @Test
    public void test6toVI() throws Exception {
        assertEquals("VI", r.convertArabicNumber(6));
    }
    @Test
    public void test7toVII() throws Exception {
        assertEquals("VII", r.convertArabicNumber(7));
    }
    @Test
    public void test8toVIII() throws Exception {
        assertEquals("VIII", r.convertArabicNumber(8));
    }
    @Test
    public void test9toIX() throws Exception {
        assertEquals("IX", r.convertArabicNumber(9));
    }
    @Test
    public void test10toX() throws Exception {
        assertEquals("X", r.convertArabicNumber(10));
    }

    // helper methods

    // convert

    @Test
    public void testConvert2Thousands() {
        assertEquals("MM",r.convertThousands(2));
    }
    @Test
    public void testConvert9Hundreds() {
        assertEquals("CM",r.convertHundreds(9));
    }
    @Test
    public void testConvert8Hundreds() {
        assertEquals("DCCC",r.convertHundreds(8));
    }
    @Test
    public void testConvert6Hundreds() {
        assertEquals("DC",r.convertHundreds(6));
    }
    @Test
    public void testConvert5Hundreds() {
        assertEquals("D",r.convertHundreds(5));
    }
    @Test
    public void testConvert4Hundreds() {
        assertEquals("CD",r.convertHundreds(4));
    }
    @Test
    public void testConvert2Hundreds() {
        assertEquals("CC",r.convertHundreds(2));
    }
    @Test
    public void testConvert1Hundreds() {
        assertEquals("C",r.convertHundreds(1));
    }
    @Test
    public void testConvert9Tens() {
        assertEquals("XC",r.convertTens(9));
    }
    @Test
    public void testConvert8Tens() {
        assertEquals("LXXX",r.convertTens(8));
    }
    @Test
    public void testConvert7Tens() {
        assertEquals("LXX",r.convertTens(7));
    }

    @Test
    public void testConvert5Tens() {
        assertEquals("L",r.convertTens(5));
    }
    @Test
    public void testConvert4Tens() {
        assertEquals("XL",r.convertTens(4));
    }
    @Test
    public void testConvert3Tens() {
        assertEquals("XXX",r.convertTens(3));
    }
    @Test
    public void testConvert1Tens() {
        assertEquals("X",r.convertTens(1));
    }




    @Test
    public void testConvert9Ones() {
        assertEquals("IX",r.convertOnes(9));
    }
    @Test
    public void testConvert8Ones() {
        assertEquals("VIII",r.convertOnes(8));
    }
    @Test
    public void testConvert7Ones() {
        assertEquals("VII",r.convertOnes(7));
    }

    @Test
    public void testConvert5Ones() {
        assertEquals("V",r.convertOnes(5));
    }
    @Test
    public void testConvert4Ones() {
        assertEquals("IV",r.convertOnes(4));
    }
    @Test
    public void testConvert3Ones() {
        assertEquals("III",r.convertOnes(3));
    }
    @Test
    public void testConvert1Ones() {
        assertEquals("I",r.convertOnes(1));
    }


    // get

    @Test
    public void testGetThousands2345() {
        assertEquals(2,r.getThousands(2345));
    }
    @Test
    public void testGetThousands1987() {
        assertEquals(1,r.getThousands(1987));
    }
    @Test
    public void testGetHundreds987() {
        assertEquals(9,r.getHundreds(987));
    }
    @Test
    public void testGetTens87() {
        assertEquals(8,r.getTens(87));
    }


}