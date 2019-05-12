import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FormTest {
    private static WebDriver driver;
    private static final StringBuffer verificationErrors = new StringBuffer();
    private WebDriverWait waitDriver;
    private WebElement button, birthDate, parentsCheckbox, doctorCheckbox;

    @BeforeClass
    public static void addChromeDriverUrlProperty() {
        System.setProperty("webdriver.chrome.driver","chromedriver.exe");
    }

    @Before
    public void init() throws Exception {
        driver = new ChromeDriver();
        waitDriver = new WebDriverWait(driver, 10);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get("https://lamp.ii.us.edu.pl/~mtdyd/zawody/");
        driver.findElement(By.id("inputEmail3")).sendKeys("Imie");
        driver.findElement(By.id("inputPassword3")).sendKeys("Nazwisko");
        birthDate = driver.findElement(By.id("dataU"));
        button = driver.findElement(By.className("btn-default"));
        parentsCheckbox = driver.findElement(By.id("rodzice"));
        doctorCheckbox = driver.findElement(By.id("lekarz"));
    }

    @Test
    public void testUnder10() throws Exception {
        String[] results = new String[4];
        Arrays.fill(results, "Brak kwalifikacji");
        fillFormFromLoop(2009, 2019, results);
    }

    @Test
    public void testSkrzat() throws Exception {
        String[] results = new String[] {"Blad danych", "Blad danych", "Skrzat", "Blad danych"};
        fillFormFromLoop(2007, 2008, results);
    }

    @Test
    public void testYoungster() throws Exception {
        String[] results = new String[] {"Blad danych", "Blad danych", "Mlodzik", "Blad danych"};
        fillFormFromLoop(2005, 2006, results);
    }

    @Test
    public void testJunior() throws Exception {
        String[] results = new String[] {"Blad danych", "Blad danych", "Junior", "Blad danych"};
        fillFormFromLoop(2001, 2004, results);
    }

    @Test
    public void testAdult() throws Exception {
        int[] years = new int[] {1954, 2000};
        String[] results = new String[4];
        Arrays.fill(results, "Dorosly");
        fillFormFromArray(years, results);
    }

    @Test
    public void testSenior() throws Exception {
        int[] years = new int[] {1953};
        String[] results = new String[] {"Blad danych", "Blad danych", "Senior", "Senior"};
        fillFormFromArray(years, results);
    }

    @Test
    public void checkDate() throws Exception {
        birthDate.sendKeys("bledna data");
        if (!parentsCheckbox.isSelected()) {
            parentsCheckbox.click();
        }
        if (!doctorCheckbox.isSelected()) {
            doctorCheckbox.click();
        }
        button.click();
        closeAlert();
        verifyCategory("Blad danych");
        birthDate.clear();
        birthDate.sendKeys("1995-01-01");
        button.click();
        closeAlert();
        verifyCategory("Blad danych");
    }

    private void fillFormFromLoop(int start, int end, String[] categories) {
        for(int year = start; year<=end; year++){
            fillForm(year, categories);
        }
    }
    // Dla doroslych sprawdzam tylko date koncowa i poaczatkowa dlatego tablica
    private void fillFormFromArray(int[] years, String[] categories){
        for(int index = 0; index < years.length; index++) {
            fillForm(years[index], categories);
        }
    }

    // Wypelnianie formularza
    private void fillForm(int year, String[] categories) {
        birthDate.clear();
        birthDate.sendKeys("01-07-" + year);
        button.click();
        closeAlert();
        verifyCategory(categories[0]);
        if(!parentsCheckbox.isSelected()){
            parentsCheckbox.click();
        }
        button.click();
        closeAlert();
        verifyCategory(categories[1]);
        if(!doctorCheckbox.isSelected()){
            doctorCheckbox.click();
        }
        button.click();
        closeAlert();
        verifyCategory(categories[2]);
        if(parentsCheckbox.isSelected()){
            parentsCheckbox.click();
        }
        button.click();
        closeAlert();
        verifyCategory(categories[3]);
        if(doctorCheckbox.isSelected()){
            doctorCheckbox.click();
        }
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    public void closeAlert() {
        Alert alert = waitDriver.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        alert.dismiss();
    }

    public void verifyCategory(String category) {
        try {
            assertEquals("Imie Nazwisko zostal zakwalifikowany do kategorii " + category, driver.findElement(By.id("returnSt")).getText());
        } catch (Error e) {
            verificationErrors.append(e.toString());
        }
    }
}
