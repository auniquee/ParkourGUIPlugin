package utils;

import java.util.ArrayList;
import java.util.Date;

public class ParkourUser {
    String user;
    Date time;
    int completions;

    public ParkourUser(String new_user, Date new_time, int new_completions){
        user = new_user;
        time = new_time;
        completions = new_completions;
    }
}
