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
public class GetYHDThree implements PageProcessor {
    //Jedis jedis = new Jedis("10.100.50.55", 7001, 10000);
    //static List threeUrls = new ArrayList();
    static List result = new ArrayList();
    static String fileName = null;
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
            /*List<WebElement> webElements = driver.findElement(By.xpath("//div[@class='mod_search_guide clearfix']/div[@class='classWrap']/div[@class='guide_box']/div[@class='guide_main']/ul/")).findElements(By.tagName("li"));
            if(webElements!=null&&!webElements.isEmpty()){
                List<WebElement> webElementList = driver.findElements(By.xpath("//ul[@class='listCon clearfix']/li[@class='crumb_list']"));
                Html html = new Html(webElementList.get(0).getAttribute("outerHTML"));
                String c1 = html.xpath("//span/a/text()").toString();

                html = new Html(webElementList.get(1).getAttribute("outerHTML"));
                String c2 = html.xpath("//span/a/text()").toString();

                html = new Html(webElementList.get(2).getAttribute("outerHTML"));
                String c3 = html.xpath("//span/a/text()").toString();
                Boolean e = jedis.sismember("threeUrls","http:"+html.xpath("//span").links().get());
                if(e!=null&&!e){
                    result.add(c1+"|"+c2+"|"+c3);
                    jedis.sadd("threeUrls","http:"+html.xpath("//span").links().get());
                    UrlQueue.addThree("http:"+html.xpath("//span").links().get());
                }
            }*/

            List<WebElement> webElements = driver.findElements(By.xpath("//div[@class='proImg']"));
            for(WebElement o:webElements){
                Html html = new Html(o.getAttribute("outerHTML"));
                String threeUrls = html.xpath("//div").links().toString();
                Boolean e = jedis.sismember("threeUrls","http:"+threeUrls);
                if(e!=null&&!e){
                    jedis.sadd("threeUrls","http:"+threeUrls);
                    UrlQueue.addThree("http:"+threeUrls);
                }
            }
            int pageCountPage = Integer.parseInt(driver.findElement(By.xpath("//input[@id='pageCountPage']")).getAttribute("value"));
            for(int i=2 ;i<=pageCountPage;i++){
                Boolean e = jedis.sismember("secondUrls",page.getUrl().toString().split("#")[0]+"#page="+i+"&sort=1");
                if(e!=null&&!e){
                    Long r = jedis.sadd("secondUrls",page.getUrl().toString().split("#")[0]+"#page="+i+"&sort=1");
                    if(r>0)
                    UrlQueue.addSecond( page.getUrl().toString().split("#")[0]+"#page="+i+"&sort=1");
                }
            }
            //System.out.println(urls);
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

        while(UrlQueue.getSecondSize()==0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            String secondUrl = UrlQueue.getSecond();
            page.addTargetRequests(Arrays.asList(secondUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Site getSite() {
        return site;
    }

}
