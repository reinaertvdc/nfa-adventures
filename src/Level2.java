/**
 * Checks if the language accepted by a given automaton is non-empty on Cohan Level.
 *
 * @author Reinaert Van de Cruys
 */
public class Level2 extends Level {
    /**
     * Entry point of the program.
     *
     * @param args the arguments, of which the first one must be the filename of the '.aut' file representing the
     *             automaton to check, any other arguments are ignored
     */
    public static void main(String[] args) {
        new Level2().run(args);
    }

    @Override
    protected final Automaton applyConstraints(Automaton aut) {
        aut = aut.intersection(constraintFindKeyBeforePassingThroughGates());
        aut = aut.intersection(constraintJumpInRiverWhenPassingDragonWithoutSword());
        aut = aut.intersection(constraintFindNoTreasuresAfterDragonHasBeenPassed());
        aut = aut.intersection(constraintFindAtLeastTwoTreasuresAndLoseAllWhenPassingThroughArc());
        return aut;
    }
}
