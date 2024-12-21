package database;

import me.jonis.database_test.Database_test;
import org.bukkit.OfflinePlayer;
import utils.ConfigManager;
import utils.ParkourColumn;
import utils.ParkourUser;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.ErrorManager;

public class RetreiveParkours {

    // returns a list players top based on records on parkour
    public static ArrayList<ParkourUser> getRecordsTop(String parkour, int page){
        String query = String.format(
                "SELECT %s_records,%s_plays,uuid FROM open_dummy ORDER BY %s_records ASC LIMIT %d OFFSET %d",
                parkour, parkour, parkour, page*7+7, page*7
        );
        ArrayList<ParkourUser> players = new ArrayList<ParkourUser>();

        DatabaseManager db = DatabaseManager.getInstance();

        try (Connection connection = DatabaseManager.getInstance().getConnection()){
            try(ResultSet results = connection.prepareStatement(query).executeQuery()){
                while (results.next()) {
                    players.add(new ParkourUser(
                            results.getString("uuid"),
                            new Date(results.getInt(String.format("%s_records", parkour))),
                            results.getInt(String.format("%s_plays", parkour))
                    ));
                }
            }catch (SQLException e){
                System.err.println("Error reading database: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }catch(SQLException e){
            System.err.println("Error getting connection to database: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        return players;
        //return List.of(new ParkourUser("", new Date(), 1));
    }

    // returns a list players top based on plays on parkour
    public static ArrayList<ParkourUser> getPlaysTop(String parkour, int page){
        String query = String.format(
                "SELECT %s_plays,uuid FROM open_dummy ORDER BY %s_plays DESC LIMIT %d OFFSET %d",
                parkour, parkour, page*7+7, page*7
        );
        ArrayList<ParkourUser> players = new ArrayList<ParkourUser>();

        try (Connection connection = DatabaseManager.getInstance().getConnection()){
            try(ResultSet results = connection.prepareStatement(query).executeQuery()){
                while (results.next()) {
                    players.add(new ParkourUser(
                            results.getString("uuid"),
                            new Date(),
                            results.getInt(String.format("%s_plays", parkour))
                    ));
                }
            }catch (SQLException e){
                System.err.println("Error reading database: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }catch(SQLException e){
            System.err.println("Error getting connection to database: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        return players;
    }

    // returns entire row from the database, this will include all completions and records
    public static Map<String, ParkourColumn> getPlayerRow(String uuid){

        List<String> parkours = ConfigManager.getInstance().getParkourList();
        Map<String, ParkourColumn> playerStats = new HashMap<String, ParkourColumn>();
        String query = "SELECT * FROM open_dummy WHERE uuid = ?";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, uuid);

            try(ResultSet results = statement.executeQuery()){
                if (results.next()){
                for(int i = 0; i < parkours.size(); i++){
                        playerStats.put(
                                parkours.get(i),
                                new ParkourColumn(
                                        new Date(results.getInt(parkours.get(i)+"_records")),
                                        results.getInt(parkours.get(i)+"_plays")
                                )
                        );
                    }
                }else{
                    System.out.printf("NO PLAYER WITH THAT NAME FOUND!");
                }
            }catch (SQLException e){
                System.err.println("Error reading database: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }catch(SQLException e){
            System.err.println("Error getting connection to database: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        return playerStats;
    }

    //returns amoumt of pages when viewing parkours
    public static int getPages(String parkour) {
        String query = String.format("SELECT COUNT(*) AS entries FROM open_dummy WHERE %s_records != 0", parkour);
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            try (ResultSet results = connection.prepareStatement(query).executeQuery()) {
                if (results.next()) {
                    return Math.ceilDiv(results.getInt("entries"), 7);
                }else{
                    return 0;
                }

            }catch (SQLException e){
                System.err.println("Error reading database: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }catch(SQLException e){
            System.err.println("Error getting connection to database: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        return 1;
    }

    // returns inputted players ranking in all parkours
    public static Map<String, Integer> getPlayerRanking(String uuid){
        List<String> parkours = ConfigManager.getInstance().getParkourList();
        Map<String, ParkourColumn> playerStats = new HashMap<String, ParkourColumn>();
        Map<String, Integer> result = new HashMap<String, Integer>();
        String query = "WITH ranks AS (SELECT uuid, ";
        for(int i = 0; i  < parkours.size(); i++){
            query += String.format("RANK() OVER (ORDER BY %s ASC) AS %s_rank", parkours.get(i)+"_records", parkours.get(i));
            if( i < parkours.size()-1){
                query += ", ";
            }
        }
        query += " FROM open_dummy) SELECT * FROM ranks WHERE uuid = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, uuid);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()){
                    for(int i = 0; i  < parkours.size(); i++){
                        result.put(parkours.get(i), results.getInt(parkours.get(i)+"_rank"));
                    }
                }else{
                System.out.printf("No player with uuid:" + uuid + " found!");
                }
            }
        }catch(SQLException e){
            System.err.println("Error getting connection to database: " + e.getMessage());
            e.printStackTrace(System.err);
        }
            return result;
    }
    public static int totalParkourCompletionsOrAvgTime(String parkour, Boolean sum) {
        String query;
        if(sum){
            query = String.format("SELECT SUM(%s_plays) AS res FROM open_dummy", parkour);
        }else{
            query = String.format("SELECT AVG(%s_records) AS res FROM open_dummy", parkour);
        }

        int result = 0;
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            try  (ResultSet results = statement.executeQuery()) {
                if(results.next()){
                    result = results.getInt("res");
                }else{
                    System.err.println("No parkour with that name!: ");
                }

            }catch(SQLException e){
                System.err.println("Error reading table: " + e.getMessage());
                //e.printStackTrace();
            }

        }catch(SQLException e){
            System.err.println("Error getting connection to database: " + e.getMessage());
            //e.printStackTrace();
        }
        return result;
    }
    public static int totalParkourCompletions(String parkour) {
        String query = String.format("SELECT SUM(%s_completions) AS sum FROM open_dummy");
        int result = 0;
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            try  (ResultSet results = statement.executeQuery()) {
                if(results.next()){
                    result = results.getInt("sum");
                }else{
                    System.err.println("No parkour with that name!: ");
                }

            }catch(SQLException e){
                System.err.println("Error reading table: " + e.getMessage());
                //e.printStackTrace();
            }

        }catch(SQLException e){
            System.err.println("Error getting connection to database: " + e.getMessage());
            //e.printStackTrace();
        }
        return result;
    }


    public static int totalPlayerCompletions(OfflinePlayer p) {
        String query = "SELECT (";
        List<String> parkours = ConfigManager.getInstance().getParkourList();
        for(int i = 0; i < parkours.size(); i++){
            query += parkours.get(i) + "_plays ";
            if(i != parkours.size() - 1){
                query += " + ";
            }
        }
        query += ") AS row_sum FROM open_dummy WHERE uuid = ?";
        int result = 0;
        try (Connection connection = DatabaseManager.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, p.getUniqueId().toString());
            try  (ResultSet results = statement.executeQuery()) {
                if(results.next()){
                    for(int i = 0; i < parkours.size(); i++){
                        result = results.getInt("row_sum");
                    }
                }else{
                    System.out.printf("No player with uuid:" + p.getUniqueId() + " found!");
                }


            }catch(SQLException e){
                System.err.println("Error reading table: " + e.getMessage());
                e.printStackTrace();
            }

        }catch(SQLException e){
            System.err.println("Error getting connection to database: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

}
