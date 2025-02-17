import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private static TaskManager instance;

    private List<Task> tasks; // Λίστα για τις εργασίες
    private List<Category> categories; // Λίστα για τις κατηγορίες
    private List<Priority> priorities; // Λίστα για τις προτεραιότητες
    private List<Reminder> reminders; // Λίστα για τις υπενθυμίσεις

    private static final String DIRECTORY = "medialab/";

    // Constructor
    public TaskManager() {
        this.tasks = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.priorities = new ArrayList<>();
        this.reminders = new ArrayList<>();

        Priority defaultPriority = new Priority("Default");
        priorities.add(defaultPriority);
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    // Μέθοδος για αποθήκευση δεδομένων σε JSON
    public void saveToJSON() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();

        try {
            // Δημιουργία φακέλου αν δεν υπάρχει
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(DIRECTORY));

            // Αποθήκευση εργασιών
            try (FileWriter tasksWriter = new FileWriter(DIRECTORY + "tasks.json")) {
                gson.toJson(tasks, tasksWriter);
            }

            // Αποθήκευση κατηγοριών
            try (FileWriter categoriesWriter = new FileWriter(DIRECTORY + "categories.json")) {
                gson.toJson(categories, categoriesWriter);
            }

            // Αποθήκευση προτεραιοτήτων
            try (FileWriter prioritiesWriter = new FileWriter(DIRECTORY + "priorities.json")) {
                gson.toJson(priorities, prioritiesWriter);
            }

            // Αποθήκευση υπενθυμίσεων
            try (FileWriter remindersWriter = new FileWriter(DIRECTORY + "reminders.json")) {
                gson.toJson(reminders, remindersWriter);
            }

            System.out.println("Data saved to JSON files.");
        } catch (IOException e) {
            System.out.println("Error saving data to JSON: " + e.getMessage());
        }
    }

    // Μέθοδος για ανάκτηση δεδομένων από JSON
    public void loadFromJSON() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        try {
            // Φόρτωση εργασιών
            try (FileReader tasksReader = new FileReader(DIRECTORY + "tasks.json")) {
                Type taskListType = new TypeToken<ArrayList<Task>>() {}.getType();
                tasks = gson.fromJson(tasksReader, taskListType);
                if (tasks == null) {
                    tasks = new ArrayList<>();
                }
            }

            // Φόρτωση κατηγοριών
            try (FileReader categoriesReader = new FileReader(DIRECTORY + "categories.json")) {
                Type categoryListType = new TypeToken<ArrayList<Category>>() {}.getType();
                categories = gson.fromJson(categoriesReader, categoryListType);
                if (categories == null) {
                    categories = new ArrayList<>();
                }
            }

            // Φόρτωση προτεραιοτήτων
            try (FileReader prioritiesReader = new FileReader(DIRECTORY + "priorities.json")) {
                Type priorityListType = new TypeToken<ArrayList<Priority>>() {}.getType();
                priorities = gson.fromJson(prioritiesReader, priorityListType);
                if (priorities == null) {
                    priorities = new ArrayList<>();
                }
            }

            // Φόρτωση υπενθυμίσεων
            try (FileReader remindersReader = new FileReader(DIRECTORY + "reminders.json")) {
                Type reminderListType = new TypeToken<ArrayList<Reminder>>() {}.getType();
                reminders = gson.fromJson(remindersReader, reminderListType);
                if (reminders == null) {
                    reminders = new ArrayList<>();
                }
            }

            System.out.println("Data loaded from JSON files.");
        } catch (IOException e) {
            System.out.println("Error loading data from JSON: " + e.getMessage());
        }
    }

    // Μέθοδος για προσθήκη εργασίας
    public void addTask(Task task) {
        this.tasks.add(task);
        System.out.println("Task added: " + task.getTitle());
    }

    public void updateTaskStatuses() {
        LocalDate today = LocalDate.now();
        for (Task task : tasks) {
            if (task.getStatus() != Task.Status.COMPLETED && task.getDeadline() != null && task.getDeadline().isBefore(today)) {
                task.setStatus(Task.Status.DELAYED);
            }
        }
        saveToJSON(); // Αποθήκευση των αλλαγών στο αρχείο
    }


    // Μέθοδος για επεξεργασία εργασίας (επιλεκτικά)
    public void updateTask(Task task, String newTitle, String newDescription, String newCategory, String newPriority, LocalDate newDeadline, Task.Status newStatus) {
        // Ενημέρωση των στοιχείων της εργασίας
        task.setTitle(newTitle);
        task.setDescription(newDescription);
        task.setCategory(newCategory);
        task.setPriority(newPriority);
        task.setDeadline(newDeadline);
        task.setStatus(newStatus);

        System.out.println("Task updated: " + task.getTitle());
    }

    // Μέθοδος για διαγραφή εργασίας
    public void deleteTask(int taskId) {
        if (taskId >= 0 && taskId < tasks.size()) {
            Task removedTask = tasks.remove(taskId);
            // Διαγραφή υπενθυμίσεων για την εργασία
            reminders.removeIf(reminder -> reminder.getTask().equals(removedTask));
            System.out.println("Task deleted: " + removedTask.getTitle());
        } else {
            System.out.println("Task ID not found!");
        }
    }

    // Μέθοδος για προσθήκη κατηγορίας
    public void addCategory(Category category) {
        this.categories.add(category);
        System.out.println("Category added: " + category.getName());
    }

    public void updateCategoryName(String oldName, String newName) {
        // Βρες την κατηγορία και ενημέρωσε το όνομα
        for (Category category : categories) {
            if (category.getName().equals(oldName)) {
                category.setName(newName); // Ενημέρωση του ονόματος της κατηγορίας
            }
        }

        // Ενημέρωση των εργασιών που ανήκουν σε αυτή την κατηγορία
        for (Task task : tasks) {
            if (task.getCategory().equals(oldName)) {
                task.setCategory(newName); // Ενημέρωση κατηγορίας στις εργασίες
            }
        }

        System.out.println("Category updated: " + oldName + " -> " + newName);
    }

    // Μέθοδος για διαγραφή κατηγορίας
    public void deleteCategory(String categoryName) {
        // Διαγραφή της κατηγορίας από τη λίστα
        categories.removeIf(category -> category.getName().equalsIgnoreCase(categoryName));

        // Συλλογή των εργασιών που ανήκουν στην κατηγορία
        List<Task> tasksToDelete = tasks.stream()
                .filter(task -> task.getCategory().equalsIgnoreCase(categoryName))
                .collect(Collectors.toList());

        // DEBUGGING: Εκτύπωση των υπενθυμίσεων πριν τη διαγραφή
        System.out.println("Reminders before deletion: " + reminders.size());

        // Διαγραφή υπενθυμίσεων για τις εργασίες που διαγράφονται
        reminders.removeIf(reminder -> {
            boolean shouldRemove = tasksToDelete.contains(reminder.getTask());
            if (shouldRemove) {
                System.out.println("Deleting reminder for task: " + reminder.getTask().getTitle());
            }
            return shouldRemove;
        });

        // DEBUGGING: Εκτύπωση των υπενθυμίσεων μετά τη διαγραφή
        System.out.println("Reminders after deletion: " + reminders.size());

        // Διαγραφή των εργασιών
        tasks.removeAll(tasksToDelete);

        System.out.println("Category deleted: " + categoryName);
    }

    // Μέθοδος για προσθήκη προτεραιότητας
    public void addPriority(Priority priority) {
        this.priorities.add(priority);
        System.out.println("Priority added: " + priority.getName());
    }

    public void updatePriorityName(String oldPriorityName, String newPriorityName) {
        for (Priority priority : priorities) {
            if (priority.getName().equals(oldPriorityName)) {
                priority.setName(newPriorityName);
                break;
            }
        }
        boolean taskUpdated = false;
        // Ενημέρωση όλων των εργασιών που είχαν την παλιά προτεραιότητα
        for (Task task : tasks) {
            if (task.getPriority().equals(oldPriorityName)) {
                task.setPriority(newPriorityName);
                taskUpdated = true;
            }
        }

        if(taskUpdated) {
            saveToJSON(); //  Αποθήκευση αλλαγών στα JSON
            System.out.println("Updated tasks with new priority: " + newPriorityName);
        } else {
            System.out.println("No tasks found with priority: " + oldPriorityName);
        }

    }

    // Μέθοδος για διαγραφή προτεραιότητας
    public void deletePriority(String priorityName) {
        if (priorityName.equals("Default")) {
            System.out.println("Cannot delete the default priority!");
            return;
        }

        boolean removed = priorities.removeIf(priority -> priority.getName().equals(priorityName));

        if (removed) {
            // Ενημέρωση εργασιών: όλες οι εργασίες που είχαν αυτή την προτεραιότητα παίρνουν την "Default"
            for (Task task : tasks) {
                if (task.getPriority().equals(priorityName)) {
                    task.setPriority("Default");
                }
            }
            System.out.println("Priority deleted: " + priorityName);
        } else {
            System.out.println("Priority not found: " + priorityName);
        }
    }

    // Μέθοδος για προσθήκη υπενθύμισης
    public void addReminder(Reminder reminder) {
        if (reminder.getTask().getStatus() != Task.Status.COMPLETED) {
            reminders.add(reminder);
            System.out.println("Reminder added for task: " + reminder.getTask().getTitle());
        } else {
            System.out.println("Cannot add reminder for a completed task!");
        }
    }

    public List<Priority> searchPriorities(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(priorities);
        }
        String lowerQuery = query.toLowerCase();
        return priorities.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    public void completeTask(Task task) {
        task.setStatus(Task.Status.COMPLETED);
        long countBefore = reminders.size();
        reminders.removeIf(reminder -> {
            boolean matches = reminder.getTask().equals(task);
            if (matches) {
                System.out.println("Removing reminder: " + reminder);
            }
            return matches;
        });
        long countAfter = reminders.size();
        System.out.println("Removed " + (countBefore - countAfter) + " reminders for task: " + task.getTitle());
    }



    public void updateReminder(Reminder reminder) {
        // Αν βρεις την υπενθύμιση στον πίνακα, ανανέωσέ την με τα νέα δεδομένα
        for (Reminder existingReminder : reminders) {
            if (existingReminder.getTask().equals(reminder.getTask()) && existingReminder.getReminderDate().equals(reminder.getReminderDate())) {
                existingReminder.setReminderType(reminder.getReminderType());
                existingReminder.setReminderDate(reminder.getReminderDate());  // Ενημερώνουμε την υπενθύμιση
                return;  // Τερματίζουμε μόλις βρούμε την υπενθύμιση
            }
        }
    }

    // Μέθοδος για διαγραφή υπενθύμισης
    public void deleteReminder(Reminder reminder) {
        reminders.remove(reminder);
        System.out.println("Reminder deleted.");
    }

    public List<Reminder> searchReminders(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(reminders); // Επιστρέφει όλες τις υπενθυμίσεις αν δεν υπάρχει φίλτρο
        }

        return reminders.stream()
                .filter(reminder -> reminder.getTask().getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Category> searchCategories(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(categories); // Αν δεν υπάρχει φίλτρο, επιστρέφει όλες τις κατηγορίες
        }

        return categories.stream()
                .filter(category -> category.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Μέθοδος για αναζήτηση εργασιών με βάση κατηγορία
    public List<Task> searchTasks(String title, String category, String priority) {
        // Ξεκινάμε με όλες τις εργασίες
        List<Task> result = new ArrayList<>(tasks);

        // 1. Φιλτράρισμα βάσει τίτλου (αν έχει δοθεί)
        if (title != null && !title.trim().isEmpty()) {
            String lowerTitle = title.trim().toLowerCase();
            result = result.stream()
                    .filter(t -> t.getTitle() != null && t.getTitle().toLowerCase().contains(lowerTitle))
                    .collect(Collectors.toList());
        }

        // 2. Φιλτράρισμα βάσει κατηγορίας (αν έχει δοθεί)
        if (category != null && !category.trim().isEmpty() && !category.equals("All Categories")) {
            String lowerCategory = category.trim().toLowerCase();
            result = result.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().toLowerCase().contains(lowerCategory))
                    .collect(Collectors.toList());
        }

        // 3. Φιλτράρισμα βάσει προτεραιότητας (αν έχει δοθεί)
        if (priority != null && !priority.trim().isEmpty() && !priority.equals("All Priorities")) {
            String lowerPriority = priority.trim().toLowerCase();
            result = result.stream()
                    .filter(t -> t.getPriority() != null && t.getPriority().toLowerCase().contains(lowerPriority))
                    .collect(Collectors.toList());
        }

        return result;
    }


    public List<Category> getCategories() {
        return this.categories;
    }

    public List<Reminder> getReminders() {
        return this.reminders;
    }

    public List<Priority> getPriorities() {
        return priorities;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Task> searchTasks(String title, String category, String priority, LocalDate deadline) {
        return tasks.stream()
                .filter(task -> (title == null || task.getTitle().toLowerCase().contains(title.toLowerCase())) &&
                        (category == null || task.getCategory().equalsIgnoreCase(category)) &&
                        (priority == null || task.getPriority().equalsIgnoreCase(priority)) &&
                        (deadline == null || task.getDeadline().isEqual(deadline) || task.getDeadline().isBefore(deadline)))
                .collect(Collectors.toList());
    }

}