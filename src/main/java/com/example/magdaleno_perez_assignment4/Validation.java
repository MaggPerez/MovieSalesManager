package com.example.magdaleno_perez_assignment4;
//I certify that this submission is my original work - Magdaleno Perez

public class Validation{
    static String titleMessage;
    static String yearMessage;
    static String salesMessage;


    //**********************************************************
    // Validating Title
    //**********************************************************

    /**
     * validateTitle does 3 things:
     * - If title is empty, alert message pops up saying that it can't be empty
     * - If title validates the regular expression, its message is empty, meaning it passes.
     * - Otherwise alert message shows the invalid input
     */
    public static String validateTitle(String title){
        if(title.isEmpty()){
            Validation.titleMessage = "Title cannot be empty";
        }
        else if(title.matches("[A-Z][A-Za-z0-9-:. ]*")){
            titleMessage = " ";
            //this worked
        }
        else{
            titleMessage = "Title must contain letters, numbers, hyphens, colons, periods, and " +
                "spaces. The first letter must be capitalized.";
        }

        return titleMessage;
    }





    //**********************************************************
    // Validating Year
    //**********************************************************
    /**
     * validateYear does 3 things:
     * - If year is empty, alert message pops up saying that it can't be empty
     * - If year validates the regular expression, its message is empty, meaning it passed.
     * - Otherwise alert message shows the invalid input and 4 digits are needed
     */
    public static String validateYear(String year){
        if(year.isEmpty()){
            Validation.yearMessage = "Year cannot be empty";
        }
        else if(year.matches("\\d{4}")){
            yearMessage = " ";
        }
        else{
            yearMessage = "Year can only contains 4 digits.";

        }

        return yearMessage;
    }




    //**********************************************************
    // Validating Sales
    //**********************************************************
    /**
     * validateSales does 3 things:
     * - If sales is empty, alert message pops up saying that it can't be empty
     * - If sales validates the regular expression, its message is empty, meaning it passed.
     * - Otherwise alert message shows the invalid input
     */
    public static String validateSales(String sales){
        if(sales.isEmpty()){
            Validation.salesMessage = "Sales cannot be empty";
        }
        else if(sales.matches("[0-9]+(\\.\\d+)?")){
            salesMessage = " ";
        }
        else{
            salesMessage = "Sales can only contain digits. The decimal point is optional." +
            " If the decimal point is included there must be at least one number before " +
            "and at least one number after it.";

        }

        return salesMessage;
    }

}
