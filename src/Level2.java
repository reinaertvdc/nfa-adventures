/**
 * Prints the shortest string accepted by a given automaton on Cohan Level.
 *
 * @author Robin Machiels
 * @author Reinaert Van de Cruys
 */
public class Level2 extends Level {
    /**
     * Entry point of the program.
     *
     * @param args the arguments, of which the first one can be the filename of the <code>.aut</code> file representing
     *             the automaton to check, any other arguments are ignored
     */
    public static void main(String[] args) {
        new Level2().run(args);
    }

    @Override
    protected final Automaton applyConstraints(Automaton aut) throws Exception {
        // return the intersection of the given automaton with all the constraints of this level
        return aut
                .intersection(loadAutomaton(FIND_KEY_BEFORE_PASSING_THROUGH_GATES))
                .intersection(loadAutomaton(JUMP_IN_RIVER_WHEN_PASSING_DRAGON_WITHOUT_SWORD))
                .intersection(loadAutomaton(FIND_NO_TREASURES_AFTER_DRAGON_HAS_BEEN_PASSED))
                .intersection(loadAutomaton(FIND_AT_LEAST_TWO_TREASURES_AND_LOSE_ALL_WHEN_PASSING_THROUGH_ARC));
    }
}
