
public class GroceryItem {
    private String name;
    private String category;
    private int quantity;
    private double price;

    public GroceryItem(String name, String category,
                       int quantity, double price) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getSubtotal() {
        return quantity * price;
    }

    // Convert an item into a line for the text file.
    public String toFileString() {
        return name + "|" + category + "|"
                + quantity + "|" + price;
    }

    // Re-create an item from a saved line.
    public static GroceryItem fromFileString(String line) {
        String[] parts = line.split("\\|", -1);

        if (parts.length != 4) {
            throw new IllegalArgumentException(
                    "Invalid grocery record");
        }

        String name = parts[0];
        String category = parts[1];
        int quantity = Integer.parseInt(parts[2]);
        double price = Double.parseDouble(parts[3]);

        if (name.trim().isEmpty()
                || category.trim().isEmpty()
                || quantity <= 0
                || !Double.isFinite(price)
                || price < 0) {
            throw new IllegalArgumentException(
                    "Invalid grocery values");
        }

        return new GroceryItem(
                name, category, quantity, price);
    }
}
