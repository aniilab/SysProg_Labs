import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexAnalyzer {
    private final static String number = "\\b\\d+|\\b\\d+.\\d+|\\b\\d+e\\d+|0[xX][0-9a-fA-F]+";
    private final static String identifier = "^([a-zA-Z_$])([a-zA-Z_$0-9])*$";
    private final static String reserved = "abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|String|string|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while";
    private final static String operator = "(\\+|-|\\*|/|=|%|\\+\\+|--|==|!=|>|<|>=|<=|&|\\||^|~|<<|>>|>>>|&&|\\|\\||!|\\+=|-=|\\*=|/=|%=)";
    private final static String punctuator = "([()\\[\\]{},:;.])";

    private final ArrayList<Data> data;
    private boolean inComment = false;

    public LexAnalyzer(File file) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(file);
        data = new ArrayList<>();
        while (scanner.hasNextLine())
        {
            readLine(scanner.nextLine());
        }
    }
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for (Data data : data)
        {
            builder.append(data.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
    private int matchString(String pattern, String string, int index)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (m.find(index))
        {
            return m.end();
        } else
            return -1;
    }
    private void readLine(String line)
    {
        int i = 0;
        int index;
        while (i < line.length())
        {
            while (line.substring(i, i + 1).matches("\\s"))
            {
                if (i == line.length() - 1)
                {
                    break;
                }
                i++;
            }
            //якщо текст у коментарі - знаходимо кінець і позначаємо "поза коментом"
            if (inComment)
            {
                if ((index = matchString("\\*/", line, i)) != -1)
                {
                    i = index;
                    inComment = false;
                } else
                    break;
            //якщо текст не у коментарі - перевіряємо чи взагалі є відкриття коментаря,
            //і якщо закривається у цій же стрічці - пропускаємо вміст та закриваємо коментар, а якщо ні - то заходимо в коментар
            } else if (i < line.length() - 1 && line.substring(i, i + 2).matches("/\\*"))
            {
                if ((index = matchString("\\*/", line, i)) != -1)
                {
                    i = index - 1;
                } else {
                    inComment = true;
                    break;
                }
            //так само перевіряємо коментар на одну стрічку
            } else if (i < line.length() - 1 && line.substring(i, i + 2).matches("//"))
            {
                break;
            //перевірка на стрінг
            } else if (line.substring(i, i + 1).matches("\""))
            {
                if ((index = matchString("\"", line, i + 1)) != -1)
                {
                    data.add(new Data(line.substring(i, index), DataType.STRING_CONSTANT));
                    i = index - 1;
                } else {
                    data.add(new Data(line.substring(i), DataType.ERROR));
                    break;
                }
            //перевірка на символьну константу
            } else if (line.substring(i, i + 1).matches("'"))
            {
                if ((index = matchString("'", line, i + 1)) != -1)
                {
                    if (line.substring(i, index).matches("'([^'\\\\\\n]|\\\\.)'"))
                    {
                        data.add(new Data(line.substring(i, index), DataType.CHAR_CONSTANT));
                        i = index - 1;
                    } else
                    {
                        data.add(new Data(line.substring(i), DataType.ERROR));
                        break;
                    }
                } else
                {
                    data.add(new Data(line.substring(i), DataType.ERROR));
                    break;
                }
            //перевірка на розділовий знак
            } else if (line.substring(i, i + 1).matches(punctuator))
            {
                data.add(new Data(line.substring(i, i + 1), DataType.PUNCTUATOR));
            //перевірка на оператор (1,2 або 3-х символьний
            } else if (line.substring(i, i + 1).matches(operator))
            {
                String op = line.substring(i, i + 1);
                if ((i < line.length() - 1) && line.substring(i, i + 2).matches(operator))
                {
                    op = line.substring(i, i + 2);
                    if ((i < line.length() - 2) && line.substring(i, i + 3).matches(operator))
                    {
                        op = line.substring(i, i + 3);
                    }
                }
                i += op.length() - 1;
                data.add(new Data(op, DataType.OPERATOR));
            } else
            {
                index = i;
                while (i < line.length() && !line.substring(i, i + 1).matches("\\s") &&
                        !line.substring(i, i + 1).matches(punctuator) && !line.substring(i, i + 1).matches(operator)) {
                    i++;
                }

                String word = line.substring(index, i);
                i--;

                if (word.matches(number))
                    data.add(new Data(word, DataType.NUMBER));
                else if (word.matches(reserved))
                    data.add(new Data(word, DataType.RESERVED_WORD));
                else if (word.matches(identifier))
                    data.add(new Data(word, DataType.IDENTIFIER));
                else
                    data.add(new Data(word, DataType.ERROR));
            }
            i++;
        }
    }
}