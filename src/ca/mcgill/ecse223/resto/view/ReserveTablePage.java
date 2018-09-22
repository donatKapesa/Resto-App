package ca.mcgill.ecse223.resto.view;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import ca.mcgill.ecse223.resto.application.RestoAppApplication;
import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.model.Table;

public class ReserveTablePage extends JFrame {

	private static final long serialVersionUID = -6671167773705738484L;

	private RestoApp resto = RestoAppApplication.getRestaurantInstance();

	private JButton reserveTableButton;
	private JLabel errorLabel, dateLabel, timeLabel, nameLabel, emailLabel, phoneLabel, partySizeLabel,
			availableTablesLabel, chooseTableLabel;
	private JTextField dateDField, dateMField, dateYField, timeHourField, timeMinuteField, nameField, emailField, phoneField, partySizeField,
			availableTablesField;

	private String errorMessage = "";

	public ReserveTablePage() {
		initComponents();
	}

	private void initComponents() {
		// elements
		reserveTableButton = new JButton();

		errorLabel = new JLabel();
		dateLabel = new JLabel();
		timeLabel = new JLabel();
		nameLabel = new JLabel();
		emailLabel = new JLabel();
		phoneLabel = new JLabel();
		partySizeLabel = new JLabel();
		availableTablesLabel = new JLabel();
		chooseTableLabel = new JLabel();

		dateDField = new JTextField();
		dateMField = new JTextField();
		dateYField = new JTextField();
		timeHourField = new JTextField();
		timeMinuteField = new JTextField();
		nameField = new JTextField();
		emailField = new JTextField();
		phoneField = new JTextField();
		partySizeField = new JTextField();
		availableTablesField = new JTextField();

		// global settings and listeners
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setTitle("Reserve Table");

		// Set text
		reserveTableButton.setText("Reserve table");
		dateLabel.setText("Choose a date (day, month, year)");
		timeLabel.setText("Enter a time (hours, minutes) in 24-hour format (e.g. 17 50 for 5:50 pm)");
		nameLabel.setText("Name");
		emailLabel.setText("Email");
		phoneLabel.setText("Phone");
		partySizeLabel.setText("How many people");
		chooseTableLabel.setText(
				"<html>Choose the table(s) for this reservation.<br>Enter them separated by a comma (no space !)</html>");

		String availableTables = "<html>Available Tables:<br>" + stringTables(resto) + "</html>";
		availableTablesLabel.setText(availableTables);

		clearFields();

		// layout
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup().addComponent(errorLabel).addComponent(dateLabel)
						.addComponent(nameLabel).addComponent(phoneLabel))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(dateDField)
								.addComponent(dateMField)
								.addComponent(dateYField))
						.addComponent(nameField)
						.addComponent(phoneField)
						.addComponent(availableTablesLabel))
				.addGroup(layout.createParallelGroup().addComponent(timeLabel).addComponent(emailLabel)
						.addComponent(partySizeLabel).addComponent(chooseTableLabel))
				.addGroup(layout.createParallelGroup().addComponent(timeHourField).addComponent(emailField)
						.addComponent(partySizeField).addComponent(availableTablesField))
				.addGroup(layout.createParallelGroup().addComponent(timeMinuteField).addComponent(reserveTableButton)));

		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(errorLabel)
				.addGroup(layout.createParallelGroup().addComponent(dateLabel).addComponent(dateDField).addComponent(dateMField).addComponent(dateYField)
						.addComponent(timeLabel).addComponent(timeHourField).addComponent(timeMinuteField))
				.addGroup(layout.createParallelGroup().addComponent(nameLabel).addComponent(nameField)
						.addComponent(emailLabel).addComponent(emailField))
				.addGroup(layout.createParallelGroup().addComponent(phoneLabel).addComponent(phoneField)
						.addComponent(partySizeLabel).addComponent(partySizeField))
				.addGroup(layout.createParallelGroup().addComponent(availableTablesLabel).addComponent(chooseTableLabel)
						.addComponent(availableTablesField).addComponent(reserveTableButton)));

		reserveTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				reserveTableButtonActionPerformed();
			}
		});
	}

	private void refreshData() {
		clearFields();
	}

	private void clearFields() {
		dateDField.setText("");
		dateMField.setText("");
		dateYField.setText("");
		timeHourField.setText("");
		timeMinuteField.setText("");
		nameField.setText("");
		emailField.setText("");
		phoneField.setText("");
		partySizeField.setText("");
		availableTablesField.setText("");
		errorLabel.setText(errorMessage);
	}

	private void reserveTableButtonActionPerformed() {
		// create and call the controller
		RestoAppController rac = RestoAppApplication.getControllerInstance();
		try {
			
			Calendar c = Calendar.getInstance();
			int currDay = c.get(Calendar.DAY_OF_MONTH), currMonth = c.get(Calendar.MONTH) + 1, currYear = c.get(Calendar.YEAR);
			int chosenDay = fieldToInt(dateDField), chosenMonth = fieldToInt(dateMField), chosenYear = fieldToInt(dateYField);
			String dateError = "";
			if (chosenYear < currYear) {
				dateError += "Chosen year is before current year !";
			} else {
				if (chosenMonth < currMonth) {
					dateError += "Chosen month is before current month !";
				} else {
					if (chosenDay < currDay) {
						dateError += "Chosen day is before current day !";
					}
				}
			}
			if (dateError.trim().length() > 0) {
				errorMessage = dateError;
				refreshData();
				return;
			}
			if (!checkDMY(chosenDay, chosenMonth, chosenYear)) {
				errorMessage = "Your day-month-year combination is invalid !";
				refreshData();
				return;
			}
			
			int chosenHour = fieldToInt(timeHourField), chosenMinute = fieldToInt(timeMinuteField);
			int partySize = fieldToInt(partySizeField);
			String name = nameField.getText();
			String email = emailField.getText();
			String phone = phoneField.getText();
			String chosenTables = availableTablesField.getText();
			List<Table> tables = tablesFromNumber(chosenTables);			
			
			rac.reserveTable(new Date(chosenYear, chosenMonth, chosenDay), new Time(chosenHour, chosenMinute, 0), partySize, name, email, phone, tables);
			
			errorMessage = "";
		} catch (InvalidInputException e) {
			errorMessage = e.getMessage();
			
		} 
		refreshData();
	}

	private List<Table> tablesFromNumber(String chosenTables) {
		String[] tableNumbers = chosenTables.split(",");
		List<Table> tables = new ArrayList<Table>();
		for (String num : tableNumbers) {
			for (Table t : resto.getCurrentTables()) {
				if (t.getNumber() == Integer.parseInt(num)) {
					tables.add(t);
				}
			}
		}
		System.out.println("tablesFromNumber---" + tables.size() + "---" + tables.get(0).getNumber() + "---");
		return tables;
	}

	private String stringTables(RestoApp r) {
		try {
			String tables = "";
			List<Integer> numbers = new ArrayList<Integer>();
			for (Table table : r.getCurrentTables()) {
				if (table.getStatus().toString().equals(Table.Status.Available.toString())) {
					numbers.add(table.getNumber());
				}
			}
			tables += ("" + numbers.get(0));
			for (int i = 1; i < numbers.size(); i++) {
				tables += ("," + numbers.get(i));
			}
			return tables;
		} catch (Exception e) {
			return "";
		}
	}
	
	private static int fieldToInt(JTextField field) {
		return Integer.parseInt(field.getText());
	}
	
	private static boolean checkDMY(int day, int month, int year) {
		boolean valid = false;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			valid = between(1, day, 31);
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			valid = between(1, day, 30);
			break;
		case 2:
			valid = (isLeap(year) ? between(1, day, 29) : between(1, day, 28));
			break;
		}
		return valid;
	}
	
	private static boolean isLeap(int year) {
		if (year % 4 == 0) {
			if (year % 100 == 0) {
				if (year % 400 == 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	private static boolean between(int min, int val, int max) {
		return val >= min && val <= max;
	}

}
