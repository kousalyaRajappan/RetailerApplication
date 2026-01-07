package com.za.toptitup.loginlibrary;

public class WifiSignalUtils {

    private static final int MIN_SIGNAL_STRENGTH = -100; // Minimum expected dBm value
    private static final int MAX_SIGNAL_STRENGTH = -50;  // Maximum expected dBm value

    /**
     * Converts dBm value to a percentage.
     *
     * @param dBm The signal strength in dBm.
     * @return The signal strength as a percentage.
     */
    public static int dBmToPercentage(int dBm) {
        if (dBm <= MIN_SIGNAL_STRENGTH) {
            return 0; // No signal
        } else if (dBm >= MAX_SIGNAL_STRENGTH) {
            return 100; // Full signal
        } else {
            return (int) (((dBm - MIN_SIGNAL_STRENGTH) / (float) (MAX_SIGNAL_STRENGTH - MIN_SIGNAL_STRENGTH)) * 100);
        }
    }
}
