package hiber.service;

import hiber.dao.UserDao;
import hiber.model.Car;
import hiber.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImp implements UserService {
   @Autowired
   private UserDao userDao;

   @Transactional
   @Override
   public void add(User user) {
      userDao.add(user);
   }

   @Transactional(readOnly = true)
   @Override
   public List<User> listUsers() {
      return userDao.listUsers();
   }

   @Transactional(readOnly = true)
   @Override
   public List<User> findUsersByCar(Car car, int maxResults) {
      return userDao.findUsersByCarMaxResults(car, maxResults);
   }

   @Transactional(readOnly = true)
   @Override
   public List<User> findUsersMaxResults(User user, Car car, int maxResults) {
      return userDao.findUsersMaxResults(user, car, maxResults);
   }

   @Transactional(readOnly = true)
   @Override
   public List<Car> findCarsMaxResults(User user, Car car, int maxResults) {
      return userDao.findCarsMaxResults(user, car, maxResults);
   }
}
