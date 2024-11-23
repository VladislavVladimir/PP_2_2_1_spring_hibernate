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

    public abstract class SearchBuilder<T> implements Search<T>, CarSearchParameters<T>, UserSearchParameters<T>,
            UserAndCarSearchParameters<T>, UserAndOrCarSearchParameters<T> {

        private User searchUserParams;
        private Car searchCarParams;
        private int maxResults;

        protected int getMaxResults() { return maxResults; }
        protected Car getSearchCarParams() { return searchCarParams; }
        protected User getSearchUserParams() { return searchUserParams; }

        protected UserAndOrCarSearchParameters<T> clearParams() {
            searchUserParams = new User();
            searchCarParams = new Car();
            maxResults = defaultMaxResult;
            return this;
        }

        @Override
        public Search<T> carParam(String model, Integer series) {
            searchCarParams.setModel(model).setSeries(series == null ? 0 : series);
            return this;
        }
        @Override
        public Search<T> carId(Long carId) {
            searchCarParams.setId(carId == null ? 0 : carId);
            return this;
        }
        @Override
        public Search<T> carCar(Car car) {
            searchCarParams = car;
            if (searchUserParams != null) {
                searchUserParams.setCar(car);
            }
            return this;
        }

        @Override
        public Search<T> userUser(User user) {
            searchUserParams = user;
            return this;
        }
        @Override
        public Search<T> userId(Long userId) {
            searchUserParams.setId(userId == null ? 0 : userId);
            return this;
        }
        @Override
        public Search<T> userParam(String firstName, String lastName, String email) {
            searchUserParams.setFirstName(firstName).setLastName(lastName).setEmail(email);
            return this;
        }

        @Override
        public CarSearchParameters<T> userParamAnd(String firstName, String lastName, String email) {
            userParam(firstName, lastName, email);
            return this;
        }
        @Override
        public CarSearchParameters<T> userIdAnd(Long userId) {
            userId(userId);
            return this;
        }
        @Override
        public CarSearchParameters<T> userUserAnd(User user) {
            userUser(user);
            return this;
        }

        @Override
        public UserAndCarSearchParameters<T> whereUserAndCar() {
            return this;
        }
        @Override
        public CarSearchParameters<T> whereCar() {
            return this;
        }
        @Override
        public UserSearchParameters<T> whereUser() {
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

    public interface UserAndOrCarSearchParameters<T> extends Search<T> {
        UserSearchParameters<T> whereUser();
        CarSearchParameters<T> whereCar();
        UserAndCarSearchParameters<T> whereUserAndCar();
    }

    public interface UserAndCarSearchParameters<T> {
        CarSearchParameters<T> userUserAnd(User user);
        CarSearchParameters<T> userIdAnd(Long userId);
        CarSearchParameters<T> userParamAnd(String firstName, String lastName, String email);
    }

    public interface UserSearchParameters<T> {
        Search<T> userUser(User user);
        Search<T> userId(Long userId);
        Search<T> userParam(String firstName, String lastName, String email);
    }

    public interface CarSearchParameters<T> {
        Search<T> carCar(Car car);
        Search<T> carId(Long carId);
        Search<T> carParam(String model, Integer series);
    }

    public interface SearchFactory {
        UserAndOrCarSearchParameters<User> users();
        UserAndOrCarSearchParameters<Car> cars();
    }

    private class UsersAndCarsDataBase implements SearchFactory {
        @Override
        public UserAndOrCarSearchParameters<User> users() {
            return userSearchBuilder.clearParams();
        }

        @Override
        public UserAndOrCarSearchParameters<Car> cars() {
            return carSearchBuilder.clearParams();
        }
    }
}