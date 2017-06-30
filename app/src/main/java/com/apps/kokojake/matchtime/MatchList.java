package com.apps.kokojake.matchtime;

/**
 * Created by mohamed on 29/06/2017.
 */

public class MatchList {
    public String mode;
    public String team1;
    public String team2;
    public String resalt;
    public String imgTeam1;
    public String imgTeam2;

    MatchList(String mode , String team1 , String team2 , String resalt , String imgTeam1 , String imgTeam2){
        this.mode = mode;
        this.team1 = team1;
        this.team2 = team2;
        this.resalt = resalt;
        this.imgTeam1 = imgTeam1;
        this.imgTeam2 = imgTeam2;
    }
}
