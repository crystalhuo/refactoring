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

        for (Performance p : invoice.getPerformances()) {
            result.append(String.format(
                    "  %s: %s (%s seats)%n",
                    getPlay(p).getName(),
                    usd(getAmount(p)),
                    p.getAudience()));
        }

        int totalAmount = getTotalAmount();
        int volumeCredits = getTotalVolumeCredits();

        result.append(String.format(
                "Amount owed is %s%n",
                usd(totalAmount)));
        result.append(String.format("You earned %s credits%n", volumeCredits));

        return result.toString();
    }

    public int getTotalVolumeCredits() {
        int result = 0;
        for (Performance p : invoice.getPerformances()) {
            result += getVolumeCredits(p);
        }
        return result;
    }

    public int getTotalAmount() {
        int result = 0;
        for (Performance p : invoice.getPerformances()) {
            result += getAmount(p);
        }
        return result;
    }

    public int getVolumeCredits(Performance p) {
        int result = 0;
        result += Math.max(p.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        if ("comedy".equals(getPlay(p).getType())) {
            result += p.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    public Play getPlay(Performance p) {
        return plays.get(p.getPlayID());
    }

    public int getAmount(Performance p) {
        Play play = getPlay(p);
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
        return thisAmount;
    }

    public String usd(int amount) {
        return NumberFormat.getCurrencyInstance(Locale.US)
                .format(amount / (double) Constants.PERCENT_FACTOR);
    }
}
