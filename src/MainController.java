import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.stream.Collectors;
import javafx.scene.chart.PieChart;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class MainController {
    @FXML
    private StackPane contentStackPane;

    // Panels
    @FXML
    private VBox tasksPane;
    @FXML
    private VBox categoriesPane;
    @FXML
    private VBox remindersPane;
    @FXML
    private VBox prioritiesPane;
    // Labels στατιστικών
    @FXML
    private Label totalTasksLabel;
    @FXML
    private Label completedTasksLabel;
    @FXML
    private Label delayedTasksLabel;
    @FXML
    private Label upcomingTasksLabel;

    // Στοιχεία αναζήτησης
    @FXML
    private TextField searchTaskField;
    @FXML
    private TextField searchCategoryField;
    @FXML
    private TextField searchReminderField;
    // Νέα στοιχεία για προχωρημένη αναζήτηση
    @FXML
    private ComboBox<String> searchPriorityComboBox;
    @FXML
    private ComboBox<String> priorityComboBox;
    @FXML
    private DatePicker deadlineSearchPicker;
    @FXML
    private ListView<String> priorityListView;
    @FXML
    private TextField searchPriorityField;


    @FXML private TableView<Task> taskTableView;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> descriptionColumn;
    @FXML private TableColumn<Task, String> categoryColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, LocalDate> deadlineColumn; // Αν το deadline είναι τύπου LocalDate, αλλιώς TableColumn<Task, String>
    @FXML private TableColumn<Task, Task.Status> statusColumn;

    // ListViews
    @FXML
    private ListView<String> categoryListView;
    @FXML
    private ListView<String> reminderListView;

    // Κουμπιά για Tasks, Categories, Reminders
    @FXML
    private Button addTaskButton, editTaskButton, deleteTaskButton;
    @FXML
    private Button addCategoryButton, editCategoryButton, deleteCategoryButton;
    @FXML
    private Button addReminderButton, editReminderButton, deleteReminderButton;
    @FXML private Button addPriorityButton;
    @FXML private Button editPriorityButton;
    @FXML private Button deletePriorityButton;

    @FXML
    private Button tasksButton;
    @FXML
    private Button categoriesButton;
    @FXML
    private Button remindersButton;
    @FXML
    private Button prioritiesButton;

    @FXML
    private PieChart tasksPieChart;

    // Instance του TaskManager
    private TaskManager taskManager = TaskManager.getInstance();

    @FXML
    public void initialize() {
        taskManager.loadFromJSON();
        taskManager.updateTaskStatuses();

        // Αρχική εμφάνιση: μόνο το tasksPane
        categoriesPane.setVisible(false);
        remindersPane.setVisible(false);
        prioritiesPane.setVisible(false);
        showTasksPane();

        // Ρυθμίστε τα cellValueFactory ώστε κάθε στήλη να εμφανίζει την αντίστοιχη property του Task
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Γέμισε το TableView με τα tasks από τον TaskManager
        taskTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        taskTableView.getItems().setAll(taskManager.getTasks());

        updateCategoryList();
        updatePriorityList();
        updateReminderList();

        updateLabels();
        updatePieChart();


        long delayedCount = taskManager.getTasks().stream()
                .filter(task -> task.getStatus() == Task.Status.DELAYED)
                .count();
        if (delayedCount > 0) {
            showAlert("Delayed Tasks", "You have " + delayedCount + " delayed task(s)!");
        }

        // Αρχικοποίηση του ComboBox για προτεραιότητες στην αναζήτηση
        searchPriorityComboBox.getItems().add("All Priorities");
        taskManager.getPriorities().forEach(p -> searchPriorityComboBox.getItems().add(p.getName()));
        searchPriorityComboBox.setValue("All Priorities");
    }

    // Ενημέρωση εμφάνισης στοιχείων ανάλογα με το ενεργό pane
    private void updateActivePane() {
        if (tasksPane.isVisible()) {
            // Εμφάνιση στοιχείων για Tasks
            searchTaskField.setVisible(true);
            searchTaskField.setManaged(true);
            taskTableView.setVisible(true);
            taskTableView.setManaged(true);

            // Απόκρυψη όλων των άλλων στοιχείων
            searchCategoryField.setVisible(false);
            searchCategoryField.setManaged(false);
            categoryListView.setVisible(false);
            categoryListView.setManaged(false);

            searchReminderField.setVisible(false);
            searchReminderField.setManaged(false);
            reminderListView.setVisible(false);
            reminderListView.setManaged(false);

            // *** Απόκρυψη στοιχείων για Priorities ***
            searchPriorityField.setVisible(false);
            searchPriorityField.setManaged(false);
            priorityListView.setVisible(false);
            priorityListView.setManaged(false);
            addPriorityButton.setVisible(false);
            addPriorityButton.setManaged(false);
            editPriorityButton.setVisible(false);
            editPriorityButton.setManaged(false);
            deletePriorityButton.setVisible(false);
            deletePriorityButton.setManaged(false);

        } else if (categoriesPane.isVisible()) {
            // Εμφάνιση στοιχείων για Categories
            searchCategoryField.setVisible(true);
            searchCategoryField.setManaged(true);
            categoryListView.setVisible(true);
            categoryListView.setManaged(true);

            // Απόκρυψη στοιχείων για Tasks
            searchTaskField.setVisible(false);
            searchTaskField.setManaged(false);
            taskTableView.setVisible(false);
            taskTableView.setManaged(false);

            // Απόκρυψη στοιχείων για Reminders
            searchReminderField.setVisible(false);
            searchReminderField.setManaged(false);
            reminderListView.setVisible(false);
            reminderListView.setManaged(false);

            // *** Απόκρυψη στοιχείων για Priorities ***
            searchPriorityField.setVisible(false);
            searchPriorityField.setManaged(false);
            priorityListView.setVisible(false);
            priorityListView.setManaged(false);
            addPriorityButton.setVisible(false);
            addPriorityButton.setManaged(false);
            editPriorityButton.setVisible(false);
            editPriorityButton.setManaged(false);
            deletePriorityButton.setVisible(false);
            deletePriorityButton.setManaged(false);

        } else if (remindersPane.isVisible()) {
            // Εμφάνιση στοιχείων για Reminders
            searchReminderField.setVisible(true);
            searchReminderField.setManaged(true);
            reminderListView.setVisible(true);
            reminderListView.setManaged(true);

            // Απόκρυψη στοιχείων για Tasks
            searchTaskField.setVisible(false);
            searchTaskField.setManaged(false);
            taskTableView.setVisible(false);
            taskTableView.setManaged(false);

            // Απόκρυψη στοιχείων για Categories
            searchCategoryField.setVisible(false);
            searchCategoryField.setManaged(false);
            categoryListView.setVisible(false);
            categoryListView.setManaged(false);

            // *** Απόκρυψη στοιχείων για Priorities ***
            searchPriorityField.setVisible(false);
            searchPriorityField.setManaged(false);
            priorityListView.setVisible(false);
            priorityListView.setManaged(false);
            addPriorityButton.setVisible(false);
            addPriorityButton.setManaged(false);
            editPriorityButton.setVisible(false);
            editPriorityButton.setManaged(false);
            deletePriorityButton.setVisible(false);
            deletePriorityButton.setManaged(false);

        } else if (prioritiesPane.isVisible()) {
            // Εμφάνιση στοιχείων για Priorities (μόνο όταν ενεργό)
            searchPriorityField.setVisible(true);
            searchPriorityField.setManaged(true);
            priorityListView.setVisible(true);
            priorityListView.setManaged(true);
            addPriorityButton.setVisible(true);
            addPriorityButton.setManaged(true);
            editPriorityButton.setVisible(true);
            editPriorityButton.setManaged(true);
            deletePriorityButton.setVisible(true);
            deletePriorityButton.setManaged(true);

            // Απόκρυψη στοιχείων για Tasks
            searchTaskField.setVisible(false);
            searchTaskField.setManaged(false);
            taskTableView.setVisible(false);
            taskTableView.setManaged(false);

            // Απόκρυψη στοιχείων για Categories
            searchCategoryField.setVisible(false);
            searchCategoryField.setManaged(false);
            categoryListView.setVisible(false);
            categoryListView.setManaged(false);

            // Απόκρυψη στοιχείων για Reminders
            searchReminderField.setVisible(false);
            searchReminderField.setManaged(false);
            reminderListView.setVisible(false);
            reminderListView.setManaged(false);
        }
    }

    // Ορισμός των style strings για τα κουμπιά
    private String defaultButtonStyle = "-fx-background-color: linear-gradient(#6a0dad, #8b00ff); "
            + "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; "
            + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 15, 0, 5, 5);";

    private String selectedButtonStyle = "-fx-background-color: linear-gradient(#e066a0, #cc3366); "
            + "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12; -fx-border-radius: 12; "
            + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 25, 0, 5, 5);";

    private void updateTaskTable() {
        taskTableView.getItems().setAll(taskManager.getTasks());
    }

    private void updatePieChart() {
        List<Task> tasks = taskManager.getTasks();

        // Υπολογισμός του πλήθους για κάθε κατάσταση
        int openCount = (int) tasks.stream().filter(t -> t.getStatus() == Task.Status.OPEN).count();
        int inProgressCount = (int) tasks.stream().filter(t -> t.getStatus() == Task.Status.IN_PROGRESS).count();
        int postponedCount = (int) tasks.stream().filter(t -> t.getStatus() == Task.Status.POSTPONED).count();
        int completedCount = (int) tasks.stream().filter(t -> t.getStatus() == Task.Status.COMPLETED).count();
        int delayedCount = (int) tasks.stream().filter(t -> t.getStatus() == Task.Status.DELAYED).count();

        // Φτιάχνουμε μια λίστα με όλα τα δεδομένα
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Open", openCount),
                new PieChart.Data("In Progress", inProgressCount),
                new PieChart.Data("Postponed", postponedCount),
                new PieChart.Data("Completed", completedCount),
                new PieChart.Data("Delayed", delayedCount)
        );

        // Αναθέτουμε τα δεδομένα στο διάγραμμα
        tasksPieChart.setData(pieData);
    }

    // Συνάρτηση για προσθήκη νέου επιπέδου προτεραιότητας
    @FXML
    private void addPriority() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Priority");
        dialog.setHeaderText("Enter new priority name:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String name = result.get().trim();
            if (name.isEmpty()) {
                showAlert("Invalid Input", "Priority name cannot be empty.");
                return;
            }
            // Έλεγχος αν υπάρχει ήδη
            boolean exists = taskManager.getPriorities().stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(name));
            if (exists) {
                showAlert("Duplicate Priority", "This priority already exists.");
                return;
            }
            // Δημιουργία και προσθήκη νέου Priority
            Priority newPriority = new Priority(name);
            taskManager.addPriority(newPriority);
            updatePriorityList();
            updatePriorityComboBoxes();
        }
    }

    @FXML
    private void editPriority() {
        String selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
        if (selectedPriority == null) {
            showAlert("No Priority Selected", "Please select a priority to edit.");
            return;
        }
        if (selectedPriority.equals("Default")) {
            showAlert("Operation Not Allowed", "Cannot rename the default priority.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selectedPriority);
        dialog.setTitle("Edit Priority");
        dialog.setHeaderText("Enter new name for the priority:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newName = result.get().trim();
            if (newName.isEmpty()) {
                showAlert("Invalid Input", "Priority name cannot be empty.");
                return;
            }
            if (newName.equalsIgnoreCase(selectedPriority)) {
                // No change
                return;
            }
            boolean exists = taskManager.getPriorities().stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(newName));
            if (exists) {
                showAlert("Duplicate Priority", "This priority already exists.");
                return;
            }
            taskManager.updatePriorityName(selectedPriority, newName);
            updatePriorityList();
            updatePriorityComboBoxes();
            updateTaskTable();

            // 🚀 Αναγκαστική ανανέωση του Task TableView
            taskTableView.getItems().clear();
            taskTableView.getItems().addAll(taskManager.getTasks());
        }
    }


    private String formatReminder(Reminder reminder) {
        return reminder.getTask().getTitle() + " - " + reminder.getReminderType() + " - " + reminder.getReminderDate();
    }


    // Συνάρτηση για τροποποίηση ονόματος επιπέδου προτεραιότητας
    @FXML
    private void editReminder() {
        String selectedReminderString = reminderListView.getSelectionModel().getSelectedItem();
        if (selectedReminderString == null) {
            showAlert("No Reminder Selected", "Please select a reminder to edit.");
            return;
        }

        // Αναζήτηση της υπενθύμισης στη λίστα
        Reminder selectedReminder = taskManager.getReminders().stream()
                .filter(r -> formatReminder(r).equals(selectedReminderString))
                .findFirst().orElse(null);

        if (selectedReminder == null) {
            showAlert("Error", "Could not find the selected reminder.");
            return;
        }

        Stage editReminderStage = new Stage();
        editReminderStage.setTitle("Edit Reminder");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        Label taskLabel = new Label("Task: " + selectedReminder.getTask().getTitle());

        ComboBox<String> reminderTypeComboBox = new ComboBox<>();
        reminderTypeComboBox.getItems().addAll("1 day before", "1 week before", "1 month before", "Custom date");
        reminderTypeComboBox.setValue(getReminderTypeString(selectedReminder.getReminderType()));

        DatePicker reminderDatePicker = new DatePicker(selectedReminder.getReminderDate());
        reminderDatePicker.setDisable(!reminderTypeComboBox.getValue().equals("Custom date"));

        // Ενεργοποίηση του DatePicker μόνο αν επιλεγεί "Custom date"
        reminderTypeComboBox.setOnAction(e -> {
            boolean isCustom = reminderTypeComboBox.getValue().equals("Custom date");
            reminderDatePicker.setDisable(!isCustom);
        });

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            String selectedReminderType = reminderTypeComboBox.getValue();
            LocalDate newReminderDate = reminderDatePicker.getValue();
            LocalDate taskDeadline = selectedReminder.getTask().getDeadline();

            // Υπολογισμός ημερομηνίας αν ο τύπος είναι προκαθορισμένος
            if (!"Custom date".equals(selectedReminderType)) {
                switch (selectedReminderType) {
                    case "1 day before":
                        newReminderDate = taskDeadline.minusDays(1);
                        break;
                    case "1 week before":
                        newReminderDate = taskDeadline.minusWeeks(1);
                        break;
                    case "1 month before":
                        newReminderDate = taskDeadline.minusMonths(1);
                        break;
                    default:
                        showAlert("Invalid Input", "Invalid reminder type selected.");
                        return;
                }
            } else if (newReminderDate == null || newReminderDate.isAfter(taskDeadline)) {
                showAlert("Invalid Date", "Reminder date cannot be after the task deadline.");
                return;
            }

            // Ενημέρωση της υπενθύμισης
            selectedReminder.setReminderType(convertToReminderType(selectedReminderType));
            selectedReminder.setReminderDate(newReminderDate);

            taskManager.saveToJSON();
            updateReminderList();
            editReminderStage.close();
        });

        vbox.getChildren().addAll(taskLabel, reminderTypeComboBox, reminderDatePicker, saveButton);
        Scene scene = new Scene(vbox, 300, 250);
        editReminderStage.setScene(scene);
        editReminderStage.show();
    }



    // Βοηθητική μέθοδος για μετατροπή ReminderType -> String
    private String getReminderTypeString(Reminder.ReminderType reminderType) {
        switch (reminderType) {
            case ONE_DAY_BEFORE:
                return "1 day before";
            case ONE_WEEK_BEFORE:
                return "1 week before";
            case ONE_MONTH_BEFORE:
                return "1 month before";
            case SPECIFIC_DATE:
                return "Custom date";
            default:
                return "Custom date";
        }
    }

    // Βοηθητική μέθοδος για μετατροπή String -> ReminderType
    private Reminder.ReminderType convertToReminderType(String typeString) {
        switch (typeString) {
            case "1 day before":
                return Reminder.ReminderType.ONE_DAY_BEFORE;
            case "1 week before":
                return Reminder.ReminderType.ONE_WEEK_BEFORE;
            case "1 month before":
                return Reminder.ReminderType.ONE_MONTH_BEFORE;
            case "Custom date":
                return Reminder.ReminderType.SPECIFIC_DATE;
            default:
                throw new IllegalArgumentException("Unknown reminder type: " + typeString);
        }
    }

    // Συνάρτηση για διαγραφή επιπέδου προτεραιότητας
    @FXML
    private void deletePriority() {
        String selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
        if (selectedPriority == null) {
            showAlert("No Priority Selected", "Please select a priority to delete.");
            return;
        }
        if (selectedPriority.equalsIgnoreCase("Default")) {
            showAlert("Operation Not Allowed", "Cannot delete the default priority.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Priority");
        alert.setHeaderText("Are you sure you want to delete this priority?");
        alert.setContentText("This will reassign all tasks with this priority to 'Default'.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Διαγραφή του priority και ανακατανομή των tasks στο "Default"
            taskManager.deletePriority(selectedPriority);

            // Ενημέρωση του UI: λίστες των priorities, ComboBoxes και tasks
            updatePriorityList();
            updatePriorityComboBoxes();
            updateTaskTable();
            updateLabels();
            updateReminderList(); // αν υπάρχει UI για υπενθυμίσεις

            System.out.println("Priority '" + selectedPriority + "' deleted. All tasks reassigned to 'Default'.");
        }
    }


    @FXML
    private void showTasksPane() {
        clearButtonStyles();
        tasksButton.setStyle(selectedButtonStyle);
        animateButton(tasksButton);

        tasksPane.setVisible(true);
        categoriesPane.setVisible(false);
        remindersPane.setVisible(false);
        prioritiesPane.setVisible(false);
        tasksPane.toFront();
        updateActivePane();
    }

    @FXML
    private void showCategoriesPane() {
        clearButtonStyles();
        categoriesButton.setStyle(selectedButtonStyle);
        animateButton(categoriesButton);

        tasksPane.setVisible(false);
        categoriesPane.setVisible(true);
        remindersPane.setVisible(false);
        prioritiesPane.setVisible(false);
        categoriesPane.toFront();
        updateActivePane();
    }

    @FXML
    private void showRemindersPane() {
        clearButtonStyles();
        remindersButton.setStyle(selectedButtonStyle);
        animateButton(remindersButton);

        tasksPane.setVisible(false);
        categoriesPane.setVisible(false);
        remindersPane.setVisible(true);
        prioritiesPane.setVisible(false);
        remindersPane.toFront();
        updateActivePane();
    }

    @FXML
    private void showPrioritiesPane() {
        clearButtonStyles();
        prioritiesButton.setStyle(selectedButtonStyle);
        animateButton(prioritiesButton);

        tasksPane.setVisible(false);
        categoriesPane.setVisible(false);
        remindersPane.setVisible(false);
        prioritiesPane.setVisible(true);
        prioritiesPane.toFront();
        updateActivePane();
    }

    // Ενημέρωση λίστας εργασιών
    private void updateTaskList(String categoryFilter) {
        List<Task> filteredTasks = taskManager.getTasks().stream()
                .filter(task -> categoryFilter == null
                        || categoryFilter.equals("All Categories")
                        || task.getCategory().equals(categoryFilter))
                .collect(Collectors.toList());
        taskTableView.getItems().setAll(filteredTasks);
    }

    private void updateTaskListForSearch(List<Task> tasks) {
        // Καθαρίζουμε το TableView
        taskTableView.getItems().clear();

        if (tasks.isEmpty()) {
            // Αν δεν υπάρχουν tasks, ορίζουμε ένα placeholder που θα εμφανίζεται στον πίνακα
            taskTableView.setPlaceholder(new Label("No tasks found."));
        } else {
            // Γεμίζουμε το TableView με τα tasks
            taskTableView.getItems().setAll(tasks);
        }
    }

    // Ενημέρωση λίστας κατηγοριών
    private void updateCategoryList() {
        categoryListView.getItems().clear();
        for (Category c : taskManager.getCategories()) {
            // Παραλείπουμε την Default κατηγορία
            if (!c.getName().equalsIgnoreCase("Default")) {
                categoryListView.getItems().add(c.getName());
            }
        }
    }

    private void updateCategoryListForSearch(List<Category> categories) {
        categoryListView.getItems().clear();
        if (categories.isEmpty()) {
            categoryListView.getItems().add("No categories found.");
        } else {
            categories.forEach(category -> {
                if (!category.getName().equalsIgnoreCase("Default")) {
                    categoryListView.getItems().add(category.getName());
                }
            });
        }
    }

    // Ενημέρωση λίστας υπενθυμίσεων
    private void updateReminderList() {
        reminderListView.getItems().clear();
        for (Reminder reminder : taskManager.getReminders()) {
            reminderListView.getItems().add(formatReminder(reminder));
        }
    }



    private void updateReminderListForSearch(List<Reminder> reminders) {
        reminderListView.getItems().clear();
        if (reminders.isEmpty()) {
            reminderListView.getItems().add("No reminders found.");
        } else {
            reminders.forEach(reminder -> reminderListView.getItems().add(reminder.getReminderInfo()));
        }
    }

    private void updatePriorityList() {
        priorityListView.getItems().clear();
        for (Priority p : taskManager.getPriorities()) {
            priorityListView.getItems().add(p.getName());
        }
    }

    // Ενημέρωση των labels στατιστικών
    private void updateLabels() {
        List<Task> tasks = taskManager.getTasks();
        totalTasksLabel.setText("Total Tasks: " + tasks.size());
        completedTasksLabel.setText("Completed Tasks: " + tasks.stream().filter(task -> task.getStatus() == Task.Status.COMPLETED).count());
        delayedTasksLabel.setText("Delayed Tasks: " + tasks.stream().filter(task -> task.getStatus() == Task.Status.DELAYED).count());
        upcomingTasksLabel.setText("Upcoming Tasks: " +
                tasks.stream().filter(task -> task.getDeadline() != null && task.getDeadline().isBefore(LocalDate.now().plusDays(7))).count());
    }

    // Προσθήκη νέας εργασίας (με popup παράθυρο όπως στο MainGUI)
    @FXML
    private void addTask() {
        Stage addTaskStage = new Stage();
        addTaskStage.setTitle("Add New Task");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        ComboBox<String> categoryComboBox = new ComboBox<>();
        taskManager.getCategories().forEach(category -> categoryComboBox.getItems().add(category.getName()));
        categoryComboBox.setPromptText("Select Category");

        ComboBox<String> priorityComboBox = new ComboBox<>();
        taskManager.getPriorities().forEach(priority -> priorityComboBox.getItems().add(priority.getName()));
        priorityComboBox.setPromptText("Select Priority");

        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setPromptText("Deadline");

        Button saveButton = new Button("Save Task");
        saveButton.setOnAction(e -> {
            String title = titleField.getText();
            String description = descriptionField.getText();
            String category = categoryComboBox.getValue();
            String priority = priorityComboBox.getValue();
            LocalDate deadline = deadlinePicker.getValue();

            if (deadline != null && deadline.isBefore(LocalDate.now())) {
                showAlert("Invalid Deadline", "The deadline cannot be in the past.");
                return;
            }

            if (title != null && !title.isEmpty() && category != null && priority != null && deadline != null) {
                Task newTask = new Task(title, description, category, priority, deadline, Task.Status.OPEN);
                taskManager.addTask(newTask);
                updateTaskList(null);
                updateLabels();
                updatePieChart();
                addTaskStage.close();
            } else {
                showAlert("Invalid Input", "Please fill all fields.");
            }
        });

        vbox.getChildren().addAll(titleField, descriptionField, categoryComboBox, priorityComboBox, deadlinePicker, saveButton);
        Scene scene = new Scene(vbox, 300, 400);
        addTaskStage.setScene(scene);
        addTaskStage.show();
        updatePieChart();
    }

    // Επεξεργασία εργασίας
    @FXML
    private void editTask() {
        // Αντί για getSelectedIndex(), παίρνουμε το επιλεγμένο Task από το TableView
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            // selectedTask είναι ήδη το Task που θέλουμε να επεξεργαστούμε
            Task taskToEdit = selectedTask;
            Stage editTaskStage = new Stage();
            editTaskStage.setTitle("Edit Task");

            VBox vbox = new VBox(10);
            vbox.setPadding(new Insets(20));

            TextField titleField = new TextField(taskToEdit.getTitle());
            TextField descriptionField = new TextField(taskToEdit.getDescription());

            ComboBox<String> categoryComboBox = new ComboBox<>();
            taskManager.getCategories().forEach(category -> categoryComboBox.getItems().add(category.getName()));
            categoryComboBox.setValue(taskToEdit.getCategory());

            ComboBox<String> priorityComboBox = new ComboBox<>();
            taskManager.getPriorities().forEach(priority -> priorityComboBox.getItems().add(priority.getName()));
            priorityComboBox.setValue(taskToEdit.getPriority());

            DatePicker deadlinePicker = new DatePicker(taskToEdit.getDeadline());

            ComboBox<Task.Status> statusComboBox = new ComboBox<>();
            statusComboBox.getItems().addAll(Task.Status.values());
            statusComboBox.setValue(taskToEdit.getStatus());

            Button saveButton = new Button("Save Changes");
            saveButton.setOnAction(e -> {
                String newTitle = titleField.getText();
                String newDescription = descriptionField.getText();
                String newCategory = categoryComboBox.getValue();
                String newPriority = priorityComboBox.getValue();
                LocalDate newDeadline = deadlinePicker.getValue();
                Task.Status newStatus = statusComboBox.getValue();

                if (newDeadline != null && newDeadline.isBefore(LocalDate.now())) {
                    showAlert("Invalid Deadline", "The deadline cannot be in the past.");
                    return;
                }

                if (newTitle != null && !newTitle.isEmpty() && newCategory != null
                        && newPriority != null && newDeadline != null && newStatus != null) {
                    if (newStatus == Task.Status.COMPLETED) {
                        taskManager.completeTask(taskToEdit);
                    } else {
                        taskManager.updateTask(taskToEdit, newTitle, newDescription, newCategory, newPriority, newDeadline, newStatus);
                    }                    // Ενημέρωσε το TableView μετά την αλλαγή
                    updateTaskTable();
                    updateLabels();
                    updatePieChart();
                    updateReminderList();
                    editTaskStage.close();
                } else {
                    showAlert("Invalid Input", "Please fill all the fields.");
                }
            });

            vbox.getChildren().addAll(titleField, descriptionField, categoryComboBox, priorityComboBox, deadlinePicker, statusComboBox, saveButton);
            Scene scene = new Scene(vbox, 300, 500);
            editTaskStage.setScene(scene);
            editTaskStage.show();
        } else {
            showAlert("No Task Selected", "Please select a task to edit.");
        }
    }

    // Διαγραφή εργασίας
    @FXML
    private void deleteTask() {
        // Παίρνουμε το επιλεγμένο Task από το TableView
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Task");
            alert.setHeaderText("Are you sure you want to delete this task?");
            alert.setContentText("This action cannot be undone.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Βρίσκουμε το index του επιλεγμένου task στη λίστα
                int taskIndex = taskManager.getTasks().indexOf(selectedTask);
                taskManager.deleteTask(taskIndex);
                updateTaskTable();  // ενημέρωση του TableView
                updateLabels();
                updatePieChart();
                updateReminderList();
            }
        } else {
            showAlert("No Task Selected", "Please select a task to delete.");
        }
    }

// --- Λειτουργίες για Categories ---

    @FXML
    private void addCategory() {
        Stage addCategoryStage = new Stage();
        addCategoryStage.setTitle("Add Category");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category Name");

        Button saveButton = new Button("Save Category");
        saveButton.setOnAction(e -> {
            String categoryName = categoryField.getText();
            if (categoryName != null && !categoryName.isEmpty()) {
                taskManager.addCategory(new Category(categoryName));
                updateCategoryList();
                addCategoryStage.close();
            } else {
                showAlert("Invalid Input", "Please enter a valid category name.");
            }
        });

        vbox.getChildren().addAll(categoryField, saveButton);
        Scene scene = new Scene(vbox, 300, 200);
        addCategoryStage.setScene(scene);
        addCategoryStage.show();
    }

    @FXML
    private void editCategory() {
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            TextInputDialog dialog = new TextInputDialog(selectedCategory);
            dialog.setTitle("Edit Category");
            dialog.setHeaderText("Edit the category name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newCategoryName -> {
                taskManager.updateCategoryName(selectedCategory, newCategoryName);
                updateCategoryList();
                updateTaskTable();
            });
        } else {
            showAlert("No Category Selected", "Please select a category to edit.");
        }
    }

    @FXML
    private void deleteCategory() {
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Category");
            alert.setHeaderText("Are you sure you want to delete this category?");
            alert.setContentText("This will remove all tasks in this category.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                taskManager.deleteCategory(selectedCategory);
                updateCategoryList();
                updateTaskList(null);
                updateReminderList();
                updatePieChart();
            }
        } else {
            showAlert("No Category Selected", "Please select a category to delete.");
        }
    }

// --- Λειτουργίες για Reminders ---

    @FXML
    private void addReminder() {
        Stage addReminderStage = new Stage();
        addReminderStage.setTitle("Add Reminder");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        ComboBox<String> taskComboBox = new ComboBox<>();
        taskManager.getTasks().stream()
                .filter(t -> t.getStatus() != Task.Status.COMPLETED)
                .forEach(task -> taskComboBox.getItems().add(task.getTitle()));
        taskComboBox.setPromptText("Select Task");

        ComboBox<String> reminderTypeComboBox = new ComboBox<>();
        reminderTypeComboBox.getItems().addAll("1 day before", "1 week before", "1 month before", "Custom date");
        reminderTypeComboBox.setPromptText("Select Reminder Type");

        DatePicker reminderDatePicker = new DatePicker();
        reminderDatePicker.setPromptText("Reminder Date");
        reminderDatePicker.setDisable(true); // Αρχικά απενεργοποιημένο

        // Ενεργοποίηση DatePicker μόνο αν επιλεγεί "Custom date"
        reminderTypeComboBox.setOnAction(e -> {
            boolean isCustom = reminderTypeComboBox.getValue().equals("Custom date");
            reminderDatePicker.setDisable(!isCustom);
            if (!isCustom) {
                reminderDatePicker.setValue(null); // Καθαρίζει προηγούμενη τιμή αν αλλάξει ο τύπος
            }
        });

        Button saveButton = new Button("Save Reminder");
        saveButton.setOnAction(e -> {
            String selectedTaskTitle = taskComboBox.getValue();
            String selectedReminderType = reminderTypeComboBox.getValue();

            if (selectedTaskTitle == null || selectedReminderType == null) {
                showAlert("Invalid Input", "Please select a task and a reminder type.");
                return;
            }

            Task task = taskManager.getTasks().stream()
                    .filter(t -> t.getTitle().equals(selectedTaskTitle))
                    .findFirst().orElse(null);

            if (task == null) {
                showAlert("Invalid Task", "Selected task does not exist.");
                return;
            }

            LocalDate computedReminderDate = null;
            LocalDate deadline = task.getDeadline();

            switch (selectedReminderType) {
                case "1 day before":
                    computedReminderDate = deadline.minusDays(1);
                    break;
                case "1 week before":
                    computedReminderDate = deadline.minusWeeks(1);
                    break;
                case "1 month before":
                    computedReminderDate = deadline.minusMonths(1);
                    break;
                case "Custom date":
                    computedReminderDate = reminderDatePicker.getValue();
                    if (computedReminderDate == null) {
                        showAlert("Invalid Input", "Please select a valid reminder date.");
                        return;
                    }
                    break;
                default:
                    showAlert("Invalid Selection", "Please select a valid reminder type.");
                    return;
            }

            if (computedReminderDate.isBefore(LocalDate.now())) {
                showAlert("Invalid Date", "The reminder date cannot be in the past.");
                return;
            }

            Reminder.ReminderType reminderType;
            switch (selectedReminderType) {
                case "1 day before":
                    reminderType = Reminder.ReminderType.ONE_DAY_BEFORE;
                    break;
                case "1 week before":
                    reminderType = Reminder.ReminderType.ONE_WEEK_BEFORE;
                    break;
                case "1 month before":
                    reminderType = Reminder.ReminderType.ONE_MONTH_BEFORE;
                    break;
                case "Custom date":
                    reminderType = Reminder.ReminderType.SPECIFIC_DATE;
                    break;
                default:
                    return;
            }

            Reminder newReminder = new Reminder(task, reminderType, computedReminderDate);
            taskManager.addReminder(newReminder);
            updateReminderList();
            addReminderStage.close();
        });

        vbox.getChildren().addAll(taskComboBox, reminderTypeComboBox, reminderDatePicker, saveButton);
        Scene scene = new Scene(vbox, 300, 300);
        addReminderStage.setScene(scene);
        addReminderStage.show();
    }


    @FXML
    private void deleteReminder() {
        String selectedReminderInfo = reminderListView.getSelectionModel().getSelectedItem();
        if (selectedReminderInfo != null) {
            Reminder reminderToDelete = taskManager.getReminders().stream()
                    .filter(r -> r.getReminderInfo().equals(selectedReminderInfo))
                    .findFirst().orElse(null);
            if (reminderToDelete != null) {
                taskManager.deleteReminder(reminderToDelete);
                updateReminderList();
            } else {
                showAlert("Error", "Selected reminder not found!");
            }
        } else {
            showAlert("No Reminder Selected", "Please select a reminder to delete.");
        }
    }

// --- Λειτουργίες αναζήτησης ---

    @FXML
    private void searchTasks() {
        String title = searchTaskField.getText();
        String priority = searchPriorityComboBox.getValue().equals("All Priorities") ? null : searchPriorityComboBox.getValue();
        String category = null; // Αν υπάρχει επιπλέον πεδίο για κατηγορία μπορείτε να το χρησιμοποιήσετε
        LocalDate deadline = deadlineSearchPicker.getValue();

        List<Task> filteredTasks = taskManager.searchTasks(title, category, priority, deadline);
        updateTaskListForSearch(filteredTasks);
    }

    @FXML
    private void searchCategories() {
        String query = searchCategoryField.getText();
        List<Category> filteredCategories = taskManager.searchCategories(query);
        updateCategoryListForSearch(filteredCategories);
    }

    @FXML
    private void searchPriorities() {
        String query = searchPriorityField.getText();
        List<Priority> filteredPriorities = taskManager.searchPriorities(query);
        updatePriorityListForSearch(filteredPriorities);
    }

    @FXML
    private void searchReminders() {
        String query = searchReminderField.getText();
        List<Reminder> filteredReminders = taskManager.searchReminders(query);
        updateReminderListForSearch(filteredReminders);
    }

    private void updatePriorityListForSearch(List<Priority> filteredPriorities) {
        priorityListView.getItems().clear();
        for (Priority p : filteredPriorities) {
            priorityListView.getItems().add(p.getName());
        }
    }

    private void updatePriorityComboBoxes() {
        searchPriorityComboBox.getItems().clear();
        // Προσθέτουμε την επιλογή "All Priorities" ή ό,τι θέλεις ως προεπιλεγμένη επιλογή
        searchPriorityComboBox.getItems().add("All Priorities");
        // Προσθέτουμε όλες τις προτεραιότητες που υπάρχουν στο TaskManager
        for (Priority p : taskManager.getPriorities()) {
            searchPriorityComboBox.getItems().add(p.getName());
        }
        // Μπορείς επίσης να ορίσεις προεπιλεγμένη τιμή, π.χ. "All Priorities"
        searchPriorityComboBox.setValue("All Priorities");
    }

    // Μέθοδος εμφάνισης alert
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearButtonStyles() {
        tasksButton.setStyle(defaultButtonStyle);
        tasksButton.setScaleX(1.0);
        tasksButton.setScaleY(1.0);

        categoriesButton.setStyle(defaultButtonStyle);
        categoriesButton.setScaleX(1.0);
        categoriesButton.setScaleY(1.0);

        remindersButton.setStyle(defaultButtonStyle);
        remindersButton.setScaleX(1.0);
        remindersButton.setScaleY(1.0);

        prioritiesButton.setStyle(defaultButtonStyle);
        prioritiesButton.setScaleX(1.0);
        prioritiesButton.setScaleY(1.0);
    }


    private void animateButton(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(500), button);
        st.setToX(1.5);
        st.setToY(1.5);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

}