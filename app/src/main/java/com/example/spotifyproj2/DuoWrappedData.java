package com.example.spotifyproj2;
import java.util.List;

public class DuoWrappedData {
    private List<String> commonTopArtists;
    private List<String> commonTopSongs;
    private List<String> uniqueTopArtistsUser1;
    private List<String> uniqueTopArtistsUser2;
    public DuoWrappedData() {
    }

    // Constructor with all fields
    public DuoWrappedData(List<String> commonTopArtists, List<String> commonTopSongs, List<String> uniqueTopArtistsUser1, List<String> uniqueTopArtistsUser2) {
        this.commonTopArtists = commonTopArtists;
        this.commonTopSongs = commonTopSongs;
        this.uniqueTopArtistsUser1 = uniqueTopArtistsUser1;
        this.uniqueTopArtistsUser2 = uniqueTopArtistsUser2;
        //TODO: Discuss and add more
    }

    public List<String> getCommonTopArtists() {
        return commonTopArtists;
    }

    public void setCommonTopArtists(List<String> commonTopArtists) {
        this.commonTopArtists = commonTopArtists;
    }

    public List<String> getCommonTopSongs() {
        return commonTopSongs;
    }

    public void setCommonTopSongs(List<String> commonTopSongs) {
        this.commonTopSongs = commonTopSongs;
    }

    public List<String> getUniqueTopArtistsUser1() {
        return uniqueTopArtistsUser1;
    }

    public void setUniqueTopArtistsUser1(List<String> uniqueTopArtistsUser1) {
        this.uniqueTopArtistsUser1 = uniqueTopArtistsUser1;
    }

    public List<String> getUniqueTopArtistsUser2() {
        return uniqueTopArtistsUser2;
    }

    public void setUniqueTopArtistsUser2(List<String> uniqueTopArtistsUser2) {
        this.uniqueTopArtistsUser2 = uniqueTopArtistsUser2;
    }
}

