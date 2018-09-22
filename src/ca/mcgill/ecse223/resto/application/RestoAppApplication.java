package ca.mcgill.ecse223.resto.application;

import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.persistence.PersistenceObjectStream;
import ca.mcgill.ecse223.resto.view.*;

public class RestoAppApplication {

	private static RestoApp restaurant = null;
	private static RestoAppController rac = null;

	private static String fileName = "src/menu.resto";

	public static void main(String[] args) {

		restaurant = getRestoApp();
		rac = new RestoAppController();
		
		PersistenceObjectStream.setFilename(fileName);
		
		save(restaurant);
		restaurant = load();
	
		// start UI
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				MainPage main = new MainPage();
				main.loadPage("main");
			}
		});

	}

	public static RestoApp getRestoApp() {
		if (restaurant == null) {
			restaurant = load();
		}
		return restaurant;
	}

	// save restoapp
	public static void save(Object o) {
		PersistenceObjectStream.serialize(o);
	}

	// load restoapp from persistence
	public static RestoApp load() {
		PersistenceObjectStream.setFilename(fileName);
		restaurant = (RestoApp) PersistenceObjectStream.deserialize();
		if (restaurant == null) {
			restaurant = new RestoApp();
		} else {
			restaurant.reinitialize();
		}
		setRestaurantInstance(restaurant);
		return restaurant;
	}
	
	public static RestoApp getRestaurantInstance() {
		return restaurant;
	}
	
	public static RestoAppController getControllerInstance() {
		return rac;
	}
	
	public static void setRestaurantInstance(RestoApp r) {
		restaurant = r;
	}
}
