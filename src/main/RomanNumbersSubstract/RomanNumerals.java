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


german wikipedia
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
    private final Map<Integer, Block> blocks;

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
        romanNumbers.put(0,""); // not nice: we need this only to get addToBaseValue working correctly for 1000s

        // the reverse order is important, the algorithm in convertArabicNumber expects it descending!
        blocks = new TreeMap<>(Collections.reverseOrder());
        blocks.put(1000, new Block(10000, 5000, 1000));
        blocks.put(100, new Block(1000, 500, 100));
        blocks.put(10, new Block(100, 50, 10));
        blocks.put(1, new Block(10, 5, 1));
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
        int tmpArabicNumer = arabicNumber;
        for(Block b: blocks.values() ) {

            int arabicDigit        = getDigitAtPlace(tmpArabicNumer, b.lowerLimit);
            convertedArabicNumber += convertArabicDigit(arabicDigit, b.upperLimit, b.middleLimit, b.lowerLimit);

            tmpArabicNumer         = getRemainder(tmpArabicNumer, b.lowerLimit);
        }

        if( tmpArabicNumer != 0) {
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
     * @param int thousands
     * @return String
     */
    public String convertThousands(int thousands) {
        Block b = getBlock1000();
        return convertArabicDigit(thousands, b.upperLimit, b.middleLimit, b.lowerLimit);
    }

    public String convertHundreds(int hundreds) {
        Block b = getBlock100();
        return convertArabicDigit(hundreds, b.upperLimit, b.middleLimit, b.lowerLimit);
    }

    public String convertTens(int tens) {
        Block b = getBlock10();
        return convertArabicDigit(tens, b.upperLimit, b.middleLimit, b.lowerLimit);
    }

    public String convertOnes(int ones) {
        Block b = getBlock1();
        return convertArabicDigit(ones, b.upperLimit, b.middleLimit, b.lowerLimit);
    }
    // discussion: make these helper methods protected, so that the main useful method is the only public method (convertArabicNumber)
    // or make them private, but then we can't test directly


    // ----------------------------------------------------------

    // non domain -> implementation logic

    private String convertArabicDigit(int arabicDigitValue, int upperLimitOfBlock, int middleLimitOfBlock, int lowerLimitOfBlock) {
        if( arabicDigitValue == 0) {
            return "";
        }

        String result = "";
        int distanceFromUppderLimit = 10 - arabicDigitValue;
        int distanceFromMiddleLimit  = arabicDigitValue - 5;

        if( arabicDigitValue >= 5 ) { // if number of tens further from the middle value
            result = computeRomanValueUpper(upperLimitOfBlock, middleLimitOfBlock, lowerLimitOfBlock, distanceFromUppderLimit, distanceFromMiddleLimit, distanceFromMiddleLimit);
        } else {
            distanceFromMiddleLimit  =  5 - arabicDigitValue;
            int distanceFromLowerLimit  =  arabicDigitValue - 1;
            result = computeRomanValueLower(upperLimitOfBlock, middleLimitOfBlock, lowerLimitOfBlock, distanceFromUppderLimit, distanceFromMiddleLimit, distanceFromLowerLimit);
        }

        return result;
    }

    private String computeRomanValueUpper(int upperLimit, int baseValue, int lowerLimit, int distanceOfBaseValueFromUpperLimit, int distanceOfBaseValueFromMiddleLimit , int distanceFromMiddleLimit) {
        String result;
        if( distanceOfBaseValueFromUpperLimit == 1 // subtraction: subtract the lower value only once => distance == 1
                && distanceOfBaseValueFromUpperLimit < distanceOfBaseValueFromMiddleLimit) {
            result = subtractFromBaseValue(upperLimit, lowerLimit, distanceOfBaseValueFromUpperLimit);
        } else {
            result = addToBaseValue(baseValue, lowerLimit, distanceOfBaseValueFromMiddleLimit);
        }
        return result;
    }

    private String computeRomanValueLower(int upperLimit, int baseValue, int lowerLimitOfBlock, int distanceFromUpperLimit, int distanceOfBaseValueFromMiddleLimit, int distanceFromLowerLimit) {
        String result;
        if( distanceOfBaseValueFromMiddleLimit == 1
                && distanceOfBaseValueFromMiddleLimit < distanceFromLowerLimit) {
            result = subtractFromBaseValue(baseValue, lowerLimitOfBlock, distanceOfBaseValueFromMiddleLimit);
        } else {
            result = addToBaseValue(lowerLimitOfBlock, lowerLimitOfBlock, distanceFromLowerLimit);
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
     * @param baseValue
     * @param lowerValue
     * @param distanceOfLowerValueFromBaseValue
     * @return roman numeral expression
     */
    private String subtractFromBaseValue(int baseValue, int lowerValue, int distanceOfLowerValueFromBaseValue) {
        String result = "";
        for(int i=1; i<= distanceOfLowerValueFromBaseValue; i++) {
            result += romanNumbers.get(lowerValue);
        }
        result += romanNumbers.get(baseValue);
        return result;
    }

    /**
     *
     * @param baseValue
     * @param lowerValue
     * @param distanceFromBaseValue
     * @return roman numeral expression
     */
    private String addToBaseValue(int baseValue, int lowerValue, int distanceFromBaseValue) {
        String result = romanNumbers.get(baseValue);
        for(int i = 1; i<= distanceFromBaseValue; i++) {
            result += romanNumbers.get(lowerValue);
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
     * Roman numerals have an interior structure of "blocks"
     * e.g. the 1000 block contains 1000, 500 and 100.
     * Because, if we want to convert an arabic value of 654 we see that 600 is in the upper half
     * of the block (600 is in the range 1000 - 500).
     * 50 is in the block 100, 50, 10 => 50 is the lowest number of the "upper" half i.e. the range 100 - 50.
     * and
     * 4 is in the block 10, 5, 1 => 4 is in the "lower" half of the block.
     *
     * To convert e.g. 321, we see that 300 is in the range 500 - 100, the "lower" half of the block 1000,500,100.
     *
     * In order to compute roman numbers in the subtracting / additive way, we have to know these blocks and the boundaries
     * at which we have to change the base value of the computation.
     *
     * In the example above: 654 has the baseValue 500, and we add 100 to compute the roman representation.
     */
    class Block {
        int upperLimit;
        int middleLimit;
        int lowerLimit;

        public Block(int upper, int middle, int lower) {
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

    private Map getBlocks() {
        return this.blocks;
    }

    private Block getBlock1000() {
        return this.blocks.get(1000);
    }
    private Block getBlock100() {
        return this.blocks.get(100);
    }
    private Block getBlock10() {
        return this.blocks.get(10);
    }
    private Block getBlock1() {
        return this.blocks.get(1);
    }
}

