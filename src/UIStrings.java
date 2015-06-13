/**
 * Strings used by the GUI such as tooltips and errors.
 */
public class UIStrings {
    // Tooltips
    public static final String startButtonTip = "Add a start point";
    public static final String endButtonTip = "Add an end point";
    public static final String blockButtonTip = "Block a point";
    public static final String noneButtonTip = "Reset a point";
    public static final String navigateButtonTip = "Calculate the path between"
                                                   + " the start and end"
                                                   + " points";
    public static final String resetButtonTip = "Reset all the points to an"
                                                + " empty state";

    // Errors
    public static final String sameStartEndPoints = "The end point cannot be"
                                                    + " the same as the start"
                                                    + " point";
    public static final String missingStartEndPoint = "Both a start and end"
                                                      + " point must be chosen";
    public static final String unreachableEndpoint = "The endpoint is"
                                                     + " unreachable";
    public static final String blockedStartEndPoint = "The start and end"
                                                      + " points cannot be"
                                                      + " blocked";
}


