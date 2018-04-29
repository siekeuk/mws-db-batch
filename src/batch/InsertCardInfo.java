package batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.CardInfoModel;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang3.StringUtils;

import utils.Prop;
import utils.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.util.JSON;

import define.Def;

public class InsertCardInfo {
    public static final String PLAINSWALKER = "プレインズウォーカー";

    public static void main(String[] args) {

        String pathProp = "";
        File[] releacePathAry = null;
        List<CardInfoModel> cardInfoModelList = null;
        MongoClient client = null;
        MongoCredential credential = null;

        try {
            String dbHost = Prop.getValue("db.host");
            String dbDatabase = Prop.getValue("db.database");
            String dbUser = Prop.getValue("db.user");
            String dbPass = Prop.getValue("db.pass");
            int dbPort = Integer.parseInt(Prop.getValue("db.port"));

            // MongoDBサーバに接続
            credential =  MongoCredential.createCredential(dbUser, dbDatabase, dbPass.toCharArray());
            client = new MongoClient(new ServerAddress(dbHost, dbPort), Arrays.asList(credential));

            // 利用するdbDatabaseを取得
            DB db = client.getDB(dbDatabase);
            DBCollection cardInfoCol = db.getCollection("cardInfo");

            System.out.println("connect to "+dbDatabase);

            pathProp = Prop.getValue("msePath");
            System.out.println(pathProp);
            releacePathAry = Utils.getDirList(pathProp);

            if (args != null) {

                System.out.println("実行引数....");
                for(String arg : args){
                    System.out.println(arg);
                }
                System.out.println("...");

                if ("FULL".equals(StringUtils.upperCase(args[0]))) {
                    cardInfoCol.drop();
                    for (File releaseDate : releacePathAry) {
                        loopInsert(cardInfoCol, cardInfoModelList, releaseDate.getName());
                    }
                    return;

                } else if (StringUtils.isNumeric(args[0])) {

                    @SuppressWarnings("unchecked")
                    Comparator<String> comp = ComparatorUtils.naturalComparator();
                    Arrays.sort(args, comp);

                    for (String releaseDate : args) {
                        loopInsert(cardInfoCol, cardInfoModelList, releaseDate);
                    }

                    return;

                }else{

                    System.out.println("不正な引数です。処理を終了します....");
                    return;
                }

            } else {
                for (File releaseDate : releacePathAry) {
                    loopInsert(cardInfoCol, cardInfoModelList, releaseDate.getName());
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {

            if (client != null) {
                client.close();

            }
        }
    }

    private static void loopInsert(DBCollection cardInfoCol, List<CardInfoModel> cardInfoModelList, String releaseDate)
            throws JsonProcessingException {

        for (Map.Entry<String, String> exp : Def.EXP_MAP.entrySet()) {
            System.out.println(releaseDate + "_" + exp.getKey() + " 実行中....");

            cardInfoModelList = fileRead(releaseDate, exp.getKey());

            insertCollection(cardInfoCol, cardInfoModelList);

        }

    }

    private static void insertCollection(DBCollection cardInfoCol, List<CardInfoModel> cardInfoModelList)
            throws JsonProcessingException {

        BulkWriteOperation builder = null;

        if (cardInfoModelList.size() != 0) {

            builder = cardInfoCol.initializeOrderedBulkOperation();

            int generalIdIdx = getMaxGeneralId(cardInfoCol);

            ObjectMapper mapper = new ObjectMapper();
            String json = "";

            for (CardInfoModel cim : cardInfoModelList) {
                cim.setGeneralId(++generalIdIdx);
                json = mapper.writeValueAsString(cim);
                builder.insert((DBObject) JSON.parse(json));
            }

            BulkWriteResult result = builder.execute();

            System.out.println(result);
        }

    }

    @SuppressWarnings("unused")
    private static void dropCollection(DBCollection cardInfoCol, String releaceDate, String exp)
            throws JsonProcessingException {

        DBObject json = (DBObject) JSON.parse("{'releaseDate':'" + releaceDate + "', 'expansion':'" + exp + "'}");
        cardInfoCol.remove(json);

    }

    private static Map<String, String> createTransMap(String releaseDate, String exp) {

        Map<String, String> transMap = new HashMap<String, String>();

        try {
            File file = new File(Prop.getValue("msePath") + "/" + releaseDate + "/" + exp + ".lng");

            if (!file.exists()) {
                System.out.println(file.toPath() + "は存在しません。");
                return null;
            }

            FileInputStream fis = new FileInputStream(file);
            InputStreamReader in = new InputStreamReader(fis, "JISAutoDetect");
            BufferedReader br = new BufferedReader(in);

            String line;
            String[] lineSplit;

            while ((line = br.readLine()) != null) {

                if (StringUtils.isNoneEmpty(line)) {

                    lineSplit = StringUtils.split(line, "/");
                    if (lineSplit.length == 2) {
                        transMap.put(StringUtils.trimToEmpty(lineSplit[0]), StringUtils.trimToEmpty(lineSplit[1]));
                    }

                }
            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
        return transMap;
    }

    private static Map<String, Map<String, String>> createSupportMap(String releaseDate) {

        Map<String, Map<String, String>> supportMap = new HashMap<String, Map<String, String>>();
        Map<String, String> supportDetailMap = new HashMap<String, String>();

        try {
            File file = new File(Prop.getValue("msePath") + "/" + releaseDate + "/" + "PW.txt");

            if (!file.exists()) {
                System.out.println(file.toPath() + "は存在しません。");
                return supportMap;
            }

            FileInputStream fis = new FileInputStream(file);
            InputStreamReader in = new InputStreamReader(fis, "UTF8");
            BufferedReader br = new BufferedReader(in);

            String line;
            String[] lineSplit;

            while ((line = br.readLine()) != null) {

                if (StringUtils.isNoneEmpty(line)) {

                    lineSplit = StringUtils.split(line, "\t");
                    if (lineSplit.length == 4) {
                        supportDetailMap = new HashMap<String, String>();
                        supportDetailMap.put("loyalty", StringUtils.trimToEmpty(lineSplit[2]));
                        supportDetailMap.put("activate", StringUtils.trimToEmpty(lineSplit[3]));
                        supportMap.put(StringUtils.trimToEmpty(lineSplit[1]), supportDetailMap);
                    }

                }
            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
        return supportMap;
    }

    private static List<CardInfoModel> fileRead(String releaseDate, String exp) {

        List<CardInfoModel> cardInfoModelList = new ArrayList<CardInfoModel>();
        try {
            File file = new File(Prop.getValue("msePath") + "/" + releaseDate + "/" + exp + ".txt");

            if (!file.exists()) {
                System.out.println(file.toPath() + "は存在しません。");
                return cardInfoModelList;
            }

            FileInputStream fis = new FileInputStream(file);
            InputStreamReader in = new InputStreamReader(fis, "JISAutoDetect");
            BufferedReader br = new BufferedReader(in);

            String line;
            String[] lineSplit;
            String lineKey = "";
            String lineValue = "";
            CardInfoModel cardInfoModel = new CardInfoModel(releaseDate, exp);

            Map<String, String> transMap = createTransMap(releaseDate, exp);
            Map<String, Map<String, String>> supportMap = createSupportMap(releaseDate);

            while ((line = br.readLine()) != null) {

                if (StringUtils.isNoneEmpty(line)) {

                    Pattern propSplit = Pattern.compile(":\t+");
                    lineSplit = propSplit.split(line);

                    if (lineSplit.length == 1) {
                        if (Def.MSE_PROP_LIST.contains(lineSplit[0])) {
                            lineKey = StringUtils.trimToEmpty(lineSplit[0]);
                            lineValue = "";
                        } else {
                            lineValue = StringUtils.trimToEmpty(lineSplit[0]);
                        }
                    } else if (lineSplit.length == 2) {
                        lineKey = StringUtils.trimToEmpty(lineSplit[0]);
                        lineValue = StringUtils.trimToEmpty(lineSplit[1]);
                    }

                    // カード名
                    if (StringUtils.equals("Card Name", lineKey)) {

                        cardInfoModel.getCardName().put("jpn", lineValue);
                        cardInfoModel.getCardName().put("eng", Normalizer.normalize(transMap.get(lineValue), Normalizer.Form.NFKC));
                        //cardInfoModel.getCardName().put("eng", Normalizer.normalize(transMap.get(lineValue), Normalizer.Form.NFKC));

                    }
                    // 色（マナコストから判別するので設定なし）
                    else if (StringUtils.equals("Card Color", lineKey)) {

                    }
                    // マナコスト（マナコスト,点数で見たマナコスト,色）
                    else if (StringUtils.equals("Mana Cost", lineKey)) {

                        String manaDate = lineValue;

                        // 色
                        for (String key : Def.COLOR_MAP.keySet()) {
                            if (StringUtils.contains(lineValue, key)) {
                                cardInfoModel.getColor().add(key);
                            }
                        }
                        if(cardInfoModel.getColor().isEmpty()){
                            cardInfoModel.getColor().add("");
                        }

                        // 色混成(W/U)
                        String regex = "\\D/\\w";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(manaDate);
                        while (matcher.find()) {
                            cardInfoModel.setConvertedManaCost(cardInfoModel.getConvertedManaCost() + 1);
                        }
                        manaDate = StringUtils.replacePattern(manaDate, pattern.pattern(), StringUtils.EMPTY);

                        // 色混成(2/U)
                        regex = "2/\\D";
                        pattern = Pattern.compile(regex);
                        matcher = pattern.matcher(manaDate);
                        while (matcher.find()) {
                            cardInfoModel.setConvertedManaCost(cardInfoModel.getConvertedManaCost() + 2);
                        }
                        manaDate = StringUtils.replacePattern(manaDate, pattern.pattern(), StringUtils.EMPTY);

                        // 単色シンボル
                        regex = "(W|U|B|R|G)";
                        pattern = Pattern.compile(regex);
                        matcher = pattern.matcher(manaDate);
                        while (matcher.find()) {
                            cardInfoModel.setConvertedManaCost(cardInfoModel.getConvertedManaCost() + 1);
                        }
                        manaDate = StringUtils.replacePattern(manaDate, pattern.pattern(), StringUtils.EMPTY);

                        // 不特定マナ
                        regex = "\\d+";
                        pattern = Pattern.compile(regex);
                        matcher = pattern.matcher(manaDate);
                        while (matcher.find()) {
                            String generic = matcher.group();
                            if (StringUtils.isNumeric(generic)) {
                                int genericInt = 0;
                                genericInt = Integer.parseInt(generic);
                                cardInfoModel.setConvertedManaCost(cardInfoModel.getConvertedManaCost() + genericInt);
                            }
                        }
                        manaDate = StringUtils.replacePattern(manaDate, pattern.pattern(), StringUtils.EMPTY);

                        // マナコスト
                        cardInfoModel.setManaCost(lineValue);
                    }
                    // タイプ
                    else if (StringUtils.equals("Type & Class", lineKey)) {

                        String[] typeAry = lineValue.replaceAll(" {2,}", StringUtils.EMPTY).split(" - ");

                        // 特殊タイプ
                        for (String superType : Def.SUPERTYPE_LIST) {
                            if (StringUtils.startsWith(typeAry[0], superType)) {
                                cardInfoModel.getSupertype().add(superType);
                                typeAry[0] = StringUtils.replaceChars(typeAry[0], superType, StringUtils.EMPTY);
                            }
                        }

                        // カード・タイプ
                        if (StringUtils.startsWith(typeAry[0], "部族")) {
                            cardInfoModel.getCardType().add("部族");
                            typeAry[0] = StringUtils.replaceChars(typeAry[0], "部族", StringUtils.EMPTY);
                        }
                        for (String cardType : typeAry[0].split("・")) {
                            cardInfoModel.getCardType().add(cardType);
                        }

                        // サブタイプ
                        if (typeAry.length == 2) {
                            for (String subType : typeAry[1].split("・")) {
                                cardInfoModel.getSubtype().add(subType.replaceAll(" |　", ""));
                            }
                        }

                    }
                    else if (StringUtils.equals("Pow/Tou", lineKey)) {

                        if (StringUtils.isNoneEmpty(lineValue)) {

                            String[] powTouLoyAry = StringUtils.split(lineValue, "/");

                            if (powTouLoyAry.length == 1) {
                                cardInfoModel.setLoyalty(powTouLoyAry[0]);
                            } else {
                                cardInfoModel.setPower(powTouLoyAry[0]);
                                cardInfoModel.setToughness(powTouLoyAry[1]);
                            }
                        }else if(cardInfoModel.getCardType().contains(PLAINSWALKER)){

                            if(supportMap.containsKey(cardInfoModel.getCardName().get("jpn"))){
                                cardInfoModel.setLoyalty(supportMap.get(cardInfoModel.getCardName().get("jpn")).get("loyalty"));
                            }
                        }
                    }
                    else if (StringUtils.equals("Card Text", lineKey)) {

                        cardInfoModel.getRulesText().add(lineValue);
                    }
                    else if (StringUtils.equals("Flavor Text", lineKey)) {

                        cardInfoModel.getFlavorText().add(lineValue);

                    }
                    else if (StringUtils.equals("Artist", lineKey)) {

                        cardInfoModel.setArtist(lineValue);

                    }
                    else if (StringUtils.equals("Rarity", lineKey)) {

                        cardInfoModel.setRarity(lineValue);

                    }
                    // コレクターナンバー他
                    else if (StringUtils.equals("Card #", lineKey)) {

                        // プレインズウォーカーのルールテキストを編集
                        if (cardInfoModel.getCardType().contains(PLAINSWALKER) && supportMap.containsKey(cardInfoModel.getCardName().get("jpn"))) {
                            List<String> oldRulesTextList = cardInfoModel.getRulesText();
                            String activateCost = supportMap.get(cardInfoModel.getCardName().get("jpn")).get("activate");
                            String[] activateCostAry = activateCost.split(",");

                            int ruleIndex = 0;
                            for(String ruleText : oldRulesTextList){
                                String newRuleText = activateCostAry[ruleIndex] + ruleText;
                                cardInfoModel.getRulesText().set(ruleIndex, newRuleText);
                                ruleIndex++;
                            }
                        }

                        //タイプ行
                        String typesLine = "";
                        for(String supertype : cardInfoModel.getSupertype()){
                            typesLine += supertype;
                        }
                        for(String cardType : cardInfoModel.getCardType()){
                            if(cardInfoModel.getCardType().indexOf(cardType) == 0){
                                typesLine += cardType;
                            }else{
                                typesLine += "・"+cardType;
                            }
                        }
                        for(String subtype : cardInfoModel.getSubtype()){
                            if(cardInfoModel.getSubtype().indexOf(subtype) == 0){
                                typesLine += " - "+subtype;
                            }else{
                                typesLine += "・"+subtype;
                            }
                        }
                        cardInfoModel.setTypesLine(typesLine);


                        // PT行
                        if(StringUtils.isNotEmpty(cardInfoModel.getPower())){
                            cardInfoModel.setPowTouLoyLine(
                                    cardInfoModel.getPower()+"/"+cardInfoModel.getToughness());
                        }else if(StringUtils.isNotEmpty(cardInfoModel.getLoyalty())){
                            cardInfoModel.setPowTouLoyLine(cardInfoModel.getLoyalty());
                        }

                        // マナソート
                        List<String> colorList = cardInfoModel.getColor();
                        String manaCost = cardInfoModel.getManaCost();
                        for(int manaSortKey : Def.COLOR_SORT.keySet()){
                            if(colorList.isEmpty()){

                                if(StringUtils.equals(manaCost, Def.COLOR_SORT.get(manaSortKey).get(0))){
                                    cardInfoModel.setColorSort(manaSortKey);
                                    break;
                                }else{
                                    cardInfoModel.setColorSort(1);
                                    break;
                                }
                            }
                            else if(Def.COLOR_SORT.get(manaSortKey).containsAll(colorList)
                                    && Def.COLOR_SORT.get(manaSortKey).size() == colorList.size()){
                                cardInfoModel.setColorSort(manaSortKey);
                                break;
                            }
                        }

                        //rarity行
                        cardInfoModel.setRarityLine(Def.RARITY_MAP.get(cardInfoModel.getRarity()));

                        // セット行
                        String expansionLine = "";
                        expansionLine = "["+exp+"] "+Def.EXP_MAP.get(exp)+" ("+lineValue+")";
                        cardInfoModel.setExpansionLine(expansionLine);

                        cardInfoModel.setCollectorNumber(Integer.parseInt(lineValue.split("/")[0]));

                        cardInfoModelList.add(cardInfoModel);
                        cardInfoModel = new CardInfoModel();
                        cardInfoModel.setReleaseDate(releaseDate);
                        cardInfoModel.setExpansion(exp);
                    }

                }

            }

            br.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
        return cardInfoModelList;
    }

    private static int getMaxGeneralId(DBCollection cardInfoCol) {

        int generalIdIdx = 10000;
        DBObject sort = new BasicDBObject();
        sort.put("generalId", -1);

        DBCursor cursor = cardInfoCol.find().sort(sort).limit(1);
        if (cursor.hasNext()) {
            DBObject generalIdObj = cursor.next();
            generalIdIdx = (int)generalIdObj.get("generalId");
        }
        System.out.println("generalIdIdx:"+generalIdIdx);

        return generalIdIdx;

    }

}
