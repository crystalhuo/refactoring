package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Generates a formatted statement for a given invoice of performances.
 * <p>
 * This class calculates the total amount and volume credits for an invoice,
 * based on the type and audience of each performance.
 * </p>
 */
public class StatementPrinter {

    private final Invoice invoice;
    private final Map<String, Play> plays;

    /**
     * Creates a StatementPrinter for the given invoice and plays map.
     *
     * @param invoice the invoice to print
     * @param plays   the map of play IDs to Play objects
     */
    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement for the invoice.
     *
     * @return the formatted statement
     * @throws RuntimeException if a play type is unknown
     */
    public String statement() {
        final StringBuilder result = new StringBuilder(
                "Statement for " + invoice.getCustomer() + System.lineSeparator());

        for (Performance performance : invoice.getPerformances()) {
            result.append(String.format(
                    "  %s: %s (%s seats)%n",
                    getPlay(performance).getName(),
                    usd(getAmount(performance)),
                    performance.getAudience()));
        }

        final int totalAmount = getTotalAmount();
        final int volumeCredits = getTotalVolumeCredits();

        result.append(String.format("Amount owed is %s%n", usd(totalAmount)));
        result.append(String.format("You earned %s credits%n", volumeCredits));

        return result.toString();
    }

    /**
     * Computes the total volume credits for the invoice.
     *
     * @return total volume credits
     */
    public int getTotalVolumeCredits() {
        int result = 0;
        for (Performance performance : invoice.getPerformances()) {
            result += getVolumeCredits(performance);
        }
        return result;
    }

    /**
     * Computes the total amount owed for the invoice.
     *
     * @return total amount in cents
     */
    public int getTotalAmount() {
        int result = 0;
        for (Performance performance : invoice.getPerformances()) {
            result += getAmount(performance);
        }
        return result;
    }

    /**
     * Computes the volume credits for a single performance.
     *
     * @param performance the performance
     * @return volume credits
     */
    public int getVolumeCredits(Performance performance) {
        int result = 0;
        result += Math.max(performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        if ("comedy".equals(getPlay(performance).getType())) {
            result += performance.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    /**
     * Retrieves the play associated with a performance.
     *
     * @param performance the performance
     * @return the play
     */
    public Play getPlay(Performance performance) {
        return plays.get(performance.getPlayID());
    }

    /**
     * Computes the cost for a single performance.
     *
     * @param performance the performance
     * @return amount in cents
     */
    public int getAmount(Performance performance) {
        final Play play = getPlay(performance);
        int thisAmount = 0;

        switch (play.getType()) {
            case "tragedy":
                thisAmount = Constants.TRAGEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;

            case "comedy":
                thisAmount = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD);
                }
                thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * performance.getAudience();
                break;

            default:
                throw new RuntimeException(
                        String.format("unknown type: %s", play.getType()));
        }
        return thisAmount;
    }

    /**
     * Converts an amount in cents to US currency format.
     *
     * @param amount the amount in cents
     * @return formatted currency string
     */
    public String usd(int amount) {
        return NumberFormat.getCurrencyInstance(Locale.US)
                .format(amount / (double) Constants.PERCENT_FACTOR);
    }
}
