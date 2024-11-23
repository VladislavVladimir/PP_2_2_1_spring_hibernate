package hiber.service;

import hiber.model.Car;
import hiber.model.User;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    void add(User user);
    List<User> listUsers();
    List<User> findUsersByCar(Car car, int maxResults);

    List<User> findUsersMaxResults(User user, Car car, int maxResults);
    List<Car> findCarsMaxResults(User user, Car car, int maxResults);

    @Transactional(readOnly = true)
    default List<User> findUsersByCar(Car car) { // перегрузка без указания maxResults
        return findUsersByCar(car, Integer.MAX_VALUE);
    }

    @Transactional(readOnly = true)
    default List<User> findUsersByCar(String model, int series, int maxResults) { // перегрузка по модели и серии
        return findUsersByCar(new Car(model, series), maxResults);
    }

    @Transactional(readOnly = true)
    default List<User> findUsersByCar(String model, int series) { // перегрузка без указания maxResults
        return findUsersByCar(new Car(model, series), Integer.MAX_VALUE);
    }

    @Transactional(readOnly = true)
    default Optional<User> findFirstUserByCar(Car car) { // Метод для получения первого найденного User по объекту Car
        // Под капотом используем метод для получения всех пользователей по машине с ограничением в 1 штуку
        List<User> users = findUsersByCar(car, 1);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0)); // Возвращаем первого найденного пользователя в Optional
    }

    @Transactional(readOnly = true)
    default Optional<User> findFirstUserByCar(String model, int series) { // перегрузка по модели и серии
        return findFirstUserByCar(new Car(model, series));
    }

    @Transactional(readOnly = true)
    default List<User> findUsersWithoutCar(int maxResults) { // поиск User у которых нет Car
        // Вызываем findUsersByCarMaxResult с null в качестве параметра Car
        return findUsersByCar((Car) null, maxResults);
    }

    @Transactional(readOnly = true)
    default List<User> findUsersWithoutCar() { // перегрузка без указания maxResults
        return findUsersByCar((Car) null, Integer.MAX_VALUE);
    }

    @Transactional(readOnly = true)
    default List<User> findUsersWithCar(int maxResults) { // поиск User у которых есть любая Car
        // Вызываем findUsersByCarMaxResult с null в качестве параметра Car
        return findUsersByCar(new Car(), maxResults);
    }

    @Transactional(readOnly = true)
    default List<User> findUsersWithCar() { // перегрузка без указания maxResults
        return findUsersByCar(new Car(), Integer.MAX_VALUE);
    }

    @Transactional(readOnly = true)
    default Optional<User> findFirstUserWithoutCar() { // поиска первого User без Car
        return findFirstUserByCar(null);
    }

    @Transactional(readOnly = true)
    default List<User> findUsersByCarModel(String model, int maxResults) { // перегрузка по модели
        return findUsersByCar(new Car().setModel(model), maxResults);
    }

    @Transactional(readOnly = true)
    default List<User> findUsersByCarSeries(int series, int maxResults) { // перегрузка по серии
        return findUsersByCar(new Car().setSeries(series), maxResults);
    }
}
