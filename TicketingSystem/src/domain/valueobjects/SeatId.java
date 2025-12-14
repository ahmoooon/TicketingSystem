/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain.valueobjects;

import java.util.Objects;

public class SeatId {
    
    private final char row;
    private final int column;
    
    public SeatId(char row, int column) {
        // Validation logic is encapsulated here
        if (row < 'A' || row > 'Z') {
            throw new IllegalArgumentException("Invalid seat row: " + row);
        }
        if (column < 1) {
            throw new IllegalArgumentException("Invalid seat column: " + column);
        }
        
        this.row = row;
        this.column = column;
    }
    
    public char getRow() { return row; }
    public int getColumn() { return column; }
    
    public String toDisplayString() {
        return String.valueOf(row) + column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatId seatId = (SeatId) o;
        return row == seatId.row && column == seatId.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
    
    @Override
    public String toString() {
        return toDisplayString();
    }
}
