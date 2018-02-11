package textanalysis;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Parser {
	
	String text;
	
	ArrayList<String> keyword;
	ArrayList<String> monthKeyword;
	ArrayList<String> weekKeyword;
	LocalDate today;
	LocalDate queryDate;
	LocalTime queryTime;
	String locationText;
	String dateText;
	String timeText;
	String parts[];
	String queryType;
	int flag;
	String errorMessage;
	
	private void init(String text){
		this.text = text;
		flag = 0;
		today = LocalDate.now();
		parts = text.split(",\\s|\\s|,|\\?|\\'");
		
		keyword = new ArrayList<String>();
		keyword.add("in");
		keyword.add("on");
		keyword.add("at");
		keyword.add("today");
		keyword.add("yesterday");
		keyword.add("tomorrow");
		keyword.add("now");
		
		monthKeyword = new ArrayList<String>();
		monthKeyword.add("January");
		monthKeyword.add("February");
		monthKeyword.add("March");
		monthKeyword.add("April");
		monthKeyword.add("May");
		monthKeyword.add("June");
		monthKeyword.add("July");
		monthKeyword.add("August");
		monthKeyword.add("September");
		monthKeyword.add("October");
		monthKeyword.add("November");
		monthKeyword.add("December");
		
		weekKeyword = new ArrayList<String>();
		weekKeyword.add("Monday");
		weekKeyword.add("Tuesday");
		weekKeyword.add("Wednesday");
		weekKeyword.add("Thursday");
		weekKeyword.add("Friday");
		weekKeyword.add("Saturday");
		weekKeyword.add("Sunday");
	}
	
	public Parser(String text){
		init(text);
		int i=0;
		
		while(i<parts.length){
			switch(parts[i]){
			case "weather":
				queryType="weather";
				i++;
				break;
			case "temperature":
				queryType="temperature";
				i++;
				break;
			case "wind":
				//
				queryType="wind";
				i++;
				break;
			case "in":
				i=locParser(i);
				break;
			case "on":
				i=dateParser(i);
				break;
			case "at":
				i=timeParser(i);
				break;
			default:
				i++;
			}
		}
		
	}
	
	private int locParser(int i){
		locationText="";
		i++;
		while(!keyword.contains(parts[i])){
			if(locationText.length()>0)
				locationText+=" ";
			locationText+=parts[i];
			i++;
			if(i>=parts.length){
				break;
			}
		}
		if(locationText==""){
			flag=1;
			errorMessage="Location error";
			i=parts.length;
		}
		return i;
	}
	
	public static int getDayOfWeek(String day)
	 {
	  if(day.equalsIgnoreCase("Monday"))
	   return 1;
	  if(day.equalsIgnoreCase("Tuesday"))
	   return 2;
	  if(day.equalsIgnoreCase("Wednesday"))
	   return 3;
	  if(day.equalsIgnoreCase("Thrusday"))
	   return 4;
	  if(day.equalsIgnoreCase("Friday"))
	   return 5;
	  if(day.equalsIgnoreCase("Saturday"))
	   return 6;
	  if(day.equalsIgnoreCase("Sunday"))
	   return 7;
	  return 0; 
	 }

	private int dateParser(int i){
		DateTimeFormatter formatter;
		dateText="";
		i++;
		int j=i;
		while(!keyword.contains(parts[j])){
			if(dateText.length()>0)
				dateText+=" ";
			dateText+=parts[j];
			j++;
			if(j>=parts.length){
				break;
			}
		}
		if(j==i){
			flag=1;
			errorMessage="Date error";
			j=parts.length;
		}
		else if(j-i==1){
			if(weekKeyword.contains(parts[i])){
				int days=(getDayOfWeek(parts[i]) - today.getDayOfWeek().getValue()+7)%7;
				queryDate = today.plusDays(days);
			}
			else{
				formatter=DateTimeFormatter.ofPattern("M-d-yyyy");
				String timestr = parts[i].replaceAll("st|nd|rd|th", "");
				timestr = today.getMonthValue()+"-"+timestr+"-"+today.getYear();
				queryDate = LocalDate.parse(timestr,formatter);
			}
		}
		else if(j-i==2){
			String timestr = parts[i+1].replaceAll("st|nd|rd|th", "");
			timestr = parts[i]+"-"+timestr+"-"+today.getYear();
			try{
				formatter=DateTimeFormatter.ofPattern("M-d-yyyy");
				queryDate = LocalDate.parse(timestr,formatter);
			}
			catch(Exception e){
				formatter=DateTimeFormatter.ofPattern("MMMM-d-yyyy");
				queryDate = LocalDate.parse(timestr,formatter);
			}
		}
		else if(j-i==3){
			String timestr = parts[i+1].replaceAll("st|nd|rd|th", "");
			timestr = parts[i]+"-"+timestr+"-"+parts[i+2];
			try{
				formatter=DateTimeFormatter.ofPattern("M-d-yyyy");
				queryDate = LocalDate.parse(timestr,formatter);
			}
			catch(Exception e){
				formatter=DateTimeFormatter.ofPattern("MMMM-d-yyyy");
				queryDate = LocalDate.parse(timestr,formatter);
			}
		}
		return j;
	}
	
	private int timeParser(int i){
		DateTimeFormatter formatter;
		String ampm="";
		boolean oclock=false;
		timeText="";
		i++;
		int j=i;
		while(!keyword.contains(parts[j])){
			if(timeText.length()>0)
				timeText+=" ";
			timeText+=parts[j];
			if(parts[j].toLowerCase().equals("am") || parts[j].toLowerCase().equals("pm")){
				ampm = parts[j];
			}
			if(parts[j]=="o"){
				oclock=true;
			}
			j++;
			if(j>=parts.length){
				break;
			}
		}
		if(j==i){
			flag=1;
			errorMessage="Time error";
			j=parts.length;
		}
		else if(ampm==""){
			if(oclock){
				formatter=DateTimeFormatter.ofPattern("H");
				queryTime = LocalTime.parse(parts[i],formatter);
			}
			else{
				try{
					formatter=DateTimeFormatter.ofPattern("H");
					queryTime = LocalTime.parse(parts[i],formatter);
				}
				catch(Exception e){
					formatter=DateTimeFormatter.ofPattern("H:mm");
					queryTime = LocalTime.parse(parts[i],formatter);
				}
			}
		}
		else{
			String timestr = parts[i]+" "+ampm.toUpperCase();
			try{
				formatter=DateTimeFormatter.ofPattern("h a");
				queryTime = LocalTime.parse(timestr,formatter);
			}
			catch(Exception e){
				formatter=DateTimeFormatter.ofPattern("h:mm a");
				queryTime = LocalTime.parse(timestr,formatter);
			}
		}
		return j;
	}
	
	public String getLocation(){
		return locationText;
	}
	
	public LocalDate getdate(){
		return queryDate;
	}
	
	public LocalTime gettime(){
		return queryTime;
	}
}
