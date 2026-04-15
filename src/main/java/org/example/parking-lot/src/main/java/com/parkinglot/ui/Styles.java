package com.parkinglot.ui;

/**
 * Central place for all inline style constants.
 */
public final class Styles {
    private Styles() {}

    public static final String BG_MAIN   = "#1e1e2e";
    public static final String BG_CARD   = "#2a2a3e";
    public static final String BG_INPUT  = "#313145";
    public static final String ACCENT    = "#7c3aed";
    public static final String ACCENT_HOVER = "#6d28d9";
    public static final String SUCCESS   = "#22c55e";
    public static final String ERROR     = "#ef4444";
    public static final String WARNING   = "#f59e0b";
    public static final String TEXT      = "#e2e8f0";
    public static final String TEXT_MUTED = "#94a3b8";
    public static final String BORDER    = "#3f3f5a";

    // Spot colors
    public static final String SPOT_FREE        = "#166534";  // dark green
    public static final String SPOT_FREE_TEXT   = "#22c55e";
    public static final String SPOT_OCCUPIED    = "#7f1d1d";  // dark red
    public static final String SPOT_OCCUPIED_TEXT = "#ef4444";
    public static final String SPOT_ELECTRIC    = "#1e3a5f";  // dark blue
    public static final String SPOT_ELECTRIC_TEXT = "#60a5fa";
    public static final String SPOT_HANDICAPPED = "#78350f";  // dark yellow
    public static final String SPOT_HANDICAPPED_TEXT = "#f59e0b";

    public static String card() {
        return "-fx-background-color:" + BG_CARD + "; -fx-background-radius:10;";
    }

    public static String button(String bg, String fg) {
        return "-fx-background-color:" + bg + "; -fx-text-fill:" + fg +
               "; -fx-font-weight:bold; -fx-background-radius:6; -fx-cursor:hand;" +
               " -fx-padding:8 18 8 18; -fx-font-size:13px;";
    }

    public static String accentButton() {
        return button(ACCENT, "white");
    }

    public static String dangerButton() {
        return button(ERROR, "white");
    }

    public static String successButton() {
        return button(SUCCESS, "white");
    }

    public static String input() {
        return "-fx-background-color:" + BG_INPUT + "; -fx-text-fill:" + TEXT +
               "; -fx-prompt-text-fill:" + TEXT_MUTED +
               "; -fx-border-color:" + BORDER + "; -fx-border-radius:6;" +
               " -fx-background-radius:6; -fx-padding:8; -fx-font-size:13px;";
    }

    public static String label() {
        return "-fx-text-fill:" + TEXT + "; -fx-font-size:13px;";
    }

    public static String labelMuted() {
        return "-fx-text-fill:" + TEXT_MUTED + "; -fx-font-size:12px;";
    }

    public static String title() {
        return "-fx-text-fill:" + TEXT + "; -fx-font-size:18px; -fx-font-weight:bold;";
    }

    public static String sectionTitle() {
        return "-fx-text-fill:" + TEXT_MUTED + "; -fx-font-size:11px; -fx-font-weight:bold;";
    }

    public static String tableView() {
        return "-fx-background-color:" + BG_CARD + "; -fx-text-fill:" + TEXT +
               "; -fx-border-color:" + BORDER + "; -fx-border-radius:8; -fx-background-radius:8;";
    }

    public static String tabPane() {
        return "-fx-background-color:" + BG_MAIN + "; -fx-tab-min-width:110px;";
    }

    public static String mainBackground() {
        return "-fx-background-color:" + BG_MAIN + ";";
    }
}
