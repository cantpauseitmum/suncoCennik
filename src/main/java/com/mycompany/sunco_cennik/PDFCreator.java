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
import java.util.ArrayList;

/**
 *
 * @author Hubert
 */
public final class PDFCreator {

    public PDFCreator(File file, BlindList blindList) throws FileNotFoundException, DocumentException, IOException {
        createDocument(file, blindList);
    }

    public void createDocument(File file, BlindList blindList) throws FileNotFoundException, DocumentException, IOException {
        Document doc = new Document(PageSize.A4);
        Font regular = new Font(BaseFont.createFont("arialuni-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 14);
        Font bold = new Font(BaseFont.createFont("arialuni-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 14);
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        double totalValue = 0;
        for (NewBlind newBlind : blindList.blindList) {
            totalValue += newBlind.getBlindCount() * newBlind.getBlindPrice();
            if (newBlind.getSimpleBlind() != null) {
                for (SimpleBlind simpleBlind : newBlind.getSimpleBlind()) {
                    totalValue += simpleBlind.getSimpleBlindPrice() * newBlind.getBlindCount();
                }
            }
            writeBlind(newBlind, regular, bold, doc);
        }
        doc.add(writePara("RAZEM: " + String.valueOf(round(totalValue, 2)) + "zł", Paragraph.ALIGN_RIGHT, bold));
        doc.close();
    }

    public void writeBlind(NewBlind newBlind, Font regular, Font bold, Document doc) throws DocumentException {
        doc.add(writePara("Ilość: " + String.valueOf(newBlind.getBlindCount()), Paragraph.ALIGN_RIGHT, bold));
        String pricePara;
        if (newBlind.getSimpleBlind() == null) {
            pricePara = String.valueOf(round(newBlind.getBlindPrice(), 2)) + "zł";
        } else {
            pricePara = String.valueOf(round(getFullPrice(newBlind), 2)) + "zł";
        }
        doc.add(writePara("Cena/szt: " + pricePara, Paragraph.ALIGN_RIGHT, bold));
        doc.add(writePara("Model: " + newBlind.getBlindModel().getName(), Paragraph.ALIGN_RIGHT, regular));
        doc.add(writePara("Kolor: " + newBlind.getBlindColour(), Paragraph.ALIGN_RIGHT, regular));
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
    }

    public Paragraph writePara(String toWrite, int align, Font font) {
        Paragraph para = new Paragraph(toWrite + "\n", font);
        para.setAlignment(align);
        return para;
    }

    public void writeList(NewBlind newBlind, ArrayList<BlindPriceList> blindPriceList, Font font, Document doc) throws DocumentException {
        if (blindPriceList != null) {
            boolean divided = false;
            for (BlindPriceList blindList : blindPriceList) {
                if ((blindList.getName().equals("PODZIAŁ ROLET") || blindList.getName().equals("PRZENIESIENIE NAPĘDU")) && newBlind.getSimpleBlind() != null) {
                    divided = true;
                    continue;
                }
                doc.add(writePara(blindList.getName(), Paragraph.ALIGN_LEFT, font));
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
}
