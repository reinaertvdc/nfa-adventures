import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds an automaton from a <code>.aut</code> file.
 *
 * @author Robin Machiels
 * @author Reinaert Van de Cruys
 */
public class AutomatonParser {
    /**
     * the builder used to parse the automaton
     */
    private final Automaton.Builder mBuilder = new Automaton.Builder();
    /**
     * the file from which the automaton is parsed, every entry representing the next line in the file
     */
    private final List<String> mSourceFile = new ArrayList<>();
    /**
     * the automaton parsed by this parser
     */
    private Automaton mResult = null;

    /**
     * Reads in the given <code>.aut</code> file.
     *
     * @param filename the name of the <code>.aut</code> file to build an automaton from
     * @throws Exception if the given file cannot be read
     */
    public AutomatonParser(String filename) throws Exception {
        // open the given file
        BufferedReader br = new BufferedReader(new FileReader(filename));
        // read the file line by line
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            mSourceFile.add(currentLine);
        }
        // close the file
        br.close();
    }

    /**
     * Returns the parsed automaton.
     *
     * @return the automaton built by <code>parse()</code> from the <code>.aut</code> file given to the constructor, or
     * a null pointer if <code>parse()</code> has not yet been successfully executed
     */
    public Automaton automaton() {
        return mResult;
    }

    /**
     * Builds an automaton from the <code>.aut</code> file given to the constructor.
     *
     * @throws Exception if the file given to the constructor is not a valid <code>.aut</code> file
     */
    public void parse() throws Exception {
        // run over every line in the source file
        for (String currentLine : mSourceFile) {
            // divide the line into its components, which are chunks of characters separated by one or more spaces
            String[] components = currentLine.trim().split("\\s+");
            // If the line does not have 3 components, it is invalid, and if the first component starts with '#', it is
            // a comment. Ignore the line in both cases.
            if (components.length != 3 || components[0].charAt(0) == '#') {
                continue;
            }
            // otherwise, parse the components
            parse(components);
        }
        // return the finished automaton
        mResult = mBuilder.getResult();
    }

    /**
     * Parses the given components of the line of a <code>.aut</code> file.
     *
     * @param components the components to parse
     * @throws Exception if the components are not valid
     */
    private void parse(String[] components) throws Exception {
        final String START = "(START)", T_LEFT = "|-", FINAL = "(FINAL)", T_RIGHT = "-|", EPSILON = "$";
        if (components[2].equals(FINAL) && components[1].equals(T_RIGHT)) {
            // if the last two components are '-|' and '(FINAL)', the first component is an accept state
            mBuilder.addAcceptState(components[0]);
        } else if (components[0].equals(START) && components[1].equals(T_LEFT)) {
            // if the first two components are '(START)' and '|-', the last component is the start state
            mBuilder.setStartState(components[2]);
        } else if (components[1].equals(EPSILON)) {
            // if the middle component is '$', there is an epsilon transition from the first to the last component
            mBuilder.addTransition(components[0], components[2], null);
        } else {
            // otherwise, there is a transition from the first to the last component on the symbol in the middle one
            mBuilder.addTransition(components[0], components[2], components[1]);
        }
    }
}
