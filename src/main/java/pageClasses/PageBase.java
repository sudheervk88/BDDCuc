package pageClasses;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class PageBase {

    private WebDriver driver;

    @FindBy(xpath = "//*[@id=\"header\"]/ul[1]/li[11]/a")
    public WebElement logOut;

    public PageBase(WebDriver driver){

        this.driver = driver;
        PageFactory.initElements(driver,this);
    }

    protected WebDriverWait getWait(){
        WebDriverWait wait = new WebDriverWait(driver,60);
        wait.pollingEvery(250, TimeUnit.MILLISECONDS);
        wait.ignoring(NoSuchElementException.class);
        return  wait;
    }

    protected PageBase logoutFromApplication() {
        logOut.click();
        HomePageClass homePage = new HomePageClass(driver);
        WebDriverWait wait = getWait();
        wait.until(ExpectedConditions.elementToBeClickable(homePage.fileABug));
        return homePage;

    }


}
