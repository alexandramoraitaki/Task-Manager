public class Category {
    private String name;

    // Constructor
    public Category(String name) {
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
        return "Category{" +
                "name='" + name + '\'' +
                '}';
    }
}
