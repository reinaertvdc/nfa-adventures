/**
 * Prints the shortest string accepted by a given automaton in God Mode.
 *
 * @author Robin Machiels
 * @author Reinaert Van de Cruys
 */
public class Level0 extends Level {
    /**
     * Entry point of the program.
     *
     * @param args the arguments, of which the first one can be the filename of the <code>.aut</code> file representing
     *             the automaton to check, any other arguments are ignored
     */
    public static void main(String[] args) {
        new Level0().run(args);
    }

    @Override
    protected final Automaton applyConstraints(Automaton aut) throws Exception {
        // there are no constraints in this level, just return the given automaton without any modifications
        return aut;
    }
}
