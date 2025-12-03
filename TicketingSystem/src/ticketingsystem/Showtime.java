/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ticketingsystem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Showtime {
   private LocalDate date;
   private int count;
   private Movie movie;
   private String showtime;
   
   public Showtime() {}
   
   public Showtime(Movie movie, int year, int month, int day, String showtime){
       date = LocalDate.of(year, month, day);
       this.movie = movie;
       this.showtime = showtime;
   }
   //GETTERS
   //-----------------------------------------------------------------------------------------------
   public LocalDate getDate(){
       return date;
   }
   
   public int getCount(){
       return count;
   }
   
   public String getShowtime(){
       return showtime;
   }
   
   public String getMovieName(){
       return movie.getMovieName();
   }
   //-----------------------------------------------------------------------------------------------
   
   
   //SETTERS
   //-----------------------------------------------------------------------------------------------
   public void setDate(LocalDate date){
       this.date = date;
   }
   
   public void setShowtime(String showtime){
       this.showtime = showtime;
   }
   //-----------------------------------------------------------------------------------------------
   
   public List<LocalTime> generateShowtimes(LocalTime startTime, LocalTime endTime, int intervalMinutes) {
        List<LocalTime> showtimes = new ArrayList<>();
        LocalTime currentTime = startTime;

        while (currentTime.isBefore(endTime)) {
            showtimes.add(currentTime);
            currentTime = currentTime.plusMinutes(intervalMinutes);
        }

        return showtimes;
    }

   
public String toString(){
    return  movie +
            "\nShowtime Date   : " + date +
            "\nShowtime        : " + showtime;
}

}
