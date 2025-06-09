import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID; // For generating unique IDs

// 1. RoomCategory Enum
enum RoomCategory {
    STANDARD("Standard Room", 100.00, 2),
    DELUXE("Deluxe Room", 150.00, 3),
    SUITE("Suite", 250.00, 4);

    private final String name;
    private final double basePrice;
    private final int capacity;

    RoomCategory(String name, double basePrice, int capacity) {
        this.name = name;
        this.basePrice = basePrice;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public int getCapacity() {
        return capacity;
    }
}

// 2. Room Class
class Room {
    private String roomNumber;
    private RoomCategory category;
    private double pricePerNight;
    private int capacity;
    private Set<LocalDate> bookedDates; // Stores dates this room is booked

    public Room(String roomNumber, RoomCategory category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = category.getBasePrice();
        this.capacity = category.getCapacity();
        this.bookedDates = new HashSet<>();
    }

    // Getters
    public String getRoomNumber() {
        return roomNumber;
    }

    public RoomCategory getCategory() {
        return category;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public int getCapacity() {
        return capacity;
    }

    public Set<LocalDate> getBookedDates() {
        return bookedDates;
    }

    /**
     * Checks if the room is available for the given date range.
     * @param checkInDate
     * @param checkOutDate
     * @return true if available, false otherwise.
     */
    public boolean isAvailable(LocalDate checkInDate, LocalDate checkOutDate) {
        for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
            if (bookedDates.contains(date)) {
                return false; // Room is booked on this date
            }
        }
        return true;
    }

    /**
     * Marks the dates as booked for this room.
     * @param checkInDate
     * @param checkOutDate
     */
    public void bookDates(LocalDate checkInDate, LocalDate checkOutDate) {
        for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
            bookedDates.add(date);
        }
    }

    /**
     * Frees up the dates for this room (e.g., on cancellation).
     * @param checkInDate
     * @param checkOutDate
     */
    public void unbookDates(LocalDate checkInDate, LocalDate checkOutDate) {
        for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
            bookedDates.remove(date);
        }
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + category.getName() + ") - Price: $" + pricePerNight + "/night, Capacity: " + capacity;
    }
}

// 3. User Class
class User {
    private String userId;
    private String username;
    private String password; // In a real app, hash and salt passwords!

    public User(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User ID: " + userId + ", Username: " + username;
    }
}

// 4. BookingStatus Enum
enum BookingStatus {
    CONFIRMED,
    PENDING_PAYMENT,
    CANCELLED,
    CHECKED_IN,
    CHECKED_OUT
}

// 5. Booking Class
class Booking {
    private String bookingId;
    private User user;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalAmount;
    private BookingStatus status;

    public Booking(String bookingId, User user, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.bookingId = bookingId;
        this.user = user;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = calculateTotalAmount();
        this.status = BookingStatus.PENDING_PAYMENT; // Initial status
    }

    // Getters and Setters
    public String getBookingId() {
        return bookingId;
    }

    public User getUser() {
        return user;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    private double calculateTotalAmount() {
        long numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return numberOfNights * room.getPricePerNight();
    }

    @Override
    public String toString() {
        return "Booking ID: " + bookingId +
               "\n  User: " + user.getUsername() +
               "\n  Room: " + room.getRoomNumber() + " (" + room.getCategory().getName() + ")" +
               "\n  Check-in: " + checkInDate +
               "\n  Check-out: " + checkOutDate +
               "\n  Total Amount: $" + String.format("%.2f", totalAmount) +
               "\n  Status: " + status;
    }
}

// 6. PaymentGateway Class (Simulated)
class PaymentGateway {

    public boolean processPayment(double amount) {
        // In a real application, this would integrate with a payment service (e.g., Stripe, PayPal).
        // For simplicity, we'll simulate success.
        System.out.println("Processing payment of $" + String.format("%.2f", amount) + "...");
        System.out.println("Payment successful!");
        return true;
    }
}

// 7. Hotel Class
class Hotel {
    private List<Room> rooms;
    private Map<String, User> users; // username -> User
    private Map<String, Booking> bookings; // bookingId -> Booking
    private PaymentGateway paymentGateway;

    public Hotel() {
        this.rooms = new ArrayList<>();
        this.users = new HashMap<>();
        this.bookings = new HashMap<>();
        this.paymentGateway = new PaymentGateway();
        initializeHotelData(); // Populate with some initial rooms and users
    }

    private void initializeHotelData() {
        // Add some rooms
        rooms.add(new Room("101", RoomCategory.STANDARD));
        rooms.add(new Room("102", RoomCategory.STANDARD));
        rooms.add(new Room("201", RoomCategory.DELUXE));
        rooms.add(new Room("202", RoomCategory.DELUXE));
        rooms.add(new Room("301", RoomCategory.SUITE));

        // Add some users
        registerUser("user1", "pass1");
        registerUser("user2", "pass2");
    }

    public User registerUser(String username, String password) {
        if (users.containsKey(username)) {
            System.out.println("Username already exists.");
            return null;
        }
        String userId = UUID.randomUUID().toString();
        User newUser = new User(userId, username, password);
        users.put(username, newUser); // Store by username for easy login lookup
        System.out.println("User " + username + " registered successfully with ID: " + userId);
        return newUser;
    }

    public User loginUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful for " + username);
            return user;
        }
        System.out.println("Invalid username or password.");
        return null;
    }

    /**
     * Searches for available rooms based on category and date range.
     * @param category The desired room category.
     * @param checkInDate
     * @param checkOutDate
     * @return A list of available rooms.
     */
    public List<Room> searchAvailableRooms(RoomCategory category, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getCategory() == category && room.isAvailable(checkInDate, checkOutDate)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    /**
     * Makes a reservation for a user and a selected room.
     * @param user The user making the reservation.
     * @param room The selected room.
     * @param checkInDate
     * @param checkOutDate
     * @return The created Booking object if successful, null otherwise.
     */
    public Booking makeReservation(User user, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        if (!room.isAvailable(checkInDate, checkOutDate)) {
            System.out.println("Room " + room.getRoomNumber() + " is not available for the selected dates.");
            return null;
        }

        String bookingId = UUID.randomUUID().toString();
        Booking newBooking = new Booking(bookingId, user, room, checkInDate, checkOutDate);

        // Simulate payment
        if (paymentGateway.processPayment(newBooking.getTotalAmount())) {
            room.bookDates(checkInDate, checkOutDate); // Mark dates as booked
            newBooking.setStatus(BookingStatus.CONFIRMED);
            bookings.put(bookingId, newBooking);
            System.out.println("Reservation successful! Booking ID: " + bookingId);
            return newBooking;
        } else {
            System.out.println("Payment failed. Reservation could not be completed.");
            return null;
        }
    }

    public List<Booking> getUserBookings(User user) {
        List<Booking> userBookings = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            // Compare by user ID, not object reference
            if (booking.getUser().getUserId().equals(user.getUserId())) {
                userBookings.add(booking);
            }
        }
        return userBookings;
    }

    public Booking getBookingDetails(String bookingId) {
        return bookings.get(bookingId);
    }

    public boolean cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("Booking with ID " + bookingId + " not found.");
            return false;
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            System.out.println("Booking " + bookingId + " is already cancelled.");
            return false;
        }

        // Unbook the dates
        booking.getRoom().unbookDates(booking.getCheckInDate(), booking.getCheckOutDate());
        booking.setStatus(BookingStatus.CANCELLED);
        System.out.println("Booking " + bookingId + " cancelled successfully.");
        // In a real system, you'd also handle refunds here
        return true;
    }
}

// 8. HotelReservationSystem Main Class
public class HotelReservationSystem {
    private Hotel hotel;
    private Scanner scanner;
    private User currentUser; // Stores the currently logged-in user

    public HotelReservationSystem() {
        this.hotel = new Hotel();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        HotelReservationSystem app = new HotelReservationSystem();
        app.run();
    }

    public void run() {
        System.out.println("Welcome to the Hotel Reservation System!");
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showAuthMenu() {
        System.out.println("\n--- Authentication Menu ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");

        int choice = getIntInput();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                registerUser();
                break;
            case 2:
                loginUser();
                break;
            case 3:
                System.out.println("Thank you for using the system. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void registerUser() {
        System.out.print("Enter desired username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        hotel.registerUser(username, password);
    }

    private void loginUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        currentUser = hotel.loginUser(username, password);
    }

    private void showMainMenu() {
        System.out.println("\n--- Main Menu (Logged in as: " + currentUser.getUsername() + ") ---");
        System.out.println("1. Search and Book Room");
        System.out.println("2. View My Bookings");
        System.out.println("3. View Booking Details by ID");
        System.out.println("4. Cancel Booking");
        System.out.println("5. Logout");
        System.out.print("Enter your choice: ");

        int choice = getIntInput();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                searchAndBookRoom();
                break;
            case 2:
                viewMyBookings();
                break;
            case 3:
                viewBookingDetailsById();
                break;
            case 4:
                cancelBooking();
                break;
            case 5:
                currentUser = null; // Logout
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void searchAndBookRoom() {
        System.out.println("\n--- Search and Book Room ---");
        System.out.print("Enter desired room category (STANDARD, DELUXE, SUITE): ");
        String categoryStr = scanner.nextLine().toUpperCase();
        RoomCategory selectedCategory;
        try {
            selectedCategory = RoomCategory.valueOf(categoryStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid room category. Please try again.");
            return;
        }

        System.out.print("Enter Check-in Date (YYYY-MM-DD): ");
        LocalDate checkInDate = parseDate(scanner.nextLine());
        if (checkInDate == null) return;

        System.out.print("Enter Check-out Date (YYYY-MM-DD): ");
        LocalDate checkOutDate = parseDate(scanner.nextLine());
        if (checkOutDate == null) return;

        // Ensure dates are valid and check-out is after check-in
        if (checkInDate.isBefore(LocalDate.now()) || checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            System.out.println("Invalid dates. Check-in date cannot be in the past, and check-out date must be after check-in date.");
            return;
        }

        List<Room> availableRooms = hotel.searchAvailableRooms(selectedCategory, checkInDate, checkOutDate);

        if (availableRooms.isEmpty()) {
            System.out.println("No rooms of type " + selectedCategory.getName() + " available for the selected dates.");
        } else {
            System.out.println("\nAvailable Rooms:");
            for (int i = 0; i < availableRooms.size(); i++) {
                System.out.println((i + 1) + ". " + availableRooms.get(i));
            }

            System.out.print("Enter the number of the room you want to book (0 to cancel): ");
            int roomChoice = getIntInput();
            scanner.nextLine(); // Consume newline

            if (roomChoice > 0 && roomChoice <= availableRooms.size()) {
                Room selectedRoom = availableRooms.get(roomChoice - 1);
                hotel.makeReservation(currentUser, selectedRoom, checkInDate, checkOutDate);
            } else if (roomChoice != 0) {
                System.out.println("Invalid room choice.");
            }
        }
    }

    private void viewMyBookings() {
        System.out.println("\n--- My Bookings ---");
        List<Booking> userBookings = hotel.getUserBookings(currentUser);
        if (userBookings.isEmpty()) {
            System.out.println("You have no bookings.");
        } else {
            for (Booking booking : userBookings) {
                System.out.println(booking);
                System.out.println("--------------------");
            }
        }
    }

    private void viewBookingDetailsById() {
        System.out.print("Enter Booking ID: ");
        String bookingId = scanner.nextLine();
        Booking booking = hotel.getBookingDetails(bookingId);
        if (booking != null) {
            System.out.println("\n--- Booking Details ---");
            System.out.println(booking);
        } else {
            System.out.println("Booking with ID " + bookingId + " not found.");
        }
    }

    private void cancelBooking() {
        System.out.print("Enter Booking ID to cancel: ");
        String bookingId = scanner.nextLine();
        hotel.cancelBooking(bookingId);
    }

    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // consume the invalid input
        }
        return scanner.nextInt();
    }

    private LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return null;
        }
    }
}
