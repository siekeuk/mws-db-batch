package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CardInfoModel {

    int generalId;
    String releaseDate;
    String expansion;
    Map<String, String> cardName;
    List<String> color;
    String manaCost;
    int convertedManaCost;
    List<String> supertype;
    List<String> cardType;
    List<String> subtype;
    String power;
    String toughness;
    String loyalty;
    List<String> rulesText;
    List<String> flavorText;
    String artist;
    int collectorNumber;
    String rarity;

    String typesLine;
    String powTouLoyLine;
    String rarityLine;
    String expansionLine;

    int colorSort;

    public CardInfoModel() {
        cardName = new LinkedHashMap<String, String>();
        color = new ArrayList<String>();
        supertype = new ArrayList<String>();
        cardType = new ArrayList<String>();
        subtype = new ArrayList<String>();
        rulesText = new ArrayList<String>();
        flavorText = new ArrayList<String>();
        artist = "";
        expansion = "";
        generalId = 0;
        loyalty = "";
        manaCost = "";
        power = "";
        releaseDate = "";
        toughness = "";
        rarity = "";
        convertedManaCost = 0;
        collectorNumber = 0;
        typesLine = "";
        powTouLoyLine = "";
        rarityLine = "";
        expansionLine = "";
        colorSort = 0;
    }

    public CardInfoModel(String releaseDate, String exp) {
        cardName = new LinkedHashMap<String, String>();
        color = new ArrayList<String>();
        supertype = new ArrayList<String>();
        cardType = new ArrayList<String>();
        subtype = new ArrayList<String>();
        rulesText = new ArrayList<String>();
        flavorText = new ArrayList<String>();
        artist = "";
        this.expansion = exp;
        generalId = 0;
        loyalty = "";
        manaCost = "";
        power = "";
        this.releaseDate = releaseDate;
        toughness = "";
        rarity = "";
        convertedManaCost = 0;
        collectorNumber = 0;
        typesLine = "";
        powTouLoyLine = "";
        rarityLine = "";
        expansionLine = "";
        colorSort = 0;
    }

    public int getGeneralId() {
        return generalId;
    }

    public void setGeneralId(int generalId) {
        this.generalId = generalId;
    }

    public String getExpansion() {
        return expansion;
    }

    public void setExpansion(String expansion) {
        this.expansion = expansion;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Map<String, String> getCardName() {
        return cardName;
    }

    public void setCardName(Map<String, String> cardName) {
        this.cardName = cardName;
    }

    public List<String> getColor() {
        return color;
    }

    public void setColor(List<String> color) {
        this.color = color;
    }

    public String getManaCost() {
        return manaCost;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    public int getConvertedManaCost() {
        return convertedManaCost;
    }

    public void setConvertedManaCost(int convertedManaCost) {
        this.convertedManaCost = convertedManaCost;
    }

    public List<String> getSupertype() {
        return supertype;
    }

    public void setSupertype(List<String> supertype) {
        this.supertype = supertype;
    }

    public List<String> getCardType() {
        return cardType;
    }

    public void setCardType(List<String> cardType) {
        this.cardType = cardType;
    }

    public List<String> getSubtype() {
        return subtype;
    }

    public void setSubtype(List<String> subtype) {
        this.subtype = subtype;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getToughness() {
        return toughness;
    }

    public void setToughness(String toughness) {
        this.toughness = toughness;
    }

    public String getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(String loyalty) {
        this.loyalty = loyalty;
    }

    public List<String> getRulesText() {
        return rulesText;
    }

    public void setRulesText(List<String> rulesText) {
        this.rulesText = rulesText;
    }

    public List<String> getFlavorText() {
        return flavorText;
    }

    public void setFlavorText(List<String> flavorText) {
        this.flavorText = flavorText;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getCollectorNumber() {
        return collectorNumber;
    }

    public void setCollectorNumber(int collectorNumber) {
        this.collectorNumber = collectorNumber;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getTypesLine() {
        return typesLine;
    }

    public void setTypesLine(String typesLine) {
        this.typesLine = typesLine;
    }

    public String getPowTouLoyLine() {
        return powTouLoyLine;
    }

    public void setPowTouLoyLine(String powTouLoyLine) {
        this.powTouLoyLine = powTouLoyLine;
    }

    public String getRarityLine() {
        return rarityLine;
    }

    public void setRarityLine(String rarityLine) {
        this.rarityLine = rarityLine;
    }

    public String getExpansionLine() {
        return expansionLine;
    }

    public void setExpansionLine(String expansionLine) {
        this.expansionLine = expansionLine;
    }

    public int getColorSort() {
        return colorSort;
    }

    public void setColorSort(int colorSort) {
        this.colorSort = colorSort;
    }

}
