import java.io.*;
import java.util.*;
public class StudentD {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter file name to load/save student data: ");
        String fileName = scanner.nextLine();
        File file = new File("./" + fileName);
        int subjectCount = 0;
        double PASS_MARK = 0;
        final int MAX_MARKS_PER_SUBJECT = 100;
        ArrayList<Long> ids = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<double[]> marks = new ArrayList<>();
        if (file.exists()) {
            try {
                Scanner fr = new Scanner(file);
                subjectCount = Integer.parseInt(fr.nextLine());
                PASS_MARK = Double.parseDouble(fr.nextLine());
                int studentCount = Integer.parseInt(fr.nextLine());
                for (int i = 0; i < studentCount; i++) {
                    long id = Long.parseLong(fr.nextLine());
                    String name = fr.nextLine();
                    double[] m = new double[subjectCount];
                    for (int s = 0; s < subjectCount; s++) {
                        m[s] = Double.parseDouble(fr.nextLine());
                    }
                    ids.add(id);
                    names.add(name);
                    marks.add(m);
                }
                fr.close();
                System.out.println("\nFile loaded successfully.\n");
            } catch (Exception e) {
                System.out.println("Error reading file!");
                return;
            }
        } else {
            System.out.println("File does not exist.");
            System.out.print("Create new file? (yes/no): ");
            if (!scanner.nextLine().equalsIgnoreCase("yes")) {
                System.out.println("Exiting...");
                return;
            }
            System.out.print("Enter number of subjects: ");
            subjectCount = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter pass mark per subject: ");
            PASS_MARK = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter number of students to enter now: ");
            int initialStudents = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < initialStudents; i++) {
                System.out.println("\nStudent " + (i + 1) + ":");
                long id;
                while (true) {
                    System.out.print("Enter ID (max 15 digits): ");
                    id = scanner.nextLong();
                    scanner.nextLine();
                    if (String.valueOf(id).length() <= 15) break;
                    System.out.println("ID too large!");
                }
                System.out.print("Enter Name: ");
                String name = scanner.nextLine();
                double[] m = new double[subjectCount];
                for (int s = 0; s < subjectCount; s++) {
                    double mark;
                    do {
                        System.out.print("Subject " + (s + 1) + ": ");
                        mark = scanner.nextDouble();
                    } while (mark < 0 || mark > 100);
                    m[s] = mark;
                }
                scanner.nextLine();
                ids.add(id);
                names.add(name);
                marks.add(m);
            }
            saveToFile(file, subjectCount, PASS_MARK, ids, names, marks);
            System.out.println("\nNew file created.\n");
        }
        int choice;
        do {
            System.out.println("\n===== Student Management System =====");
            System.out.println("1. Add Student");
            System.out.println("2. View Students (Sorted by Percentage)");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Save & Exit");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    System.out.println("\nEnter new student details:");
                    long id;
                    while (true) {
                        System.out.print("Enter ID (max 15 digits): ");
                        id = scanner.nextLong();
                        scanner.nextLine();
                        if (String.valueOf(id).length() <= 15) break;
                        System.out.println("ID too large!");
                    }
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    double[] newMarks = new double[subjectCount];
                    for (int s = 0; s < subjectCount; s++) {
                        double mark;
                        do {
                            System.out.print("Subject " + (s + 1) + ": ");
                            mark = scanner.nextDouble();
                        } while (mark < 0 || mark > 100);
                        newMarks[s] = mark;
                    }
                    scanner.nextLine();
                    ids.add(id);
                    names.add(name);
                    marks.add(newMarks);
                    System.out.println("Student added.");
                    saveToFile(file, subjectCount, PASS_MARK, ids, names, marks);
                    break;
                case 2:
                    if (ids.size() == 0) {
                        System.out.println("No students available.");
                        break;
                    }
                    Integer[] order = new Integer[ids.size()];
                    double[] percentages = new double[ids.size()];
                    for (int i = 0; i < ids.size(); i++) {
                        order[i] = i;
                        double sum = 0;
                        for (double m : marks.get(i)) sum += m;
                        percentages[i] = (sum / (subjectCount * MAX_MARKS_PER_SUBJECT)) * 100;
                    }
                    Arrays.sort(order, (a, b) -> Double.compare(percentages[b], percentages[a]));
                    System.out.println("\n===== Sorted Student List =====");
                    System.out.printf("%-15s %-20s", "ID", "Name");
                    for (int s = 1; s <= subjectCount; s++) System.out.printf("Sub%-5d", s);
                    System.out.printf("%-8s %-8s %-10s %-10s %-6s\n",
                            "Total", "Avg", "Percent", "Result", "Grade");
                    for (int idx : order) {
                        double sum = 0;
                        boolean pass = true;
                        System.out.printf("%-15d %-20s", ids.get(idx), names.get(idx));
                        for (int s = 0; s < subjectCount; s++) {
                            double m = marks.get(idx)[s];
                            System.out.printf("%-8.2f", m);
                            sum += m;
                            if (m < PASS_MARK) pass = false;
                        }
                        double avg = sum / subjectCount;
                        double percent = (sum / (subjectCount * MAX_MARKS_PER_SUBJECT)) * 100;
                        String result = pass ? "PASS" : "FAIL";
                        String grade = pass ?
                                (percent >= 85 ? "A" : percent >= 70 ? "B" :
                                 percent >= 55 ? "C" : percent >= 40 ? "D" : "F")
                                : "F";
                        System.out.printf("%-8.2f %-8.2f %-10.2f %-10s %-6s\n",
                                sum, avg, percent, result, grade);
                    }
                    break;
                case 3:
                    System.out.print("Enter ID to update: ");
                    long updateId = scanner.nextLong();
                    scanner.nextLine();
                    int index = ids.indexOf(updateId);
                    if (index == -1) {
                        System.out.println("Student not found.");
                        break;
                    }
                    System.out.print("Enter new name: ");
                    names.set(index, scanner.nextLine());
                    double[] updatedMarks = new double[subjectCount];
                    for (int s = 0; s < subjectCount; s++) {
                        double mark;
                        do {
                            System.out.print("Subject " + (s + 1) + ": ");
                            mark = scanner.nextDouble();
                        } while (mark < 0 || mark > 100);
                        updatedMarks[s] = mark;
                    }
                    scanner.nextLine();
                    marks.set(index, updatedMarks);
                    System.out.println("Updated!");
                    saveToFile(file, subjectCount, PASS_MARK, ids, names, marks);
                    break;
                case 4:
                    System.out.print("Enter ID to delete: ");
                    long deleteId = scanner.nextLong();
                    scanner.nextLine();
                    int delIndex = ids.indexOf(deleteId);
                    if (delIndex == -1) {
                        System.out.println("Student not found.");
                        break;
                    }
                    ids.remove(delIndex);
                    names.remove(delIndex);
                    marks.remove(delIndex);
                    System.out.println("Student deleted!");
                    saveToFile(file, subjectCount, PASS_MARK, ids, names, marks);
                    break;
                case 5:
                    saveToFile(file, subjectCount, PASS_MARK, ids, names, marks);
                    System.out.println("Saved & exiting...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 5);
        scanner.close();
    }
    public static void saveToFile(File file, int subjectCount, double PASS_MARK,
                                  ArrayList<Long> ids, ArrayList<String> names, ArrayList<double[]> marks) {
        try {
            PrintWriter pw = new PrintWriter(file);
            pw.println(subjectCount);
            pw.println(PASS_MARK);
            pw.println(ids.size());
            for (int i = 0; i < ids.size(); i++) {
                pw.println(ids.get(i));
                pw.println(names.get(i));
                for (double m : marks.get(i)) pw.println(m);
            }
            pw.close();
        } catch (Exception e) {
            System.out.println("Error writing file.");
        }
    }
}
