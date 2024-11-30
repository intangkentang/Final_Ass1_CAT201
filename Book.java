public class Book {
    private String title;
    private String author;
    private int ISBN;
    private boolean status; //true if available false if not
    private String borrowerName;

    public Book(String title, String author, int ISBN, boolean status, String borrowerName) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.status = status;
        this.borrowerName = borrowerName == null ? "" : borrowerName; //set to empty string if null
    } //constructor with parameter

    //getter method to pass value to another class
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public int getISBN() {
        return ISBN;
    }
    public boolean getStatus() {
        return status;
    }
    public String getBorrowerName() {
        return borrowerName;
    }

    public boolean borrowBook(String borrowerName) {
        if (status) {
            this.status = false;
            this.borrowerName = borrowerName;
            return true; }
        else {
            return false; }
    } //method to borrow

    public boolean returnBook() {
        if (!status) {
            this.status = true;
            this.borrowerName = "";
            return true; }
        else {
            return false; }
    } //method to return

    public String toCSV() {
        return title + "," + author + "," + ISBN + "," + status + "," + borrowerName;
    } //method to save private data in csv file format
}
