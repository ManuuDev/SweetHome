package Test;

import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.shdevelopment.Core.SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyntacticAnalyzerTests {

    String[] prefixes = {"www.","http://www.","https://www.","http://","https://"};

    String formatToURL(String webpage){
        return "<a href=%s>%s</a>".replaceAll("%s", webpage);
    }

    List<Pair<String, String>> getTuples(String domain){

        List<String> l1 = getAllPossibleFormats(domain);
        List<String> l2 = getExpectedFormats(domain);

        List<Pair<String, String>> listOfTuples = new ArrayList<>();

        for(int i = 0; i < Math.min(l1.size(), l2.size()); i++){
            listOfTuples.add(new Pair<>(l1.get(i), l2.get(i)));
        }

        return listOfTuples;
    }

    List<String> getAllPossibleFormats(String domain){
        List<String> list = new ArrayList<>();
        String[] prefixes = {"www.","http://www.","https://www.","http://","https://"};
        Arrays.stream(prefixes).forEach(x -> list.add(x.concat(domain)));
        return list;
    }

    List<String> getExpectedFormats(String domain){
        List<String> list = new ArrayList<>();
        Arrays.stream(prefixes).forEach(x -> list.add(formatToURL(x.concat(domain))));
        return list;
    }

    @Test
    public void wellFormedTest1(){
        String domainName = "google.com.ar";
        List<Pair<String, String>> list = getTuples(domainName);
        list.forEach(x -> Assert.assertEquals(x.getValue(), SyntacticAnalyzer.executeAnalyzers(x.getKey())));
    }


    @Test
    public void wellFormedTest2(){
        String domainName = "google.com";
        List<Pair<String, String>> list = getTuples(domainName);
        list.forEach(x -> Assert.assertEquals(x.getValue(), SyntacticAnalyzer.executeAnalyzers(x.getKey())));
    }

    @Test
    public void wellFormedTest3(){
        String domainName = "google.net";
        List<Pair<String, String>> list = getTuples(domainName);
        list.forEach(x -> Assert.assertEquals(x.getValue(), SyntacticAnalyzer.executeAnalyzers(x.getKey())));
    }

    @Test
    public void wellFormedTest4(){
        String domainName = "othername.large.dom.ar";
        List<Pair<String, String>> list = getTuples(domainName);
        list.forEach(x -> Assert.assertEquals(x.getValue(), SyntacticAnalyzer.executeAnalyzers(x.getKey())));
    }

    @Test
    public void badURLTest1(){
        String someText = "configuraron.Sigue";
        Assert.assertEquals(someText, SyntacticAnalyzer.executeAnalyzers(someText));
    }

    @Test
    public void badURLTest2(){
        String someText = "www.something";
        Assert.assertEquals(someText, SyntacticAnalyzer.executeAnalyzers(someText));
    }

    @Test
    public void badURLTest3(){
        String someText = "three.words.line";
        Assert.assertEquals(someText, SyntacticAnalyzer.executeAnalyzers(someText));
    }
}
