package so.sao.crawler;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import java.util.*;
/**
 * 
 * @author guangpu.yan
 * @create 2017-09-30 10:24
 **/
public class GetYHDFour implements PageProcessor {
    private static Site site = Site.me().setTimeOut(200000).setCycleRetryTimes(5).setDomain("item.yhd.com").setCharset("utf-8");
    public void process(Page page) {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\ChromeDriver.exe");
        WebDriver driver = null;
        try{
            driver = new ChromeDriver();
            driver.get(page.getUrl().get());
            WebElement webElement = driver.findElement(By.xpath("//div[@class='crumb clearfix']"));
            Html html = new Html(webElement.getAttribute("outerHTML"));
            Map<String,String> m = new HashMap<String,String>();
            m.put("one",html.xpath("//div/a[1]/em/text()").toString());
            m.put("two",html.xpath("//div/a[2]/em/text()").toString());
            m.put("three",html.xpath("//div/a[3]/em/text()").toString());
            m.put("four",html.xpath("//div/a[4]/em/text()").toString());
            m.put("five",html.xpath("//div/span/em/text()").toString());
            DB.addData(m);
            //System.out.println(urls);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                driver.quit();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }

        while(UrlQueue.getThreeSize()==0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            page.addTargetRequest(UrlQueue.getThree());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Site getSite() {
        return site;
    }

   /* public static void main(String[] args) {

    }*/
}
