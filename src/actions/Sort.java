package actions;

import fileio.ActionInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;

import java.util.*;

public final class Sort {
    private Sort() {
    }

    /**
     * Function to sort a map by key and value.
     * @param map list of all actors
     * @param action list of all actions
     * @return a sorted a map of actors by name and rating
     */
    public static Map<String, Double> sortingDouble(final Map<String, Double> map,
                                                    final ActionInputData action) {
        Map<String, Double> sorted = new LinkedHashMap<>();
        Map<String, Double> finalSorted = new LinkedHashMap<>();
        if (action.getSortType().equals("asc")) {
            map.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
            sorted.entrySet()
                    .stream()
                    .sorted((e1, e2) -> {
                        if (e1.getValue().equals(e2.getValue())) {
                            return e1.getKey().compareTo(e2.getKey());
                        }
                        return 0;
                    }).forEachOrdered(x -> finalSorted.put(x.getKey(), x.getValue()));
        } else if (action.getSortType().equals("desc")) {
            map.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
            sorted.entrySet()
                    .stream()
                    .sorted((e1, e2) -> {
                        if (e1.getValue().equals(e2.getValue())) {
                            return e2.getKey().compareTo(e1.getKey());
                        }
                        return 0;
                    }).forEachOrdered(x -> finalSorted.put(x.getKey(), x.getValue()));
        }
        return finalSorted;
    }

    /**
     * Function to sort films from map by key and value.
     *
     * @param films map of films to sort
     * @param action current action
     * @return
     */
    public static Map<String, Integer> sorting(final Map<String, Integer> films,
                                               final ActionInputData action) {
        Map<String, Integer> sorted = new LinkedHashMap<>();
        Map<String, Integer> finalSorted = new LinkedHashMap<>();
        if (action.getSortType().equals("asc")) {
            films.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
            sorted.entrySet()
                    .stream()
                    .sorted((e1, e2) -> {
                        if (e1.getValue().equals(e2.getValue())) {
                            return e1.getKey().compareTo(e2.getKey());
                        }
                        return 0;
                    }).forEachOrdered(x -> finalSorted.put(x.getKey(), x.getValue()));
        } else if (action.getSortType().equals("desc")) {
            films.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
            sorted.entrySet()
                    .stream()
                    .sorted((e1, e2) -> {
                        if (e1.getValue().equals(e2.getValue())) {
                            return e2.getKey().compareTo(e1.getKey());
                        }
                        return 0;
                    }).forEachOrdered(x -> finalSorted.put(x.getKey(), x.getValue()));
        }

        return finalSorted;
    }

    /**
     * Function to sort the films after rating and name.
     *
     * @param films map of films to sort
     * @param action current action
     * @param movies list of all movies
     * @return a sorted arraylist
     */
    public static ArrayList<String> sortingFilms(final Map<String, Integer> films,
                                                 final ActionInputData action,
                                                 final List<MovieInputData> movies) {
        Map<String, Integer> finalSorted = Sort.sorting(films, action);

        List<List<String>> filters = action.getFilters();
        ArrayList<String> filmsList = new ArrayList<>();
        for (String film : finalSorted.keySet()) {
            for (MovieInputData movie : movies) {
                if (movie.getTitle().equals(film)) {
                    if (filters.get(0).get(0) != null
                            && filters.get(1) != null) {
                        if (movie.getYear() == Integer.parseInt(filters.get(0).get(0))
                                && movie.getGenres().contains(filters.get(1).get(0))) {
                            filmsList.add(film);
                        }
                    } else if (filters.get(0).get(0) != null) {
                        if (movie.getYear() == Integer.parseInt(filters.get(0).get(0))) {
                            filmsList.add(film);
                        }
                    } else if (filters.get(1) != null) {
                        if (movie.getGenres().contains(filters.get(1).get(0))) {
                            filmsList.add(film);
                        }
                    }
                }
            }
        }

        return filmsList;
    }

    /**
     * Function to sort the serials.
     *
     * @param films map of films to sort
     * @param action current action
     * @param serials list of all serials
     * @return a sorted arraylist
     */
    public static ArrayList<String> sortingSerials(final Map<String, Integer> films,
                                                   final ActionInputData action,
                                                   final List<SerialInputData> serials) {

        Map<String, Integer> finalSorted = Sort.sorting(films, action);
        List<List<String>> filters = action.getFilters();
        ArrayList<String> filmsList = new ArrayList<>();
        for (String film : finalSorted.keySet()) {
            for (SerialInputData movie : serials) {
                if (movie.getTitle().equals(film)) {
                    if (filters.get(0).get(0) != null
                            && filters.get(1) != null) {
                        if (movie.getYear() == Integer.parseInt(filters.get(0).get(0))
                                && movie.getGenres().contains(filters.get(1).get(0))) {
                            filmsList.add(film);
                        }
                    } else if (filters.get(0).get(0) != null) {
                        if (movie.getYear() == Integer.parseInt(filters.get(0).get(0))) {
                            filmsList.add(film);
                        }
                    } else if (filters.get(1) != null) {
                        if (movie.getGenres().contains(filters.get(1).get(0))) {
                            filmsList.add(film);
                        }
                    }
                }
            }
        }
        return filmsList;
    }
}
