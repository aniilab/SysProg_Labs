import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;

public class LexAnalyzer {
    private final List<String> reserved = Arrays.asList(
            "abstract","assert","boolean","break","byte","case","catch","char","class","const",
            "continue","default","do","double","else","enum","extends","final","finally",
            "float","for","goto","if","implements","import","instanceof", "int", "interface","long",
            "native","new","package","private","protected","public","return","short","static",
            "strictfp","String","string","super","switch","synchronized","this","throw","throws",
            "transient","try","void","volatile","while"
    );
    private final List<String> operators = Arrays.asList(
            ">",">=","&","&&","|", "||","<", "<=","=","==","!", "!=","<<",">>",
            "*","*=","%","%=","/","/=","+","++", "+=","-","-=", "--","}", ";"
    );
    private final List<String> punctuators = Arrays.asList(
            "(",")","{","}","[", "]",",", "."
    );
    private final List<Data> data = new ArrayList<Data>();
    private boolean inComment = false;

    public LexAnalyzer(File file) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            startState(line);
        }
    }
    public void showResult()
    {
        for (Data d : data)
        {
            System.out.println(d.toString() + "\n");
        }
    }

    public void startState(String s0)
    {
        if (!inComment)
            LineState(s0);
        else
            CommentState(s0);
    }
    private void CommentState(String line)
    {
        for (int i = 0; i < line.length() - 1; i++)
        {
            if (line.charAt(i) == '*' && line.charAt(i + 1) == '/')
            {
                inComment = false;
                LineState(line.substring(i + 2));
                break;
            }
        }
    }


    private void QuoteState(String line) {
        StringBuilder word = new StringBuilder();
        word.append('\"');
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '\"') {
                word.append('\"');
                Data d = new Data(word.toString(), DataType.STRING_CONSTANT);
                data.add(d);
                LineState(line.substring(i + 1));
                return;
            } else {
                word.append(line.charAt(i));
            }
        }
        Data d = new Data(word.toString(), DataType.ERROR);
        data.add(d);
    }

    private void NumberState(String line)
    {
        StringBuilder word = new StringBuilder();
        Data d;
        for (int i = 0; i < line.length(); i++)
        {
            if (Character.isDigit(line.charAt(i)) || Character.isAlphabetic(line.charAt(i)) || line.charAt(i) == '.')
            {
                word.append(line.charAt(i));
            }
            else
            {
                String w = word.toString();
                if(Check_Number(w))
                    d = new Data(w, DataType.NUMBER);
                else
                    d = new Data(w, DataType.ERROR);
                data.add(d);
                LineState(line.substring(i));
                return;
            }
        }
        String w = word.toString();
        if(Check_Number(w))
            d = new Data(w, DataType.NUMBER);
        else
            d = new Data(w, DataType.ERROR);
        data.add(d);
    }

    private void LineState(String l)
    {
        String line = l.trim();
        int length = line.length();
        for (int i = 0; i < length; i++) {
            if (line.charAt(i) == '/' && i != length - 1 && line.charAt(i + 1) == '/') {
                return;
            } else if (line.charAt(i) == '/' && i != length - 1 && line.charAt(i + 1) == '*') {
                inComment = true;
                CommentState(line.substring(i));
                return;
            }
            char current = line.charAt(i);
            if (punctuators.contains(Character.toString(current))) {
                Data d = new Data(Character.toString(current), DataType.PUNCTUATOR);
                data.add(d);
            } else if (operators.contains(Character.toString(current))) {
                if(i != length - 1){
                    String op = Character.toString(current);
                    op+= line.charAt(i + 1);
                    if(operators.contains(op)){
                        Data d = new Data(op, DataType.OPERATOR);
                        data.add(d);
                        i++;
                    }
                    else {
                        Data d = new Data(Character.toString(current), DataType.OPERATOR);
                        data.add(d);
                    }
                } else {
                        Data d = new Data(Character.toString(current), DataType.OPERATOR);
                        data.add(d);
                    }
                }
             else if (Character.isDigit(current)) {
                NumberState(line.substring(i));
                return;
            } else if (Character.isLetter(current) || current == '_' || current == '\'') {
                WordState(line.substring(i));
                return;
            } else if(current == '\"'){
                QuoteState(line.substring(i + 1));
                return;
            }else if (Character.isWhitespace(current)) {
                continue;
            } else {
                Data d = new Data(Character.toString(current), DataType.ERROR);
                data.add(d);
                break;
            }
        }
    }

    private void WordState(String line)
    {
        StringBuilder word = new StringBuilder();
        Data d;
        for (int i = 0; i < line.length(); i++)
        {
            if (Character.isAlphabetic(line.charAt(i)) || Character.isDigit(line.charAt(i)) ||
                    line.charAt(i) == '_' || line.charAt(i) == '\'' || line.charAt(i) == '\"' || line.charAt(i)=='\\')
            {
                word.append(line.charAt(i));
            }
            else
            {
                String w = word.toString();
                if (reserved.contains(w))
                    d = new Data(w, DataType.RESERVED_WORD);
                else if ((w.length() == 3 || w.length()==4) && Check_Character(w))
                    d = new Data(w, DataType.CHAR_CONSTANT);
                else if (Check_Identifier(w))
                    d = new Data(w, DataType.IDENTIFIER);
                else
                    d = new Data(w, DataType.ERROR);
                data.add(d);
                LineState(line.substring(i));
                return;
            }
        }
        String w = word.toString();
        if (reserved.contains(w))
            d = new Data(w, DataType.RESERVED_WORD);
        else if (w.charAt(0)=='\'' && (w.length() == 3 || w.length()==4) && Check_Character(w))
            d = new Data(w, DataType.CHAR_CONSTANT);
        else if (Check_Identifier(w))
            d = new Data(w, DataType.IDENTIFIER);
        else
            d = new Data(w, DataType.ERROR);
        data.add(d);
    }

    private boolean Check_Number(String num) {
        if (num == null) {
            return false;
        }
        return Check_Int(num) || Check_Double(num) || Check_16(num);
    }

    private boolean Check_Int(String num) {
        try {
            int i = Integer.parseInt(num);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    private boolean Check_Double(String num) {
        try {
            double d = Double.parseDouble(num);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    private boolean Check_16(String num) {
        if (num.length() > 2)
        {
            if (num.charAt(0) == '0' && (num.charAt(1) == 'x' || num.charAt(1) == 'X'))
            {
                for (int i = 2; i < num.length(); i++)
                {
                    if (!Character.isDigit(num.charAt(i)) && !(num.charAt(i) > 64 && num.charAt(i) < 71)
                            && !(num.charAt(i) > 96 && num.charAt(i) < 103))
                        return false;
                }
                return true;
            }
            return false;
        }
        else return false;
    }
    private boolean Check_Identifier(String str)
    {
        char first = str.charAt(0);
        if(first == '_' || Character.isLetter(first))
        {
            for (int i = 1; i < str.length(); i++)
            {
                if (!isLatinLetter(str.charAt(i)) && !Character.isDigit(str.charAt(i)) && str.charAt(i) != '_' )
                    return false;
            }
            return true;
        }
        else return false;
    }
    private boolean Check_Character(String str) {
        if (str.charAt(1) == '\\' && str.length() == 4 && Character.isAlphabetic(str.charAt(2)) && str.charAt(3) == '\'')
            return true;
        else if (Character.isAlphabetic(str.charAt(1)) && str.length() == 3 && str.charAt(2) == '\'')
            return true;
        else return false;
    }

    private boolean isLatinLetter(char c) {
        return (Character.isLetter(c) && ((c > 64 && c < 91) || (c > 96 && c < 123)));
    }
}
