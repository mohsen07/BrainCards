package com.example.mdic.data.model;


import java.util.List;

public class FolderCardViewModel {

    private List<Folder> folderList;
    private List<Card> cardList;

    public List<Folder> getFolderList() {
        return folderList;
    }

    public void setFolderList(List<Folder> folderList) {
        this.folderList = folderList;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }
}
