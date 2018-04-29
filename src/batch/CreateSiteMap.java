package batch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import model.CardInfoModel;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import utils.Prop;
import utils.Utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.util.JSON;

public class CreateSiteMap {
     public static void main(String[] args) {

        String msePath = "";
        File[] releacePathAry = null;

        String dbHost = Prop.getValue("db.host");
        String dbDatabase = Prop.getValue("db.database");
        String dbUser = Prop.getValue("db.user");
        char[] dbPass = Prop.getValue("db.pass").toCharArray();
        int dbPort = Integer.parseInt(Prop.getValue("db.port"));
        String url = Prop.getValue("url");
        String sitemapPath = Prop.getValue("sitemapPath");

        Namespace ns = Namespace.getNamespace("http://www.google.com/schemas/sitemap/0.9");
        Element urlset = new Element("urlset", ns);

        String cardPath = "card";

        String loc = url + "/" + cardPath + "/";

        MongoClient client = null;
        MongoCredential credential = null;

        try {
            // 利用するDBを取得
            credential = MongoCredential.createCredential(dbUser, dbDatabase, dbPass);
            client = new MongoClient(new ServerAddress(dbHost, dbPort), Arrays.asList(credential));
            DB db = client.getDB(dbDatabase);
            DBCollection cardInfoCol = db.getCollection("cardInfo");
            System.out.println("connect to " + dbDatabase);

            msePath = Prop.getValue("msePath");
            releacePathAry = Utils.getDirList(msePath);

            Namespace xmlns = Namespace.getNamespace("http://www.google.com/schemas/sitemap/0.9");

            int priority = 10 - releacePathAry.length * 1;

            for (File releaseDate : releacePathAry) {
                urlset = new Element("urlset", xmlns);
                List<CardInfoModel> cardInfoModelList = null;
                cardInfoModelList = getSiteMapResourceList(cardInfoCol, releaseDate.getName());
                for (CardInfoModel cim : cardInfoModelList) {
                    Element elUrl = new Element("url", xmlns);
                    elUrl.addContent(new Element("loc", xmlns).setText(loc + releaseDate.getName() + "/" + cim.getExpansion()
                            + "/" + cim.getCardName().get("jpn") + "/"));
                    elUrl.addContent(new Element("lastmod", xmlns).setText(convertLastmod(releaseDate.getName())));
                    elUrl.addContent(new Element("priority", xmlns).setText("0."+String.valueOf(priority)));
                    urlset.addContent(elUrl);
                }

                File newfile = new File(sitemapPath + "/" + releaseDate.getName() + ".xml");
                if (newfile.exists()) {
                    newfile.delete();
                }
                System.out.println(Paths.get(newfile.getAbsolutePath()));
                XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
                xmlOut.output(new Document(urlset),
                        Files.newOutputStream(Paths.get(newfile.getAbsolutePath()), StandardOpenOption.CREATE_NEW));
                priority++;
            }

            // base
            urlset = new Element("urlset", xmlns);

            Element elUrl = new Element("url", xmlns);
            elUrl.addContent(new Element("loc", xmlns).setText(url));
            elUrl.addContent(new Element("lastmod", xmlns).setText("2015-11-22"));
            elUrl.addContent(new Element("priority", xmlns).setText("1.0"));

            urlset.addContent(elUrl);
            elUrl = new Element("url", xmlns);
            elUrl.addContent(new Element("loc", xmlns).setText(url + "/deck"));
            elUrl.addContent(new Element("lastmod", xmlns).setText("2015-11-22"));
            elUrl.addContent(new Element("priority", xmlns).setText("0.9"));

            urlset.addContent(elUrl);

            File newfile = new File(sitemapPath + "/" + "base.xml");
            if (newfile.exists()) {
                newfile.delete();
            }
            System.out.println(Paths.get(newfile.getAbsolutePath()));
            XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
            xmlOut.output(new Document(urlset), Files.newOutputStream(Paths.get(newfile.getPath()), StandardOpenOption.CREATE_NEW));

            // 元sitemap作成
            loc = url + "/";
            Element sitemapindex = new Element("sitemapindex", xmlns);
            Element sitemap = new Element("sitemap", xmlns);
            sitemapindex.addContent(sitemap);
            sitemap.addContent(new Element("loc", xmlns).setText(loc + "sitemap/base.xml"));
            sitemap.addContent(new Element("lastmod", xmlns).setText("2015-11-22"));
            for (File releaseDate : releacePathAry) {
                sitemap = new Element("sitemap", xmlns);
                sitemapindex.addContent(sitemap);
                sitemap.addContent(new Element("loc", xmlns).setText(loc + "sitemap/" + releaseDate.getName() + ".xml"));
                sitemap.addContent(new Element("lastmod", xmlns).setText(convertLastmod(releaseDate.getName())));
            }

            newfile = new File(sitemapPath + "/" + "sitemap.xml");
            if (newfile.exists()) {
                newfile.delete();
            }
            System.out.println(Paths.get(newfile.getAbsolutePath()));
            xmlOut = new XMLOutputter(Format.getPrettyFormat());
            xmlOut.output(new Document(sitemapindex), Files.newOutputStream(Paths.get(newfile.getPath()), StandardOpenOption.CREATE_NEW));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static List<CardInfoModel> getSiteMapResourceList(DBCollection cardInfoCol, String releaseDate) {

        List<CardInfoModel> cardInfoModelList = new ArrayList<CardInfoModel>();
        CardInfoModel cardInfoModel = null;
        DBObject sort = new BasicDBObject();
        sort.put("generalId", 1);
        DBObject find = (DBObject) JSON.parse("{'releaseDate':'" + releaseDate + "'}");

        DBCursor cursor = cardInfoCol.find(find).sort(sort);

        while (cursor.hasNext()) {
            cardInfoModel = new CardInfoModel();
            DBObject dbObj = cursor.next();
            cardInfoModel.setCardName((Map<String, String>) dbObj.get("cardName"));
            cardInfoModel.setExpansion((String) dbObj.get("expansion"));
            cardInfoModelList.add(cardInfoModel);
        }

        return cardInfoModelList;

    }

    private static String convertLastmod(String releaseDate) {

        StringBuilder sb = new StringBuilder(releaseDate);

        sb.insert(6, "-");
        sb.insert(4, "-");

        return sb.toString();

    }
}
