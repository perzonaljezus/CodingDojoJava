package RomanNumbersSubstract;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/*
The Romans wrote numbers using letters - I, V, X, L, C, D, M. (notice these letters have lots of straight lines and are hence easy to hack into stone tablets)
The Kata says you should write a function to convert from normal numbers to Roman Numerals: eg
     1 --> I
     10 --> X
     7 --> VII
etc.

There is no need to be able to convert numbers larger than about 3000. (The Romans themselves didn't tend to go any higher)

Note that you can't write numerals like "IM" for 999.
Wikipedia says: Modern Roman numerals ...
are written by expressing each digit separately
starting with the left most digit
and skipping any digit with a value of zero.
To see this in practice, consider the ...
example of 1990. In Roman numerals 1990
is rendered: 1000=M, 900=CM, 90=XC; resulting in MCMXC.
2008 is written as 2000=MM, 8=VIII;
or MMVIII.


German wikipedia
Subtract Algo
 Die Subtraktionsregel in ihrer Normalform besagt,
 dass die Zahlzeichen I, X und C einem ihrer beiden jeweils
 nächstgrößeren Zahlzeichen vorangestellt werden dürfen
 und dann in ihrem Zahlwert von dessen Wert abzuziehen sind:

I vor V oder X: IV (4), IX (9)
X vor L oder C: XL (40), XC (90)
C vor D oder M: CD (400), CM (900)
Zahlzeichen der Fünferbündelung (V, L, D)
werden generell nicht in subtraktiver Stellung
einem größeren Zeichen vorangestellt.
 */
public class RomanNumerals {
    private TreeMap<Integer,String> romanNumbers;
    private final Map<Integer, RomanNumeralRange> romanNumeralRange;

    private int remainder;

    public RomanNumerals() {
        romanNumbers = new TreeMap<Integer,String>();
        //
        romanNumbers.put(1000,"M");
        romanNumbers.put(500,"D"); // middle between 1000 and 100
        romanNumbers.put(100,"C");
        romanNumbers.put(50,"L"); // middle between 100 and 10
        romanNumbers.put(10,"X");
        romanNumbers.put(5,"V"); // middle between 10 and 1
        romanNumbers.put(1,"I");
        //
        romanNumbers.put(0,""); // not nice: we need this only to get addToMiddleBound working correctly for 1000s

        // the reverse order is important, the algorithm in convertArabicNumber expects it descending!
        romanNumeralRange = new TreeMap<>(Collections.reverseOrder());
        romanNumeralRange.put(4, new RomanNumeralRange(10000, 5000, 1000));
        romanNumeralRange.put(3, new RomanNumeralRange(1000, 500, 100));
        romanNumeralRange.put(2, new RomanNumeralRange(100, 50, 10));
        romanNumeralRange.put(1, new RomanNumeralRange(10, 5, 1));
    }

    // main
    public String convertArabicNumber(int arabicNumber) throws IllegalArgumentException {

        // contract: we operate only in the specified range
        if( arabicNumber < 1 || arabicNumber > 3000) {
            throw new IllegalArgumentException();
        }

        //convertedArabicNumber will be a String built by adding
        //          thousandsConverted
        //        + hundredsConverted
        //        + tensConverted
        //        + onesConverted
        String convertedArabicNumber = "";

        // loop through the digits of the arabic number and convert to roman number
        String arabicNumberString = new Integer(arabicNumber).toString();
        int romanMagnitude = arabicNumberString.length();
        int tmpArabicNumber = arabicNumber;
        //
        for(char arabicDigit: arabicNumberString.toCharArray()) {
            RomanNumeralRange r = romanNumeralRange.get(romanMagnitude);
            romanMagnitude --;

            convertedArabicNumber += convertArabicDigit(Character.getNumericValue(arabicDigit), r);
            tmpArabicNumber        = getRemainder(tmpArabicNumber, r.lowerLimit);
        }

        if( tmpArabicNumber != 0) {
            throw new RuntimeException("tmpArabicNumber must be 0 after converting");
        }

        return convertedArabicNumber;
    }


    // ---------------------------------------------------------

    // business logic / domain method names
    // used in test -> we could remove the tests and remove these methods.

    /**
     * returns the roman numeral representing "thousands * 1000"
     * e.g. thousands = 2 => 2*1000 => MM
     * @para thousands int
     * @return String
     */
    public String convertThousands(int thousands) {
        RomanNumeralRange b = getBlock1000();
        return convertArabicDigit(thousands, b);
    }

    public String convertHundreds(int hundreds) {
        RomanNumeralRange b = getBlock100();
        return convertArabicDigit(hundreds, b);
    }

    public String convertTens(int tens) {
        RomanNumeralRange b = getBlock10();
        return convertArabicDigit(tens, b);
    }

    public String convertOnes(int ones) {
        RomanNumeralRange b = getBlock1();
        return convertArabicDigit(ones, b);
    }
    // discussion: make these helper methods protected, so that the main useful method is the only public method (convertArabicNumber)
    // or make them private, but then we can't test directly


    // ----------------------------------------------------------

    // non domain -> implementation logic

    private String convertArabicDigit(int arabicDigitValue, RomanNumeralRange r) {
        if (arabicDigitValue == 0) {
            return "";
        }

        // |     |     |
        // |--a--|--a--|
        // U     M     L
        //10     5     1

        String result = "";

        int distanceFromUpperLimit       = 10 - arabicDigitValue; // U - a
        int distanceFromMiddleLimitLeft  = arabicDigitValue - 5; // a - M
        int distanceFromMiddleLimitRight = 5 - arabicDigitValue; // M - a
        int distanceFromLowerLimit       = arabicDigitValue - 1; // a - L

        if( arabicDigitValue <= 5 ) {// Mi >= iM: looking from L upto M
            //if( Mi >= iL ) {// we are closer to L than to M
            if( 3 >= arabicDigitValue ) {// we are closer to L than to M
                result = addToLowerBound(r, distanceFromLowerLimit); // eg add I to I -> II
            } else {
                result = subtractFromMiddleBound(r, distanceFromMiddleLimitRight); // eg sub I from V -> IV
            }
        } else {// Ui+1 >= iM: looking from U down to M
            if( arabicDigitValue <= 8 ) {// we are closer to M than to U
                result = addToMiddleBound(r, distanceFromMiddleLimitLeft); // eg add I to V -> VI
            } else {
                result = subtractFromHigherBound(r, distanceFromUpperLimit); // eg sub I from X -> IX
            }
        }

        return result;
    }

    /**
     *
     * @param i arabic number
     * @param place position in arabic number
     * @return int the digit at the specified position
     */
    private int getDigitAtPlace(int i, int place) {
        return getQuotient(i, place);
    }

    /**
     *
     * @param r RomanNumeralRange
     * @param distance int
     * @return roman numeral expression
     */
    private String subtractFromMiddleBound(RomanNumeralRange r, int distance) {
        String result = "";
        for(int i=1; i<= distance; i++) {
            result += romanNumbers.get(r.lowerLimit);
        }
        result += romanNumbers.get(r.middleLimit);
        return result;
    }
    private String subtractFromHigherBound(RomanNumeralRange r, int distance) {
        String result = "";
        for(int i=1; i<= distance; i++) {
            result += romanNumbers.get(r.lowerLimit);
        }
        result += romanNumbers.get(r.upperLimit);
        return result;
    }

    /**
     *
     * @param r RomanNumeralRange
     * @param distance int
     * @return roman numeral expression
     */
    private String addToMiddleBound(RomanNumeralRange r, int distance) {
        String result = romanNumbers.get(r.middleLimit);
        for(int i = 1; i<= distance; i++) {
            result += romanNumbers.get(r.lowerLimit);
        }
        return result;
    }
    private String addToLowerBound(RomanNumeralRange r, int distance) {
        String result = romanNumbers.get(r.lowerLimit);
        for(int i = 1; i<= distance; i++) {
            result += romanNumbers.get(r.lowerLimit);
        }
        return result;
    }


    // discussion: I made this private. why?

    private int getQuotient(int i, int place) {
        return (int) i / place;
    }

    /**
     *
     * @param placeValue
     * @param initialValue
     * @return int new remainder
     */
    private int getRemainder( int initialValue, int placeValue ) {
        return (int) initialValue % placeValue;
    }

    /**
     * Roman numerals have an interior structure of "blocks" i.e. a range
     * e.g. the 1000 block contains 1000, 500 and 100.
     * Because, if we want to convert an arabic value of 654 we see that 600 is in the upper half
     * of the block (600 is in the range 1000 - 500).
     * 50 is in the block 100, 50, 10 => 50 is the lowest number of the "upper" half i.e. the range 100 - 50.
     * and
     * 4 is in the block 10, 5, 1 => 4 is in the "lower" half of the block.
     *
     * To convert e.g. 321, we see that 300 is in the range 500 - 100, the "lower" half of the block 1000,500,100.
     *
     * In order to compute roman numbers in the subtracting / additive way, we have to know these blocks i.e. romanNumeralRange and the boundaries
     * at which we have to change the base value of the computation.
     *
     * In the example above: 654 has the baseValue 500, and we add 100 to compute the roman representation.
     */
    class RomanNumeralRange {
        int upperLimit;
        int middleLimit;
        int lowerLimit;

        public RomanNumeralRange(int upper, int middle, int lower) {
            this.upperLimit = upper;
            this.middleLimit = middle;
            this.lowerLimit = lower;

        }
        public void addUpperLimit(int limit) {
            this.upperLimit = limit;
        }
        public void addMiddleLimit(int limit) {
            this.middleLimit = limit;
        }
        public void addLowerLimit(int limit) {
            this.lowerLimit = limit;
        }

        public int getUpperLimit() {
            return this.upperLimit;
        }
        public int getMiddleLimit() {
            return this.middleLimit;
        }
        public int getLowerLimit() {
            return this.lowerLimit;
        }
    }

    private Map getRomanNumeralRange() {
        return this.romanNumeralRange;
    }

    private RomanNumeralRange getBlock1000() {
        return this.romanNumeralRange.get(4);
    }
    private RomanNumeralRange getBlock100() {
        return this.romanNumeralRange.get(3);
    }
    private RomanNumeralRange getBlock10() {
        return this.romanNumeralRange.get(2);
    }
    private RomanNumeralRange getBlock1() {
        return this.romanNumeralRange.get(1);
    }
}

