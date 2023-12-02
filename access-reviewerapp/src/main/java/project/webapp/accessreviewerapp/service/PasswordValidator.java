package project.webapp.accessreviewerapp.service;

import java.util.ArrayList;
import java.util.List;

public class PasswordValidator {

	public static List<String> validatePassword(final String password) {
	    List<String> unmetCriteria = new ArrayList<>();

	    if (!password.matches("^(?=.*[a-z]).*$")) {
	        unmetCriteria.add("at least one lowercase letter.");
	    }
	    if (!password.matches("^(?=.*[A-Z]).*$")) {
	        unmetCriteria.add("at least one uppercase letter.");
	    }
	    if (!password.matches("^(?=.*[0-9]).*$")) {
	        unmetCriteria.add("at least one digit.");
	    }
	    if (!password.matches("^(?=.*[@#$%^&+=]).*$")) {
	        unmetCriteria.add("at least one special character (@#$%^&+=).");
	    }
	    if (!password.matches("^\\S+$")) {
	        unmetCriteria.add("Must not contain whitespaces.");
	    }
	    if (!password.matches("^.{8,}$")) {
	        unmetCriteria.add("Must be at least 8 characters long.");
	    }

	    return unmetCriteria;
	}
}