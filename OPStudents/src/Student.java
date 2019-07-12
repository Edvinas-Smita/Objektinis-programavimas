import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Student
{
	private List<Integer> absentDates = new ArrayList<>();
	private String name;
	private String surname;
	private Boolean isFilter = false;
	
	public Student(List<Integer> absentDates, String name, String sureName)
	{
		this.absentDates = absentDates;
		this.name = name;
		this.surname = sureName;
	}
	
	public Student(List<Integer> absentDates, String name)
	{
		this.absentDates = absentDates;
		this.name = name;
	}
	
	public Student(String name, String surname)
	{
		this.name = name;
		this.surname = surname;
	}
	
	public Student(String name)
	{
		this.name = name;
	}
	
	
	public List<Integer> getAbsentDates()
	{
		return absentDates;
	}
	
	public void setAbsentDates(List<Integer> absentDates)
	{
		this.absentDates = absentDates;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getSurname()
	{
		return surname;
	}
	
	public void setSurname(String surname)
	{
		this.surname = surname;
	}
	
	public Boolean isFilter()
	{
		return isFilter;
	}
	
	public void setFilter(Boolean filter)
	{
		isFilter = filter;
	}
	
	public boolean getAbsent(int absentDay)
	{
		for (Integer day : absentDates)
		{
			if (day.equals(absentDay))
			{
				return true;
			}
		}
		return false;
	}
	
	public void setAbsent(int absentDay, boolean absent)
	{
		if (absent)
		{
			if (!absentDates.contains(absentDay))
			{
				absentDates.add(absentDay);
			}
		} else
		{
			if (absentDates.contains(absentDay))
			{
				absentDates = absentDates.stream()
										 .filter(value -> value != absentDay)
										 .collect(Collectors.toList());
			}
		}
		absentDates.sort(Integer::compareTo);
		//System.out.println(absentDates);
	}
	
	
	public static TreeItem<Student> makeStudentItem(List<Integer> absentDates, String name, String sureName)
	{
		return new TreeItem<>(new Student(absentDates, name, sureName));
	}
	
	public static TreeItem<Student> makeStudentItem(List<Integer> absentDates, String name)
	{
		return new TreeItem<>(new Student(absentDates, name));
	}
	
	public static TreeItem<Student> makeStudentItem(String name, String surname)
	{
		return new TreeItem<>(new Student(name, surname));
	}
	
	public static TreeItem<Student> makeStudentItem(String name)
	{
		return new TreeItem<>(new Student(name));
	}
	
	public static TreeItem<Student> makeFilterItem(String name)
	{
		return new TreeItem<>(new Student(name)
		{{
			setFilter(true);
		}})
		{{
			setExpanded(true);
		}};
	}
}