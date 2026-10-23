

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class GroceryGUI extends JFrame {

    private final ArrayList<GroceryItem> items =
            new ArrayList<>();

    private final String fileName = "groceries.txt";

    private final String[] categories = {
        "Fruits", "Vegetables", "Dairy", "Bakery",
        "Snacks", "Beverages", "Household", "Other"
    };

    private JTextField nameField;
    private JTextField quantityField;
    private JTextField priceField;
    private JTextField searchField;

    private JComboBox<String> categoryBox;
    private JComboBox<String> filterBox;

    private JTable table;
    private DefaultTableModel model;
    private JLabel totalLabel;
    private JLabel countLabel;

    // Stores the input panel so it can be placed above the table.
    private JPanel inputPanel;

    public GroceryGUI() {
        setTitle("Grocery List App with Categories");
        setSize(1050, 650);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        getContentPane().setBackground(
                new Color(245, 248, 245));

        createHeader();
        createInputPanel();
        createTable();
        createBottomPanel();

        loadItems();
        refreshTable();
    }

    // Header section
    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(35, 120, 75));
        header.setBorder(
                BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel("My Grocery List");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));

        JLabel subtitle = new JLabel(
                "Organize your shopping and track your budget");
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel textPanel = new JPanel(
                new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
    }

    // Input fields and action buttons
    private void createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                "Add a Grocery Item"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        nameField = new JTextField(12);
        quantityField = new JTextField(6);
        priceField = new JTextField(7);
        categoryBox = new JComboBox<>(categories);

        addInput(panel, gbc, 0, "Item Name", nameField);
        addInput(panel, gbc, 1, "Category", categoryBox);
        addInput(panel, gbc, 2, "Quantity", quantityField);
        addInput(panel, gbc, 3, "Price per Unit (Rs.)", priceField);

        JButton addButton = createButton(
                "Add Item", new Color(35, 120, 75));
        JButton deleteButton = createButton(
                "Delete Selected", new Color(190, 65, 65));

        addButton.addActionListener(e -> addItem());
        deleteButton.addActionListener(e -> deleteItem());

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        panel.add(addButton, gbc);

        gbc.gridx = 2;
        panel.add(deleteButton, gbc);

        // Keep the panel for placement inside the top section.
        inputPanel = panel;
    }

    private void addInput(JPanel panel, GridBagConstraints gbc,
                          int column, String label,
                          JComponent input) {
        gbc.gridx = column;
        gbc.gridy = 0;
        gbc.gridwidth = 1;

        JPanel fieldPanel = new JPanel(
                new GridLayout(2, 1, 0, 5));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.add(new JLabel(label));
        fieldPanel.add(input);

        panel.add(fieldPanel, gbc);
    }

    // Search, filter and grocery table
    private void createTable() {
        String[] columns = {
            "Item Name", "Category", "Quantity",
            "Price (Rs.)", "Subtotal (Rs.)"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(27);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel centerPanel = new JPanel(
                new BorderLayout(8, 8));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Search and category filter controls
        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField(15);
        filterBox = new JComboBox<>();
        filterBox.addItem("All Categories");

        for (String category : categories) {
            filterBox.addItem(category);
        }

        JButton searchButton = createButton(
                "Search / Filter", new Color(55, 105, 170));
        JButton clearButton = createButton(
                "Clear Filters", new Color(100, 100, 100));

        searchButton.addActionListener(e -> refreshTable());

        // Pressing Enter in the search field also searches.
        searchField.addActionListener(e -> refreshTable());

        clearButton.addActionListener(e -> {
            searchField.setText("");
            filterBox.setSelectedIndex(0);
            refreshTable();
        });

        filterBox.addActionListener(e -> refreshTable());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(filterBox);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        // Correct layout: input form and search controls
        // share the top area, above the grocery table.
        JPanel topPanel = new JPanel(
                new BorderLayout(5, 5));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    // Total and item count section
    private void createBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15));

        totalLabel = new JLabel("Total: Rs. 0.00");
        totalLabel.setFont(
                new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setForeground(new Color(35, 120, 75));

        countLabel = new JLabel("Items: 0");
        countLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 14));

        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(countLabel, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Add a grocery item
    private void addItem() {
        String name = nameField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String priceText = priceField.getText().trim();
        String category =
                (String) categoryBox.getSelectedItem();

        if (name.isEmpty() || quantityText.isEmpty()
                || priceText.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (name.contains("|") || name.contains("\n")
                || name.contains("\r")) {
            showError("Item name contains an invalid character.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            double price = Double.parseDouble(priceText);

            if (quantity <= 0) {
                showError("Quantity must be greater than zero.");
                return;
            }

            if (!Double.isFinite(price) || price < 0) {
                showError("Enter a valid non-negative price.");
                return;
            }

            GroceryItem item = new GroceryItem(
                    name, category, quantity, price);

            items.add(item);

            if (!saveItems()) {
                items.remove(item);
                return;
            }

            nameField.setText("");
            quantityField.setText("");
            priceField.setText("");

            refreshTable();

            JOptionPane.showMessageDialog(
                    this, "Grocery item added successfully!");

        } catch (NumberFormatException ex) {
            showError("Quantity must be a whole number and "
                    + "price must be numeric.");
        }
    }

    // Delete the selected grocery item
    private void deleteItem() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            showError("Please select an item to delete.");
            return;
        }

        String itemName =
                model.getValueAt(selectedRow, 0).toString();
        String category =
                model.getValueAt(selectedRow, 1).toString();
        String quantityText =
                model.getValueAt(selectedRow, 2).toString();
        String priceText =
                model.getValueAt(selectedRow, 3).toString();

        GroceryItem target = null;

        for (GroceryItem item : items) {
            boolean sameName =
                    item.getName().equals(itemName);
            boolean sameCategory =
                    item.getCategory().equals(category);
            boolean sameQuantity =
                    String.valueOf(item.getQuantity())
                            .equals(quantityText);

            // Compare prices consistently to two decimal places.
            boolean samePrice =
                    String.format("%.2f", item.getPrice())
                            .equals(priceText);

            if (sameName && sameCategory
                    && sameQuantity && samePrice) {
                target = item;
                break;
            }
        }

        if (target == null) {
            showError("Could not find the selected item.");
            return;
        }

        items.remove(target);

        if (!saveItems()) {
            items.add(target);
            refreshTable();
            return;
        }

        refreshTable();
    }

    // Refresh table based on search and category selection
    private void refreshTable() {
        if (model == null) {
            return;
        }

        model.setRowCount(0);

        String search = searchField == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        String selectedCategory = filterBox == null
                ? "All Categories"
                : (String) filterBox.getSelectedItem();

        for (GroceryItem item : items) {
            boolean matchesName = item.getName()
                    .toLowerCase().contains(search);

            boolean matchesCategory =
                    selectedCategory == null
                    || selectedCategory.equals("All Categories")
                    || item.getCategory().equals(selectedCategory);

            if (matchesName && matchesCategory) {
                model.addRow(new Object[] {
                    item.getName(),
                    item.getCategory(),
                    item.getQuantity(),
                    String.format("%.2f", item.getPrice()),
                    String.format("%.2f", item.getSubtotal())
                });
            }
        }

        // Total represents all saved items, not just filtered rows.
        double total = 0.0;

        for (GroceryItem item : items) {
            total += item.getSubtotal();
        }

        totalLabel.setText(
                String.format("Total: Rs. %.2f", total));

        countLabel.setText(
                "Items: " + items.size()
                + " | Showing: " + model.getRowCount());
    }

    // Save all grocery items to a text file
    private boolean saveItems() {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(fileName))) {

            for (GroceryItem item : items) {
                writer.write(item.toFileString());
                writer.newLine();
            }

            return true;

        } catch (IOException ex) {
            showError("Could not save grocery data:\n"
                    + ex.getMessage());
            return false;
        }
    }

    // Load saved grocery items when the app starts
    private void loadItems() {
        File file = new File(fileName);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                try {
                    GroceryItem item =
                            GroceryItem.fromFileString(line);
                    items.add(item);

                } catch (RuntimeException ex) {
                    System.err.println(
                            "Skipping invalid record at line "
                            + lineNumber);
                }
            }

        } catch (IOException ex) {
            showError("Could not load saved data:\n"
                    + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Grocery List App",
                JOptionPane.ERROR_MESSAGE);
    }
}
