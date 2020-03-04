package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/**
 * The suite of all JUnit tests for the Permutation class. For the purposes of
 * this lab (in order to test) this is an abstract class, but in proj1, it will
 * be a concrete class. If you want to copy your tests for proj1, you can make
 * this class concrete by removing the 4 abstract keywords and implementing the
 * 3 abstract methods.
 *
 *  @author kenny liao
 */
public class PermutationTest {

    /**
     * For this lab, you must use this to get a new Permutation,
     * the equivalent to:
     * new Permutation(cycles, alphabet)
     * @return a Permutation with cycles as its cycles and alphabet as
     * its alphabet
     * @see Permutation for description of the Permutation conctructor
     */
     Permutation getNewPermutation(String cycles, Alphabet alphabet) {
         return new Permutation(cycles, alphabet);
     }

    /**
     * For this lab, you must use this to get a new Alphabet,
     * the equivalent to:
     * new Alphabet(chars)
     * @return an Alphabet with chars as its characters
     * @see Alphabet for description of the Alphabet constructor
     */
     Alphabet getNewAlphabet(String chars) {
         return new Alphabet(chars);
     }

    /**
     * For this lab, you must use this to get a new Alphabet,
     * the equivalent to:
     * new Alphabet()
     * @return a default Alphabet with characters ABCD...Z
     * @see Alphabet for description of the Alphabet constructor
     */
     Alphabet getNewAlphabet() {
         return new Alphabet();
     }

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /** Check that PERM has an ALPHABET whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha,
                           Permutation perm, Alphabet alpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                    c, perm.invert(e));
            int ci = alpha.toInt(c), ei = alpha.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    /* ***** ALPHABET TESTS ***** */
    @Test
    public void testAlphabet() throws EnigmaException{
        Alphabet A = getNewAlphabet("ABCD");
        assertTrue(A.contains('C'));
        assertFalse(A.contains('Z'));
        assertEquals(2, A.toInt('C'));
        assertEquals('A', A.toChar(0));
        assertEquals(4, A.size());
    }
    /* ***** PERMUTATION PARTIAL TESTS ***** */

    @Test
    public void testPermuteperm() {
        Permutation p = getNewPermutation("(YKNEACBD)", getNewAlphabet("KENYABCD"));
        assertEquals(1,p.permute(2));
    }

    @Test
    public void testPermuteperm2(){
        Permutation p = getNewPermutation("(YKNEACBD)", getNewAlphabet("KENYABCD"));
        assertEquals('A',p.permute('E'));
        assertEquals('Y',p.permute('D'));
    }

    @Test(expected = EnigmaException.class)
    public void testPermuteperm3(){
        Permutation p = getNewPermutation("(()", getNewAlphabet("KENYABCD"));
    }

    @Test
    public void testInv() {
        Permutation p = getNewPermutation("(YKNEACB)", getNewAlphabet("KENYABCD"));
        assertEquals('D',p.invert('D'));
        assertEquals('B',p.invert('Y'));
        assertFalse(p.derangement());
    }

    /* ***** Rotors PARTIAL TESTS ***** */
    @Test
    public void rotor1() {
        Permutation p = getNewPermutation("(YKNEACB) (D)", getNewAlphabet("KENYABCD"));
        Rotor R = new Rotor("Namba one", p);
        assertEquals(0, R.setting());
    }

    /* ***** Moving Rotors PARTIAL TESTS ***** */
    @Test
    public void Moving1() {
        Permutation p = getNewPermutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", getNewAlphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        Rotor R = new MovingRotor("Namba one", p, "C");
        assertEquals(0, R.setting());
        R.advance();
        System.out.println(R.convertForward(19));
    }
    /* ***** Fixed Rotors PARTIAL TESTS ***** */
    @Test
    public void FixedRoter1() {
        Permutation p = getNewPermutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", getNewAlphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        Rotor R = new FixedRotor("Namba one", p);
        assertEquals(0, R.setting());
        R.advance();
        System.out.println(R.setting());
    }
    /* ***** Reflectors PARTIAL TESTS ***** */
    @Test
    public void Reflector1() {
        Permutation p = getNewPermutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", getNewAlphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        Rotor R = new Reflector("Namba one", p);
        assertEquals(0, R.setting());
        R.advance();
        System.out.println(R.setting());
    }
    /* ***** Machine PARTIAL TESTS ***** */

    @Test
    public void Machine1() {
        Permutation p = getNewPermutation("(YKNEACB) (D)", getNewAlphabet("KENYABCD"));
        Rotor M = new MovingRotor("Namba two", p, "C");
        Rotor R = new Reflector("Namba one", p);
        Permutation p2 = getNewPermutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", getNewAlphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        Rotor R2 = new MovingRotor("Namba three", p2, "");
        Rotor[] test = {R, M, R2};
        Machine Mach = new Machine(getNewAlphabet("KENYABCD"), 3, 2, Arrays.asList(test));
        Mach.insertRotors(new String[] {"Namba one", "Namba two","Namba three"});
        Mach.setRotors("AQ");
        assertTrue(M.setting() == 4);
        assertTrue(R2.setting() == 16);
        Mach.setPlugboard(p);
    }

    /* ***** Accumulative TESTS ***** */

    @Test
    public void VIDEOEXAMPLE() {
        Permutation PR1 = getNewPermutation("(AE) (BN) (CK) (DQ) (FU) (GY) (HW) (IJ) (LO) (MP) (RX) (SZ) (TV)", getNewAlphabet());
        Rotor R1 = new Reflector("B", PR1);

        Permutation PR2 = getNewPermutation("(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", getNewAlphabet());
        Rotor R2 = new Rotor("Beta", PR2);

        Permutation PR3 = getNewPermutation("(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", getNewAlphabet());
        MovingRotor R3 = new MovingRotor("III", PR3, "V");

        Permutation PR4 = getNewPermutation("(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)", getNewAlphabet());
        MovingRotor R4 = new MovingRotor("IV", PR4, "J");

        Permutation PR5 = getNewPermutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", getNewAlphabet());
        MovingRotor R5 = new MovingRotor("I", PR5, "Q");

        Permutation plugboard = getNewPermutation("(YF) (ZH)", getNewAlphabet());


        Rotor[] test = {R1, R2, R3, R4, R5};
        Machine Mach = new Machine(getNewAlphabet(), 5, 3, Arrays.asList(test));
        Mach.insertRotors(new String[] {"B", "Beta", "III", "IV", "I"});
        Mach.setRotors("AXLE");
        System.out.println(R5.setting());
        Mach.setPlugboard(plugboard);
        assertEquals(25, Mach.convert(24));
    }

    /* ***** PERMUTATION TESTS ***** */
    @Test
    public void checkIdTransform() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        checkPerm("identity", UPPER_STRING, UPPER_STRING, perm, alpha);
    }

    @Test
    public void testInvertChar() throws EnigmaException{
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals('B', p.invert('A'));
        assertEquals('D', p.invert('B'));
    }

    @Test
    public void testSize() {
        Permutation p2 = getNewPermutation("(YKNE) (ACBD)", getNewAlphabet("KENYABCD"));
        assertEquals(8, p2.size());
        Permutation p = getNewPermutation("(YKNEACBD)", getNewAlphabet("KENYABCD"));
        assertEquals(8, p.size());
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() throws EnigmaException{
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        p.invert('F');
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet2() throws EnigmaException{
        Permutation p = getNewPermutation("(BACZ)", getNewAlphabet("ABCD"));
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet4() throws EnigmaException{
        Permutation p = getNewPermutation("(ABCD%dm)", getNewAlphabet("ABCD%dmm"));
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet5() throws EnigmaException{
        Permutation p = getNewPermutation("(ABCD%dm)", getNewAlphabet("ABCD%dm"));
        p.permute(' ');
    }

    @Test
    public void testPermute() {
        Permutation p = getNewPermutation("(YKNEACBD)", getNewAlphabet("KENYABCD"));
        assertEquals(1,p.permute(18));
    }
    @Test
    public void testPermute1() {
        Permutation p = getNewPermutation("(YKNEACBD)", getNewAlphabet("KENYABCD"));
        assertEquals(0,p.permute(-5));
        // 先找在alphabet裡對應的號碼，對應過去，map, 對應回alphabet找號碼
    }
    @Test
    public void testPermute2() {
        Permutation p = getNewPermutation("(YKNEACBD)", getNewAlphabet("KENYABCD"));
        assertEquals(1,p.permute(2));
    }
    @Test
    public void testPermute3() {
        Permutation p = getNewPermutation("(YKNEACBD)", getNewAlphabet("KENYABCD"));
        assertEquals(2,p.permute(0)); //2
    }
    @Test
    public void testPermute4(){
        Permutation p = getNewPermutation("(YKNEACBD)", getNewAlphabet("KENYABCD"));
        assertEquals('A',p.permute('E'));
        assertEquals('Y',p.permute('D'));
    }
    @Test(expected = EnigmaException.class)
    public void testPermute5() throws  EnigmaException{
        Permutation p = getNewPermutation("(YKNEACBD)", getNewAlphabet("KENYABCD"));
        p.permute('Z');
    }

    @Test
    public void testinvert2() {
        Permutation p = getNewPermutation("(YKNEACBD)", getNewAlphabet("KENYABCD"));
        assertEquals(1,p.invert(4));
        assertEquals(3,p.invert(0));
    }
    @Test
    public void testinvert3() {
        Permutation p = getNewPermutation("(YKNEACBD)", getNewAlphabet("KENYABCD"));
        assertEquals(2,p.invert(-7));  // 看alphabet連invert也是從左邊數
    }

    @Test
    public void testinvert4() {
        Permutation p = getNewPermutation("(YKNEACB)(D)", getNewAlphabet("KENYABCD"));
        assertEquals('D',p.invert('D'));
        assertEquals('C',p.invert('B'));
        assertEquals('B',p.invert('Y'));
        assertEquals(2,p.invert(-7));
        assertEquals(7,p.invert(-9));
        assertEquals(3,p.invert(80));
        assertEquals(3,p.invert(800));
        assertEquals(3,p.invert(8000));
        Permutation p2 = getNewPermutation("(YKNEACB)", getNewAlphabet("KENYABC"));
        checkPerm("error", "YKNEACB", "KNEACBY", p2, getNewAlphabet("KENYABC") );
    }

    @Test
    public void testP5() {
        Permutation p = getNewPermutation("(YKNEACB)(D)", getNewAlphabet("KENYABCD"));
        assertEquals('D',p.invert('D'));
        assertEquals('B',p.invert('Y'));
    }

    @Test
    public void test() {
        Permutation p = getNewPermutation("(YKNEACBD)", getNewAlphabet("KENYABCD"));
        assertTrue(p.derangement());
    }

    @Test
    public void testlast() {
        Permutation p = getNewPermutation("(YKNEAC)(BD)", getNewAlphabet("KENYABCD"));
        assertEquals('B',p.invert('D'));
        assertEquals('D',p.invert('B'));
        assertEquals('B',p.permute('D'));
        assertEquals(7,p.invert(5));
        assertEquals(3,p.permute(6));
        assertTrue(p.derangement());
    }
    // FIXME: Add tests here that pass on a correct Permutation and fail on buggy Permutations.
}
