package theater;

/**
 * Class representing a performance of a play.
 */
public class Performance {

    private final String playID;
    private final int audience;

    /**
     * Creates a performance with the given play ID and audience size.
     *
     * @param playID   the ID of the play being performed
     * @param audience the number of audience members
     */
    public Performance(String playID, int audience) {
        this.playID = playID;
        this.audience = audience;
    }

    /**
     * This method will return the value of Play ID.
     *
     * @return the play ID
     */
    public String getPlayID() {
        return playID;
    }

    /**
     * Returns the number of audience members for this performance.
     *
     * @return the audience size
     */
    public int getAudience() {
        return audience;
    }
}
