/**
 * A base class for all levels.
 *
 * @author Reinaert Van de Cruys
 */
public abstract class Level {
    /**
     * Returns an automaton equal to the given automaton with the constraints of this level applied to it.
     *
     * @param aut the automaton to apply the constraints to
     * @return an automaton equal to the given automaton with the constraints of this level applied to it
     */
    protected abstract Automaton applyConstraints(Automaton aut);

    /**
     * Runs this level with the given arguments.
     *
     * @param args the arguments, of which the first one must be the filename of the '.aut' file representing the
     *             automaton to check, any other arguments are ignored
     */
    protected final void run(String[] args) {
        if (args.length < 1) {
            System.out.println("Error: The first argument must be the path to a '.aut' file.");
            return;
        }
        try {
            AutomatonParser automatonParser = new AutomatonParser(args[0]);
            automatonParser.parse();
            System.out.println(applyConstraints(automatonParser.automaton()).getShortestExample(true));
        } catch (Exception e) {
            System.out.println("Error: The given file is not a valid '.aut' file.");
        }
    }
}
