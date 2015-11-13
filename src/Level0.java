/**
 * Checks if the language accepted by a given automaton is non-empty in God Mode.
 *
 * @author Reinaert Van de Cruys
 */
public class Level0 extends Level {
    /**
     * Entry point of the program.
     *
     * @param args the arguments, of which the first one must be the filename of the '.aut' file representing the
     *             automaton to check, any other arguments are ignored
     */
    public static void main(String[] args) {
        new Level0().run(args);
    }

    @Override
    protected final Automaton applyConstraints(Automaton aut) {
        return aut;
    }
}
