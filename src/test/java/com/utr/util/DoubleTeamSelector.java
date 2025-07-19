package com.utr.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DoubleTeamSelector {
    static class Player {
        String name;
        double rating;
        String gender;

        Player(String name, double rating, String gender) {
            this.name = name;
            this.rating = rating;
            this.gender = gender;
        }
    }

    static class Pair {
        Player p1;
        Player p2;
        double totalRating;

        Pair(Player p1, Player p2) {
            this.p1 = p1;
            this.p2 = p2;
            this.totalRating = p1.rating + p2.rating;
        }

        boolean contains(Player player) {
            return p1.equals(player) || p2.equals(player);
        }

        List<String> names() {
            return Arrays.asList(p1.name, p2.name);
        }

        @Override
        public String toString() {
            return p1.name + " & " + p2.name + " (" + totalRating + ")";
        }
    }

    public static void main(String[] args) throws IOException {
        List<Player> players = Arrays.asList(
                new Player("Mitch", 7.04, "Male"),
                new Player("Wei", 6.89, "Male"),
                new Player("Ping", 6.80, "Male"),
                new Player("Mike", 6.73, "Male"),
                new Player("Yun", 6.22, "Male"),
                new Player("Bin", 6.07, "Male"),
                new Player("Hong", 6.01, "Male"),
                new Player("Qianyang(Kevin)", 5.92, "Male"),
                new Player("Austin", 5.58, "Male"),
                new Player("Thomas", 5.54, "Male"),
                new Player("Bill", 5.35, "Male"),
                new Player("Orien", 5.08, "Male"),
                new Player("Yiming", 4.96, "Male"),
                new Player("Samuel", 4.25, "Male"),
                new Player("May", 5.40, "Female"),
                new Player("Lucy", 4.31, "Female"),
                new Player("Chenchen", 4.16, "Female"),
                new Player("Lambda", 2.85, "Female"),
                new Player("Lihui", 2.31, "Female")
        );

        Set<String> excluded = new HashSet<>(Arrays.asList("Jingjing", "Yangjun", "Shuanglong"));
        Player bill = players.stream().filter(p -> p.name.equals("Bill")).findFirst().get();
        Player lambda = players.stream().filter(p -> p.name.equals("Lambda")).findFirst().get();
        Player chenchen = players.stream().filter(p -> p.name.equals("Chenchen")).findFirst().get();
        Player ping = players.stream().filter(p -> p.name.equals("Ping")).findFirst().get();
        Player lihui = players.stream().filter(p -> p.name.equals("Lihui")).findFirst().get();
        Player mitch = players.stream().filter(p -> p.name.equals("Mitch")).findFirst().get();

        Pair fixedD4 = new Pair(bill, lambda);
        Pair fixedPair1 = new Pair(chenchen, ping);
        Pair fixedPair2 = new Pair(lihui, mitch);

        List<Player> availablePlayers = players.stream()
                .filter(p -> !excluded.contains(p.name))
                .filter(p -> !fixedD4.names().contains(p.name))
                .filter(p -> !fixedPair1.names().contains(p.name))
                .filter(p -> !fixedPair2.names().contains(p.name))
                .collect(Collectors.toList());

        // Generate all valid pairs
        List<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < availablePlayers.size(); i++) {
            for (int j = i + 1; j < availablePlayers.size(); j++) {
                Player p1 = availablePlayers.get(i);
                Player p2 = availablePlayers.get(j);
                if (Math.abs(p1.rating - p2.rating) <= 3.5) {
                    pairs.add(new Pair(p1, p2));
                }
            }
        }

        List<List<Pair>> results = new ArrayList<>();

        // Generate combinations
        for (Pair d1 : pairs) {
            for (Pair d2 : pairs) {
                if (hasOverlap(d1, d2)) continue;
                for (Pair d3 : pairs) {
                    if (hasOverlap(d1, d3) || hasOverlap(d2, d3)) continue;

                    double total = d1.totalRating + d2.totalRating + d3.totalRating + fixedD4.totalRating
                            + fixedPair1.totalRating + fixedPair2.totalRating;

                    if (total > 40) continue;

                    List<Pair> team = Arrays.asList(d1, d2, d3, fixedD4, fixedPair1, fixedPair2);

                    if (isValidOrder(d1, d2, d3, fixedD4)) {
                        results.add(team);
                    }
                }
            }
        }

        // Sort and take top 20
        results.sort(Comparator.comparingDouble(t -> -(t.get(0).totalRating + t.get(1).totalRating + t.get(2).totalRating + t.get(3).totalRating + t.get(4).totalRating + t.get(5).totalRating)));

        List<List<Pair>> topResults = results.stream().limit(20).collect(Collectors.toList());

        // Write CSV
        try (FileWriter csvWriter = new FileWriter("team_combinations.csv")) {
            csvWriter.append("D1,D2,D3,D4,Fixed1,Fixed2,Total Rating\n");
            for (List<Pair> team : topResults) {
                double sum = team.stream().mapToDouble(p -> p.totalRating).sum();
                csvWriter.append(String.join(",", team.stream().map(Pair::toString).collect(Collectors.toList())))
                        .append(",").append(String.valueOf(sum)).append("\n");
            }
        }

        System.out.println("CSV file generated: team_combinations.csv");
    }

    private static boolean hasOverlap(Pair p1, Pair p2) {
        return p1.p1.equals(p2.p1) || p1.p1.equals(p2.p2) || p1.p2.equals(p2.p1) || p1.p2.equals(p2.p2);
    }

    private static boolean isValidOrder(Pair d1, Pair d2, Pair d3, Pair d4) {
        return d1.totalRating >= d2.totalRating &&
                d2.totalRating >= d3.totalRating &&
                d3.totalRating >= d4.totalRating;
    }
}
