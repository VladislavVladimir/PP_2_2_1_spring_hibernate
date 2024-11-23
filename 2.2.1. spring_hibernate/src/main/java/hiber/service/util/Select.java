package hiber.service.util;

import hiber.model.Car;
import hiber.model.User;
import hiber.service.UserService;

import java.util.List;

public class Select {
    private int defaultMaxResult = Integer.MAX_VALUE;
    private final UserService userService;
    private final UserSearchBuilder userSearchBuilder = new UserSearchBuilder();
    private final CarSearchBuilder carSearchBuilder = new CarSearchBuilder();
    private final UsersAndCarsDataBase usersAndCarsDataBase = new UsersAndCarsDataBase();

    public Select(UserService userService) {
        this.userService = userService;
    }

    public SearchFactory from() {
        return usersAndCarsDataBase;
    }

    public Select setDefaultMaxResult(int defaultMaxResult) {
        this.defaultMaxResult = defaultMaxResult;
        return this;
    }

    private abstract class SearchBuilder<T> implements SearchOrWhere<T>, UserAndOrCarSearchParameters<T> {

        private User searchUserParams;
        private Car searchCarParams;
        private int maxResults;

        protected int getMaxResults() { return maxResults; }
        protected Car getSearchCarParams() { return searchCarParams; }
        protected User getSearchUserParams() { return searchUserParams; }

        protected SearchOrWhere<T> clearParams() {
            searchUserParams = new User();
            searchCarParams = new Car();
            maxResults = defaultMaxResult;
            return this;
        }

        @Override
        public Search<T> cParam(String model, Integer series) {
            searchCarParams.setModel(model).setSeries(series == null ? 0 : series);
            return this;
        }
        @Override
        public Search<T> cId(Long carId) {
            searchCarParams.setId(carId == null ? 0 : carId);
            return this;
        }
        @Override
        public Search<T> cCar(Car car) {
            searchCarParams = car;
            if (searchUserParams != null) {
                searchUserParams.setCar(car);
            }
            return this;
        }

        @Override
        public Search<T> uUser(User user) {
            searchUserParams = user;
            return this;
        }
        @Override
        public Search<T> uId(Long userId) {
            searchUserParams.setId(userId == null ? 0 : userId);
            return this;
        }
        @Override
        public Search<T> uParam(String firstName, String lastName, String email) {
            searchUserParams.setFirstName(firstName).setLastName(lastName).setEmail(email);
            return this;
        }

        @Override
        public CarSearchParameters<T> uParamAnd(String firstName, String lastName, String email) {
            uParam(firstName, lastName, email);
            return this;
        }
        @Override
        public CarSearchParameters<T> uIdAnd(Long userId) {
            uId(userId);
            return this;
        }
        @Override
        public CarSearchParameters<T> uUserAnd(User user) {
            uUser(user);
            return this;
        }

        @Override
        public UserAndOrCarSearchParameters<T> where() {
            return this;
        }

        @Override
        public List<T> search(int maxResults) {
            this.maxResults = maxResults;
            return search();
        }
    }

    private class UserSearchBuilder extends SearchBuilder<User> {
        @Override
        public List<User> search() {
            return userService.findUsersMaxResults(getSearchUserParams(), getSearchCarParams(), getMaxResults());
        }
    }

    private class CarSearchBuilder extends SearchBuilder<Car> {
        @Override
        public List<Car> search() {
            return userService.findCarsMaxResults(getSearchUserParams(), getSearchCarParams(), getMaxResults());
        }
    }

    public interface Search<T> {
        List<T> search();
        List<T> search(int maxResults);
    }

    public interface SearchOrWhere<T> extends Search<T> {
        UserAndOrCarSearchParameters<T> where();
    }

    public interface UserAndOrCarSearchParameters<T> extends UserSearchParameters<T>, CarSearchParameters<T> {
        CarSearchParameters<T> uUserAnd(User user);
        CarSearchParameters<T> uIdAnd(Long userId);
        CarSearchParameters<T> uParamAnd(String firstName, String lastName, String email);
    }

    public interface UserSearchParameters<T> {
        Search<T> uUser(User user);
        Search<T> uId(Long userId);
        Search<T> uParam(String firstName, String lastName, String email);
    }

    public interface CarSearchParameters<T> {
        Search<T> cCar(Car car);
        Search<T> cId(Long carId);
        Search<T> cParam(String model, Integer series);
    }

    public interface SearchFactory {
        SearchOrWhere<User> users();
        SearchOrWhere<Car> cars();
    }

    private class UsersAndCarsDataBase implements SearchFactory {
        @Override
        public SearchOrWhere<User> users() {
            return userSearchBuilder.clearParams();
        }

        @Override
        public SearchOrWhere<Car> cars() {
            return carSearchBuilder.clearParams();
        }
    }
}