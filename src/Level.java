/**
 * A base class for all levels.
 *
 * @author Robin Machiels
 * @author Reinaert Van de Cruys
 */
public abstract class Level {
    protected static final String FIND_AT_LEAST_TWO_TREASURES =
            "constraintFindAtLeastTwoTreasures.aut";
    protected static final String FIND_AT_LEAST_TWO_TREASURES_AND_LOSE_ALL_WHEN_PASSING_THROUGH_ARC =
            "constraintFindAtLeastTwoTreasuresAndLoseAllWhenPassingThroughArc.aut";
    protected static final String FIND_KEY_BEFORE_PASSING_THROUGH_GATES =
            "constraintFindKeyBeforePassingThroughGates.aut";
    protected static final String FIND_NO_TREASURES_AFTER_DRAGON_HAS_BEEN_PASSED =
            "constraintFindNoTreasuresAfterDragonHasBeenPassed.aut";
    protected static final String JUMP_IN_RIVER_WHEN_PASSING_DRAGON_WITHOUT_SWORD =
            "constraintJumpInRiverWhenPassingDragonWithoutSword.aut";

    /**
     * Returns an automaton equal to the given automaton with the constraints of this level applied to it.
     *
     * @param aut the automaton to apply the constraints to
     * @return an automaton equal to the given automaton with the constraints of this level applied to it
     * @throws Exception if (one of) the constraints could not be loaded or parsed
     */
    protected abstract Automaton applyConstraints(Automaton aut) throws Exception;

    /**
     * Returns the automaton corresponding to the given <code>.aut</code> file.
     *
     * @param filename the name of the file to read
     * @return the automaton corresponding to the given file
     * @throws Exception if the file could not be read or is corrupted
     */
    protected final Automaton loadAutomaton(String filename) throws Exception {
        // attempt to read in the given file
        AutomatonParser automatonParser;
        try {
            automatonParser = new AutomatonParser(filename);
        } catch (Exception e) {
            System.out.println("Error: The file '" + filename + "' cannot be read.");
            throw e;
        }
        // attempt to parse the file
        try {
            automatonParser.parse();
        } catch (Exception e) {
            System.out.println("Error: The file '" + filename + "' seems to be corrupted.");
            throw e;
        }
        // return the finished automaton
        return automatonParser.automaton();
    }

    /**
     * Runs this level with the given arguments.
     *
     * @param args the arguments, of which the first one can be the filename of the <code>.aut</code> file representing
     *             the automaton to check, any other arguments are ignored
     */
    protected final void run(String[] args) {
        // if no filename is given (as the first command line argument), default to 'adventure.aut'
        String filename;
        if (args.length >= 1) {
            filename = args[0];
        } else {
            filename = "adventure.aut";
        }
        // load the automaton, apply the constraints and print the shortest accepted string
        try {
            Automaton aut = loadAutomaton(filename);
            System.out.println(applyConstraints(aut).getShortestExample(true));
        } catch (Exception e) {
            // loadAutomaton already prints an error, no need to do anything here
        }
    }
}
