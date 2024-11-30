import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class LibAppController { //controller class for application
    private Library lib;
    public ObservableList<Book> bookList; //to bind data in table

    //fxml element declaration
    @FXML
    private TextField searchTitle, searchAuthor, searchISBN;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> titleColumn, authorColumn, borrowerColumn;
    @FXML
    private TableColumn<Book, Integer> isbnColumn;
    @FXML
    private TableColumn<Book, Boolean> statusColumn;
    @FXML
    private TextField newBookTitle, newBookAuthor, newBookISBN;
    @FXML
    private CheckBox newBookStatus;

    @FXML
    public void initialize() { //initialize controller, set up and load data
        lib = new Library();
        bookList = FXCollections.observableArrayList(lib.getBooks());

        //map table column to Book class private members
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        borrowerColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));

        bookTable.setItems(bookList); //bind book list to table
    } //initialize library and table

    @FXML
    public void handleSearch() {

        String title = searchTitle.getText().trim();
        String author = searchAuthor.getText().trim();
        String isbnText = searchISBN.getText().trim();

        bookList.clear(); //clear any activity before

        if(!title.isEmpty()) {
            Book bookByTitle = lib.searchBookByTitle(title);
            if(bookByTitle != null) bookList.add(bookByTitle); //if book's title found, display

        }
        else if(!author.isEmpty()) {
            Book bookByAuthor = lib.searchBookByAuthor(author);
            if(bookByAuthor != null) bookList.add(bookByAuthor); //if book's author found, display
        }
        else if(!isbnText.isEmpty()) {
            try {
                int isbn = Integer.parseInt(isbnText);
                Book bookByISBN = lib.searchBookByISBN(isbn);
                if (bookByISBN != null) {
                    bookList.add(bookByISBN);
                }
            }
            catch (NumberFormatException e)
            { showAlert(Alert.AlertType.ERROR, "Error", "ISBN must be only in integer.");}
        }
    } //method to search book

    @FXML
    public void handleBorrow(){
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) { //exception if no selected
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to borrow.");
            return;
        }

        if (!selectedBook.getStatus()) { //exception if book status is false
            showAlert(Alert.AlertType.INFORMATION, "Book Not Available", "The selected book is not available in the library.");
            return;
        }
        //method to borrow book
        if(selectedBook != null && selectedBook.getStatus()) {
            TextInputDialog dialog = new TextInputDialog(); //create new input text
            dialog.setTitle("Borrow Book");
            dialog.setHeaderText("Enter borrower name: ");
            dialog.showAndWait().ifPresent(borrowerName-> { //if user enter name
                if(lib.borrowABook(selectedBook, borrowerName)) { //then borrow is success
                    showAlert(Alert.AlertType.INFORMATION, "Success", "You have successfully borrowed the book.");
                    refreshTable(); //refresh to update latest book list
                }
                else { //exception if borrow is not success
                    showAlert(Alert.AlertType.WARNING, "Book Unavailable", "The book is not available.");
                }
            });
        }
    } //method to borrow book

    @FXML
    public void handleReturn() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) { //error bcs no selected row
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to return.");
            return;
        }

        if (selectedBook.getStatus()) { //error because book status is true
            showAlert(Alert.AlertType.INFORMATION, "Book Not Borrowed", "The selected book is already available in the library.");
            return;
        }
        //method to return book
        if (selectedBook != null && !selectedBook.getStatus()) {
            if (lib.returnABook(selectedBook.getISBN())) { //use isbn as unique identity
                showAlert(Alert.AlertType.INFORMATION, "Success", "You have successfully returned the book.");
                refreshTable();
            } else { //return is not success
                showAlert(Alert.AlertType.WARNING, "Error", "The book could not be returned.");
            }
        }
    } //method to return book

    @FXML
    public void handleDisplayAll() {
        bookList.clear();
        bookList.addAll(lib.getBooks());
    } //method display all book

    private void refreshTable() {
        bookList.clear();
        bookList.addAll(lib.getBooks());
    } //method refresh table

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    } //method to show message

    private void clearAddBookFields() {
        newBookTitle.clear();
        newBookAuthor.clear();
        newBookISBN.clear();
        newBookStatus.setSelected(false);
    } //method clear fields add book after new book is added

    @FXML
    public void handleAddBook() {
        String title = newBookTitle.getText().trim();
        String author = newBookAuthor.getText().trim();
        String isbnText = newBookISBN.getText().trim();
        boolean status = newBookStatus.isSelected();
        String borrower = "";

        if (title.isEmpty() || author.isEmpty() || isbnText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            return;
        }

        try {
            int isbn = Integer.parseInt(isbnText); //convert int to string bcs csv file is declared as string
            Book newBook = new Book(title, author, isbn, status,borrower);

            if (lib.searchBookByISBN(isbn) == null) {
                lib.addBook(newBook);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book added successfully.");
                refreshTable();
                clearAddBookFields();
            } else {
                showAlert(Alert.AlertType.WARNING, "Duplicate", "A book with this ISBN already exists.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid ISBN format.");
        }
    }//method to add book

    @FXML
    public void handleSave() {
        try {
            // Save the entire book list to the file
            lib.saveBookList(bookList);
            showAlert(Alert.AlertType.INFORMATION, "Save Successful", "The book data has been saved to the file successfully.");
        } catch (Exception e) {
            System.err.println("An error occurred while saving the file: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Save Failed", "An error occurred while saving the file.");
        }
    } //method to save updated data into csv file
}
