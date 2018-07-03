package com.olympics;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.jsoup.internal.ConstrainableInputStream;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Ordering;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MadelTable {

	WebDriver driver;
	List<WebElement> countrylist;

	@BeforeClass
	public void setUp() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().fullscreen();
		driver.get("https://en.wikipedia.org/wiki/2016_Summer_Olympics");
	}

	@Test(priority = 1)
	public void SortedByRank() throws InterruptedException {

		List<Integer> obtain_ranklist = new ArrayList<Integer>();
		// rank column

		List<WebElement> rank_list = driver.findElements(
				By.xpath("//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']//tr/td[1]"));

		for (int i = 0; i < rank_list.size() - 1; i++) {
			obtain_ranklist.add(Integer.valueOf(rank_list.get(i).getText()));
		}

		List<Integer> expected = obtain_ranklist;

		// to sort for ascending order
		Collections.sort(expected);
		Assert.assertEquals(obtain_ranklist, expected, "rank column in ascending order");

	}

	@Test(priority = 2)
	public void SortedByCountryNames() {
		// click link noc
		driver.findElement(
				By.xpath("//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']/thead/tr[1]/th[2]"))
				.click();
		// column of country names
		countrylist = driver.findElements(By.xpath(
				"//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']" + "//tr/th[@scope='row']"));
		List<String> obtain_countryList = new ArrayList<String>();

		for (int i = 0; i < countrylist.size() - 1; i++) {
			obtain_countryList.add(countrylist.get(i).getText());
		}

		List<String> expectedcountry = obtain_countryList;
		Collections.sort(expectedcountry);
		// verify that the table is now sorted by the country names.
		Assert.assertEquals(obtain_countryList, expectedcountry);
		List<WebElement> rerank_list = driver.findElements(
				By.xpath("//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']//tr/td[1]"));
		List<Integer> re_ranks = new ArrayList<Integer>();
		for (int i = 0; i < rerank_list.size() - 1; i++) {
			re_ranks.add(Integer.valueOf(rerank_list.get(i).getText()));
		}

		boolean isSorted = Ordering.natural().isOrdered(re_ranks);
		Assert.assertFalse(isSorted, "rank column is not ascending order");

	}

	@Test(priority = 3)
	public void TheMost() {
		String xpath_gold = "//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']/tbody/tr/td[2]";
		String xpath_silver = "//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']/tbody/tr/td[3]";
		String xpath_bronz = "//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']/tbody/tr/td[4]";
		String xpath_total = "//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']/tbody/tr/td[5]";
		System.out.println("the name of the country with the most number of GOLD: " + mostNumberOfMedals(xpath_gold));
		System.out
				.println("the name of the country with the most number of SILVER: " + mostNumberOfMedals(xpath_silver));
		System.out.println("the name of the country with the most number of BRONZ: " + mostNumberOfMedals(xpath_bronz));
		System.out.println("the name of the country with the most number of TOTAL: " + mostNumberOfMedals(xpath_total));

	}

	@Test(priority = 4)
	public void CountryByMedal() {
		String dubleno = "18";
		String xpath_silver = "//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']/tbody/tr/td[3]";
		SilverMedal(xpath_silver, dubleno);
		System.out.println(SilverMedal(xpath_silver, dubleno));
		List<String> obtain_list = Arrays.asList(" France (FRA)=18", " China (CHN)=18");
		Assert.assertEquals(SilverMedal(xpath_silver, dubleno), obtain_list);
	}

	@Test(priority = 5)
	public void GetIndex() {
		indexofCountry("Japan");
		// after click noc, japan is in 7th row but it has still 6 rank..
		assertEquals(indexofCountry("Japan"), 7);

	}
	
	@Test(priority = 6)
	public void GetSum() {
		String xpath ="//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']/tbody/tr/td[4]";
		String xpath_country = "//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']//tr/th[@scope='row']";
		List<WebElement> country = driver.findElements(By.xpath(xpath_country));
		List<WebElement> medal = driver.findElements(By.xpath(xpath));
		Map map = new HashMap<String, String>();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < country.size() - 1; i++) {
			map.put(country.get(i).getText(), medal.get(i).getText());
		}
		Set<Entry<String, String>> entries = map.entrySet();
		for (Entry<String, String> o_entry : entries) {
			for (Entry<String, String> i_entry : entries) {
				if(!o_entry.getKey().equals(i_entry.getKey()) && !list.contains(o_entry.getKey())
						&& Integer.valueOf(o_entry.getValue()) + Integer.valueOf(i_entry.getValue())==18 ){
					list.add(o_entry.getKey());
					list.add(i_entry.getKey());
				}
			}
		}
		List<String> obtain_list = Arrays.asList(" Australia (AUS)"," Italy (ITA)");
		Assert.assertEquals(list, obtain_list);
		
	}
	
	
	
	
	

	public int indexofCountry(String countryName) {
		int row;
		List<WebElement> countrylist = driver.findElements(By.xpath(
				"//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']" + "//tr/th[@scope='row']"));
		int count = 1;
		for (WebElement each : countrylist) {
			if (each.getText().contains(countryName)) {
				return count;
			} else {
				count++;

			}
		}
		return count;
	}

	public List<String> SilverMedal(String xpath, String dubleNo) {

		String xpath_country = "//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']"
				+ "//tr/th[@scope='row']";
		List<WebElement> country = driver.findElements(By.xpath(xpath_country));
		List<WebElement> medal = driver.findElements(By.xpath(xpath));
		Map map = new HashMap<String, String>();
		List<String> dublist = new ArrayList<String>();
		for (int i = 0; i < country.size() - 1; i++) {
			map.put(country.get(i).getText(), medal.get(i).getText());
		}
		Set<Entry<String, String>> entries = map.entrySet();
		for (Entry<String, String> each : entries) {
			if (each.getValue().equals(dubleNo)) {
				dublist.add(each.toString());
			}
		}

		return dublist;

	}

	public String mostNumberOfMedals(String xpathofMedal) {

		String xpath_country = "//table[@class='wikitable sortable plainrowheaders jquery-tablesorter']"
				+ "//tr/th[@scope='row']";
		List<WebElement> medal = driver.findElements(By.xpath(xpathofMedal));
		List<WebElement> country = driver.findElements(By.xpath(xpath_country));

		List<Integer> medallist = new ArrayList<Integer>();

		Map map = new HashMap<String, String>();
		for (int i = 0; i < medal.size(); i++) {
			medallist.add(Integer.valueOf(medal.get(i).getText()));
			map.put(country.get(i).getText(), medal.get(i).getText());
		}

		String maxgold = (Collections.max(medallist)).toString();

		String winner = null;
		Set<Entry<String, String>> entries = map.entrySet();
		for (Entry<String, String> each : entries) {
			if (each.getValue().equals(maxgold)) {
				winner = each.toString();
			}
		}
		return winner;

	}

	@AfterClass
	public void tearsDown() {
		driver.close();
	}

}
