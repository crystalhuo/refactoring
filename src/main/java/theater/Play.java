package theater;

/**
 * Represents a play with a name and type.
 * <p>
 * The type is used to determine pricing or categorization in the theater system.
 * </p>
 */
public class Play {

    private final String name;
    private final String type;

    /**
     * Creates a play with the given name and type.
     *
     * @param name the name of the play
     * @param type the type of the play
     */

    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Returns the name of the play.
     *
     * @return the play name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of the play.
     *
     * @return the type of the play
     */
    public String getType() {
        return type;
    }
}
