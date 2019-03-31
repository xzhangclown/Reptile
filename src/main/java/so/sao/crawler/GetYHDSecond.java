package so.sao.crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author xzhang
 * @create 2017-09-30 10:24
 **/
public class GetYHDSecond implements PageProcessor {
    //Jedis jedis = new Jedis("10.100.50.55", 7001, 10000);
    //static List secondUrls = new ArrayList();
    //public static Tree result = new Tree();
    public static List result = new ArrayList();
    private static Site site = Site.me().setTimeOut(200000).setCycleRetryTimes(5).setDomain("search.yhd.com").setCharset("utf-8");
                /*.addCookie("3AB9D23F7A4B3C9B",	"EWN36RRRCKHO7QJIQLUZHLFNMQHL67PJLZWSLN2QNFEMNO6XU5NHFSVBXSWCWPHYZXXU3IEVDBLNHSDP4YU3DLP7BE")
                .addCookie("__jda","122270672.193823978.1500130744.1506738733.1506747940.5")
                .addCookie("__jdb","122270672.3.193823978|5.1506747940")
                .addCookie("__jdc","122270672")
                .addCookie("__jdu","193823978")
                .addCookie("__jdv","122270672|baidu-pinzhuan|t_288551095_baidupinzhuan|cpc|0f3d30c8dba7459bb52f2eb5eba8ac7d_0_327c74f8c71b4cee92813c3e1b995688|1506736337005")
                .addCookie("ipLoc-djd","1-72-2799-0")
                .addCookie("qrsc","3")
                .addCookie("rkv","V0800")
                .addCookie("unpl","V2_ZzNtbUBURUdzDRFdKR9cVmJRRglKVEoVd1pEAHtKVA1jBBtUclRCFXMUR1xnG10UZgsZWUFcQxBFCHZXchBYAWcCGllyBBNNIEwHDCRSBUE3XHxcFVUWF3RaTwEoSVoAYwtBDkZUFBYhW0IAKElVVTUFR21yVEMldQl2VHMbVQZlCxRUcmdEJUU4Qld6G14AVwIiXHIVF0lzD05dfR0RBW8BG15AX0UcRQl2Vw%3d%3d");*/
    public void process(Page page) {
        //System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\ChromeDriver.exe");
        ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\ChromeDriver.exe")).usingAnyFreePort().build();
        WebDriver driver = null;
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 7001, 10000);
            service.start();
            //driver = new ChromeDriver();
            driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
            driver.get(page.getUrl().get());
            List<WebElement> webElementList = driver.findElements(By.xpath("//ul[@class='listCon clearfix']/li[@class='crumb_list']"));
            //System.out.print(webElementList.get(1).getAttribute("outerHTML"));

            Html html = new Html(webElementList.get(1).getAttribute("outerHTML"));
            List<String> secondUrl = html.xpath("//div/ul/li").links().all();

            for(String url:secondUrl){
                Boolean e = jedis.sismember("secondUrls","http:"+url);
                Boolean ig = jedis.sismember("ig","http:"+url);
                if(e!=null&&!e&&!ig){
                    Long r = jedis.sadd("secondUrls","http:"+url);
                    if(r>0)
                    UrlQueue.addSecond( "http:"+url);
                }
            }
            //System.out.println(secondUrl);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                driver.quit();
                service.stop();
            }catch (Exception ee){
                ee.printStackTrace();
            }
            try{
                if(jedis!=null){
                    jedis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        while(UrlQueue.getFirstSize()==0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            String firstUrl = UrlQueue.getFirst();
            page.addTargetRequests(Arrays.asList(firstUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*public static void main(String[] args) {
        Spider.create(new GetYHDSecond())
                .addUrl("http://search.yhd.com/c0-0-1003427/")
                .addPipeline(new ConsolePipeline()).run();
    }*/
    public Site getSite() {
        return site;
    }


}