package tp1.server.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.User;
import tp1.api.service.rest.RestUsers;

@Singleton
public class UsersResource implements RestUsers {

	private final Map<String,User> users = new HashMap<>();

	private static final Logger Log = Logger.getLogger(UsersResource.class.getName());
	
	public UsersResource() {
	}
		
	@Override
	public String createUser(User user) {
		Log.info("createUser : " + user);
		
		// Check if user data is valid
		if(user.getUserId() == null || user.getPassword() == null || user.getFullName() == null || 
				user.getEmail() == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}
		
		// Check if userId already exists
		if( users.containsKey(user.getUserId())) {
			Log.info("User already exists.");
			throw new WebApplicationException( Status.CONFLICT );
		}

		//Add the user to the map of users
		users.put(user.getUserId(), user);
		return user.getUserId();
	}


	@Override
	public User getUser(String userId, String password) {
		Log.info("getUser : user = " + userId + "; pwd = " + password);
		
		User user = users.get(userId);
		
		// Check if user exists 
		if( user == null ) {
			Log.info("User does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}
		
		//Check if the password is correct
		if( !user.getPassword().equals( password)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}
		
		return user;
	}


	@Override
	public User updateUser(String userId, String password, User user) {
		Log.info("updateUser : user = " + userId + "; pwd = " + password + " ; user = " + user);
		if ( !users.containsKey(userId) ) {
			Log.info("User does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}

		User oldUser = users.get(userId);

		if( !oldUser.getPassword().equals(password)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		if( user == null )
			throw new WebApplicationException( Status.BAD_REQUEST );

		if ( badUserData(user) ) {
			if (user.getEmail() == null)
				user.setEmail(oldUser.getEmail());

			if (user.getFullName() == null)
				user.setFullName(oldUser.getFullName());

			if (user.getPassword() == null)
				user.setPassword(oldUser.getPassword());
		}

		user.setUserId(oldUser.getUserId());

		users.remove(userId);
		users.put(userId, user);
		return users.get(userId);
	}


	@Override
	public User deleteUser(String userId, String password) {
		Log.info("deleteUser : user = " + userId + "; pwd = " + password);
		if ( !users.containsKey(userId) ) {
			Log.info("User does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}
		if( !users.get(userId).getPassword().equals( password)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}
		return users.remove(userId);
	}


	@Override
	public List<User> searchUsers(String pattern) {
		if(pattern == null)
			throw new WebApplicationException(Status.BAD_REQUEST);

		Log.info("searchUsers : pattern = " + pattern);

		List<User> patternUsers = new ArrayList<>();

		for(User u : users.values()) {
			if( u.getFullName().toLowerCase().contains(pattern.toLowerCase()) ) {
				User userNoPw = new User(u.getUserId(), u.getFullName(), u.getEmail(), "");
				patternUsers.add(userNoPw);
			}
		}

		return patternUsers;
	}

	/**
	 * Checks if the user has correct data
	 * @param user - user to be checked
	 * @return true in case of a bad user; false otherwise
	 */
	private boolean badUserData(User user) {
		return user.getUserId() == null || user.getEmail() == null ||
				user.getFullName() == null || user.getPassword() == null;
	}

}
