/**
 * A base class for all levels.
 *
 * @author Robin Machiels
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
     * Returns an automaton accepting only the paths on which at least two treasures are found.
     *
     * @return an automaton representing the constraint
     */
    protected final Automaton constraintFindAtLeastTwoTreasures() {
        Automaton.Builder builder = new Automaton.Builder();
        String t0 = "t0", t1 = "t1", t2 = "t2";
        builder.setStartState(t0);
        builder.addTransition(t0, t1, Automaton.TREASURE);
        builder.addTransition(t1, t2, Automaton.TREASURE);
        builder.addTransitionsOnRemainingSymbols(t0, t0);
        builder.addTransitionsOnRemainingSymbols(t1, t1);
        builder.addTransitionsOnRemainingSymbols(t2, t2);
        builder.addAcceptState(t2);
        return builder.getResult();
    }

    /**
     * Returns an automaton accepting only the paths on which at least two treasures are found and, while all treasures
     * are lost when passing through an arc.
     *
     * @return an automaton representing the constraint
     */
    protected final Automaton constraintFindAtLeastTwoTreasuresAndLoseAllWhenPassingThroughArc() {
        Automaton.Builder builder = new Automaton.Builder();
        String t0 = "t0", t1 = "t1", t2 = "t2";
        builder.setStartState(t0);
        builder.addTransition(t0, t1, Automaton.TREASURE);
        builder.addTransition(t1, t2, Automaton.TREASURE);
        builder.addTransition(t1, t0, Automaton.ARC);
        builder.addTransition(t2, t0, Automaton.ARC);
        builder.addTransitionsOnRemainingSymbols(t0, t0);
        builder.addTransitionsOnRemainingSymbols(t1, t1);
        builder.addTransitionsOnRemainingSymbols(t2, t2);
        builder.addAcceptState(t2);
        return builder.getResult();
    }

    /**
     * Returns an automaton accepting only the paths on which a key is found before passing through any gates.
     *
     * @return an automaton representing the constraint
     */
    protected final Automaton constraintFindKeyBeforePassingThroughGates() {
        // TODO: 2015-11-13 fix
        return null;
    }

    /**
     * Returns an automaton accepting only the paths on which no treasures are found once a dragon has been passed.
     *
     * @return an automaton representing the constraint
     */
    protected final Automaton constraintFindNoTreasuresAfterDragonHasBeenPassed() {
        // TODO: 2015-11-13 implement
        return null;
    }

    /**
     * Returns an automaton accepting only the paths on which a river is passed immediately after every dragon when a
     * sword in not found first.
     *
     * @return an automaton representing the constraint
     */
    protected final Automaton constraintJumpInRiverWhenPassingDragonWithoutSword() {
        Automaton.Builder builder = new Automaton.Builder();
        String s0 = "s0", s1 = "s1", f = "f";
        builder.setStartState(s0);
        builder.addTransition(s0, s1, Automaton.SWORD);
        builder.addTransition(s0, f, Automaton.DRAGON);
        builder.addTransition(f, s0, Automaton.RIVER);
        builder.addTransitionsOnRemainingSymbols(s0, s0);
        builder.addTransitionsOnRemainingSymbols(s1, s1);
        builder.addAcceptState(s0);
        builder.addAcceptState(s1);
        return builder.getResult();
    }

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
        AutomatonParser automatonParser;
        try {
            automatonParser = new AutomatonParser(args[0]);
        } catch (Exception e) {
            System.out.println("Error: The given file cannot be read. Make sure the path is correct.");
            return;
        }
        try {
            automatonParser.parse();
            System.out.println(applyConstraints(automatonParser.automaton()).getShortestExample(true));
        } catch (Exception e) {
            System.out.println("Error: The given file is not a valid '.aut' file.");
        }
    }
}
