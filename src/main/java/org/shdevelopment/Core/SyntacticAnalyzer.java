package org.shdevelopment.Core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntacticAnalyzer {
    
    public static String executeAnalyzers(String string){
        return analyzeURLs(string);
    }
    
    private static String analyzeURLs(String string){
      String input = string;
      String regex = "(https:\\/{2}|http:\\/{2})?([w]{3}\\.)?([a-zA-Z0-9_]*)(\\.[a-zA-Z]+)+";
      Pattern r = Pattern.compile(regex);
      Matcher m = r.matcher(input);
      
      return m.replaceAll("<a href=$0>$0</a>");
    }
}
