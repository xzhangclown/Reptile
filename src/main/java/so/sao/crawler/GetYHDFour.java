package so.sao.crawler;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author xzhang
 * @create 2017-09-30 10:24
 **/
public class GetYHDFour implements PageProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //private static Site site = Site.me().setTimeOut(200000).setCycleRetryTimes(5).setDomain("item.yhd.com").setCharset("utf-8");
    private static Site site = Site.me().setTimeOut(200000).setCycleRetryTimes(5).setDomain("item.yhd.com").setCharset("utf-8")
            .addCookie("_jda","81617359.1507712107639446960832.1507712108.1508811989.1508825592.26")
            .addCookie("__jdb","81617359.1.1507712107639446960832|26.1508825592")
            .addCookie("__jdc","81617359")
            .addCookie("__jdv","259140492|baidu-pinzhuan|t_288551095_baidupinzhuan|cpc|yhdbaidupcpz001_0_87d73f10d41147b4826bd5efe7a7ffde|1507712107640")
            .addCookie("cart_cookie_uuid","1ce9bb5d-752e-48f1-8772-a9d0919d0b93")
            .addCookie("cityId","2817")
            .addCookie("ipLoc-djd","2-2817-51973-0")
            .addCookie("provinceId","2")
            .addCookie("unpl","V2_ZzNtbUtRFhN3X0ZVLhxcBWIFQVhLVERGIAwSAy8fDAMwVEcJclRCFXMURlVnGVwUZwQZWUZcQhJFCHZXchBYAWcCGllyBBNNIEwHDCRSBUE3XHwVGwIQRS1dAhUpWBcEZgMibUFXcxV0OEZceRBfB28FG21yUHMlRQpOVnoYXzVmMxNtAAMfHXMPR1R%2fVFwNZQoRX0pRSiV0OEU%3d")
            .addCookie("yhd_location","2_2817_51973_0");

    public void process(Page page) {
        //System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\ChromeDriver.exe");
        ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\ChromeDriver.exe")).usingAnyFreePort().build();
        WebDriver driver = null;
        try{
            service.start();
            //driver = new ChromeDriver();
            driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
            driver.get(page.getUrl().get());
            logger.info("调用chrome驱动获取链接");
            WebElement webElement = driver.findElement(By.xpath("//div[@class='crumb clearfix']"));
            Html html = new Html(webElement.getAttribute("outerHTML"));
            logger.info("解析详情页成功");
            Map<String,String> m = new HashMap<String,String>();
            m.put("one",html.xpath("//div/a[1]/em/text()").toString());
            m.put("two",html.xpath("//div/a[2]/em/text()").toString());
            m.put("three",html.xpath("//div/a[3]/em/text()").toString());
            m.put("four",html.xpath("//div/a[4]/em/text()").toString());
            m.put("five",html.xpath("//div/span/em/text()").toString());
            DB.addData(m);
            logger.info("插入数据库成功");
            String folder=System.getProperty("java.io.tmpdir");
            boolean success = Test.delAllFile(folder);
            if (success) {
                System.out.println("Successfully deleted populated directory: " + folder);
            } else {
                System.out.println("Failed to delete populated directory: " + folder);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }finally {
            try{
                driver.quit();
                service.stop();
            }catch (Exception ee){
                logger.error(ee.getMessage());
            }
        }

        while(UrlQueue.getThreeSize()==0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
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
