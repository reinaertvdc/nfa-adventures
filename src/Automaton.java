/**
 * Represents a finite automaton.
 *
 * @author Reinaert Van de Cruys
 */
public class Automaton {
    /**
     * Returns the shortest string that is (not) accepted by this automaton.
     *
     * @param accept <code>true</code> to return the shortest accepted string, <code>false</code> to return the
     *               shortest string that is not accepted
     * @return the requested string, or <code>null</code> if no such string exists
     */
    public String getShortestExample(Boolean accept) {
        // TODO: 2015-11-12 implement
        return null;
    }

    /**
     * Returns an automaton accepting the intersection of the languages accepted by this and the given automaton.
     *
     * @param aut the automaton accepting the language to take the intersection with
     * @return an automaton accepting the intersection of the languages accepted by this and the given automaton
     */
    public Automaton intersection(Automaton aut) {
        // TODO: 2015-11-12 implement
        return null;
    }
}
