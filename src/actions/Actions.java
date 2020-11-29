package actions;

import actor.ActorsAwards;
import fileio.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;
import utils.Utils;

/**
 * Below are all actions.
 */
public final class Actions {
    /**
     * Add to favorite list of user.
     *
     * @param users
     * @param action
     * @param fileWriter
     * @param arrayResult
     * @throws IOException
     */
    public void favorite(final List<UserInputData> users, final ActionInputData action,
                         final Writer fileWriter, final JSONArray arrayResult) throws IOException {
        for (UserInputData user : users) {
            if (user.getUsername().equals(action.getUsername())) {
                if (user.getHistory().containsKey(action.getTitle())) {
                    if (!user.getFavoriteMovies().contains(action.getTitle())) {
                        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                                "success -> " + action.getTitle()
                                        + " was added as favourite");
                        arrayResult.add(result);
                    } else {
                        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                                "error -> " + action.getTitle()
                                        + " is already in favourite list");
                        arrayResult.add(result);
                    }
                } else {
                    JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                            "error -> " + action.getTitle()
                                    + " is not seen");
                    arrayResult.add(result);
                }
            }
        }
    }

    /**
     * Implementation of giving rating command.
     *
     * @param users
     * @param action
     * @param fileWriter
     * @param arrayResult
     * @throws IOException
     */
    public void rating(final List<UserInputData> users,
                       final ActionInputData action,
                       final Writer fileWriter,
                       final JSONArray arrayResult) throws IOException {
        for (UserInputData user : users) {
            if (action.getUsername().equals(user.getUsername())) {
                if (user.getHistory().containsKey(action.getTitle())) {
                    double grade = action.getGrade();
                    JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                            "success -> " + action.getTitle()
                                    + " was rated with " + grade + " by " + user.getUsername());
                    arrayResult.add(result);
                } else {
                    JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                            "error -> " + action.getTitle()
                                    + " is not seen");
                    arrayResult.add(result);
                }
            }
        }
    }

    /**
     * Implementation of view command.
     *
     * @param users
     * @param allactions
     * @param action
     * @param fileWriter
     * @param arrayResult
     * @throws IOException
     */
    public void view(final List<UserInputData> users, final List<ActionInputData> allactions,
                     final ActionInputData action,
                     final Writer fileWriter, final JSONArray arrayResult) throws IOException {
        for (UserInputData user : users) {
            if (user.getUsername().equals(action.getUsername())) {
                if (user.getHistory().containsKey(action.getTitle())) {
                    Integer views = user.getHistory().get(action.getTitle());
                    views++;
                    for (int i = 0; i < action.getActionId() - 1; i++) {
                        if (allactions.get(i).getUsername() != null) {
                            if (allactions.get(i).getUsername().equals(user.getUsername())
                                    && allactions.get(i).getType().equals("view")
                                    && allactions.get(i).getTitle().equals(action.getTitle())) {
                                views++;
                            }
                        }
                    }
                    if (views > 1) {
                        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                                "success -> " + action.getTitle()
                                        + " was viewed with total views of " + views);
                        arrayResult.add(result);
                    }
                } else {
                    int views = 1;
                    for (int i = 0; i < action.getActionId() - 1; i++) {
                        if (allactions.get(i).getUsername() != null) {
                            if (allactions.get(i).getUsername().equals(user.getUsername())
                                    && allactions.get(i).getType().equals("view")
                                    && allactions.get(i).getTitle().equals(action.getTitle())) {
                                views++;
                            }
                        }
                    }
                    JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                            "success -> " + action.getTitle()
                                    + " was viewed with total views of " + views);
                    arrayResult.add(result);
                }
            }
        }
    }

    /**
     * Implement standard recommendation.
     *
     * @param users
     * @param action
     * @param movies
     * @param fileWriter
     * @param arrayResult
     * @throws IOException
     */
    public void standard(final List<UserInputData> users, final ActionInputData action,
                         final List<MovieInputData> movies, final Writer fileWriter,
                         final JSONArray arrayResult) throws IOException {
        for (UserInputData user : users) {
            if (action.getUsername().equals(user.getUsername())) {
                JSONObject result = null;
                for (MovieInputData movie : movies) {
                    if (!user.getHistory().containsKey(movie.getTitle())) {
                        result = fileWriter.writeFile(action.getActionId(), "",
                                "StandardRecommendation result: " + movie.getTitle());
                        arrayResult.add(result);
                        break;
                    }
                }
                if (result == null) {
                    result = fileWriter.writeFile(action.getActionId(), "",
                            "StandardRecommendation cannot be applied!");
                    arrayResult.add(result);
                }
            }
        }
    }

    /**
     * Implementation of Best Unseen recommendation.
     *
     * @param users
     * @param actions
     * @param action
     * @param movies
     * @param serials
     * @param fileWriter
     * @param arrayResult
     * @throws IOException
     */
    public void bestUnseen(final List<UserInputData> users,
                           final List<ActionInputData> actions,
                           final ActionInputData action,
                           final List<MovieInputData> movies,
                           final List<SerialInputData> serials,
                           final Writer fileWriter,
                           final JSONArray arrayResult) throws IOException {
        ArrayList<ActionInputData> actionRating = new ArrayList<>(); // filme
        ArrayList<ActionInputData> actionRating1 = new ArrayList<>(); // seriale

        for (int i = 0; i < action.getActionId() - 1; i++) {
            if (actions.get(i).getType() != null) {
                if (actions.get(i).getType().equals("rating")) {
                    for (UserInputData user : users) {
                        if (actions.get(i).getUsername().equals(user.getUsername())) {
                            if (user.getHistory().containsKey(actions.get(i).getTitle())) {
                                if (actions.get(i).getSeasonNumber() != 0) {
                                    for (SerialInputData serial : serials) {
                                        if (serial.getTitle().equals(actions.get(i).getTitle())) {
                                            int contained = 0;
                                            for (int o = 0; o < actionRating1.size(); o++) {
                                                if (actionRating1.get(o).getUsername()
                                                        .equals(actions.get(i).getUsername())
                                                        && actionRating1.get(o).getTitle()
                                                        .equals(serial.getTitle())) {
                                                    contained++;
                                                }
                                            }
                                            if (contained == 0) {
                                                actionRating1.add(actions.get(i));
                                            }
                                        }
                                    }
                                } else {
                                    for (MovieInputData movie : movies) {
                                        if (movie.getTitle().equals(actions.get(i).getTitle())) {
                                            int contained = 0;
                                            for (int o = 0; o < actionRating.size(); o++) {
                                                if (actionRating.get(o).getUsername()
                                                        .equals(actions.get(i).getUsername())
                                                        && actionRating.get(o).getTitle()
                                                        .equals(movie.getTitle())) {
                                                    contained++;
                                                }
                                            }
                                            if (contained == 0) {
                                                actionRating.add(actions.get(i));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Map<String, Double> ratings = new HashMap<>(); // filme
        // calcularea mediei pentru filme
        for (int i = 0; i < actionRating.size(); i++) {
            double k = 1;
            double grade = actionRating.get(i).getGrade();
            for (int j = i; j < actionRating.size(); j++) {
                if (actionRating.get(i).getTitle().equals(actionRating.get(j).getTitle())) {
                    grade += actionRating.get(j).getGrade();
                    k++;
                }
            }
            grade = grade / k;
            ratings.put(actionRating.get(i).getTitle(), grade);
        }

        // calculare mediei pentru fiecare sezon
        if (actionRating1.size() != 0) {
            ArrayList<Double> grades = new ArrayList<>();
            for (int i = 0; i < actionRating1.size(); i++) {
                double k = 1;
                double grade = actionRating1.get(i).getGrade();
                for (int j = i; j < actionRating1.size(); j++) {
                    if (actionRating1.get(i).getTitle().equals(actionRating1.get(j).getTitle())
                            && actionRating1.get(i).getSeasonNumber()
                            == actionRating1.get(j).getSeasonNumber()) {
                        grade += actionRating1.get(j).getGrade();
                        k++;
                    }
                }
                grade = grade / k;
                grades.add(grade);
            }

            for (int i = 0; i < actionRating1.size(); i++) {
                double grade = grades.get(i);
                for (int j = i; j < actionRating1.size(); j++) {
                    if (actionRating1.get(i).getTitle().equals(actionRating1.get(j).getTitle())) {
                        grade += grades.get(j);
                    }
                }
                int nrSeasons = 0;
                for (SerialInputData serial : serials) {
                    if (serial.getTitle().equals(actionRating1.get(i).getTitle())) {
                        nrSeasons = serial.getNumberSeason();
                    }
                }
                grade = grade / nrSeasons;
                ratings.put(actionRating1.get(i).getTitle(), grade);
            }
        }


        // sortare dupa note descrescator
        Map<String, Double> sorted = new LinkedHashMap<>();
        ratings.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));


        UserInputData user = null;
        JSONObject result = null;
        for (UserInputData findUser : users) {
            if (findUser.getUsername().equals(action.getUsername())) {
                user = findUser;
            }
        }

        for (String movie : sorted.keySet()) {
            if (!user.getHistory().containsKey(movie)) {
                result = fileWriter.writeFile(action.getActionId(), "",
                        "BestRatedUnseenRecommendation result: " + movie);
                arrayResult.add(result);
                break;
            }
        }

        if (result == null) {
            for (MovieInputData movie : movies) {
                if (!user.getHistory().containsKey(movie.getTitle())) {
                    result = fileWriter.writeFile(action.getActionId(), "",
                            "BestRatedUnseenRecommendation result: " + movie.getTitle());
                    arrayResult.add(result);
                    break;
                }
            }
        }

        if (result == null) {
            result = fileWriter.writeFile(action.getActionId(), "",
                    "BestRatedUnseenRecommendation cannot be applied!");
            arrayResult.add(result);
        }

    }

    /**
     * @param users
     * @param allactions
     * @param action
     * @param movies
     * @param serials
     * @param fileWriter
     * @param arrayResult
     * @throws IOException
     */
    public void favoriteRating(final List<UserInputData> users,
                               final List<ActionInputData> allactions,
                               final ActionInputData action,
                               final List<MovieInputData> movies,
                               final List<SerialInputData> serials,
                               final Writer fileWriter,
                               final JSONArray arrayResult) throws IOException {
        UserInputData user = null;
        for (UserInputData findUser : users) {
            if (findUser.getUsername().equals(action.getUsername())) {
                user = findUser;
            }
        }

        JSONObject result = null;
        if (user.getSubscriptionType().equals("PREMIUM")) {
            ArrayList<String> filme = new ArrayList<>();
            for (UserInputData favoriteUsers : users) {
                if (!favoriteUsers.equals(user)) {
                    // movies
                    for (MovieInputData movie : movies) {
                        if (favoriteUsers.getFavoriteMovies().contains(movie.getTitle())) {
                            filme.add(movie.getTitle());
                        }
                    }
                    // serials
                    for (SerialInputData serial : serials) {
                        if (favoriteUsers.getFavoriteMovies().contains(serial.getTitle())) {
                            filme.add(serial.getTitle());
                        }
                    }
                    // from actions
                    for (int i = 0; i < action.getActionId() - 1; i++) {
                        if (allactions.get(i).getType() != null) {
                            if (allactions.get(i).getType().equals("favorite")) {
                                if (user.getUsername().equals(action.getUsername())) {
                                    if (user.getHistory()
                                            .containsKey(allactions.get(i).getTitle())) {
                                        filme.add(allactions.get(i).getTitle());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Map<String, Integer> rating = new HashMap<>();
            for (int i = 0; i < filme.size(); i++) {
                int adds = 1;
                for (int j = i + 1; j < filme.size(); j++) {
                    if (filme.get(i).equals(filme.get(j)) && !filme.get(j).equals("altfel")) {
                        adds++;
                        filme.set(j, "altfel");

                    }
                }
                if (!filme.get(i).equals("altfel")) {
                    rating.put(filme.get(i), adds);
                }
            }

            Map<String, Integer> sorted = new LinkedHashMap<>();
            rating.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));

            for (String movie : sorted.keySet()) {
                if (!user.getHistory().containsKey(movie)) {
                    result = fileWriter.writeFile(action.getActionId(), "",
                            "FavoriteRecommendation result: " + movie);
                    arrayResult.add(result);
                }
            }

            if (result == null) {
                result = fileWriter.writeFile(action.getActionId(), "",
                        "FavoriteRecommendation cannot be applied!");
                arrayResult.add(result);
            }
        } else {
            result = fileWriter.writeFile(action.getActionId(), "",
                    "FavoriteRecommendation cannot be applied!");
            arrayResult.add(result);
        }
    }

    /**
     * Implementation of search recommendation.
     *
     * @param users
     * @param actions
     * @param action
     * @param movies
     * @param serials
     * @param fileWriter
     * @param arrayResult
     * @throws IOException
     */
    public void searchRecommendation(final List<UserInputData> users,
                                     final List<ActionInputData> actions,
                                     final ActionInputData action,
                                     final List<MovieInputData> movies,
                                     final List<SerialInputData> serials,
                                     final Writer fileWriter,
                                     final JSONArray arrayResult) throws IOException {
//        JSONObject result = null;
//        UserInputData findUser = null;
//        for (UserInputData user : users) {
//            if (action.getUsername().equals(user.getUsername())) {
//                findUser = user;
//            }
//        }
//
//        ArrayList<String> films = new ArrayList<>();
//        ArrayList<Double> grades = new ArrayList<>();
//        String[] genres = action.getGenre().split(",");
//        if (findUser.getSubscriptionType().equals("PREMIUM")) {
//            for (int i = 0; i < actions.size(); i++) {
//                if (actions.get(i).getType().equals("rating")) {
//
//                }
//            }
//        }

//                // adaug filme
//                for (MovieInputData movie : movies) {
//                    if (movie.getGenres().contains(genre)) {
//                        films.add(movie.getTitle());
//                    }
//                }
//                // adaug seriale
//                for (SerialInputData serial : serials) {
//                    if (serial.getGenres().contains(genre)) {
//                        films.add(serial.getTitle());
//                    }
//                }
//                // creez rating-ul filmelor
//                Map<String, Integer> rating = new HashMap<>();
//                for (int i = 0; i < films.size(); i++) {
//                    int adds = 1;
//                    for (int j = i + 1; j < films.size(); j++) {
//                        if (films.get(i).equals(films.get(j)) && !films.get(j).equals("altfel")) {
//                            adds++;
//                            films.set(j, "altfel");
//                        }
//                    }
//                    if (!films.get(i).equals("altfel")) {
//                        rating.put(films.get(i), adds);
//                    }
//                }
//
//                // sortez filmele si serialele
//                Map<String, Integer> sorted = new LinkedHashMap<>();
//                rating.entrySet()
//                        .stream()
//                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                        .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
//
//                for (String movie : rating.keySet()) {
//                    if (!user.getHistory().containsKey(movie)) {
//                        result = fileWriter.writeFile(action.getActionId(), "",
//                                "SearchRecommendation result: " + movie);
//                        arrayResult.add(result);
//                        break;
//                    }
//                }
//
//                if (result != null) {
//                    result = fileWriter.writeFile(action.getActionId(), "",
//                            "SearchRecommendation cannot be applied!");
//                    arrayResult.add(result);
//                }
//            } else {
//                result = fileWriter.writeFile(action.getActionId(), "",
//                        "SearchRecommendation cannot be applied!");
//                arrayResult.add(result);
//                break;
//            }
//        }
//
//        if (result == null) {
//            result = fileWriter.writeFile(action.getActionId(), "",
//                    "SearchRecommendation cannot be applied!");
//            arrayResult.add(result);
//        }
    }

    /**
     * Implementation of popular recommendation.
     *
     * @param users
     * @param actions
     * @param action
     * @param movies
     * @param fileWriter
     * @param arrayResult
     * @param serials
     * @throws IOException
     */
    public void popularRecommendation(final List<UserInputData> users,
                                      final List<ActionInputData> actions,
                                      final ActionInputData action,
                                      final List<MovieInputData> movies,
                                      final Writer fileWriter,
                                      final JSONArray arrayResult,
                                      final List<SerialInputData> serials) throws IOException {
        UserInputData user = null;
        JSONObject result = null;
        for (UserInputData findUser : users) {
            if (action.getUsername().equals(findUser.getUsername())) {
                user = findUser;
            }
        }

        if (user.getSubscriptionType().equals("PREMIUM")) {
            Map<String, Integer> ratings = new HashMap<>();
            for (UserInputData findUser : users) {
                Map<String, Integer> films = findUser.getHistory();
                for (String film : films.keySet()) {
                    for (MovieInputData movie : movies) {
                        if (movie.getTitle().equals(film)) {
                            ArrayList<String> genres = movie.getGenres();
                            for (String gen : genres) {
                                if (ratings.containsKey(gen)) {
                                    Integer views = ratings.get(gen);
                                    views += films.get(film);
                                    ratings.put(gen, views);
                                } else {
                                    Integer views = films.get(film);
                                    ratings.put(gen, views);
                                }
                            }
                        }
                    }
                    for (SerialInputData serial : serials) {
                        if (serial.getTitle().equals(film)) {
                            ArrayList<String> genres = serial.getGenres();
                            for (String gen : genres) {
                                if (ratings.containsKey(gen)) {
                                    Integer views = ratings.get(gen);
                                    views += films.get(film);
                                    ratings.put(gen, views);
                                } else {
                                    Integer views = films.get(film);
                                    ratings.put(gen, views);
                                }
                            }
                        }
                    }
                }
            }

            Map<String, Integer> sorted = new LinkedHashMap<>();
            ratings.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));

            for (String genre : sorted.keySet()) {
                for (SerialInputData movie : serials) {
                    if (movie.getGenres().contains(genre)) {
                        if (!user.getHistory().containsKey(movie.getTitle())) {
                            result = fileWriter.writeFile(action.getActionId(), "",
                                    "PopularRecommendation result: " + movie.getTitle());
                            arrayResult.add(result);
                            break;
                        }
                    }
                    if (result != null) {
                        break;
                    }
                }
                if (result != null) {
                    break;
                }
                for (MovieInputData movie : movies) {
                    if (movie.getGenres().contains(genre)) {
                        if (!user.getHistory().containsKey(movie.getTitle())) {
                            result = fileWriter.writeFile(action.getActionId(), "",
                                    "PopularRecommendation result: " + movie.getTitle());
                            arrayResult.add(result);
                        }
                    }
                }
            }

            if (result == null) {
                result = fileWriter.writeFile(action.getActionId(), "",
                        "PopularRecommendation cannot be applied!");
                arrayResult.add(result);
            }
        } else {
            result = fileWriter.writeFile(action.getActionId(), "",
                    "PopularRecommendation cannot be applied!");
            arrayResult.add(result);
        }
    }

    /**
     * Implementation of query users.
     *
     * @param actions
     * @param action
     * @param users
     * @param fileWriter
     * @param arrayResult
     * @throws IOException
     */
    public void usersQuery(final List<ActionInputData> actions,
                           final ActionInputData action,
                           final List<UserInputData> users,
                           final Writer fileWriter,
                           final JSONArray arrayResult) throws IOException {
        Map<String, Integer> query = new HashMap<>();
        if (action.getCriteria().equals("num_ratings")) {
            for (int i = 0; i < action.getActionId() - 1; i++) {
                if (actions.get(i).getType() != null) {
                    if (actions.get(i).getType().equals("rating")) {
                        for (UserInputData user : users) {
                            if (user.getUsername().equals(actions.get(i).getUsername())) {
                                if (user.getHistory().containsKey(actions.get(i).getTitle())) {
                                    if (query.containsKey(actions.get(i).getUsername())) {
                                        Integer marks = query.get(actions.get(i).getUsername());
                                        marks++;
                                        query.put(actions.get(i).getUsername(), marks);
                                    } else {
                                        query.put(actions.get(i).getUsername(), 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Map<String, Integer> sorted = new LinkedHashMap<>();
        Map<String, Integer> finalSorted = new LinkedHashMap<>();
        if (action.getSortType().equals("asc")) {
            query.entrySet()
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
            query.entrySet()
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


        ArrayList<String> res = new ArrayList<>();
        String[] rate = finalSorted.keySet().toArray(new String[0]);
        if (rate.length > action.getNumber()) {
            for (int i = 0; i < action.getNumber(); i++) {
                res.add(rate[i]);
            }
        } else {
            for (int i = 0; i < rate.length; i++) {
                res.add(rate[i]);
            }
        }


        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                "Query result: " + res.toString());
        arrayResult.add(result);

    }

    /**
     * Implement query for actors awards and filter description.
     * @param action
     * @param actors
     * @param fileWriter
     * @param arrayResult
     * @throws IOException
     */
    public void actorQuery(final ActionInputData action,
                           final List<ActorInputData> actors,
                           final Writer fileWriter,
                           final JSONArray arrayResult) throws IOException {
        Map<String, Integer> actori = new HashMap<>();
        if (action.getCriteria().equals("awards")) {
            int number = 3;
            List<String> awards = action.getFilters().get(number);
            int k;
            for (ActorInputData actor : actors) {
                k = 0;
                for (String award : awards) {
                    ActorsAwards actorAward = Utils.stringToAwards(award);
                    if (actor.getAwards().containsKey(actorAward)) {
                        k++;
                    }
                    if (k == awards.size()) {
                        k = 0;
                        for (ActorsAwards premiu : actor.getAwards().keySet()) {
                            if (actor.getAwards().containsKey(premiu)) {
                                k += actor.getAwards().get(premiu);
                            }
                        }
                        actori.put(actor.getName(), k);
                    }
                }
            }
            Map<String, Integer> sorted = new LinkedHashMap<>();
            if (action.getSortType().equals("asc")) {
                actori.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue())
                        .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
            } else if (action.getSortType().equals("desc")) {
                actori.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
            }
            ArrayList<String> res = new ArrayList<>();
            String[] rate = sorted.keySet().toArray(new String[0]);
            if (rate.length > action.getNumber()) {
                for (int i = 0; i < action.getNumber(); i++) {
                    res.add(rate[i]);
                }
            } else {
                for (int i = 0; i < rate.length; i++) {
                    res.add(rate[i]);
                }
            }
            JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                    "Query result: " + res.toString());
            arrayResult.add(result);
        } else if (action.getCriteria().equals("filter_description")) {
            int number = 2;
            List<String> words = action.getFilters().get(number);
            int k;

            for (ActorInputData actor : actors) {
                k = 0;
                for (String word : words) {
                    if (actor.getCareerDescription().toLowerCase().contains(" " + word)
                       || actor.getCareerDescription().toLowerCase().contains("-" + word)) {
                        k++;
                    }
                    if (k >= words.size()) {
                        actori.put(actor.getName(), 1);
                    }
                }
            }
            List<String> sorted = new ArrayList<>(actori.keySet());
            if (action.getSortType().equals("asc")) {
                Collections.sort(sorted);
            } else if (action.getSortType().equals("desc")) {
                Collections.sort(sorted, Collections.reverseOrder());
            }

            JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                    "Query result: " + sorted.toString());
            arrayResult.add(result);
        }
    }

    /**
     * Implementation for query actors average.
     * @param users
     * @param actions
     * @param movies
     * @param fileWriter
     * @param arrayResult
     * @param serials
     * @param actors
     * @param action
     * @throws IOException
     */
    public void actorQueryRatings(final List<UserInputData> users,
                                  final List<ActionInputData> actions,
                                  final List<MovieInputData> movies,
                                  final Writer fileWriter,
                                  final JSONArray arrayResult,
                                  final List<SerialInputData> serials,
                                  final List<ActorInputData> actors,
                                  final ActionInputData action) throws IOException {
        ArrayList<ActionInputData> actionRating = new ArrayList<>(); // filme
        ArrayList<ActionInputData> actionRating1 = new ArrayList<>(); // seriale

        for (int i = 0; i < action.getActionId() - 1; i++) {
            if (actions.get(i).getType() != null) {
                if (actions.get(i).getType().equals("rating")) {
                    for (UserInputData user : users) {
                        if (actions.get(i).getUsername().equals(user.getUsername())) {
                            if (user.getHistory().containsKey(actions.get(i).getTitle())) {
                                if (actions.get(i).getSeasonNumber() != 0) {
                                    for (SerialInputData serial : serials) {
                                        if (serial.getTitle().equals(actions.get(i).getTitle())) {
                                            int contained = 0;
                                            for (int o = 0; o < actionRating1.size(); o++) {
                                                if (actionRating1.get(o).getUsername()
                                                        .equals(actions.get(i).getUsername())
                                                    && actionRating1.get(o).getTitle()
                                                        .equals(serial.getTitle())) {
                                                    contained++;
                                                }
                                            }
                                            if (contained == 0) {
                                                actionRating1.add(actions.get(i));
                                            }
                                        }
                                    }
                                } else {
                                    for (MovieInputData movie : movies) {
                                        if (movie.getTitle().equals(actions.get(i).getTitle())) {
                                            int contained = 0;
                                            for (int o = 0; o < actionRating.size(); o++) {
                                                if (actionRating.get(o).getUsername()
                                                .equals(actions.get(i).getUsername())
                                                && actionRating.get(o).getTitle()
                                                .equals(movie.getTitle())) {
                                                    contained++;
                                                }
                                            }
                                            if (contained == 0) {
                                                actionRating.add(actions.get(i));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Map<String, Double> ratings = new HashMap<>(); // filme
        for (int i = 0; i < actionRating.size(); i++) {
            double k = 1;
            double grade = actionRating.get(i).getGrade();
            for (int j = i; j < actionRating.size(); j++) {
                if (actionRating.get(i).getTitle().equals(actionRating.get(j).getTitle())) {
                    grade += actionRating.get(j).getGrade();
                    k++;
                }
            }
            grade = grade / k;
            ratings.put(actionRating.get(i).getTitle(), grade);
        }

        if (actionRating1.size() != 0) {
            ArrayList<Double> grades = new ArrayList<>();
            for (int i = 0; i < actionRating1.size(); i++) {
                double k = 1;
                double grade = actionRating1.get(i).getGrade();
                for (int j = i + 1; j < actionRating1.size(); j++) {
                    if (actionRating1.get(i).getTitle().equals(actionRating1.get(j).getTitle())
                            && actionRating1.get(i).getSeasonNumber()
                            == actionRating1.get(j).getSeasonNumber()) {
                        grade += actionRating1.get(j).getGrade();
                        k++;
                    }
                }
                grade = grade / k;
                grades.add(grade);
            }

            for (int i = 0; i < actionRating1.size(); i++) {
                double grade = grades.get(i);
                for (int j = i + 1; j < actionRating1.size(); j++) {
                    if (actionRating1.get(i).getTitle().equals(actionRating1.get(j).getTitle())) {
                        grade += grades.get(j);
                    }
                }
                int nrSeasons = 0;
                for (SerialInputData serial : serials) {
                    if (serial.getTitle().equals(actionRating1.get(i).getTitle())) {
                        nrSeasons = serial.getNumberSeason();
                    }
                }
                grade = grade / nrSeasons;
                ratings.put(actionRating1.get(i).getTitle(), grade);
            }
        }

        System.out.println(ratings.toString());

        Map<String, Double> actori = new HashMap<>();
        ArrayList<String> filmography;
        int k;
        for (ActorInputData actor : actors) {
            filmography = actor.getFilmography();
            k = 0;
            Double mark = (double) 0;
            for (String film : filmography) {
                if (ratings.containsKey(film)) {
                    k++;
                    mark += ratings.get(film);
                }
            }
            mark = mark / k;
            actori.put(actor.getName(), mark);
        }

        System.out.println(actori.toString());

        Map<String, Double> sorted = new LinkedHashMap<>();
        Map<String, Double> finalSorted = new LinkedHashMap<>();
        if (action.getSortType().equals("asc")) {
            actori.entrySet()
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
            actori.entrySet()
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

        System.out.println(sorted.toString());


        List<String> listActori = new ArrayList<>();
        for (String actor : finalSorted.keySet()) {
            if (finalSorted.get(actor) > 0) {
                listActori.add(actor);
            }
        }
        System.out.println(listActori.toString());
        List<String> res = new ArrayList<>();

        if (listActori.size() > action.getNumber()) {
            for (int i = 0; i < action.getNumber(); i++) {
                res.add(listActori.get(i));
            }
        } else if (listActori.size() < action.getNumber()) {
            for (int i = 0; i < listActori.size(); i++) {
                res.add(listActori.get(i));
            }
        } else {
            res = listActori;
        }

        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                "Query result: " + res.toString());
        arrayResult.add(result);
    }

    public void moviesQueryRatings(final List<UserInputData> users,
                                   final List<ActionInputData> actions,
                                   final List<MovieInputData> movies,
                                   final Writer fileWriter,
                                   final JSONArray arrayResult,
                                   final List<SerialInputData> serials,
                                   final ActionInputData action) throws IOException {
        ArrayList<ActionInputData> actionRating = new ArrayList<>();

        for (int i = 0; i < action.getActionId() - 1; i++) {
            if (actions.get(i).getType() != null) {
                if (actions.get(i).getType().equals("rating")) {
                    for (UserInputData user : users) {
                        if (actions.get(i).getUsername().equals(user.getUsername())) {
                            if (user.getHistory().containsKey(actions.get(i).getTitle())) {
                                for (MovieInputData movie : movies) {
                                    if (movie.getTitle().equals(actions.get(i).getTitle())) {
                                        int contained = 0;
                                        for (int o = 0; o < actionRating.size(); o++) {
                                            if (actionRating.get(o).getUsername()
                                                    .equals(actions.get(i).getUsername())
                                                    && actionRating.get(o).getTitle()
                                                    .equals(movie.getTitle())) {
                                                contained++;
                                            }
                                        }
                                        if (contained == 0) {
                                            actionRating.add(actions.get(i));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        List<List<String>> filters = action.getFilters();

        Map<String, Double> ratings = new HashMap<>(); // filme
        for (int i = 0; i < actionRating.size(); i++) {
            double k = 1;
            double grade = actionRating.get(i).getGrade();
            for (int j = i; j < actionRating.size(); j++) {
                if (actionRating.get(i).getTitle().equals(actionRating.get(j).getTitle())) {
                    grade += actionRating.get(j).getGrade();
                    k++;
                }
            }
            grade = grade / k;
            ratings.put(actionRating.get(i).getTitle(), grade);
        }

        ArrayList<String> films = new ArrayList<>();
        for (String film : ratings.keySet()) {
            for (MovieInputData movie : movies) {
                if (movie.getTitle().equals(film)) {
                    if (filters.get(0) != null
                        && filters.get(1) != null) {
                        if (movie.getYear() == Integer.parseInt(filters.get(0).get(0))
                                && movie.getGenres().contains(filters.get(1).get(0))) {
                            films.add(film);
                        }
                    } else if (filters.get(0) != null) {
                        if (movie.getYear() == Integer.parseInt(filters.get(0).get(0))) {
                            films.add(film);
                        }
                    } else if (filters.get(1) != null) {
                        if (movie.getGenres().contains(filters.get(1).get(0))) {
                            films.add(film);
                        }
                    }
                }
            }
        }

        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                "Query result: " + films.toString());
        arrayResult.add(result);


    }

    /**
     * The main function which choose what function to execute.
     *
     * @param users
     * @param actions
     * @param movies
     * @param fileWriter
     * @param arrayResult
     * @param serials
     * @param actors
     * @throws IOException
     */
    public void execute(final List<UserInputData> users,
                        final List<ActionInputData> actions,
                        final List<MovieInputData> movies,
                        final Writer fileWriter,
                        final JSONArray arrayResult,
                        final List<SerialInputData> serials,
                        final List<ActorInputData> actors) throws IOException {
        for (ActionInputData action : actions) {
            if (action.getActionType() != null) {
                if (action.getActionType().equals("query")) {
                    if (action.getObjectType().equals("users")) {
                        usersQuery(actions, action, users, fileWriter, arrayResult);
                    }
                    if (action.getObjectType().equals("actors")
                        && (action.getCriteria().equals("filter_description")
                        || action.getCriteria().equals("awards"))) {
                        actorQuery(action, actors, fileWriter, arrayResult);
                    }
                    if (action.getObjectType().equals("actors")
                        && action.getCriteria().equals("average")) {
                        actorQueryRatings(users, actions, movies, fileWriter,
                                          arrayResult, serials, actors, action);
                    }
                    if (action.getObjectType().equals("movies")
                        && action.getCriteria().equals("ratings")) {
                        moviesQueryRatings(users, actions, movies, fileWriter,
                                           arrayResult, serials, action);
                    }
                }
                if (action.getActionType().equals("command")) {
                    if (action.getType().equals("favorite")) {
                        favorite(users, action, fileWriter, arrayResult);
                    }
                    if (action.getType().equals("view")) {
                        view(users, actions, action, fileWriter, arrayResult);
                    }
                    if (action.getType().equals("rating")) {
                        rating(users, action, fileWriter, arrayResult);
                    }
                }
                if (action.getActionType().equals("recommendation")) {
                    if (action.getType().equals("standard")) {
                        standard(users, action, movies, fileWriter, arrayResult);
                    }
                    if (action.getType().equals("best_unseen")) {
                        bestUnseen(users, actions, action, movies,
                                serials, fileWriter, arrayResult);
                    }
                    if (action.getType().equals("favorite")) {
                        favoriteRating(users, actions, action, movies,
                                serials, fileWriter, arrayResult);
                    }
                    if (action.getType().equals("search")) {
                        searchRecommendation(users, actions, action,
                                movies, serials, fileWriter, arrayResult);
                    }
                    if (action.getType().equals("popular")) {
                        popularRecommendation(users, actions, action, movies,
                                fileWriter, arrayResult, serials);
                    }
                }
            }
        }
    }
}
