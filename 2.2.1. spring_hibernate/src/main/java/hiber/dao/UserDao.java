package hiber.dao;

import hiber.model.Car;
import hiber.model.User;

import java.util.List;

public interface UserDao {
   void add(User user);
   List<User> listUsers();
   List<User> findUsersByCarMaxResults(Car car, int maxResults);

   List<User> findUsersMaxResults(User user, Car car, int maxResults);
   List<Car> findCarsMaxResults(User user, Car car, int maxResults);
}
