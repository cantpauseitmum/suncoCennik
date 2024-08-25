/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sunco_cennik;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Hubert
 */
public final class PDFCreator {

    public PDFCreator(File file, SuncoMainWindow suncoMainWindow) throws FileNotFoundException, DocumentException, IOException {
        createDocument(file, suncoMainWindow);
    }

    public void createDocument(File file, SuncoMainWindow suncoMainWindow) throws FileNotFoundException, DocumentException, IOException {
        Document doc = new Document(PageSize.A4);
        Font regular = new Font(BaseFont.createFont("arialuni-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 12);
        Font bold = new Font(BaseFont.createFont("arialuni-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 12);
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        double totalValue = 0;
        String currency = "zł", vat = "Netto";
        if (suncoMainWindow.currency != 1) {
            currency = "€";
        }
        if (suncoMainWindow.vat != 1) {
            vat = "Brutto";
        }
        LocalDate currentDate = LocalDate.now();
        currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        doc.add(writePara("Poznań, " + currentDate, 2, regular));
        doc.add(writePara("SUNNCO sp. z.o.o.", 0, regular));
        doc.add(writePara("ul. Świętej Trójcy 14", 0, regular));
        doc.add(writePara("61-478 Poznań", 0, regular));
        doc.add(writePara("E-mail: b.trafankowski@sunco.pro", 0, regular));
        doc.add(writePara(suncoMainWindow.blindList.clientName, 2, regular));
        doc.add(writePara("Wycena: "
                + suncoMainWindow.blindList.clientName
                + "(" + suncoMainWindow.blindList.offerName
                + ") "
                + currentDate
                + " SUNCO", 1, bold));
        for (NewBlind newBlind : suncoMainWindow.blindList.blindList) {
            totalValue += newBlind.getBlindCount() * newBlind.getBlindPrice();
            if (newBlind.getSimpleBlind() != null) {
                for (SimpleBlind simpleBlind : newBlind.getSimpleBlind()) {
                    totalValue += simpleBlind.getSimpleBlindPrice() * newBlind.getBlindCount();
                }
            }
            writeBlind(newBlind, regular, bold, doc, currency);
        }
        doc.add(writePara("", 1, regular));
        if (!suncoMainWindow.blindList.customOrder.equals("Niestandardowe prośby")) {
            doc.add(writePara(suncoMainWindow.blindList.customOrder, 0, bold));
        }
        doc.add(writePara("", 1, regular));
        doc.add(writePara("", 1, regular));
        doc.add(writePara("Podsumowanie:", 1, bold));
        for (OrderComponents singleComponent : organiseComponents(suncoMainWindow.blindList.blindList)) {
            doc.add(writePara("-" + singleComponent.getName() + ": " + singleComponent.getSize() + singleComponent.getType() + " x " + singleComponent.getPrice() * suncoMainWindow.currency + currency + " " + vat + " = " + singleComponent.getSize() * singleComponent.getPrice() * suncoMainWindow.currency * suncoMainWindow.vat, 0, regular));
        }
        totalValue += suncoMainWindow.blindList.customOrderValue;
        doc.add(writePara("RAZEM: " + String.valueOf(round(totalValue, 2)) + currency + " " + vat, Paragraph.ALIGN_RIGHT, bold));
        doc.close();
    }

    public void writeBlind(NewBlind newBlind, Font regular, Font bold, Document doc, String currency) throws DocumentException {
        doc.add(writePara("Ilość: " + String.valueOf(newBlind.getBlindCount()), Paragraph.ALIGN_RIGHT, bold));
        String pricePara;
        if (newBlind.getSimpleBlind() == null) {
            pricePara = String.valueOf(round(newBlind.getBlindPrice(), 2)) + currency;
        } else {
            pricePara = String.valueOf(round(getFullPrice(newBlind), 2)) + currency;
        }
        doc.add(writePara("Cena/szt: " + pricePara, Paragraph.ALIGN_RIGHT, bold));
        doc.add(writePara("Model: " + newBlind.getBlindModel().getName(), Paragraph.ALIGN_RIGHT, regular));
        if (newBlind.blindColour.WholeColour()) {
            doc.add(writePara("Kolor: " + newBlind.blindColour.getBoxOut(), Paragraph.ALIGN_RIGHT, regular));
        } else {
            doc.add(writePara("Kolor - skrzynka ZEW: " + newBlind.blindColour.getBoxOut(), Paragraph.ALIGN_RIGHT, regular));
            doc.add(writePara("Kolor - skrzynka WEW: " + newBlind.blindColour.getBoxIn(), Paragraph.ALIGN_RIGHT, regular));
            doc.add(writePara("Kolor - profil: " + newBlind.blindColour.getProfile(), Paragraph.ALIGN_RIGHT, regular));
            doc.add(writePara("Kolor - dolna listwa: " + newBlind.blindColour.getBotList(), Paragraph.ALIGN_RIGHT, regular));
            doc.add(writePara("Kolor - prowadnice: " + newBlind.blindColour.getLeaders(), Paragraph.ALIGN_RIGHT, regular));
            doc.add(writePara("Kolor - adapter: " + newBlind.blindColour.getAdapter(), Paragraph.ALIGN_RIGHT, regular));
        }
        String profilePara, enginePara;
        if (newBlind.getSimpleBlind() == null) {
            profilePara = newBlind.getBlindProfile();
            enginePara = "Silnik: " + newBlind.getBlindAuto().getName();
        } else {
            profilePara = getBlindProfiles(newBlind.getSimpleBlind()).get(0);
            enginePara = "";
        }
        doc.add(writePara("Profil: " + profilePara, Paragraph.ALIGN_RIGHT, regular));
        doc.add(writePara(enginePara, Paragraph.ALIGN_RIGHT, regular));
        doc.add(writePara("Skrzynka: " + String.valueOf(round(newBlind.getBlindBox(), 3)), Paragraph.ALIGN_LEFT, regular));
        doc.add(writePara("Szerokość: " + String.valueOf(round(newBlind.getBlindWidth(), 3)), Paragraph.ALIGN_LEFT, regular));
        doc.add(writePara("Wysokość okna: " + String.valueOf(round(newBlind.getBlindHeight(), 3)), Paragraph.ALIGN_LEFT, regular));
        doc.add(writePara("Wysokość  pancerza: " + String.valueOf(round(newBlind.getBlindHeightWithBox(), 3)), Paragraph.ALIGN_LEFT, regular));
        doc.add(writePara("Ciężar pancerza: " + String.valueOf(round(newBlind.getBlindWeight(), 2)), Paragraph.ALIGN_LEFT, regular));
        doc.add(writePara("", Paragraph.ALIGN_LEFT, regular));
        doc.add(writePara("Dodatki: ", Paragraph.ALIGN_LEFT, bold));
        writeList(newBlind, newBlind.getBlindAddons(), regular, doc);
        doc.add(writePara("", Paragraph.ALIGN_LEFT, regular));
        doc.add(writePara("Akcesoria: ", Paragraph.ALIGN_LEFT, bold));
        writeList(newBlind, newBlind.getBlindExtras(), regular, doc);
        doc.add(writePara("", 1, regular));
        doc.add(writePara("", 1, regular));
        doc.add(writePara("", 1, regular));

    }

    public Paragraph writePara(String toWrite, int align, Font font) {
        Paragraph para = new Paragraph(toWrite + "\n", font);
        para.setAlignment(align);
        return para;
    }

    public void writeList(NewBlind newBlind, ArrayList<BlindPriceList> blindPriceList, Font font, Document doc) throws DocumentException {
        if (blindPriceList != null) {
            boolean divided = false;
            List tempList = new ArrayList();
            for (BlindPriceList blindList : blindPriceList) {
                if ((blindList.getName().equals("PODZIAŁ ROLET") || blindList.getName().equals("PRZENIESIENIE NAPĘDU")) && newBlind.getSimpleBlind() != null) {
                    divided = true;
                    continue;
                }
                tempList.add(blindList.getName());
            }
            HashSet<String> duplicates = new HashSet<>(tempList);
            for (String duplicate : duplicates) {
                doc.add(writePara(duplicate + " :  " + Collections.frequency(tempList, duplicate), Paragraph.ALIGN_LEFT, font));
            }
            if (divided) {
                for (SimpleBlind simpleBlind : newBlind.getSimpleBlind()) {
                    doc.add(writePara("--------------------------------------------------", Paragraph.ALIGN_CENTER, font));
                    doc.add(writePara("PODZIAŁ:", Paragraph.ALIGN_LEFT, font));
                    doc.add(writePara("Szerokość: " + String.valueOf(round(simpleBlind.getBlindWidth(), 3)), Paragraph.ALIGN_LEFT, font));
                    doc.add(writePara("Wysokość  pancerza: " + String.valueOf(round(simpleBlind.getBlindHeightWithBox(), 3)), Paragraph.ALIGN_LEFT, font));
                    doc.add(writePara("Profil: " + simpleBlind.getBlindProfile(), Paragraph.ALIGN_LEFT, font));
                    doc.add(writePara("Silnik: " + simpleBlind.getBlindAuto().getName(), Paragraph.ALIGN_LEFT, font));
                }
            }
        }
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double getFullPrice(NewBlind newBlind) {
        double fullPrice = 0;
        for (SimpleBlind simpleBlind : newBlind.getSimpleBlind()) {
            fullPrice += simpleBlind.getSimpleBlindPrice();
        }
        fullPrice += newBlind.getBlindPrice();
        return fullPrice;
    }

    public ArrayList<String> getBlindProfiles(ArrayList<SimpleBlind> simpleBlindList) {
        ArrayList<String> paras = new ArrayList();
        String profilePara = "", enginePara = "";
        for (SimpleBlind simpleBlind : simpleBlindList) {
            profilePara += simpleBlind.getBlindProfile() + " - ";
            enginePara += simpleBlind.getBlindAuto().getName() + " - ";
        }
        paras.add(profilePara.substring(0, profilePara.length() - 3));
        paras.add(enginePara.substring(0, enginePara.length() - 3));
        return paras;
    }

    public ArrayList<OrderComponents> organiseComponents(ArrayList<NewBlind> blindList) {
        ArrayList<OrderComponents> orderComponents = new ArrayList();
        for (NewBlind newBlind : blindList) {
            checkComponent(new OrderComponents(
                    "rolety - " + newBlind.getBlindModel().getName(),
                    calculateSize(newBlind, newBlind.getBlindModel()),
                    newBlind.getBlindModel().getPriceType(),
                    newBlind.getBlindModel().getPrice()), orderComponents);
            for (BlindPriceList blindPriceList : newBlind.getBlindAddons()) {
                checkComponent(new OrderComponents(
                        blindPriceList.getName(),
                        calculateSize(newBlind, blindPriceList),
                        blindPriceList.getPriceType(),
                        blindPriceList.getPrice()), orderComponents);
            }
            for (BlindPriceList blindPriceList : newBlind.getBlindExtras()) {
                checkComponent(new OrderComponents(
                        blindPriceList.getName(),
                        calculateSize(newBlind, blindPriceList),
                        blindPriceList.getPriceType(),
                        blindPriceList.getPrice()), orderComponents);
            }
            checkComponent(new OrderComponents(
                    newBlind.getBlindAuto().getName(),
                    calculateSize(newBlind, newBlind.getBlindAuto()),
                    newBlind.getBlindAuto().getPriceType(),
                    newBlind.getBlindAuto().getPrice()), orderComponents);
        }
        return orderComponents;
    }

    public void checkComponent(OrderComponents newComponent, ArrayList<OrderComponents> orderComponents) {
        boolean contains = true;
        for (OrderComponents single : orderComponents) {
            if (single.getName().equals(newComponent.getName()) && Objects.equals(single.getPrice(), newComponent.getPrice())) {
                single.setSize(single.getSize() + newComponent.getSize());
                contains = false;
                break;
            }
        }
        if (contains) {
            orderComponents.add(newComponent);
        }
    }

    public double calculateSize(NewBlind newBlind, BlindPriceList blindPriceList) {
        double boxSize = 0;
        switch (blindPriceList.getPriceType()) {
            case "m2" -> {
                boxSize = newBlind.getBlindWidth() * newBlind.getBlindHeightWithBox();
                break;
            }
            case "mbS" -> {
                boxSize = newBlind.getBlindWidth();
                break;
            }
            case "mbW" -> {
                boxSize = newBlind.getBlindHeightWithBox() - newBlind.getBlindBox();
                break;
            }
            case "szt" -> {
                boxSize = 1;
                break;
            }
            case "%" -> {
                boxSize = blindPriceList.getPrice();
                break;
            }
        }
        return boxSize;
    }
}
