package com.herguan.cs562.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.google.appengine.api.datastore.KeyFactory;
import com.herguan.cs562.dao.TimesheetDAO;
import com.herguan.cs562.model.Timesheet;
import com.herguan.cs562.model.TimesheetAllocationVO;
import com.herguan.cs562.model.TimesheetScreenVO;
import com.herguan.cs562.model.TimesheetStatusEnum;
import com.herguan.cs562.model.User;
import com.herguan.cs562.model.ViewTimesheetScreenVO;
import com.opensymphony.xwork2.ActionSupport;

public class TimesheetAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = -2613425890762568273L;

	private TimesheetScreenVO timesheetScreenVO;
	private ViewTimesheetScreenVO viewTimesheetScreenVO;
	private TimesheetAllocationVO timesheetAllocationVO;

	public TimesheetAllocationVO getTimesheetAllocationVO() {
		return timesheetAllocationVO;
	}

	public void setTimesheetAllocationVO(
			TimesheetAllocationVO timesheetAllocationVO) {
		this.timesheetAllocationVO = timesheetAllocationVO;
	}

	public ViewTimesheetScreenVO getViewTimesheetScreenVO() {
		return viewTimesheetScreenVO;
	}

	public void setViewTimesheetScreenVO(
			ViewTimesheetScreenVO viewTimesheetScreenVO) {
		this.viewTimesheetScreenVO = viewTimesheetScreenVO;
	}

	private User user;

	private Map session;
	private String chosenWeek;
	private List<String> weeks;
	private List<Timesheet> timesheetList;
	private Timesheet newTimesheet;
	private String sundayHours;
	private String mondayHours;
	private String tuesdayHours;
	private String wednesdayHours;
	private String thursdayHours;
	private String fridayHours;
	private String saturdayHours;

	public String getMondayHours() {
		return mondayHours;
	}

	public void setMondayHours(String mondayHours) {
		this.mondayHours = mondayHours;
	}

	public String getTuesdayHours() {
		return tuesdayHours;
	}

	public void setTuesdayHours(String tuesdayHours) {
		this.tuesdayHours = tuesdayHours;
	}

	public String getWednesdayHours() {
		return wednesdayHours;
	}

	public void setWednesdayHours(String wednesdayHours) {
		this.wednesdayHours = wednesdayHours;
	}

	public String getThursdayHours() {
		return thursdayHours;
	}

	public void setThursdayHours(String thursdayHours) {
		this.thursdayHours = thursdayHours.trim();
	}

	public String getFridayHours() {
		return fridayHours;
	}

	public void setFridayHours(String fridayHours) {
		this.fridayHours = fridayHours;
	}

	public String getSaturdayHours() {
		return saturdayHours;
	}

	public void setSaturdayHours(String saturdayHours) {
		this.saturdayHours = saturdayHours.trim();
	}

	public String getSundayHours() {
		return sundayHours;
	}

	public void setSundayHours(String sundayHours) {
		this.sundayHours = sundayHours;
	}

	public Timesheet getNewTimesheet() {
		return newTimesheet;
	}

	public void setNewTimesheet(Timesheet newTimesheet) {
		this.newTimesheet = newTimesheet;
	}

	public List<Timesheet> getTimesheetList() {
		return timesheetList;
	}

	public void setTimesheetList(List<Timesheet> timesheetList) {
		this.timesheetList = timesheetList;
	}

	public String getChosenWeek() {
		return chosenWeek;
	}

	public void setChosenWeek(String chosenWeek) {
		System.out.println("chosen week setting " + chosenWeek);
		this.chosenWeek = chosenWeek;
	}

	public List<String> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<String> weeks) {
		this.weeks = weeks;
	}

	public Map getSession() {
		return session;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public TimesheetScreenVO getTimesheetScreenVO() {
		return timesheetScreenVO;
	}

	public void setTimesheetScreenVO(TimesheetScreenVO timesheetScreenVO) {
		this.timesheetScreenVO = timesheetScreenVO;
	}

	public String view() {
		System.out.println("user in session : " + session.get("User"));
		user = (User) session.get("User");
		viewTimesheetScreenVO = TimesheetDAO.getViewTimesheetScreenVO(user);
		if (viewTimesheetScreenVO != null) {
			session.put("viewTsVO", viewTimesheetScreenVO);
			// weeks = timesheetScreenVO.getWeeks();
			System.out.println(" Vo " + viewTimesheetScreenVO);
			System.out.println(" Vo " + user.getRole());
			formatWeeks(viewTimesheetScreenVO.getWeeks());
			System.out.println("weeks " + weeks);
			return "view";
		}
		addActionMessage("No allocations");
		return INPUT;

	}

	public String submit() {
		try {
			System.out.println("in submit : " + chosenWeek + " : "
					+ timesheetAllocationVO.getSundayHours());
			System.out.println("in submit : "
					+ timesheetAllocationVO.getTuesdayHours() + " : "
					+ timesheetAllocationVO.getMondayHours());
			System.out.println("in submit : "
					+ timesheetAllocationVO.getWednesdayHours() + " : "
					+ timesheetAllocationVO.getThursdayHours());
			System.out.println("in submit : "
					+ timesheetAllocationVO.getFridayHours() + " :"
					+ timesheetAllocationVO.getSaturdayHours() + ":");

			timesheetScreenVO = (TimesheetScreenVO) session.get("tsVO");
			String[] weekArray = chosenWeek.split(",");

			String week = weekArray[0];
			String[] firstDayofWeek = week.split("-");
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

			Date date = formatter.parse(firstDayofWeek[0]);
			calendar.setTime(date);

			System.out.println("calendar  : "
					+ calendar.get(Calendar.WEEK_OF_YEAR) + " - "
					+ calendar.get(Calendar.YEAR));

			System.out.println("chosen  : " + chosenWeek);
			int totalWeekHours = 0;
			Timesheet ts = new Timesheet();
			Integer temp = null;
			if (timesheetAllocationVO.getMondayHours() != null
					&& !timesheetAllocationVO.getMondayHours().trim()
							.equals("")) {

				temp = new Integer(timesheetAllocationVO.getMondayHours());
				ts.setMondayHours(temp);
				totalWeekHours = totalWeekHours + temp.intValue();
			}
			if (timesheetAllocationVO.getSundayHours() != null
					&& !timesheetAllocationVO.getSundayHours().trim()
							.equals("")) {
				temp = new Integer(timesheetAllocationVO.getSundayHours());
				ts.setSundayHours(temp);
				totalWeekHours = totalWeekHours + temp.intValue();
			}
			if (timesheetAllocationVO.getSaturdayHours() != null
					&& !timesheetAllocationVO.getSaturdayHours().trim().equals(
							"")) {
				temp = new Integer(timesheetAllocationVO.getSaturdayHours());
				ts.setSaturdayHours(temp);
				totalWeekHours = totalWeekHours + temp.intValue();
			}
			if (timesheetAllocationVO.getTuesdayHours() != null
					&& !timesheetAllocationVO.getTuesdayHours().trim().equals(
							"")) {
				temp = new Integer(timesheetAllocationVO.getTuesdayHours());
				ts.setTuesdayHours(temp);
				totalWeekHours = totalWeekHours + temp.intValue();
			}
			if (timesheetAllocationVO.getThursdayHours() != null
					&& !timesheetAllocationVO.getThursdayHours().trim().equals(
							"")) {
				temp = new Integer(timesheetAllocationVO.getThursdayHours());
				ts.setThursdayHours(temp);
				totalWeekHours = totalWeekHours + temp.intValue();
			}
			if (timesheetAllocationVO.getFridayHours() != null
					&& !timesheetAllocationVO.getFridayHours().trim()
							.equals("")) {
				temp = new Integer(timesheetAllocationVO.getFridayHours());
				ts.setFridayHours(temp);
				totalWeekHours = totalWeekHours + temp.intValue();
			}
			if (timesheetAllocationVO.getWednesdayHours() != null
					&& !timesheetAllocationVO.getWednesdayHours().trim()
							.equals("")) {
				temp = new Integer(timesheetAllocationVO.getWednesdayHours());
				ts.setWednesdayHours(temp);
				totalWeekHours = totalWeekHours + temp.intValue();
			}

			ts.setTimesheet_status(TimesheetStatusEnum.SUBMITTED);
			ts.setTotalWeekHours(totalWeekHours);
			ts.setWeekId(calendar.get(Calendar.YEAR) + "-"
					+ calendar.get(Calendar.WEEK_OF_YEAR));

			User user = (User) session.get("User");
			ts.setKey(KeyFactory.createKey(Timesheet.class.getSimpleName(), ts
					.getWeekId()
					+ timesheetScreenVO.getProject().getProjectCode()
					+ user.getLogin()));

			TimesheetDAO.createTimesheet(ts);
			TimesheetDAO
					.updateAllocation(timesheetScreenVO.getAllocation(), ts);
			session.remove("tsVO");
			addActionMessage("Timesheet created");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return "submit";
	}

	public String handleAjax() {
		try {
			System.out.println(" chosen week in AJAX" + chosenWeek);
			System.out.println(" chosen week in AJAX" + timesheetScreenVO);
			// Do something in respose to the ajax call
			timesheetScreenVO = (TimesheetScreenVO) session.get("tsVO");

			String[] weekArray = chosenWeek.split(",");

			String week = weekArray[0];
			String[] firstDayofWeek = week.split("-");
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

			Date date = formatter.parse(firstDayofWeek[0]);
			calendar.setTime(date);

			System.out.println("calendar  : "
					+ calendar.get(Calendar.WEEK_OF_YEAR) + " - "
					+ calendar.get(Calendar.YEAR));

			List tsList = timesheetScreenVO.getTimesheets();
			System.out.println("if list " + tsList);
			if (tsList == null || tsList.size() == 0) {
				timesheetAllocationVO = new TimesheetAllocationVO();
				timesheetAllocationVO
						.setProjectDisplay(timesheetScreenVO.getProject()
								.getProjectCode()
								+ "-"
								+ timesheetScreenVO.getProject()
										.getProjectName());

			} else {

				timesheetAllocationVO = timesheetScreenVO.getWeekTimesheetMap()
						.get(
								calendar.get(Calendar.YEAR) + "-"
										+ calendar.get(Calendar.WEEK_OF_YEAR));

				if (timesheetAllocationVO == null) {
					timesheetAllocationVO = new TimesheetAllocationVO();
					timesheetAllocationVO.setProjectDisplay(timesheetScreenVO
							.getProject().getProjectCode()
							+ "-"
							+ timesheetScreenVO.getProject().getProjectName());
				}
				System.out.println("ts al vo " + timesheetAllocationVO + " :"
						+ chosenWeek);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return SUCCESS;
	}

	public String handleViewAjax() {
		try {
			System.out.println(" handleViewAjax chosen week in AJAX"
					+ chosenWeek);
			System.out.println(" chosen week in AJAX" + timesheetScreenVO);
			viewTimesheetScreenVO = (ViewTimesheetScreenVO) session
					.get("viewTsVO");
			String[] weekArray = chosenWeek.split(",");

			String week = weekArray[0];
			String[] firstDayofWeek = week.split("-");
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

			Date date = formatter.parse(firstDayofWeek[0]);
			calendar.setTime(date);

			System.out.println("calendar  : "
					+ calendar.get(Calendar.WEEK_OF_YEAR) + " - "
					+ calendar.get(Calendar.YEAR));
			timesheetAllocationVO = viewTimesheetScreenVO
					.getWeekTimesheetAllocationMap().get(
							calendar.get(Calendar.YEAR) + "-"
									+ calendar.get(Calendar.WEEK_OF_YEAR));
			System.out.println("list " + timesheetAllocationVO);
		} catch (Exception ex) {

		}
		return SUCCESS;
	}

	public String showSubmit() {
		System.out.println("user in session : " + session.get("User"));
		user = (User) session.get("User");

		timesheetScreenVO = TimesheetDAO.getTimesheetScreenVO(user);
		if (timesheetScreenVO != null) {
			session.put("tsVO", timesheetScreenVO);
			// weeks = timesheetScreenVO.getWeeks();
			System.out.println(" Vo " + timesheetScreenVO);
			System.out.println(" Vo " + user.getRole());

			formatWeeks(timesheetScreenVO.getWeeks());

			System.out.println("weeks " + weeks);

			return "showSubmit";

		}
		addActionMessage("No allocations");
		return INPUT;
	}

	public void formatWeeks(List<String> weeksParam) {
		Calendar calendar = Calendar.getInstance();

		String week = null;
		String[] weekYear = null;
		String weekCombo1 = null;
		String weekCombo2 = null;
		Date date = null;
		if (weeksParam.size() > 0) {
			weeks = new ArrayList<String>();
		}
		for (Iterator<String> it = weeksParam.iterator(); it.hasNext();) {
			week = it.next();
			calendar.clear();
			System.out.println("week :" + week + " : ");
			weekYear = week.split("-");
			calendar.set(Calendar.WEEK_OF_YEAR, new Integer(weekYear[1])
					.intValue());
			calendar.set(Calendar.YEAR, new Integer(weekYear[0]).intValue());
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			// Now get the first day of week.
			date = calendar.getTime();
			weekCombo1 = formatter.format(date);
			System.out.println("date :" + date + " : " + weekCombo1);
			calendar.add(Calendar.DATE, 6);
			date = calendar.getTime();
			weekCombo2 = formatter.format(date);
			System.out.println("date :" + calendar.getTime() + " :"
					+ weekCombo2);
			weeks.add(weekCombo1 + "-" + weekCombo2);

		}
		if (weeksParam.size() > 0) {
			chosenWeek = weeks.get(0);
		}
		System.out.println("chosen  : " + chosenWeek);

	}

	public String approve() {
		return "approve";
	}

	@Override
	public void setSession(Map session) {
		this.session = session;

	}
}