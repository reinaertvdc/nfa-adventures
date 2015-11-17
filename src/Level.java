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
     * @throws Exception if the constraints could not be applied
     */
    protected abstract Automaton applyConstraints(Automaton aut) throws Exception;

    /**
     * Returns an automaton accepting only the paths on which at least two treasures are found.
     *
     * @return an automaton representing the constraint
     * @throws Exception if the automaton representing the constraint could not be created
     */
    protected final Automaton constraintFindAtLeastTwoTreasures() throws Exception {
        AutomatonParser p = new AutomatonParser("constraintFindAtLeastTwoTreasures.aut");
        p.parse();
        return p.automaton();
        /*Automaton.Builder builder = new Automaton.Builder();
        String t0 = "t0", t1 = "t1", t2 = "t2";
        builder.setStartState(t0);
        builder.addTransition(t0, t1, Automaton.TREASURE);
        builder.addTransition(t1, t2, Automaton.TREASURE);
        builder.addTransitionsOnRemainingSymbols(t0, t0);
        builder.addTransitionsOnRemainingSymbols(t1, t1);
        builder.addTransitionsOnRemainingSymbols(t2, t2);
        builder.addAcceptState(t2);
        return builder.getResult();*/
    }

    /**
     * Returns an automaton accepting only the paths on which at least two treasures are found and, while all treasures
     * are lost when passing through an arc.
     *
     * @return an automaton representing the constraint
     * @throws Exception if the automaton representing the constraint could not be created
     */
    protected final Automaton constraintFindAtLeastTwoTreasuresAndLoseAllWhenPassingThroughArc() throws Exception {
        AutomatonParser p = new AutomatonParser("constraintFindAtLeastTwoTreasuresAndLoseAllWhenPassingThroughArc.aut");
        p.parse();
        return p.automaton();
        /*Automaton.Builder builder = new Automaton.Builder();
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
        return builder.getResult();*/
    }

    /**
     * Returns an automaton accepting only the paths on which a key is found before passing through any gates.
     *
     * @return an automaton representing the constraint
     * @throws Exception if the automaton representing the constraint could not be created
     */
    protected final Automaton constraintFindKeyBeforePassingThroughGates() throws Exception {
        AutomatonParser p = new AutomatonParser("constraintFindKeyBeforePassingThroughGates.aut");
        p.parse();
        return p.automaton();
        /*Automaton.Builder builder = new Automaton.Builder();
        String k0 = "k0", k1 = "k1";
        builder.setStartState(k0);
        builder.addTransition(k0, k1, Automaton.KEY);
        builder.addTransitionsOnRemainingSymbols(k0, k0);
        builder.addTransitionsOnRemainingSymbols(k1, k1);
        builder.removeTransitionsOnSymbol(k0, Automaton.GATE);
        builder.addAcceptState(k0);
        builder.addAcceptState(k1);
        return builder.getResult();*/
    }

    /**
     * Returns an automaton accepting only the paths on which no treasures are found once a dragon has been passed.
     *
     * @return an automaton representing the constraint
     * @throws Exception if the automaton representing the constraint could not be created
     */
    protected final Automaton constraintFindNoTreasuresAfterDragonHasBeenPassed() throws Exception {
        AutomatonParser p = new AutomatonParser("constraintFindNoTreasuresAfterDragonHasBeenPassed.aut");
        p.parse();
        return p.automaton();
        /*Automaton.Builder builder = new Automaton.Builder();
        String d0 = "d0", d1 = "d1";
        builder.setStartState(d0);
        builder.addTransition(d0, d1, Automaton.DRAGON);
        builder.addTransitionsOnRemainingSymbols(d0, d0);
        builder.addTransitionsOnRemainingSymbols(d1, d1);
        builder.removeTransitionsOnSymbol(d1, Automaton.TREASURE);
        builder.addAcceptState(d0);
        builder.addAcceptState(d1);
        return builder.getResult();*/
    }

    /**
     * Returns an automaton accepting only the paths on which a river is passed immediately after every dragon when a
     * sword in not found first.
     *
     * @return an automaton representing the constraint
     * @throws Exception if the automaton representing the constraint could not be created
     */
    protected final Automaton constraintJumpInRiverWhenPassingDragonWithoutSword() throws Exception {
        AutomatonParser p = new AutomatonParser("constraintJumpInRiverWhenPassingDragonWithoutSword.aut");
        p.parse();
        return p.automaton();
        /*Automaton.Builder builder = new Automaton.Builder();
        String s0 = "s0", s1 = "s1", f = "f";
        builder.setStartState(s0);
        builder.addTransition(s0, s1, Automaton.SWORD);
        builder.addTransition(s0, f, Automaton.DRAGON);
        builder.addTransition(f, s0, Automaton.RIVER);
        builder.addTransitionsOnRemainingSymbols(s0, s0);
        builder.addTransitionsOnRemainingSymbols(s1, s1);
        builder.addAcceptState(s0);
        builder.addAcceptState(s1);
        return builder.getResult();*/
    }

    /**
     * Runs this level with the given arguments.
     *
     * @param args the arguments, of which the first one must be the filename of the '.aut' file representing the
     *             automaton to check, any other arguments are ignored
     */
    protected final void run(String[] args) {
        String filename;
        if (args.length >= 1) {
            filename = args[0];
        } else {
            filename = "adventure.aut";
        }
        AutomatonParser automatonParser;
        try {
            automatonParser = new AutomatonParser(filename);
        } catch (Exception e) {
            System.out.println("Error: The file '" + filename + "' cannot be read, exiting...");
            return;
        }
        try {
            automatonParser.parse();
        } catch (Exception e) {
            System.out.println("Error: The file '" + filename + "' is corrupted, exiting...");
            return;
        }
        Automaton aut;
        try {
            aut = applyConstraints(automatonParser.automaton());
        } catch (Exception e) {
            System.out.println("Error: The constraints could not be applied, is one of the constraint files missing?");
            return;
        }
        System.out.println(aut.getShortestExample(true));
    }
}
