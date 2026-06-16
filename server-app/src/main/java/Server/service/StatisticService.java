package Server.service;

import DAO.StatisticDAO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StatisticService {

    private final StatisticDAO statisticDAO = new StatisticDAO();

    public int countBorrowedByDate(LocalDate date) {
        if (date == null) {
            return 0;
        }

        return statisticDAO.byDate(date);
    }

    public int countBorrowedByMonth(int month, int year) {
        if (month < 1 || month > 12 || year <= 0) {
            return 0;
        }

        return statisticDAO.byMonth(month, year);
    }

    public int countBorrowedByYear(int year) {
        if (year <= 0) {
            return 0;
        }

        return statisticDAO.byYear(year);
    }

    public List<String[]> getBorrowReport(String type, LocalDate date, int month, int year) {
        if ("DAY".equalsIgnoreCase(type)) {
            return date == null ? new ArrayList<>() : statisticDAO.reportByDate(date);
        }

        if ("MONTH".equalsIgnoreCase(type)) {
            return statisticDAO.reportByMonth(month, year);
        }

        if ("YEAR".equalsIgnoreCase(type)) {
            return statisticDAO.reportByYear(year);
        }

        return new ArrayList<>();
    }

    public boolean exportBorrowReport(Path file, List<String[]> rows) {
        if (file == null || rows == null) {
            return false;
        }

        List<String> lines = new ArrayList<>();
        lines.add("Username,Book,Borrow date,Status");

        for (String[] row : rows) {
            lines.add(String.join(",",
                    csv(row[0]),
                    csv(row[1]),
                    csv(row[2]),
                    csv(row[3])
            ));
        }

        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String csv(String value) {
        String safe = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + safe + "\"";
    }
}
