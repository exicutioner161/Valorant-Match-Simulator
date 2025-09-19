package com.simulator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * <p>
 * JavaFX application for simulating Valorant matches between two teams.
 * </p>
 *
 * <p>
 * This application provides a graphical user interface for:
 * </p>
 * <ul>
 * <li>Selecting agents for two teams (5 agents each)</li>
 * <li>Choosing a map from available Valorant maps</li>
 * <li>Configuring simulation parameters (number of simulations)</li>
 * <li>Running match simulations and displaying detailed results</li>
 * </ul>
 *
 * <p>
 * The simulator uses team composition analysis to calculate win probabilities
 * based on agent synergies, map compatibility, and team statistics including
 * aggro, control, and midrange ratings.
 * </p>
 *
 * <p>
 * Features:
 * </p>
 * <ul>
 * <li>Real-time progress tracking for long simulations</li>
 * <li>Comprehensive validation of team selections</li>
 * <li>Detailed statistical output including win rates and team analysis</li>
 * <li>Asynchronous simulation execution to maintain UI responsiveness</li>
 * <li>CSS-styled interface with professional appearance</li>
 * </ul>
 *
 * @author exicutioner161
 * @version 0.2.1-alpha
 * @see Main
 * @see TeamComp
 * @see MatchSimulator
 */

public class SimulatorApp extends Application {

    // UI Components
    private ComboBox<String> mapSelector;
    private final List<ComboBox<String>> team1Agents = new ArrayList<>();
    private final List<ComboBox<String>> team2Agents = new ArrayList<>();
    private TextField simulationCountField;
    private TextArea resultsArea;
    private ProgressBar progressBar;
    private Button simulateButton;
    private Label statusLabel;
    private static final String ARIAL_FONT = "Arial";
    private static final Logger logger = Logger.getLogger(SimulatorApp.class.getName());
    private static final NumberFormat numberFormat = NumberFormat.getInstance();

    // Simulation components
    private ExecutorService executorService;

    // Normalization map for agent names (sanitized -> canonical display name)
    private Map<String, String> agentCanonicalMap;

    // Available agents (matching your AgentList)
    private final String[] agents = {
            "Astra", "Breach", "Brimstone", "Chamber", "Clove", "Cypher",
            "Deadlock", "Fade", "Gekko", "Harbor", "Iso", "Jett",
            "KAY/O", "Killjoy", "Neon", "Omen", "Phoenix", "Raze",
            "Reyna", "Sage", "Skye", "Sova", "Tejo", "Viper", "Vyse", "Waylay", "Yoru"
    };

    // Available maps
    private final String[] maps = {
            "Abyss", "Ascent", "Bind", "Breeze", "Corrode", "Fracture", "Haven", "Icebox",
            "Lotus", "Pearl", "Split", "Sunset"
    };

    // (Removed unused clamp helper; using explicit bounds checks where needed.)

    /**
     * <p>
     * Main entry point for the JavaFX application.
     * </p>
     *
     * @param primaryStage the primary stage for the application
     */
    @Override
    public void start(Stage primaryStage) {
        // Use a daemon-backed executor so it won't keep the JVM alive
        executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "sim-ui-worker");
            thread.setDaemon(true);
            return thread;
        });

        // Build normalization map after 'agents' array is initialized
        agentCanonicalMap = buildAgentCanonicalMap();

        primaryStage.setTitle("Valorant Match Simulator v0.2.1-alpha");

        // Ensure the app exits when the last window closes
        Platform.setImplicitExit(true);

        // Allow adaptive window size with reasonable minimums
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // Create main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.getStyleClass().add("root-pane");

        // Create sections
        VBox header = createHeaderSection();
        HBox mainContent = createMainContent();
        VBox footer = createFooterSection();

        root.setTop(header);
        root.setCenter(mainContent);
        root.setBottom(footer);

        // Create scene without hard-coded size so it adapts to content
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        // Size stage to fit initial scene content
        primaryStage.sizeToScene();
        primaryStage.setOnCloseRequest(_ -> {
            if (executorService != null) {
                executorService.shutdownNow();
            }
            // Explicitly terminate the JavaFX platform and JVM
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
    }

    @Override
    public void stop() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    /**
     * <p>
     * Creates the header section with title and description.
     * </p>
     *
     * @return VBox containing header elements
     */
    private VBox createHeaderSection() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 20, 0));

        Label titleLabel = new Label("VALORANT MATCH SIMULATOR");
        titleLabel.setFont(Font.font(ARIAL_FONT, FontWeight.BOLD, 28));
        titleLabel.getStyleClass().add("title-label");

        // Add drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.GRAY);
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        titleLabel.setEffect(dropShadow);

        Label subtitleLabel = new Label("Advanced Team Composition Analysis & Match Prediction");
        subtitleLabel.setFont(Font.font(ARIAL_FONT, FontWeight.NORMAL, 14));
        subtitleLabel.getStyleClass().add("subtitle-label");

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    /**
     * <p>
     * Creates the main content area with team selection and controls.
     * </p>
     *
     * @return HBox containing main content
     */
    private HBox createMainContent() {
        HBox mainContent = new HBox(20);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setFillHeight(true);

        // Team selection panels
        VBox team1Panel = createTeamPanel("Team 1", team1Agents, "team1-panel");
        VBox team2Panel = createTeamPanel("Team 2", team2Agents, "team2-panel");

        // Control panel
        VBox controlPanel = createControlPanel();

        // Let team panels expand with window width; control panel can stay natural
        javafx.scene.layout.HBox.setHgrow(team1Panel, javafx.scene.layout.Priority.ALWAYS);
        javafx.scene.layout.HBox.setHgrow(team2Panel, javafx.scene.layout.Priority.ALWAYS);
        javafx.scene.layout.HBox.setHgrow(controlPanel, javafx.scene.layout.Priority.SOMETIMES);

        mainContent.getChildren().addAll(team1Panel, controlPanel, team2Panel);
        return mainContent;
    }

    /**
     * <p>
     * Creates a team selection panel.
     * </p>
     *
     * @param teamName        the name of the team
     * @param agentComboBoxes list to store agent selection ComboBoxes
     * @param styleClass      CSS style class for the panel
     * @return VBox containing team selection UI
     */
    private VBox createTeamPanel(String teamName, List<ComboBox<String>> agentComboBoxes, String styleClass) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.getStyleClass().add(styleClass);
        panel.setMinWidth(250);
        panel.setFillWidth(true);

        Label teamLabel = new Label(teamName);
        teamLabel.setFont(Font.font(ARIAL_FONT, FontWeight.BOLD, 18));
        teamLabel.getStyleClass().add("team-label");

        panel.getChildren().add(teamLabel);

        // Create agent selection dropdown for each position
        for (int i = 0; i < 5; i++) {
            Label positionLabel = new Label("Agent " + (i + 1) + ":");
            positionLabel.setFont(Font.font(ARIAL_FONT, FontWeight.NORMAL, 12));

            ComboBox<String> agentComboBox = new ComboBox<>();
            agentComboBox.getItems().addAll(agents);
            agentComboBox.setPromptText("Select Agent");
            agentComboBox.setPrefWidth(200);
            agentComboBox.setMaxWidth(Double.MAX_VALUE);
            agentComboBox.getStyleClass().add("agent-selector");

            agentComboBoxes.add(agentComboBox);

            panel.getChildren().addAll(positionLabel, agentComboBox);
        }

        // Text-based input (comma-separated) with Apply button
        panel.getChildren().add(new Separator());
        Label orLabel = new Label("Or input agents (comma-separated):");
        orLabel.setFont(Font.font(ARIAL_FONT, FontWeight.NORMAL, 12));
        TextField textInput = new TextField();
        textInput.setPromptText("e.g., Jett, Sova, Viper, Omen, Sage");
        textInput.setPrefWidth(200);
        textInput.setMaxWidth(Double.MAX_VALUE);
        textInput.getStyleClass().add("agent-text-input");
        // Pressing Enter applies the text input
        textInput.setOnAction(_ -> {
            try {
                applyAgentsFromText(agentComboBoxes, textInput.getText());
                if (statusLabel != null) {
                    statusLabel.setText(teamName + ": text agents applied");
                }
                logger.log(Level.INFO, "Applied text agents for {0} via Enter: {1}", new Object[] { teamName,
                        textInput.getText() });
            } catch (IllegalArgumentException ex) {
                showValidationErrors(List.of(ex.getMessage()));
            }
        });

        Button applyTextBtn = new Button("Apply Text");
        applyTextBtn.setPrefWidth(200);
        applyTextBtn.setMaxWidth(Double.MAX_VALUE);
        applyTextBtn.getStyleClass().add("apply-text-button");
        applyTextBtn.setOnAction(_ -> {
            try {
                applyAgentsFromText(agentComboBoxes, textInput.getText());
                if (statusLabel != null) {
                    statusLabel.setText(teamName + ": text agents applied");
                }
                logger.log(Level.INFO, "Applied text agents for {0} via button: {1}", new Object[] { teamName,
                        textInput.getText() });
            } catch (IllegalArgumentException ex) {
                showValidationErrors(List.of(ex.getMessage()));
            }
        });

        panel.getChildren().addAll(orLabel, textInput, applyTextBtn);
        return panel;
    }

    /**
     * <p>
     * Creates the control panel with simulation settings.
     * </p>
     *
     * @return VBox containing control elements
     */
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(15);
        controlPanel.setPadding(new Insets(15));
        controlPanel.setAlignment(Pos.TOP_CENTER);
        controlPanel.getStyleClass().add("control-panel");
        controlPanel.setMinWidth(300);
        controlPanel.setFillWidth(true);

        Label controlLabel = new Label("Simulation Controls");
        controlLabel.setFont(Font.font(ARIAL_FONT, FontWeight.BOLD, 18));
        controlLabel.getStyleClass().add("control-label");

        // Map selection
        Label mapLabel = new Label("Select Map:");
        mapLabel.setFont(Font.font(ARIAL_FONT, FontWeight.NORMAL, 12));

        mapSelector = new ComboBox<>();
        mapSelector.getItems().addAll(maps);
        mapSelector.setValue("Ascent"); // Default map
        mapSelector.setPrefWidth(200);
        mapSelector.setMaxWidth(Double.MAX_VALUE);
        mapSelector.getStyleClass().add("map-selector");

        // Simulation count
        Label countLabel = new Label("Number of Simulations:");
        countLabel.setFont(Font.font(ARIAL_FONT, FontWeight.NORMAL, 12));

        simulationCountField = new TextField("1000");
        simulationCountField.setPrefWidth(200);
        simulationCountField.setMaxWidth(Double.MAX_VALUE);
        simulationCountField.getStyleClass().add("simulation-count");

        // Simulate button
        simulateButton = new Button("RUN SIMULATION");
        simulateButton.setPrefWidth(200);
        simulateButton.setMaxWidth(Double.MAX_VALUE);
        simulateButton.setPrefHeight(40);
        simulateButton.setFont(Font.font(ARIAL_FONT, FontWeight.BOLD, 14));
        simulateButton.getStyleClass().add("simulate-button");
        simulateButton.setOnAction(_ -> runSimulation());

        // Progress bar
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setVisible(false);
        progressBar.getStyleClass().add("progress-bar");

        // Status label
        statusLabel = new Label("Ready to simulate");
        statusLabel.setFont(Font.font(ARIAL_FONT, FontWeight.NORMAL, 12));
        statusLabel.getStyleClass().add("status-label");

        controlPanel.getChildren().addAll(
                controlLabel,
                new Separator(),
                mapLabel, mapSelector,
                countLabel, simulationCountField,
                new Separator(),
                simulateButton,
                progressBar,
                statusLabel);

        return controlPanel;
    }

    /**
     * <p>
     * Creates the footer section with results display.
     * </p>
     *
     * @return VBox containing results area
     */
    private VBox createFooterSection() {
        VBox footer = new VBox(10);
        footer.setPadding(new Insets(20, 0, 0, 0));

        Label resultsLabel = new Label("Simulation Results");
        resultsLabel.setFont(Font.font(ARIAL_FONT, FontWeight.BOLD, 16));
        resultsLabel.getStyleClass().add("results-label");

        resultsArea = new TextArea();
        resultsArea.setPrefHeight(200);
        resultsArea.setMaxHeight(Double.MAX_VALUE);
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.getStyleClass().add("results-area");
        resultsArea.setText(
                "Configure your teams and click 'RUN SIMULATION' to see detailed match results and statistics.");

        // Allow results area to grow with window height
        javafx.scene.layout.VBox.setVgrow(resultsArea, javafx.scene.layout.Priority.ALWAYS);

        footer.getChildren().addAll(resultsLabel, resultsArea);
        return footer;
    }

    /**
     * <p>
     * Validates team selections and returns any validation errors.
     * </p>
     *
     * @return List of validation error messages, empty if valid
     */
    private List<String> validateTeamSelections() {
        List<String> errors = new ArrayList<>();

        // Check Team 1
        List<String> team1Selected = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String agent = team1Agents.get(i).getValue();
            if (agent == null) {
                errors.add("Team 1: Please select an agent for position " + (i + 1));
            } else if (team1Selected.contains(agent)) {
                errors.add("Team 1: Duplicate agent selection - " + agent);
            } else {
                team1Selected.add(agent);
            }
        }

        // Check Team 2
        List<String> team2Selected = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String agent = team2Agents.get(i).getValue();
            if (agent == null) {
                errors.add("Team 2: Please select an agent for position " + (i + 1));
            } else if (team2Selected.contains(agent)) {
                errors.add("Team 2: Duplicate agent selection - " + agent);
            } else {
                team2Selected.add(agent);
            }
        }

        // Validate simulation count
        try {
            long count = Long.parseLong(simulationCountField.getText());
            if (count <= 0 || count > 1000000000) {
                errors.add("Simulation count must be between 1 and 1,000,000,000");
            }
        } catch (NumberFormatException _) {
            errors.add("Invalid simulation count - please enter a number");
        }

        return errors;
    }

    /**
     * <p>
     * Appends the selected agents for a given team to the provided results
     * buffer.
     * </p>
     *
     * <p>
     * Formats the five selected agent names for the specified team (1 or 2)
     * separated by commas and appends them to the StringBuilder. This method does
     * not add a trailing newline; callers should handle line breaks as needed.
     * </p>
     *
     * @param results    the StringBuilder to append agent names to (must not be
     *                   null)
     * @param teamNumber the team to output: 1 for Team 1, 2 for Team 2
     * @throws IllegalArgumentException if {@code teamNumber} is not 1 or 2
     */
    private void outputAgents(StringBuilder results, int teamNumber) {
        switch (teamNumber) {
            case 1 -> {
                for (int i = 0; i < 5; i++) {
                    results.append(team1Agents.get(i).getValue());
                    if (i < 4)
                        results.append(", ");
                }
            }
            case 2 -> {
                for (int i = 0; i < 5; i++) {
                    results.append(team2Agents.get(i).getValue());
                    if (i < 4)
                        results.append(", ");
                }
            }
            default -> throw new IllegalArgumentException("Invalid team number: " + teamNumber);
        }
    }

    /**
     * <p>
     * Parses a text input of agents into canonical agent names.
     * </p>
     *
     * <p>
     * Accepts comma-separated (preferred) or whitespace-separated input. Names are
     * matched case-insensitively and normalized (e.g., "kayo", "KAY O",
     * "kay/o" → "KAY/O"). Exactly 5 valid, non-duplicate agents are required.
     * </p>
     *
     * @param text the raw input text
     * @return a list of 5 canonical agent names, in order
     * @throws IllegalArgumentException if count is not 5, a name is unknown, or
     *                                  duplicates exist
     */
    private List<String> parseAgentText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Please enter 5 agent names");
        }
        String trimmed = text.trim();
        String[] parts = trimmed.contains(",") ? trimmed.split(",") : trimmed.split("\\s+");
        List<String> result = new ArrayList<>();
        List<String> unknown = new ArrayList<>();

        for (String raw : parts) {
            String name = raw.trim();
            if (name.isEmpty())
                continue;
            String key = sanitizeAgentKey(name);
            String canonical = agentCanonicalMap.get(key);
            if (canonical == null) {
                unknown.add(name);
            } else {
                result.add(canonical);
            }
        }

        if (!unknown.isEmpty()) {
            throw new IllegalArgumentException("Unknown agent name(s): " + String.join(", ", unknown));
        }
        if (result.size() != 5) {
            throw new IllegalArgumentException("Please provide exactly 5 agents (received " + result.size() + ")");
        }
        // Check duplicates
        Set<String> seen = new HashSet<>();
        for (String s : result) {
            if (!seen.add(s)) {
                throw new IllegalArgumentException("Duplicate agent: " + s);
            }
        }
        return result;
    }

    /**
     * <p>
     * Applies parsed agent names to the target ComboBoxes in order.
     * </p>
     *
     * @param targets the ComboBoxes to set (size 5)
     * @param text    the raw input text to parse
     */
    private void applyAgentsFromText(List<ComboBox<String>> targets, String text) {
        List<String> names = parseAgentText(text);
        for (int i = 0; i < 5; i++) {
            targets.get(i).setValue(names.get(i));
        }
    }

    /**
     * <p>
     * Builds a normalization map for agent names from the {@code agents} array.
     * </p>
     *
     * <p>
     * Maps a sanitized key (lowercase, letters only) to the canonical
     * display name. Handles common variants automatically (e.g., KAY/O).
     * </p>
     */
    private Map<String, String> buildAgentCanonicalMap() {
        Map<String, String> map = new HashMap<>();
        for (String ag : agents) {
            map.put(sanitizeAgentKey(ag), ag);
        }

        // Aliases for KAY/O and common variants because people are lazy and
        // inconsistent
        map.put("kayo", "KAY/O");
        map.put("kay o", "KAY/O");
        return map;
    }

    /**
     * <p>
     * Sanitizes an agent name to a key used for lookup.
     * </p>
     *
     * <p>
     * Lowercases and keeps letters only so variants like
     * "KAY/O", "Kay O", and "kay-o" normalize to "kayo".
     * </p>
     */
    private String sanitizeAgentKey(String name) {
        StringBuilder sb = new StringBuilder();
        for (char c : name.toLowerCase().toCharArray()) {
            if (c >= 'a' && c <= 'z')
                sb.append(c);
        }
        return sb.toString();
    }

    /**
     * <p>
     * Executes the configured number of match simulations using the current GUI
     * selections.
     * </p>
     *
     * <p>
     * This method builds the two team compositions from the selected agents and
     * map, then runs {@code simCount} full matches using the core
     * {@link MatchSimulator} engine. For each simulation a new
     * {@link MatchSimulator} instance is used to ensure clean state. It gathers
     * aggregate statistics (wins, win rates, team stats) and appends a
     * performance section including elapsed time.
     * </p>
     *
     * <p>
     * Threading: Runs on a background Task; periodically updates the progress bar
     * via {@link Platform#runLater(Runnable)}.
     * </p>
     *
     * @return a formatted report string containing match results, team
     *         statistics, and performance metrics
     * @throws NumberFormatException    if the simulation count is not a
     *                                  valid number
     * @throws IllegalArgumentException if team selections or parameters are
     *                                  invalid
     * @throws IllegalStateException    if simulation cannot proceed due to
     *                                  state constraints
     * @throws RuntimeException         for unexpected runtime issues during
     *                                  simulation
     */
    private String runSimulations() throws Exception {
        try {
            // Start timing and log
            long startMs = System.currentTimeMillis();
            logger.log(Level.INFO, "Simulation started...");
            // Get selected values
            String map = mapSelector.getValue();
            long simCount = Long.parseLong(simulationCountField.getText());

            // Create team compositions
            TeamComp teamOne = new TeamComp(map);
            TeamComp teamTwo = new TeamComp(map);

            // Add agents to teams
            addAgentsToTeams(teamOne, teamTwo);

            // Capture selected agent names for cloning per worker
            List<String> team1Names = new ArrayList<>(5);
            List<String> team2Names = new ArrayList<>(5);
            captureSelectedAgentNames(team1Names, team2Names);

            // Capture results
            StringBuilder results = new StringBuilder();
            appendHeaderAndCompositions(results, map, simCount);

            // Multithreaded simulation: split work across a fixed thread pool
            int threads = Math.max(1, Runtime.getRuntime().availableProcessors());
            int workers = computeWorkers(simCount, threads);
            long simulationsPerWorker = simCount / workers;
            long remainder = simCount % workers;
            AtomicLong completed = new AtomicLong(0);
            final int progressChunk = (int) Math.max(150, simCount / 100);

            long totalTeam1Wins = 0;
            long totalTeam2Wins = 0;
            try (ExecutorService simPool = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Callable<long[]>> tasks = new ArrayList<>();
                for (int w = 0; w < workers; w++) {
                    long runs = returnDistributedRuns(w, simulationsPerWorker, remainder);
                    if (runs <= 0) {
                        continue;
                    }
                    tasks.add(createWorkerTask(map, simCount, progressChunk, completed, runs));
                }
                long[] totals = aggregateWins(simPool.invokeAll(tasks));
                totalTeam1Wins += totals[0];
                totalTeam2Wins += totals[1];
            }
            // Calculate statistics
            String team1WinRate = NumberFormat.getPercentInstance().format((double) totalTeam1Wins / (double) simCount);
            String team2WinRate = NumberFormat.getPercentInstance().format((double) totalTeam2Wins / (double) simCount);

            results.append("MATCH RESULTS:\n");
            results.append("-".repeat(40)).append("\n");
            results.append(String.format("Team 1 Wins: %s (%s)%n", numberFormat.format(totalTeam1Wins), team1WinRate));
            results.append(String.format("Team 2 Wins: %s (%s)%n", numberFormat.format(totalTeam2Wins), team2WinRate));
            results.append(String.format("Win Probability: %s vs %s%n", team1WinRate, team2WinRate));

            // Add team statistics
            results.append("\nTEAM STATISTICS:\n");
            results.append("-".repeat(40)).append("\n");
            results.append("Team 1 Stats:\n");
            results.append(String.format("  Aggro: %.1f | Control: %.1f | Midrange: %.1f%n",
                    teamOne.getTotalTrueAggro(), teamOne.getTotalTrueControl(),
                    teamOne.getTotalTrueMidrange()));
            results.append(String.format("  Relative Power: %.1f%n", teamOne.getTotalRelativePower()));

            results.append("Team 2 Stats:\n");
            results.append(String.format("  Aggro: %.1f | Control: %.1f | Midrange: %.1f%n",
                    teamTwo.getTotalTrueAggro(), teamTwo.getTotalTrueControl(),
                    teamTwo.getTotalTrueMidrange()));
            results.append(String.format("  Relative Power: %.1f%n", teamTwo.getTotalRelativePower()));

            // End timing and log
            long elapsedMs = System.currentTimeMillis() - startMs;
            double elapsedSeconds = elapsedMs / 1000.0;
            logger.log(Level.INFO, "{0} ms elapsed ({1} seconds)",
                    new Object[] { elapsedMs, String.format("%.3f", elapsedSeconds) });

            // Append performance section to results
            results.append("\nPERFORMANCE:\n");
            results.append("-".repeat(40)).append("\n");
            results.append(String.format("Time elapsed: %d ms (%.3f seconds)%n", elapsedMs, elapsedSeconds));

            return results.toString();

        } catch (NumberFormatException e) {
            return "Error: Invalid simulation count format - " + e.getMessage();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "Error: Invalid simulation parameters - " + e.getMessage();
        } catch (RuntimeException e) {
            return "Error during simulation runtime - " + e.getMessage();
        }
    }

    /**
     * <p>
     * Computes the number of worker tasks to launch, clamped to [1, simCount]
     * and based on approximately 80% of available cores.
     * </p>
     */
    private int computeWorkers(long simCount, int threads) {
        long desiredWorkers = (long) Math.ceil(threads * 0.8);
        if (desiredWorkers < 1)
            desiredWorkers = 1;
        if (desiredWorkers > simCount)
            desiredWorkers = simCount;
        return (int) desiredWorkers;
    }

    /**
     * <p>
     * Appends the standard report header and team compositions section.
     * </p>
     */
    private void appendHeaderAndCompositions(StringBuilder results, String map, long simCount) {
        results.append("=".repeat(60)).append("\n");
        results.append("VALORANT MATCH SIMULATION RESULTS\n");
        results.append("=".repeat(60)).append("\n\n");
        results.append("Map: ").append(map).append("\n");
        results.append("Simulations: ").append(numberFormat.format(simCount)).append("\n\n");

        results.append("TEAM COMPOSITIONS:\n");
        results.append("-".repeat(40)).append("\n");
        results.append("Team 1: ");
        outputAgents(results, 1);
        results.append("\nTeam 2: ");
        outputAgents(results, 2);
        results.append("\n\n");
    }

    /**
     * <p>
     * Creates a worker task that runs a subset of simulations and returns
     * local win totals as a long[2] array {team1Wins, team2Wins}.
     * </p>
     */
    private Callable<long[]> createWorkerTask(String map, long simCount, int progressChunk,
            AtomicLong completed, long runs) {
        return () -> {
            // Clone team compositions for this worker
            TeamComp tOne = new TeamComp(map);
            TeamComp tTwo = new TeamComp(map);
            addAgentsToTeams(tOne, tTwo);

            MatchSimulator sim = new MatchSimulator(tOne, tTwo);
            sim.setMap(map);
            sim.setAttackingTeam(1);

            long localMatchesPlayed = 0;
            long localTeam1Wins = 0;
            long localTeam2Wins = 0;
            for (long i = 0; i < runs; i++) {
                sim.simulateMatchFast();
                if (sim.getLastMatchWinner() == 1) {
                    localTeam1Wins++;
                } else {
                    localTeam2Wins++;
                }

                // progress throttling
                localMatchesPlayed++;
                throttleProgress(completed, localMatchesPlayed, simCount, progressChunk);
            }
            // flush remaining progress for this worker
            flushRemainingProgress(completed, localMatchesPlayed, simCount, progressChunk);
            return new long[] { localTeam1Wins, localTeam2Wins };
        };
    }

    /**
     * <p>
     * Aggregates totals from worker futures.
     * </p>
     *
     * @return total number of team 1 wins and team 2 wins as a long[2] array
     *         {team1Wins, team2Wins}.
     *         <br>
     *         </br>
     * @throws Exception if any worker task failed
     */
    private long[] aggregateWins(List<Future<long[]>> futures) throws Exception {
        long totalTeam1Wins = 0;
        long totalTeam2Wins = 0;
        for (Future<long[]> f : futures) {
            long[] aggregatedWins = f.get();
            totalTeam1Wins += aggregatedWins[0];
            totalTeam2Wins += aggregatedWins[1];
        }
        return new long[] { totalTeam1Wins, totalTeam2Wins };
    }

    /**
     * <p>
     * Returns the number of simulations assigned to a given worker index using
     * near-equal distribution with the remainder spread to the earliest
     * workers.
     * </p>
     *
     * @param w                    worker index (0-based)
     * @param simulationsPerWorker base simulations per worker
     * @param remainder            extra simulations to distribute among first
     *                             workers
     * @return number of simulations to run for this worker
     */
    private long returnDistributedRuns(int w, long simulationsPerWorker, long remainder) {
        return simulationsPerWorker + (w < remainder ? 1 : 0);
    }

    /**
     * <p>
     * Adds the currently selected agents from the GUI into the provided team
     * compositions in slot order.
     * </p>
     *
     * @param teamOne target team one composition
     * @param teamTwo target team two composition
     */
    private void addAgentsToTeams(TeamComp teamOne, TeamComp teamTwo) {
        for (int i = 0; i < 5; i++) {
            teamOne.addAgent(team1Agents.get(i).getValue());
            teamTwo.addAgent(team2Agents.get(i).getValue());
        }
    }

    /**
     * <p>
     * Copies the selected agent names from the UI into the supplied lists for
     * reuse when creating worker-local team compositions.
     * </p>
     *
     * @param team1Names output list for team 1 agent names (size 5)
     * @param team2Names output list for team 2 agent names (size 5)
     */
    private void captureSelectedAgentNames(List<String> team1Names, List<String> team2Names) {
        for (int i = 0; i < 5; i++) {
            team1Names.add(team1Agents.get(i).getValue());
            team2Names.add(team2Agents.get(i).getValue());
        }
    }

    /**
     * <p>
     * Throttles UI progress updates to avoid excessive Platform.runLater calls.
     * Updates the progress bar after a fixed number of local iterations.
     * </p>
     *
     * @param completed     global completed counter
     * @param local         local iterations completed in this worker
     * @param simCount      total simulations requested
     * @param progressChunk update frequency (in matches)
     */
    private void throttleProgress(AtomicLong completed, long localMatchesPlayed, long simCount,
            int progressChunk) {
        if (localMatchesPlayed % progressChunk == 0) {
            long done = completed.addAndGet(progressChunk);
            final double progress = (double) Math.min(done, simCount) / simCount;
            Platform.runLater(() -> {
                progressBar.setProgress(progress);
                statusLabel.setText("Simulation progress: " + (int) (progress * 100) + "%");
            });
        }
    }

    /**
     * <p>
     * Flushes any remaining progress less than one chunk to the UI at the end of
     * a worker's loop.
     * </p>
     *
     * @param completed     global completed counter
     * @param local         local iterations completed in this worker
     * @param simCount      total simulations requested
     * @param progressChunk update frequency (in matches)
     */
    private void flushRemainingProgress(AtomicLong completed, long localMatchesPlayed, long simCount,
            int progressChunk) {
        long rem = localMatchesPlayed % progressChunk;
        if (rem > 0) {
            long done = completed.addAndGet(rem);
            final double progress = (double) Math.min(done, simCount) / simCount;
            Platform.runLater(() -> {
                progressBar.setProgress(progress);
                statusLabel.setText("Simulation progress: " + (int) (progress * 100) + "%");
            });
        }
    }

    /**
     * <p>
     * Runs the simulation in a background thread.
     * </p>
     */
    private void runSimulation() {
        List<String> errors = validateTeamSelections();
        if (!errors.isEmpty()) {
            showValidationErrors(errors);
            return;
        }

        // Disable UI during simulation
        simulateButton.setDisable(true);
        progressBar.setVisible(true);
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        statusLabel.setText("Running simulation...");

        // Create simulation task
        Task<String> simulationTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return runSimulations();
            }
        };

        // Handle task completion
        simulationTask.setOnSucceeded(_ -> {
            resultsArea.setText(simulationTask.getValue());
            simulateButton.setDisable(false);
            progressBar.setVisible(false);
            statusLabel.setText("Simulation completed successfully!");
        });

        simulationTask.setOnFailed(_ -> {
            resultsArea.setText("Simulation failed: " + simulationTask.getException().getMessage());
            simulateButton.setDisable(false);
            progressBar.setVisible(false);
            statusLabel.setText("Simulation failed");
        });

        // Run task
        executorService.submit(simulationTask);
    }

    /**
     * <p>
     * Shows validation error messages to the user.
     * </p>
     *
     * @param errors list of validation error messages
     */
    private void showValidationErrors(List<String> errors) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Errors");
        alert.setHeaderText("Please fix the following issues:");
        alert.setContentText(String.join("\n", errors));
        alert.showAndWait();
    }

    /**
     * <p>
     * Main method to launch the JavaFX application.
     * </p>
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}