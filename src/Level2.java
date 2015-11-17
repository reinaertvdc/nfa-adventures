/**
 * Checks if the language accepted by a given automaton is non-empty on Cohan Level.
 *
 * @author Robin Machiels
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
        return aut
                .intersection(constraintFindKeyBeforePassingThroughGates())
                .intersection(constraintJumpInRiverWhenPassingDragonWithoutSword())
                .intersection(constraintFindNoTreasuresAfterDragonHasBeenPassed())
                .intersection(constraintFindAtLeastTwoTreasuresAndLoseAllWhenPassingThroughArc());
    }
}
