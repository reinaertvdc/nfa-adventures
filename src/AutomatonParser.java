import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds an automaton from a '.aut' file.
 *
 * @author Reinaert Van de Cruys
 */
public class AutomatonParser {
    /**
     * the automaton parsed by this parser
     */
    private Automaton mResult = null;

    /**
     * the file from which the automaton is parsed, every entry representing the next line in the file
     */
    private List<String> mSourceFile = new ArrayList<>();

    /**
     * Reads in the given '.aut' file.
     *
     * @param filename the name of the '.aut' file to build an automaton from
     * @throws Exception if the given file cannot be read
     */
    public AutomatonParser(String filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            mSourceFile.add(currentLine);
        }
        br.close();
    }

    /**
     * Returns the parsed automaton.
     *
     * @return the automaton built by <code>parse()</code> from the '.aut' file given to the constructor, or a null
     * pointer if <code>parse()</code> has not yet been successfully executed
     */
    public Automaton automaton() {
        return mResult;
    }

    /**
     * Builds an automaton from the '.aut' file given to the constructor.
     *
     * @throws Exception if the file given to the constructor is not a valid '.aut' file
     */
    public void parse() throws Exception {
        final String START = "(START)", T_LEFT = "|-", FINAL = "(FINAL)", T_RIGHT = "-|";
        Automaton.Builder builder = new Automaton.Builder();
        for (String currentLine : mSourceFile) {
            String[] components = currentLine.split("\\s+");
            if (components[2].equals(FINAL) && components[1].equals(T_RIGHT)) {
                builder.addAcceptState(components[0]);
            } else if (components[0].equals(START) && components[1].equals(T_LEFT)) {
                builder.setStartState(components[2]);
            } else {
                builder.addTransition(components[0], components[2], components[1]);
            }
        }
        mResult = builder.getResult();
    }
}
