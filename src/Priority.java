public class Priority {
    private String name;

    // Constructor
    public Priority(String name) {
        this.name = name;
    }

    // Getters και Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Priority{" +
                "name='" + name + '\'' +
                '}';
    }
}
