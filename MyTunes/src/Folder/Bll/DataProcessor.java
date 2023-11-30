package Folder.Bll;

public class DataProcessor {
    public String personalizedMessage(String age, String name){
        if (Integer.parseInt(age) > 0){
            return "Greetings " + name + " and may i bid you good day";
        }
        else{
            return "the age you have entered is not a valid number";
        }
    }
}
