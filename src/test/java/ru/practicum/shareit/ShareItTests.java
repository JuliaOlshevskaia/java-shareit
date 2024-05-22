package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.EmailDublicateException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShareItTests {
	private UserService userService = new UserServiceImpl();
	private ItemService itemService = new ItemServiceImpl();

	@Test
	public void testCreateUser() {
		User user = new User(null, "Name", "Email@email.com");

		User userCreated = userService.create(user);

		assertEquals(1, userCreated.getId(), "Id созданного пользователя не ранво 1");
		assertEquals("Name", userCreated.getName(), "Имя пользователя неверно");
		assertEquals("Email@email.com", userCreated.getEmail(), "Почта пользователя неверна");
	}

	@Test
	public void testExceptionCreateUserWithDublicateEmail() {
		User user1 = new User(null, "Name 1", "Email@email.com");
		userService.create(user1);

		User user2 = new User(null, "Name 2", "Email@email.com");

		assertThrows(EmailDublicateException.class, () -> userService.create(user2));
	}

	@Test
	public void testExceptionCreateUserWithNullName() {
		User user1 = new User(null, null, "Email@email.com");
		assertThrows(ValidationException.class, () -> userService.create(user1));
	}

	@Test
	public void testExceptionCreateUserWithNullEmail() {
		User user1 = new User(null, "Name", null);
		assertThrows(ValidationException.class, () -> userService.create(user1));
	}

	@Test
	public void testUpdateUser() {
		User user = new User(null, "Name", "Email@email.com");
		User userCreated = userService.create(user);

		User userNew = new User(null, "NameNew", "EmailNew@email.com");

		User userNewUpdated = userService.update(userCreated.getId(), userNew);

		assertEquals(userNewUpdated.getId(), userCreated.getId(), "Id обновленного пользователя неверно");
		assertEquals("NameNew", userNewUpdated.getName(), "Имя пользователя не обновлено");
		assertEquals("EmailNew@email.com", userNewUpdated.getEmail(), "Почта пользователя не обновлена");
	}

	@Test
	public void testExceptionUpdateUserWithDublicateEmail() {
		User user1 = new User(null, "Name 1", "Email@email.com");
		User user1Created = userService.create(user1);

		User user2 = new User(null, "Name 2", "Email2@email.com");
		User user2Created = userService.create(user2);

		User user2New = new User(null, "Name 2 new", "Email@email.com");

		assertThrows(EmailDublicateException.class, () -> userService.update(user2Created.getId(), user2New));
	}

	@Test
	public void testDeleteUser() {
		User user = new User(null, "Name", "Email@email.com");
		User userCreated = userService.create(user);

		User user2 = new User(null, "Name 2", "Email2@email.com");
		User user2Created = userService.create(user2);

		userService.delete(userCreated.getId());

		List<User> usersCreated = userService.getAllUsers();

		assertEquals(1, usersCreated.size(), "Количество пользователей неверно");
		assertEquals(user2Created.getId(), usersCreated.get(0).getId(), "Оставлен не верный пользователь");
	}

	@Test
	public void testGetAllUsers() {
		User user1 = new User(null, "Name 1", "Email1@email.com");
		User user1Created = userService.create(user1);

		User user2 = new User(null, "Name 2", "Email2@email.com");
		User user2Created = userService.create(user2);

		List<User> usersCreated = userService.getAllUsers();

		assertEquals(2, usersCreated.size(), "Количество пользователей неверно");
	}

	@Test
	public void testGetUserById() {
		User user1 = new User(null, "Name 1", "Email1@email.com");
		User user1Created = userService.create(user1);

		User user2 = new User(null, "Name 2", "Email2@email.com");
		User user2Created = userService.create(user2);

		User userGot = userService.getUserById(user2Created.getId());

		assertEquals(userGot, user2Created, "Найденный пользователь неверен");
	}

	@Test
	public void testExceptionCheckUserExist() {
		User user1 = new User(null, "Name 1", "Email1@email.com");
		User user1Created = userService.create(user1);

		assertThrows(DataNotFoundException.class, () -> userService.checkUser(user1Created.getId()+1));
	}

	@Test
	public void testCreateItem() {
		User user = new User(null, "Name ", "Email@email.com");
		User userCreated = userService.create(user);

		Item item = new Item(null, "Name", "Description", true, userCreated.getId());
		Item itemCreated = itemService.create(item);

		assertEquals(1, itemCreated.getId(), "Id созданной вещи не равно 1");
		assertEquals("Name", itemCreated.getName(), "Имя вещи неверно");
		assertEquals("Description", itemCreated.getDescription(), "Описание вещи неверно");
		assertEquals(true, itemCreated.getAvailable(), "Доступность вещи неверна");
		assertEquals(userCreated.getId(), itemCreated.getUserId(), "Ид владельца вещи неверно");
	}

	@Test
	public void testUpdateItem() {
		User user = new User(null, "Name ", "Email@email.com");
		User userCreated = userService.create(user);

		Item item = new Item(null, "Name", "Description", true, userCreated.getId());
		Item itemCreated = itemService.create(item);

		Item itemNew = new Item(null, "Name new", "Description new", null, null);
		Item itemUpdated = itemService.update(itemCreated.getId(), itemNew);

		assertEquals(itemUpdated.getId(), itemCreated.getId(), "Id обновленной вещи неверно");
		assertEquals("Name new", itemUpdated.getName(), "Имя вещи неверно");
		assertEquals("Description new", itemUpdated.getDescription(), "Описание вещи неверно");
		assertEquals(true, itemUpdated.getAvailable(), "Доступность вещи неверна");
		assertEquals(userCreated.getId(), itemUpdated.getUserId(), "Ид владельца вещи неверно");
	}

	@Test
	public void testGetItemById() {
		User user = new User(null, "Name 1", "Email1@email.com");
		User userCreated = userService.create(user);

		Item item = new Item(null, "Name", "Description", true, userCreated.getId());
		Item itemCreated = itemService.create(item);

		Item itemGot = itemService.getItemById(itemCreated.getId());

		assertEquals(itemGot, itemCreated, "Найденная вещь неверена");
	}

	@Test
	public void testGetItemsByUserId() {
		User user = new User(null, "Name 1", "Email1@email.com");
		User userCreated = userService.create(user);

		Item item1 = new Item(null, "Name", "Description", true, userCreated.getId());
		Item item1reated = itemService.create(item1);

		Item item2 = new Item(null, "Name 2", "Description 2", true, userCreated.getId());
		Item item2Created = itemService.create(item2);

		User user2 = new User(null, "Name 2", "Email2@email.com");
		User user2Created = userService.create(user2);

		Item item3 = new Item(null, "Name 3", "Description 3", true, user2Created.getId());
		Item item3Created = itemService.create(item3);

		List<Item> itemsCreatedUser1 = itemService.getItemsByUserId(userCreated.getId());

		assertEquals(2, itemsCreatedUser1.size(), "Количество вещей пользователя неверно");
		assertEquals(1, itemsCreatedUser1.get(0).getId(), "Id первой вещи неврно");
		assertEquals(2, itemsCreatedUser1.get(1).getId(), "Id второй вещи неврно");
	}

	@Test
	public void testGetSearchItems() {
		User user = new User(null, "Name 1", "Email1@email.com");
		User userCreated = userService.create(user);

		Item item1 = new Item(null, "Name", "Description item", true, userCreated.getId());
		Item item1reated = itemService.create(item1);

		Item item2 = new Item(null, "Name item", "Description 2", true, userCreated.getId());
		Item item2Created = itemService.create(item2);

		List<Item> itemsSearched = itemService.getSearchItems("item");

		assertEquals(2, itemsSearched.size(), "Количество найденных вещей неверно");
	}

	@Test
	public void testGetNotAvailableItems() {
		User user = new User(null, "Name 1", "Email1@email.com");
		User userCreated = userService.create(user);

		Item item1 = new Item(null, "Name", "Description item", false, userCreated.getId());
		Item item1reated = itemService.create(item1);

		List<Item> itemsSearched = itemService.getSearchItems("item");

		assertEquals(0, itemsSearched.size(), "Количество найденных доступных вещей неверно");
	}



}
