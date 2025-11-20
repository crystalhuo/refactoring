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
        int totalAmount = 0;
        int volumeCredits = 0;

        final StringBuilder result = new StringBuilder(
                "Statement for " + invoice.getCustomer() + System.lineSeparator());
        final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

        for (Performance p : invoice.getPerformances()) {
            final Play play = plays.get(p.getPlayID());

            int thisAmount = 0;

            switch (play.getType()) {
                case "tragedy":
                    thisAmount = Constants.TRAGEDY_BASE_AMOUNT;
                    if (p.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                                * (p.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                    }
                    break;

                case "comedy":
                    thisAmount = Constants.COMEDY_BASE_AMOUNT;
                    if (p.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                                + Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                                * (p.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD);
                    }
                    thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * p.getAudience();
                    break;

                default:
                    throw new RuntimeException(
                            String.format("unknown type: %s", play.getType()));
            }

            // add volume credits
            volumeCredits += Math.max(p.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);

            // extra credit for every five comedy attendees
            if ("comedy".equals(play.getType())) {
                volumeCredits += p.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
            }

            // append line for this performance
            result.append(String.format(
                    "  %s: %s (%s seats)%n",
                    play.getName(),
                    frmt.format(thisAmount / (double) Constants.PERCENT_FACTOR),
                    p.getAudience()));

            totalAmount += thisAmount;
        }

        result.append(String.format(
                "Amount owed is %s%n",
                frmt.format(totalAmount / (double) Constants.PERCENT_FACTOR)));
        result.append(String.format("You earned %s credits%n", volumeCredits));

        return result.toString();
    }
}
