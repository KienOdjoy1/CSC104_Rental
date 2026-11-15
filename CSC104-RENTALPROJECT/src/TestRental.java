import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TestRental {

    static final String[] DATA_FILES = {"vans.txt", "customers.txt", "employees.txt",
            "rentals.txt", "maintenance.txt", "notifications_log.txt"};
    static final String[] EXIT = {"0"};
    static final String[] CUST = {"1", "Anna Lim", "09181234567", "anna.lim@gmail.com",
            "Purok 5; Libertad; Butuan City", "1", "N04-12-345678"};

    static Path baseDir;
    static Path workDir;
    static final LocalDate TODAY = LocalDate.now();

    static String[] cat(String[]... parts) {
        List<String> all = new ArrayList<>();
        for (String[] p : parts) all.addAll(Arrays.asList(p));
        return all.toArray(new String[0]);
    }

    static String[] in(String... items) {
        return items;
    }

    static String[] ins(String[] base, int index, String... extra) {
        List<String> list = new ArrayList<>(Arrays.asList(base));
        list.addAll(index, Arrays.asList(extra));
        return list.toArray(new String[0]);
    }

    static String[] rent(String staff, String customer, String plate, String days) {
        return new String[]{"9", staff, customer, plate, days};
    }

    static String daysAgo(int n) {
        return TODAY.minusDays(n).toString();
    }

    static Consumer<Path> write(String name, String text) {
        return w -> {
            try { Files.writeString(w.resolve(name), text); }
            catch (IOException e) { throw new RuntimeException(e); }
        };
    }

    static Consumer<Path> append(String name, String text) {
        return w -> {
            try { Files.writeString(w.resolve(name), text, StandardOpenOption.APPEND); }
            catch (IOException e) { throw new RuntimeException(e); }
        };
    }

    static Consumer<Path> delete(String... names) {
        return w -> {
            for (String n : names) {
                try { Files.deleteIfExists(w.resolve(n)); }
                catch (IOException e) { throw new RuntimeException(e); }
            }
        };
    }

    static Consumer<Path> seeded(int startDaysAgo, int days, String plate) {
        return w -> {
            try {
                String rental = "R1,C1," + plate + "," + daysAgo(startDaysAgo) + "," + days
                        + ",9000.0,false,none,0.0,E1,none\n";
                Files.writeString(w.resolve("rentals.txt"), rental);
                StringBuilder vans = new StringBuilder();
                for (String line : Files.readAllLines(w.resolve("vans.txt"))) {
                    String[] p = line.split(",");
                    if (p.length >= 5 && p[1].equals(plate)) p[4] = "false";
                    vans.append(String.join(",", p)).append("\n");
                }
                Files.writeString(w.resolve("vans.txt"), vans.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    static class T {
        final String id;
        Consumer<Path> setup = w -> { };
        final List<String[]> runs = new ArrayList<>();
        final List<String> must = new ArrayList<>();
        final List<String> not = new ArrayList<>();
        final List<String[]> files = new ArrayList<>();

        T(String id) { this.id = id; }

        T setup(Consumer<Path> s) { this.setup = s; return this; }
        T run(String[] input) { runs.add(input); return this; }
        T must(String... m) { must.addAll(Arrays.asList(m)); return this; }
        T not(String... m) { not.addAll(Arrays.asList(m)); return this; }
        T file(String name, String text) { files.add(new String[]{name, text}); return this; }
    }

    static final List<T> tests = new ArrayList<>();

    static T t(String id) {
        T x = new T(id);
        tests.add(x);
        return x;
    }

    static void defineTests() {
        t("TC-01").run(cat(CUST, EXIT)).must("Customer added. ID: C16").file("customers.txt", "C16,Anna Lim,09181234567");
        t("TC-02").run(cat(ins(CUST, 1, ""), EXIT)).must("Input cannot be empty or contain commas.", "Customer added. ID: C16");
        t("TC-03").run(cat(ins(CUST, 1, "Lim, Anna"), EXIT)).must("Input cannot be empty or contain commas.");
        t("TC-04").run(cat(ins(CUST, 2, "09abcdefghi"), EXIT)).must("Invalid number. Use 11 digits starting with 09.");
        t("TC-05").run(cat(ins(CUST, 2, "0918123456"), EXIT)).must("Invalid number. Use 11 digits starting with 09.");
        t("TC-06").run(cat(ins(CUST, 3, "annalim.gmail.com"), EXIT)).must("Invalid email format.");
        t("TC-07").run(cat(ins(CUST, 4, "Butuan"), EXIT)).must("Please enter the complete address.");
        t("TC-08").run(cat(ins(CUST, 5, "9"), EXIT)).must("Please choose a number from the list.");
        t("TC-09").run(cat(ins(CUST, 5, "abc"), EXIT)).must("Please enter a whole number.");
        t("TC-10").run(cat(ins(CUST, 6, "12"), EXIT)).must("Invalid ID number. Use 5 to 20 letters, digits, or dashes.");
        t("TC-11").run(cat(in("8"), EXIT)).must("C1 | Kien", "C15 | Bea", "****");
        t("TC-12").run(cat(in("3", "C1", "09999999999", "kien.new@gmail.com", "Purok 7; Obrero; Butuan City", "2", "P7654321A", "8"), EXIT))
                .must("Customer updated.", "09999999999").file("customers.txt", "C1,Kien,09999999999,kien.new@gmail.com");
        t("TC-13").run(cat(in("3", "C99", "09999999999", "x.y@gmail.com", "Purok 7; Obrero; Butuan City", "2", "P7654321A"), EXIT))
                .must("Error: Customer ID not found.");
        t("TC-14").run(cat(in("2", "C15", "Y", "8"), EXIT)).must("Customer deleted.").not("C15 | Bea");
        t("TC-15").run(cat(in("2", "C15", "N", "8"), EXIT)).must("Delete cancelled.", "C15 | Bea");
        t("TC-16").run(cat(in("2", "C99"), EXIT)).must("Error: Customer ID not found.");
        t("TC-17").run(cat(rent("E1", "C1", "ABC-123", "2"), in("10", "E1", "R1", "2", "C1", "Y"), EXIT))
                .must("Cannot delete a customer that has rental records (R1).");
        t("TC-18").run(cat(in("2", "C3", "Y"), CUST, in("8"), EXIT)).must("Customer added. ID: C3");

        t("TC-19").run(cat(in("4", "S", "new-111", "Toyota Hiace", "15"), EXIT)).must("Van added.")
                .file("vans.txt", "Standard,NEW-111,Toyota Hiace,15,true,false");
        t("TC-20").run(cat(in("4", "L", "LUX-999", "Hyundai Staria", "9"), EXIT)).must("Van added.")
                .file("vans.txt", "Luxury,LUX-999,Hyundai Staria,9,true,false");
        t("TC-21").run(cat(in("4", "S", "abc-123", "Toyota Hiace", "15"), EXIT)).must("Error: A van with that plate number already exists.");
        t("TC-22").run(cat(in("4", "X"), EXIT)).must("Error: Type must be S or L.");
        t("TC-23").run(cat(in("4", "S", "NEW-222", "Toyota Hiace", "0"), EXIT)).must("Error: Seats must be at least 1.");
        t("TC-24").run(cat(in("4", "S", "NEW-222", "Toyota Hiace", "abc", "12"), EXIT)).must("Please enter a whole number.", "Van added.");
        t("TC-25").run(cat(in("5", "EFG-208", "7", "EFG-208"), EXIT)).must("Van deleted.", "Van not found.");
        t("TC-26").run(cat(in("5", "ZZZ-000"), EXIT)).must("Error: Van not found.");
        t("TC-27").run(cat(in("7", "abc-123"), EXIT)).must("ABC-123 | Toyota HiAce Commuter", "Status: Available - can be rented.");
        t("TC-28").run(cat(in("7", "ZZZ-000"), EXIT)).must("Van not found.");
        t("TC-29").run(cat(in("6"), EXIT)).must("--- AVAILABLE (25) ---", "--- RENTED (0) ---", "--- UNDER MAINTENANCE (0) ---");

        t("TC-30").run(cat(rent("E1", "C1", "ABC-123", "3"), EXIT))
                .must("Rental created: R1", "[EMAIL to kienshardy145@gmail.com]", "Total fee : P9000.0")
                .file("rentals.txt", "R1,C1,ABC-123").file("notifications_log.txt", "Total fee : P9000.0");
        t("TC-31").run(cat(rent("E2", "C2", "GHI-789", "2"), EXIT)).must("Total fee : P9500.0");
        t("TC-32").run(cat(in("9", "E9"), EXIT)).must("Error: Rental staff ID not found. Valid IDs: E1, E2, E3.");
        t("TC-33").run(cat(in("9", "E1", "C99"), EXIT)).must("Error: Customer ID not found.");
        t("TC-34").run(cat(in("9", "E1", "C1", "ZZZ-000"), EXIT)).must("Error: Van not found.");
        t("TC-35").run(cat(rent("E1", "C1", "ABC-123", "2"), in("9", "E1", "C2", "ABC-123"), EXIT)).must("NOT AVAILABLE: it is already rented.");
        t("TC-36").run(cat(in("9", "E1", "C1", "ABC-123", "0"), EXIT)).must("Error: Rental days must be at least 1.").file("rentals.txt", "");
        t("TC-37").run(cat(in("9", "E1", "C1", "ABC-123", "-5"), EXIT)).must("Error: Rental days must be at least 1.");
        t("TC-38").run(cat(in("9", "E1", "C1", "ABC-123", "three", "3"), EXIT)).must("Please enter a whole number.", "Rental created: R1");
        t("TC-39").setup(append("customers.txt", "C16,Old Customer,09170000099,old@gmail.com\n"))
                .run(cat(in("9", "E1", "C16", "ABC-123", "2"), EXIT)).must("no address or valid ID on file");
        t("TC-40").run(cat(in("14", "E1", "ABC-123", "Brake check"), in("9", "E1", "C1", "ABC-123"), EXIT)).must("NOT AVAILABLE: it is under maintenance.");
        t("TC-41").run(cat(rent("E1", "C1", "ABC-123", "2"), in("6"), EXIT)).must("--- AVAILABLE (24) ---", "--- RENTED (1) ---");
        t("TC-42").run(cat(in("11"), EXIT)).must("No rentals.");
        t("TC-43").run(cat(rent("E1", "C1", "ABC-123", "2147483647"), in("6"), EXIT)).must("Error:").not("Exception");

        t("TC-44").run(cat(rent("E1", "C1", "ABC-123", "2"), in("10", "E2", "R1"), EXIT)).must("Van returned. Thank you!", "Returned on time. No late fee.");
        t("TC-45").setup(seeded(10, 3, "ABC-123")).run(cat(in("10", "E1", "R1"), EXIT))
                .must("Returned 7 day(s) late.", "Late fee   : P250.0 x 7 = P1750.0", "Total to pay: P10750.0");
        t("TC-46").run(cat(in("10", "E1", "R99"), EXIT)).must("Error: Rental ID not found.");
        t("TC-47").run(cat(rent("E1", "C1", "ABC-123", "2"), in("10", "E1", "R1", "10", "E1", "R1"), EXIT)).must("Error: This van was already returned.");
        t("TC-48").run(cat(in("10", "E9"), EXIT)).must("Error: Rental staff ID not found.");
        t("TC-49").run(cat(rent("E1", "C1", "ABC-123", "2"), in("10", "E1", "R1", "7", "ABC-123"), EXIT)).must("Status: Available - can be rented.");

        t("TC-50").setup(seeded(10, 3, "ABC-123")).run(cat(in("13"), EXIT))
                .must("Customer : Kien (C1)", "Address  : Purok 6; Obrero; Butuan City", "Late     : 7 day(s), late fee so far P1750.0");
        t("TC-51").run(cat(in("13"), EXIT)).must("No rentals are due today or overdue.");
        t("TC-52").setup(seeded(10, 3, "ABC-123")).run(cat(in("12"), EXIT))
                .must("[SMS to 09393818714]", "1 expiration message(s) sent.").file("notifications_log.txt", "[SMS to 09393818714]");
        t("TC-53").run(cat(in("12"), EXIT)).must("0 expiration message(s) sent.");
        t("TC-54").setup(seeded(3, 3, "ABC-123")).run(cat(in("13"), EXIT)).must("R1 | Kien").not("Late     :");

        t("TC-55").run(cat(in("14", "E3", "GHI-789", "Oil change", "7", "GHI-789", "16"), EXIT))
                .must("Record: M1", "Status: Under maintenance - cannot be rented.", "M1 | GHI-789 | Oil change | by Camille Perez")
                .file("maintenance.txt", "M1,GHI-789,E3,Oil change");
        t("TC-56").run(cat(rent("E1", "C1", "ABC-123", "2"), in("14", "E1", "ABC-123", "Tire change"), EXIT))
                .must("That van is currently rented. Wait until it is returned.");
        t("TC-57").run(cat(in("14", "E1", "ABC-123", "Tire change", "14", "E1", "ABC-123", "Tire change"), EXIT))
                .must("That van is already under maintenance.");
        t("TC-58").run(cat(in("14", "E1", "ZZZ-000", "Tire change"), EXIT)).must("Error: Van not found.");
        t("TC-59").run(cat(in("14", "E9"), EXIT)).must("Error: Rental staff ID not found.");
        t("TC-60").run(cat(in("14", "E1", "ABC-123", "Tire change", "15", "ABC-123", "7", "ABC-123", "16"), EXIT))
                .must("Maintenance finished. The van can be rented again.", "Status: Available - can be rented.", "| Done ");
        t("TC-61").run(cat(in("15", "ABC-123"), EXIT)).must("Error: That van is not under maintenance.");
        t("TC-62").run(cat(in("14", "E1", "ABC-123", "Tire change", "5", "ABC-123"), EXIT)).must("Cannot delete a van that is under maintenance.");
        t("TC-63").run(cat(rent("E1", "C1", "ABC-123", "2"), in("5", "ABC-123"), EXIT)).must("Cannot delete a van that is currently rented.");
        t("TC-64").run(cat(in("16"), EXIT)).must("No maintenance records.");

        t("TC-65").run(cat(in("99"), EXIT)).must("Invalid choice. Try again.");
        t("TC-66").run(cat(in("abc"), EXIT)).must("Please enter a whole number.");
        t("TC-67").run(EXIT).must("Saving data... Goodbye!");

        t("TC-68").run(cat(CUST, rent("E1", "C1", "ABC-123", "3"), in("14", "E2", "GHI-789", "Oil change"), EXIT))
                .run(cat(in("8", "11", "16", "6"), EXIT))
                .must("C16 | Anna Lim", "R1 | Kien | ABC-123", "Active", "M1 | GHI-789 | Oil change");
        t("TC-69").setup(delete("vans.txt", "customers.txt", "employees.txt", "rentals.txt", "maintenance.txt"))
                .run(cat(in("6", "8"), EXIT)).must("--- AVAILABLE (3) ---", "No customers.");
        t("TC-70").run(cat(rent("E1", "C1", "ABC-123", "3"), EXIT))
                .run(cat(in("6", "9", "E1", "C2", "ABC-123"), EXIT))
                .must("--- RENTED (1) ---", "NOT AVAILABLE: it is already rented.");
        t("TC-71").setup(append("vans.txt", "\n")).run(cat(in("6"), EXIT)).must("--- AVAILABLE (25) ---").not("Exception");
        t("TC-72").setup(append("vans.txt", "Standard,BAD-001,Test Van,abc,true,false\n")).run(cat(in("6"), EXIT))
                .must("Error loading vans").not("Exception");
        t("TC-73").run(cat(rent("E1", "C1", "ABC-123", "2"), in("10", "E1", "R1", "5", "ABC-123"), EXIT))
                .run(cat(in("11"), EXIT)).must("R1 | Kien | ABC-123");
    }

    static void freshData(Consumer<Path> setup) throws IOException {
        if (Files.exists(workDir)) {
            try (var walk = Files.walk(workDir)) {
                walk.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            }
        }
        Files.createDirectories(workDir);
        for (String f : DATA_FILES) {
            Path src = baseDir.resolve(f);
            if (Files.exists(src)) Files.copy(src, workDir.resolve(f));
        }
        setup.accept(workDir);
    }

    static String runProgram(String[] input) throws IOException, InterruptedException {
        StringBuilder cp = new StringBuilder();
        for (String entry : System.getProperty("java.class.path").split(java.io.File.pathSeparator)) {
            if (cp.length() > 0) cp.append(java.io.File.pathSeparator);
            cp.append(Paths.get(entry).toAbsolutePath().normalize());
        }
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", cp.toString(), "Main");
        pb.directory(workDir.toFile());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (OutputStream os = p.getOutputStream()) {
            os.write((String.join("\n", input) + "\n").getBytes(StandardCharsets.UTF_8));
        }
        byte[] out = p.getInputStream().readAllBytes();
        if (!p.waitFor(60, TimeUnit.SECONDS)) p.destroyForcibly();
        return new String(out, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            baseDir = Paths.get(args[0]).toAbsolutePath().normalize();
        } else if (Files.exists(Paths.get("vans.txt"))) {
            baseDir = Paths.get(".").toAbsolutePath().normalize();
        } else {
            baseDir = Paths.get("..").toAbsolutePath().normalize();
        }
        if (!Files.exists(baseDir.resolve("vans.txt"))) {
            System.out.println("Cannot find vans.txt in " + baseDir + ". Give the project folder as an argument.");
            return;
        }
        workDir = Paths.get("work").toAbsolutePath();
        defineTests();

        int passed = 0;
        for (T test : tests) {
            freshData(test.setup);
            StringBuilder output = new StringBuilder();
            for (String[] input : test.runs) output.append(runProgram(input)).append("\n");
            String text = output.toString();

            boolean ok = true;
            for (String m : test.must) if (!text.contains(m)) ok = false;
            for (String m : test.not) if (text.contains(m)) ok = false;
            for (String[] f : test.files) {
                Path file = workDir.resolve(f[0]);
                String content = Files.exists(file) ? Files.readString(file) : "";
                boolean hit = f[1].isEmpty() ? content.trim().isEmpty() : content.contains(f[1]);
                if (!hit) ok = false;
            }
            if (ok) passed++;
            System.out.println("RESULT " + test.id + " " + (ok ? "PASS" : "FAIL"));
        }
        System.out.println("SUMMARY " + passed + "/" + tests.size() + " passed");
    }
}
