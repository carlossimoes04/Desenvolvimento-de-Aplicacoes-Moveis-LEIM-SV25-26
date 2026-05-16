package com.notes.notesproxmlviews;

import com.google.firebase.Timestamp;

public class Note {
    String docId;
    String title;
    String content;
    Timestamp timestamp;
    boolean favorite;
    String tagsCsv;
    String colorLabel;
    String imageBase64; // adicionado para armazenar a imagem como string Base64 - 4.3.1 Notes with images & GOAT 

    public Note() {
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getTagsCsv() {
        return tagsCsv;
    }

    public void setTagsCsv(String tagsCsv) {
        this.tagsCsv = tagsCsv;
    }

    public String getColorLabel() {
        return colorLabel;
    }

    public void setColorLabel(String colorLabel) {
        this.colorLabel = colorLabel;
    }

    public String getImageBase64() { // adicionado para armazenar a imagem como string Base64 - 4.3.1 Notes with images & GOAT
        return imageBase64; 
    }

    public void setImageBase64(String imageBase64) { // adicionado para armazenar a imagem como string Base64 - 4.3.1 Notes with images & GOAT
        this.imageBase64 = imageBase64; 
    }
}
