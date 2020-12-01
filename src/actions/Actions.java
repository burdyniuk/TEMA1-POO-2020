package actions;

import actor.ActorsAwards;
import entertainment.Season;
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
     * @param users list of all users
     * @param action list of all actions
     * @param fileWriter writer
     * @param arrayResult array of json
     * @throws IOException fo writer
     */
    public void favorite(final List<UserInputData> users,
                         final ActionInputData action,
                         final List<ActionInputData> actions,
                         final Writer fileWriter,
                         final JSONArray arrayResult) throws IOException {
        int viewed = 0;
        for (UserInputData user : users) {
            if (user.getUsername().equals(action.getUsername())) {
                if (user.getHistory().containsKey(action.getTitle())) {
                    if (!user.getFavoriteMovies().contains(action.getTitle())) {
                        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                                "success -> " + action.getTitle()
                                        + " was added as favourite");
                        arrayResult.add(result);
                    } else {
                        // if was added in favorite list
                        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                                "error -> " + action.getTitle()
                                        + " is already in favourite list");
                        arrayResult.add(result);
                    }
                } else {
                    for (int i = 0; i < actions.size() - 1; i++) {
                        if (actions.get(i).getUsername() != null) {
                            if (actions.get(i).getUsername().equals(action.getUsername())
                                    && actions.get(i).getType().equals("view")
                                    && actions.get(i).getTitle().equals(action.getTitle())) {
                                viewed++;
                            }
                        }
                    }
                    if (viewed == 0) { // if wasn't viewed
                        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                                "error -> " + action.getTitle()
                                        + " is not seen");
                        arrayResult.add(result);
                    } else {
                        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                                "success -> " + action.getTitle()
                                        + " was added as favourite");
                        arrayResult.add(result);
                    }
                }
            }
        }
    }

    /**
     * Implementation of giving rating to a film command.
     * @param users list of users
     * @param action current action
     * @param actions list of all actions
     * @param serials list of all serials
     * @param movies list of all movies
     * @param fileWriter writer
     * @param arrayResult array of json
     * @throws IOException for writer
     */
    public void rating(final List<UserInputData> users,
                       final ActionInputData action,
                       final List<ActionInputData> actions,
                       final List<SerialInputData> serials,
                       final List<MovieInputData> movies,
                       final Writer fileWriter,
                       final JSONArray arrayResult) throws IOException {
        ArrayList<ActionInputData> actionRating = new ArrayList<ActionInputData>(); // films
        ArrayList<ActionInputData> actionRating1 = new ArrayList<ActionInputData>(); // serials
        UserInputData findUser = null;
        for (UserInputData itUser : users) {
            if (action.getUsername().equals(itUser.getUsername())) {
                findUser = itUser;
            }
        }

        // collect films from rating actions
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
                                                        .equals(serial.getTitle())
                                                        && actionRating1.get(o).getSeasonNumber()
                                                        == actions.get(i).getSeasonNumber()) {
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

        int contain = 0;
        for (ActionInputData itAction : actionRating) {
            if (itAction.getTitle().equals(action.getTitle())) {
                if (action.getUsername().equals(itAction.getUsername())) {
                    contain++;
                }
            }
        }

        if (action.getSeasonNumber() != 0) {
            for (ActionInputData itAction : actionRating1) {
                if (action.getTitle().equals(itAction.getTitle())) {
                    if (action.getUsername().equals(itAction.getUsername())) {
                        if (action.getSeasonNumber() == itAction.getSeasonNumber()) {
                            contain++;
                        }
                    }
                }
            }
        }

        // if is seen rate this video
        if (contain == 0) {
            if (findUser.getHistory().containsKey(action.getTitle())) {
                double grade = action.getGrade();
                JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                        "success -> " + action.getTitle()
                                + " was rated with " + grade + " by " + findUser.getUsername());
                arrayResult.add(result);
            } else {
                JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                            "error -> " + action.getTitle() + " is not seen");
                arrayResult.add(result);
            }
        } else {
            // if was already rated
            JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                    "error -> " + action.getTitle()
                            + " has been already rated");
            arrayResult.add(result);
        }

    }

    /**
     * Implementation of view command.
     *
     * @param users list of all users
     * @param allactions list of all actions
     * @param action current action
     * @param fileWriter writer
     * @param arrayResult array of json
     * @throws IOException for writer
     */
    public void view(final List<UserInputData> users,
                     final List<ActionInputData> allactions,
                     final ActionInputData action,
                     final Writer fileWriter,
                     final JSONArray arrayResult) throws IOException {
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
     * Implement standard recommendation. Give the first don't seen movie.
     *
     * @param users list of all users
     * @param action current action
     * @param movies list of all movies
     * @param fileWriter writer
     * @param arrayResult array of json
     * @throws IOException for writer
     */
    public void standard(final List<UserInputData> users,
                         final ActionInputData action,
                         final List<MovieInputData> movies,
                         final Writer fileWriter,
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
     * @param users list of all users
     * @param actions list of all actions
     * @param action current action
     * @param movies list of all movies
     * @param serials list of all serials
     * @param fileWriter writer
     * @param arrayResult array of json
     * @throws IOException for writer
     */
    public void bestUnseen(final List<UserInputData> users,
                           final List<ActionInputData> actions,
                           final ActionInputData action,
                           final List<MovieInputData> movies,
                           final List<SerialInputData> serials,
                           final Writer fileWriter,
                           final JSONArray arrayResult) throws IOException {
        ArrayList<ActionInputData> actionRating = new ArrayList<ActionInputData>(); // films
        ArrayList<ActionInputData> actionRating1 = new ArrayList<ActionInputData>(); // serials

        // collect all rating commands
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

        Map<String, Double> ratings = new HashMap<String, Double>(); // films
        // calculate the media for films
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

        // calculate the media for season of serial
        if (actionRating1.size() != 0) {
            ArrayList<Double> grades = new ArrayList<Double>();
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

            // calculate the media for entire serial
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


        // sort desc
        Map<String, Double> sorted = new LinkedHashMap<String, Double>();
        ratings.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));


        UserInputData user = null;
        JSONObject result = null;
        // find user which request best recommendation
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
     * Implementation of command favorite recommendation.
     *
     * @param users list of all users
     * @param actions list of all actions
     * @param action current action
     * @param movies list of all movies
     * @param serials list of all serials
     * @param fileWriter writer
     * @param arrayResult array of json
     * @throws IOException for writer
     */
    public void favoriteRating(final List<UserInputData> users,
                               final List<ActionInputData> actions,
                               final ActionInputData action,
                               final List<MovieInputData> movies,
                               final List<SerialInputData> serials,
                               final Writer fileWriter,
                               final JSONArray arrayResult) throws IOException {
        // find user which request recommendation
        UserInputData user = null;
        for (UserInputData findUser : users) {
            if (findUser.getUsername().equals(action.getUsername())) {
                user = findUser;
            }
        }

        JSONObject result = null;
        if (user.getSubscriptionType().equals("PREMIUM")) {
            ArrayList<String> films = new ArrayList<String>();
            for (UserInputData favoriteUsers : users) {
                if (!favoriteUsers.equals(user)) {
                    // movies
                    for (MovieInputData movie : movies) {
                        if (favoriteUsers.getFavoriteMovies().contains(movie.getTitle())) {
                            films.add(movie.getTitle());
                        }
                    }
                    // serials
                    for (SerialInputData serial : serials) {
                        if (favoriteUsers.getFavoriteMovies().contains(serial.getTitle())) {
                            films.add(serial.getTitle());
                        }
                    }
                    // from actions
                    for (int i = 0; i < action.getActionId() - 1; i++) {
                        if (actions.get(i).getType() != null) {
                            if (actions.get(i).getType().equals("favorite")) {
                                if (user.getUsername().equals(action.getUsername())) {
                                    if (user.getHistory()
                                            .containsKey(actions.get(i).getTitle())) {
                                        films.add(actions.get(i).getTitle());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Map<String, Integer> rating = new HashMap<String, Integer>();
            for (int i = 0; i < films.size(); i++) {
                int adds = 1;
                for (int j = i + 1; j < films.size(); j++) {
                    if (films.get(i).equals(films.get(j)) && !films.get(j).equals("altfel")) {
                        adds++;
                        // rename to not use it in future
                        films.set(j, "altfel");
                    }
                }
                if (!films.get(i).equals("altfel")) {
                    rating.put(films.get(i), adds);
                }
            }

            // sort films by rating
            Map<String, Integer> sorted = new LinkedHashMap<String, Integer>();
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
     * @param users list of all users
     * @param actions list of all actions
     * @param action current action
     * @param movies list of all movies
     * @param serials list of all serials
     * @param fileWriter writer
     * @param arrayResult array of json
     * @throws IOException for writer
     */
    public void searchRecommendation(final List<UserInputData> users,
                                     final List<ActionInputData> actions,
                                     final ActionInputData action,
                                     final List<MovieInputData> movies,
                                     final List<SerialInputData> serials,
                                     final Writer fileWriter,
                                     final JSONArray arrayResult) throws IOException {
        UserInputData user = null;
        // find user which request search recommendation
        for (UserInputData findUser : users) {
            if (findUser.getUsername().equals(action.getUsername())) {
                user = findUser;
            }
        }
        if (user.getSubscriptionType().equals("PREMIUM")) {
            ArrayList<String> films = new ArrayList<String>();
            for (MovieInputData movie : movies) {
                if (movie.getGenres().contains(action.getGenre())) {
                    films.add(movie.getTitle());
                }
            }

            for (SerialInputData serial : serials) {
                if (serial.getGenres().contains(action.getGenre())) {
                    films.add(serial.getTitle());
                }
            }

            Collections.sort(films);
            for (String film : user.getHistory().keySet()) {
                if (films.contains(film)) {
                    films.remove(film);
                }
            }

            JSONObject result;
            // if is empty it cannot be applied
            if (films.isEmpty()) {
                result = fileWriter.writeFile(action.getActionId(), "",
                        "SearchRecommendation cannot be applied!");

            } else {
                // if is not empty give result
                result = fileWriter.writeFile(action.getActionId(), "",
                        "SearchRecommendation result: " + films);
            }
            arrayResult.add(result);
        } else {
            // if is not use don't have premium subscription
            JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                    "SearchRecommendation cannot be applied!");
            arrayResult.add(result);
        }
    }

    /**
     * Implementation of popular recommendation.
     *
     * @param users list of all users
     * @param action current action
     * @param movies list of all movies
     * @param fileWriter writer
     * @param arrayResult array of json
     * @param serials list of all serials
     * @throws IOException for writer
     */
    public void popularRecommendation(final List<UserInputData> users,
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
            // make ratings hashmap
            Map<String, Integer> ratings = new HashMap<String, Integer>();
            for (UserInputData findUser : users) {
                Map<String, Integer> films = findUser.getHistory();
                for (String film : films.keySet()) {
                    for (MovieInputData movie : movies) {
                        if (movie.getTitle().equals(film)) {
                            ArrayList<String> genres = movie.getGenres();
                            for (String gen : genres) {
                                Integer views;
                                if (ratings.containsKey(gen)) {
                                    views = ratings.get(gen);
                                    views += films.get(film);
                                } else {
                                    views = films.get(film);
                                }
                                ratings.put(gen, views);
                            }
                        }
                    }
                    for (SerialInputData serial : serials) {
                        if (serial.getTitle().equals(film)) {
                            ArrayList<String> genres = serial.getGenres();
                            for (String gen : genres) {
                                Integer views;
                                if (ratings.containsKey(gen)) {
                                    views = ratings.get(gen);
                                    views += films.get(film);
                                } else {
                                    views = films.get(film);
                                }
                                ratings.put(gen, views);
                            }
                        }
                    }
                }
            }

            // sort the map by rating
            Map<String, Integer> sorted = new LinkedHashMap<String, Integer>();
            ratings.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));

            // the first video doesn't viewed by user from the most popular genre
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
            // if user don't have premium subscription
            result = fileWriter.writeFile(action.getActionId(), "",
                    "PopularRecommendation cannot be applied!");
            arrayResult.add(result);
        }
    }

    /**
     * Implementation of query users.
     *
     * @param actions list of all actions
     * @param action current action
     * @param users list of all users
     * @param fileWriter writer
     * @param arrayResult array of json
     * @throws IOException for writer
     */
    public void usersQuery(final List<ActionInputData> actions,
                           final ActionInputData action,
                           final List<UserInputData> users,
                           final Writer fileWriter,
                           final JSONArray arrayResult) throws IOException {
        // create a map with users and theirs marks
        Map<String, Integer> query = new HashMap<String, Integer>();
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
        // sorting by rating, the type of sorting is in action
        Map<String, Integer> finalSorted = Sort.sorting(query, action);
        ArrayList<String> res = new ArrayList<String>();
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
     *
     * @param action current action
     * @param actors list of all actors
     * @param fileWriter writer
     * @param arrayResult array of json
     * @throws IOException for writer
     */
    public void actorQuery(final ActionInputData action,
                           final List<ActorInputData> actors,
                           final Writer fileWriter,
                           final JSONArray arrayResult) throws IOException {
        // collect actors by number of awards
        Map<String, Integer> acts = new HashMap<String, Integer>();
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
                        for (ActorsAwards awa : actor.getAwards().keySet()) {
                            if (actor.getAwards().containsKey(awa)) {
                                k += actor.getAwards().get(awa);
                            }
                        }
                        acts.put(actor.getName(), k);
                    }
                }
            }
            // sort hashmap by ratings
            Map<String, Integer> sorted = new LinkedHashMap<String, Integer>();
            if (action.getSortType().equals("asc")) {
                acts.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue())
                        .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
            } else if (action.getSortType().equals("desc")) {
                acts.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
            }
            ArrayList<String> res = new ArrayList<String>();
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
                        acts.put(actor.getName(), 1);
                    }
                }
            }
            // sort the list
            List<String> sorted = new ArrayList<String>(acts.keySet());
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
     *
     * @param users list of all users
     * @param actions list of actions
     * @param movies list of all movies
     * @param fileWriter writer
     * @param arrayResult array of json
     * @param serials list of all serials
     * @param actors list of all actors
     * @param action current action
     * @throws IOException for writer
     */
    public void actorQueryRatings(final List<UserInputData> users,
                                  final List<ActionInputData> actions,
                                  final List<MovieInputData> movies,
                                  final Writer fileWriter,
                                  final JSONArray arrayResult,
                                  final List<SerialInputData> serials,
                                  final List<ActorInputData> actors,
                                  final ActionInputData action) throws IOException {
        ArrayList<ActionInputData> actionRating = new ArrayList<ActionInputData>(); // films
        ArrayList<ActionInputData> actionRating1 = new ArrayList<ActionInputData>(); // serials

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
                                                        .equals(serial.getTitle())
                                                    && actionRating1.get(o).getSeasonNumber()
                                                    == actions.get(i).getSeasonNumber()) {
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

        // create the rating hashmap
        Map<String, Double> ratings = new HashMap<String, Double>();
        for (int i = 0; i < actionRating.size(); i++) {
            double k = 1;
            double grade = actionRating.get(i).getGrade();
            for (int j = i + 1; j < actionRating.size(); j++) {
                if (actionRating.get(i).getTitle().equals(actionRating.get(j).getTitle())) {
                    grade += actionRating.get(j).getGrade();
                    k++;
                    actionRating.remove(j);
                }
            }
            grade = grade / k;
            ratings.put(actionRating.get(i).getTitle(), grade);
        }

        if (actionRating1.size() != 0) {
            ArrayList<Double> grades = new ArrayList<Double>();
            for (int i = 0; i < actionRating1.size(); i++) {
                double k = 1;
                double grade = actionRating1.get(i).getGrade();
                for (int j = i + 1; j < actionRating1.size(); j++) {
                    if (actionRating1.get(i).getTitle().equals(actionRating1.get(j).getTitle())
                            && actionRating1.get(i).getSeasonNumber()
                            == actionRating1.get(j).getSeasonNumber()) {
                        grade += actionRating1.get(j).getGrade();
                        k++;
                        actionRating1.remove(j);
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
                        actionRating1.remove(j);
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

        // create actors hashmap
        Map<String, Double> acts = new HashMap<String, Double>();
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
            acts.put(actor.getName(), mark);
        }

        Map<String, Double> finalSorted = Sort.sortingDouble(acts, action);
        List<String> listActors = new ArrayList<String>();
        for (String actor : finalSorted.keySet()) {
            if (finalSorted.get(actor) > 0) {
                listActors.add(actor);
            }
        }
        List<String> res = new ArrayList<String>();
        // create the list
        if (listActors.size() > action.getNumber()) {
            for (int i = 0; i < action.getNumber(); i++) {
                res.add(listActors.get(i));
            }
        } else if (listActors.size() < action.getNumber()) {
            for (int i = 0; i < listActors.size(); i++) {
                res.add(listActors.get(i));
            }
        } else {
            res = listActors;
        }
        // add to json array the result of query
        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                "Query result: " + res.toString());
        arrayResult.add(result);
    }

    /**
     * Query movies for criteria Ratings.
     *
     * @param users list of all users
     * @param actions list of all actions
     * @param movies list of all movies
     * @param fileWriter writer
     * @param arrayResult array of json
     * @param action current action
     * @throws IOException writer
     */
    public void moviesQueryRatings(final List<UserInputData> users,
                                   final List<ActionInputData> actions,
                                   final List<MovieInputData> movies,
                                   final Writer fileWriter,
                                   final JSONArray arrayResult,
                                   final ActionInputData action) throws IOException {
        // create the list of rating actions for movies
        ArrayList<ActionInputData> actionRating = new ArrayList<ActionInputData>();
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

        // get filters from action
        List<List<String>> filters = action.getFilters();
        // create the map with ratings of films
        Map<String, Double> ratings = new HashMap<String, Double>();
        for (int i = 0; i < actionRating.size(); i++) {
            double k = 1;
            double grade = actionRating.get(i).getGrade();
            for (int j = i + 1; j < actionRating.size(); j++) {
                if (actionRating.get(i).getTitle().equals(actionRating.get(j).getTitle())) {
                    grade += actionRating.get(j).getGrade();
                    k++;
                    actionRating.remove(j);
                }
            }
            grade = grade / k;
            ratings.put(actionRating.get(i).getTitle(), grade);
        }

        // filter the films
        ArrayList<String> films = new ArrayList<String>();
        for (String film : ratings.keySet()) {
            for (MovieInputData movie : movies) {
                if (movie.getTitle().equals(film)) {
                    if (filters.get(0) != null
                        && filters.get(1) != null) {
                        if (movie.getYear() == Integer.parseInt(filters.get(0).get(0))
                                && movie.getGenres().contains(filters.get(1).get(0))) {
                            films.add(film);
                            break;
                        }
                    } else if (filters.get(0) == null
                            && filters.get(1) == null) {
                        films.add(film);
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
     * Query shows for criteria ratings.
     *
     * @param users list of all users
     * @param actions list of all actions
     * @param serials list of all serials
     * @param fileWriter writer
     * @param arrayResult array of json
     * @param action current action
     * @throws IOException for writer
     */
    public void showsQueryRatings(final List<UserInputData> users,
                                  final List<ActionInputData> actions,
                                  final List<SerialInputData> serials,
                                  final Writer fileWriter,
                                  final JSONArray arrayResult,
                                  final ActionInputData action) throws IOException {
        // create the rating actions list of serials
        ArrayList<ActionInputData> actionRating1 = new ArrayList<ActionInputData>();

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
                                                        .equals(serial.getTitle())
                                                        && actionRating1.get(o).getSeasonNumber()
                                                        == actions.get(i).getSeasonNumber()) {
                                                    contained++;
                                                }
                                            }
                                            if (contained == 0) {
                                                actionRating1.add(actions.get(i));
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

        List<List<String>> filters = action.getFilters();
        Map<String, Double> ratings = new HashMap<String, Double>();

        // create the rating map
        if (actionRating1.size() != 0) {
            ArrayList<Double> grades = new ArrayList<Double>();
            for (int i = 0; i < actionRating1.size(); i++) {
                double k = 1;
                double grade = actionRating1.get(i).getGrade();
                for (int j = i + 1; j < actionRating1.size(); j++) {
                    if (actionRating1.get(i).getTitle().equals(actionRating1.get(j).getTitle())
                            && actionRating1.get(i).getSeasonNumber()
                            == actionRating1.get(j).getSeasonNumber()) {
                        grade += actionRating1.get(j).getGrade();
                        k++;
                        actionRating1.remove(j);
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
                        actionRating1.remove(j);
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

        // filter the serials
        ArrayList<String> films = new ArrayList<String>();
        for (String film : ratings.keySet()) {
            for (SerialInputData movie : serials) {
                if (movie.getTitle().equals(film)) {
                    if (filters.get(0).get(0) != null
                            && filters.get(1) != null) {
                        if (movie.getYear() == Integer.parseInt(filters.get(0).get(0))
                                && movie.getGenres().contains(filters.get(1).get(0))) {
                            films.add(film);
                        }
                    } else if (filters.get(0).get(0) != null) {
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
     * Query movies for criteria longest.
     *
     * @param movies list of all movies
     * @param fileWriter writer
     * @param arrayResult array of json
     * @param action current action
     * @throws IOException for writer
     */
    public void moviesQueryLongest(final List<MovieInputData> movies,
                                   final Writer fileWriter,
                                   final JSONArray arrayResult,
                                   final ActionInputData action) throws IOException {
        // create a map with the name od films and their duration
        Map<String, Integer> films = new HashMap<String, Integer>();
        for (MovieInputData movie : movies) {
            films.put(movie.getTitle(), movie.getDuration());
        }

        // sort by duration and name if duration is equal
        ArrayList<String> filmsList = Sort.sortingFilms(films, action, movies);
        List<String> res = new ArrayList<String>();

        if (filmsList.size() > action.getNumber()) {
            for (int i = 0; i < action.getNumber(); i++) {
                res.add(filmsList.get(i));
            }
        } else if (filmsList.size() < action.getNumber()) {
            for (int i = 0; i < filmsList.size(); i++) {
                res.add(filmsList.get(i));
            }
        } else {
            res = filmsList;
        }

        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                "Query result: " + res.toString());
        arrayResult.add(result);

    }

    /**
     * Query serials for criteria Longest.
     *
     * @param serials list of all serials
     * @param fileWriter writer
     * @param arrayResult array of json
     * @param action current action
     * @throws IOException for writer
     */
    public void serialsQueryLongest(final List<SerialInputData> serials,
                                    final Writer fileWriter,
                                    final JSONArray arrayResult,
                                    final ActionInputData action) throws IOException {
        // create a map with serials name and their duration
        Map<String, Integer> films = new HashMap<String, Integer>();
        for (SerialInputData serial : serials) {
            ArrayList<Season> seasons = serial.getSeasons();
            int durata = 0;
            for (Season season : seasons) {
                durata += season.getDuration();
            }
            films.put(serial.getTitle(), durata);
        }

        // sort by duration and name if duration is equal
        ArrayList<String> filmsList = Sort.sortingSerials(films, action, serials);
        List<String> res = new ArrayList<String>();

        if (filmsList.size() > action.getNumber()) {
            for (int i = 0; i < action.getNumber(); i++) {
                res.add(filmsList.get(i));
            }
        } else if (filmsList.size() < action.getNumber()) {
            for (int i = 0; i < filmsList.size(); i++) {
                res.add(filmsList.get(i));
            }
        } else {
            res = filmsList;
        }

        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                "Query result: " + res.toString());
        arrayResult.add(result);
    }

    /**
     * Query movies for criteria most viewed.
     *
     * @param users list of all users
     * @param movies list of all movies
     * @param fileWriter writer
     * @param arrayResult array of json
     * @param action current action
     * @throws IOException for writer
     */
    public void moviesQueryMostViewed(final List<UserInputData> users,
                                      final List<MovieInputData> movies,
                                      final Writer fileWriter,
                                      final JSONArray arrayResult,
                                      final ActionInputData action,
                                      final List<ActionInputData> actions) throws IOException {
        // create a map with name of films and their views
        Map<String, Integer> films = new HashMap<String, Integer>();
        for (UserInputData user : users) {
            Map<String, Integer> history = user.getHistory();
            for (String film : history.keySet()) {
                int views = 0;
                if (films.containsKey(film)) {
                    views = films.get(film);
                }
                views += history.get(film);
                films.put(film, views);
            }
        }

        // add views from actions
        for (ActionInputData act : actions) {
            if (act.getType() != null) {
                if (act.getType().equals("view")) {
                    int views = 0;
                    if (films.containsKey(act.getTitle())) {
                        views = films.get(act.getTitle());
                        views += 1;
                    }
                    films.put(act.getTitle(), views);
                }
            }
        }

        // sort the map
        ArrayList<String> filmsList = Sort.sortingFilms(films, action, movies);
        List<String> res = new ArrayList<String>();

        if (filmsList.size() > action.getNumber()) {
            for (int i = 0; i < action.getNumber(); i++) {
                res.add(filmsList.get(i));
            }
        } else if (filmsList.size() < action.getNumber()) {
            for (int i = 0; i < filmsList.size(); i++) {
                res.add(filmsList.get(i));
            }
        } else {
            res = filmsList;
        }

        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                "Query result: " + res.toString());
        arrayResult.add(result);
    }

    /**
     * Query show for criteria most viewed.
     *
     * @param users list of all users
     * @param serials list of all serials
     * @param fileWriter writer
     * @param arrayResult array of json
     * @param action current action
     * @param actions list of all actions
     * @throws IOException for writer
     */
    public void showsQueryMostViewed(final List<UserInputData> users,
                                     final List<SerialInputData> serials,
                                     final Writer fileWriter,
                                     final JSONArray arrayResult,
                                     final ActionInputData action,
                                     final List<ActionInputData> actions) throws IOException {
        // create a map with serials name and their views
        Map<String, Integer> films = new HashMap<String, Integer>();
        for (UserInputData user : users) {
            Map<String, Integer> history = user.getHistory();
            for (String film : history.keySet()) {
                int views = 0;
                if (films.containsKey(film)) {
                    views = films.get(film);
                }
                views += history.get(film);
                films.put(film, views);
            }
        }

        // add views from actions
        for (ActionInputData act : actions) {
            if (act.getType() != null) {
                if (act.getType().equals("view")) {
                    int views = 0;
                    if (films.containsKey(act.getTitle())) {
                        views = films.get(act.getTitle());
                        views += 1;
                    }
                    films.put(act.getTitle(), views);
                }
            }
        }

        // sort the serials map
        ArrayList<String> filmsList = Sort.sortingSerials(films, action, serials);
        List<String> res = new ArrayList<String>();

        if (filmsList.size() > action.getNumber()) {
            for (int i = 0; i < action.getNumber(); i++) {
                res.add(filmsList.get(i));
            }
        } else if (filmsList.size() < action.getNumber()) {
            for (int i = 0; i < filmsList.size(); i++) {
                res.add(filmsList.get(i));
            }
        } else {
            res = filmsList;
        }

        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                "Query result: " + res.toString());
        arrayResult.add(result);
    }

    /**
     * Query movies for criteria favorite.
     *
     * @param users list of all users
     * @param actions list of all actions
     * @param movies list of all movies
     * @param fileWriter writer
     * @param arrayResult array of json
     * @param action current action
     * @throws IOException for writer
     */
    public void moviesQueryFavorite(final List<UserInputData> users,
                                    final List<ActionInputData> actions,
                                    final List<MovieInputData> movies,
                                    final Writer fileWriter,
                                    final JSONArray arrayResult,
                                    final ActionInputData action) throws IOException {
        // create a map with films name and their adds in favorite
        Map<String, Integer> films = new HashMap<String, Integer>();
        for (UserInputData user : users) {
            ArrayList<String> favorites = user.getFavoriteMovies();
            for (String fav : favorites) {
                int adds = 0;
                if (films.containsKey(fav)) {
                    adds = films.get(fav);
                }
                adds++;
                films.put(fav, adds);
            }

            // add from actions
            for (int i = 0; i < action.getActionId() - 1; i++) {
                if (actions.get(i).getType() != null) {
                    if (actions.get(i).getType().equals("favorite")) {
                        if (actions.get(i).getUsername().equals(user.getUsername())) {
                            if (user.getHistory().containsKey(actions.get(i).getTitle())) {
                                if (films.containsKey(actions.get(i).getTitle())
                                    && !user.getFavoriteMovies()
                                        .contains(actions.get(i).getTitle())) {
                                    films.put(actions.get(i).getTitle(), 1);
                                }
                            }
                        }
                    }
                }
            }
        }

        // sort the movies
        ArrayList<String> filmsList = Sort.sortingFilms(films, action, movies);
        List<String> res = new ArrayList<String>();

        if (filmsList.size() > action.getNumber()) {
            for (int i = 0; i < action.getNumber(); i++) {
                res.add(filmsList.get(i));
            }
        } else if (filmsList.size() < action.getNumber()) {
            for (int i = 0; i < filmsList.size(); i++) {
                res.add(filmsList.get(i));
            }
        } else {
            res = filmsList;
        }

        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                "Query result: " + res.toString());
        arrayResult.add(result);
    }

    /**
     * Query serials for criteria favorite.
     *
     * @param users list of all users
     * @param actions list of all actions
     * @param serials list of all serials
     * @param fileWriter writer
     * @param arrayResult array of json
     * @param action current action
     * @throws IOException for writer
     */
    public void serialsQueryFavorite(final List<UserInputData> users,
                                    final List<ActionInputData> actions,
                                    final List<SerialInputData> serials,
                                    final Writer fileWriter,
                                    final JSONArray arrayResult,
                                    final ActionInputData action) throws IOException {
        // create the map with serials name and their adds in favorite
        Map<String, Integer> films = new HashMap<String, Integer>();
        for (UserInputData user : users) {
            ArrayList<String> favorites = user.getFavoriteMovies();
            for (String fav : favorites) {
                int adds = 0;
                if (films.containsKey(fav)) {
                    adds = films.get(fav);
                }
                adds++;
                films.put(fav, adds);
            }

            // add from favorite
            for (int i = 0; i < action.getActionId() - 1; i++) {
                if (actions.get(i).getType() != null) {
                    if (actions.get(i).getType().equals("favorite")) {
                        if (actions.get(i).getUsername().equals(user.getUsername())) {
                            if (user.getHistory().containsKey(actions.get(i).getTitle())) {
                                if (films.containsKey(actions.get(i).getTitle())
                                        && !user.getFavoriteMovies()
                                        .contains(actions.get(i).getTitle())) {
                                    films.put(actions.get(i).getTitle(), 1);
                                }
                            }
                        }
                    }
                }
            }
        }

        // sort the serials
        ArrayList<String> filmsList = Sort.sortingSerials(films, action, serials);
        List<String> res = new ArrayList<String>();

        if (filmsList.size() > action.getNumber()) {
            for (int i = 0; i < action.getNumber(); i++) {
                res.add(filmsList.get(i));
            }
        } else if (filmsList.size() < action.getNumber()) {
            for (int i = 0; i < filmsList.size(); i++) {
                res.add(filmsList.get(i));
            }
        } else {
            res = filmsList;
        }

        JSONObject result = fileWriter.writeFile(action.getActionId(), "",
                "Query result: " + res.toString());
        arrayResult.add(result);

    }

    /**
     * The main function which choose what function to execute.
     *
     * @param users list of all users
     * @param actions list of all actions
     * @param movies list of all movies
     * @param fileWriter writer
     * @param arrayResult array of json
     * @param serials list of all serials
     * @param actors list of all actors
     * @throws IOException for writer
     */
    public void execute(final List<UserInputData> users,
                        final List<ActionInputData> actions,
                        final List<MovieInputData> movies,
                        final Writer fileWriter,
                        final JSONArray arrayResult,
                        final List<SerialInputData> serials,
                        final List<ActorInputData> actors) throws IOException {
        // verify every action and choose which function to use
        for (ActionInputData action : actions) {
            if (action.getActionType() != null) {
                if (action.getActionType().equals("query")) {
                    if (action.getObjectType().equals("users")) {
                        usersQuery(actions, action, users, fileWriter, arrayResult);
                    }
                    if (action.getObjectType().equals("actors")) {
                        if (action.getCriteria().equals("filter_description")
                        || action.getCriteria().equals("awards"))  {
                            actorQuery(action, actors, fileWriter, arrayResult);
                        }
                        if (action.getCriteria().equals("average")) {
                            actorQueryRatings(users, actions, movies, fileWriter,
                                    arrayResult, serials, actors, action);
                        }
                    }
                    if (action.getObjectType().equals("movies")) {
                        if (action.getCriteria().equals("ratings")) {
                            moviesQueryRatings(users, actions, movies, fileWriter,
                                    arrayResult, action);
                        }
                        if (action.getCriteria().equals("longest")) {
                            moviesQueryLongest(movies, fileWriter, arrayResult,
                                    action);
                        }
                        if (action.getCriteria().equals("most_viewed")) {
                            moviesQueryMostViewed(users, movies, fileWriter, arrayResult,
                                    action, actions);
                        }
                        if (action.getCriteria().equals("favorite")) {
                            moviesQueryFavorite(users, actions, movies, fileWriter, arrayResult,
                                    action);
                        }
                    }
                    if (action.getObjectType().equals("shows")) {
                        if (action.getCriteria().equals("ratings")) {
                            showsQueryRatings(users, actions, serials, fileWriter,
                                    arrayResult, action);
                        }
                        if (action.getCriteria().equals("longest")) {
                            serialsQueryLongest(serials, fileWriter, arrayResult,
                                    action);
                        }
                        if (action.getCriteria().equals("most_viewed")) {
                            showsQueryMostViewed(users, serials, fileWriter, arrayResult,
                                    action, actions);
                        }
                        if (action.getCriteria().equals("favorite")) {
                            serialsQueryFavorite(users, actions, serials, fileWriter,
                                    arrayResult, action);
                        }
                    }
                }
                if (action.getActionType().equals("command")) {
                    if (action.getType().equals("favorite")) {
                        favorite(users, action, actions, fileWriter, arrayResult);
                    }
                    if (action.getType().equals("view")) {
                        view(users, actions, action, fileWriter, arrayResult);
                    }
                    if (action.getType().equals("rating")) {
                        rating(users, action, actions, serials, movies, fileWriter, arrayResult);
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
                        popularRecommendation(users, action, movies,
                                fileWriter, arrayResult, serials);
                    }
                }
            }
        }
    }
}
