package com.parkinglot.ui;

/**
 * Central place for all inline style constants.
 * Palette: #020202 · #0D2818 · #04471C · #0A9548 · #16DB65
 */
public final class Styles {
    private Styles() {}

    // ── Core palette ──────────────────────────────────────────────────────────
    public static final String BG_MAIN      = "#020202";   // near-black — window background
    public static final String BG_CARD      = "#0D2818";   // dark green — cards / panels
    public static final String BG_INPUT     = "#04471C";   // mid-dark green — inputs / secondary
    public static final String ACCENT       = "#0A9548";   // medium green — primary accent
    public static final String ACCENT_HOVER = "#0b7d3d";   // slightly darker green for hover
    public static final String SUCCESS      = "#16DB65";   // bright green — success / free spots
    public static final String ERROR        = "#ef4444";   // red — errors (kept for readability)
    public static final String WARNING      = "#f59e0b";   // amber — warnings
    public static final String TEXT         = "#e2f5ea";   // near-white with green tint
    public static final String TEXT_MUTED   = "#6db88a";   // muted green-grey
    public static final String BORDER       = "#04471C";   // dark green border

    // ── Spot colors ───────────────────────────────────────────────────────────
    public static final String SPOT_FREE          = "#04471C";
    public static final String SPOT_FREE_TEXT     = "#16DB65";
    public static final String SPOT_OCCUPIED      = "#7f1d1d";
    public static final String SPOT_OCCUPIED_TEXT = "#ef4444";
    public static final String SPOT_ELECTRIC      = "#0a3d6b";
    public static final String SPOT_ELECTRIC_TEXT = "#60a5fa";
    public static final String SPOT_HANDICAPPED   = "#78350f";
    public static final String SPOT_HANDICAPPED_TEXT = "#f59e0b";

    // ── Style helpers ─────────────────────────────────────────────────────────

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
        return button(SUCCESS, "#020202");
    }

    public static String input() {
        return "-fx-background-color:" + BG_INPUT + "; -fx-text-fill:" + TEXT +
               "; -fx-prompt-text-fill:" + TEXT_MUTED +
               "; -fx-border-color:" + BORDER + "; -fx-border-radius:6;" +
               " -fx-background-radius:6; -fx-padding:8; -fx-font-size:13px;";
    }

    public static String comboBox() {
        return "-fx-background-color:" + BG_INPUT + ";" +
               " -fx-text-fill: white;" +
               " -fx-prompt-text-fill:#a0c8a0;" +
               " -fx-border-color:" + BORDER + "; -fx-border-radius:6;" +
               " -fx-background-radius:6; -fx-font-size:13px;";
    }

    /** @deprecated use comboBox() — ChoiceBox replaced with ComboBox */
    public static String choiceBox() {
        return comboBox();
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
