/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;
import java.util.ArrayList;

public class Movie {
    private int id;
    private String movieName;
    private double movieLength;
    private String director;
    private String releaseDate;
    
    public Movie(){}
    
    public Movie(int id, String movieName, double movieLength, String director, String releaseDate){
        this.id = id;
        this.movieName = movieName;
        this.movieLength = movieLength;
        this.director = director;
        this.releaseDate = releaseDate;       
    };
    //GETTERS
    //==========================================================================
    public int getId(){
        return id;
    }
    
    public String getMovieName(){
      return movieName;  
    }
    
    public double getMovieLength(){
        return movieLength;
    }
    
    public String getDirector(){
        return director;
    }
    
    public String releaseDate(){
        return releaseDate;
    }
    //==========================================================================
    
    //SETTERS
    //==========================================================================
    public void setMovieName(String movieName){
       this.movieName = movieName;  
    }
    
    public void setMovieLength(double movieLength){
        this.movieLength = movieLength;
    }
    
    public void setDirector(String director){
        this.director = director;
    }
    
    public void releaseDate(String releaseDate){
        this.releaseDate = releaseDate;
    }
    //==========================================================================
    
    
    @Override
    public String toString(){
      return "\nMovie name: " + movieName + "\nMovie length: " + movieLength + "\nDirector: "  + director + "\nRelease Date: " + releaseDate;
    }
}
