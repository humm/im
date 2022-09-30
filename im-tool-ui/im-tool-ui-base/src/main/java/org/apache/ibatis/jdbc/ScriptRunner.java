//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.ibatis.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 源码覆盖
 *
 * @param null
 * @author: humm23693
 * @date: 2022-09-29
 * @return:
 */
public class ScriptRunner {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    private static final String DEFAULT_DELIMITER = ";";
    private static final Pattern DELIMITER_PATTERN = Pattern.compile("^\\s*((--)|(//))?\\s*(//)?\\s*@DELIMITER\\s+([^\\s]+)", 2);
    private final Connection connection;
    private boolean stopOnError;
    private boolean throwWarning;
    private boolean autoCommit;
    private boolean sendFullScript;
    private boolean removeCRs;
    private boolean escapeProcessing = true;
    private PrintWriter logWriter;
    private PrintWriter errorLogWriter;
    private String delimiter;
    private boolean fullLineDelimiter;

    public ScriptRunner(Connection connection) {
        this.logWriter = new PrintWriter(System.out);
        this.errorLogWriter = new PrintWriter(System.err);
        this.delimiter = ";";
        this.connection = connection;
    }

    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }

    public void setThrowWarning(boolean throwWarning) {
        this.throwWarning = throwWarning;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setSendFullScript(boolean sendFullScript) {
        this.sendFullScript = sendFullScript;
    }

    public void setRemoveCRs(boolean removeCRs) {
        this.removeCRs = removeCRs;
    }

    public void setEscapeProcessing(boolean escapeProcessing) {
        this.escapeProcessing = escapeProcessing;
    }

    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public void setErrorLogWriter(PrintWriter errorLogWriter) {
        this.errorLogWriter = errorLogWriter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setFullLineDelimiter(boolean fullLineDelimiter) {
        this.fullLineDelimiter = fullLineDelimiter;
    }

    public void runScript(Reader reader) throws Exception {
        this.setAutoCommit();

        try {
            if (this.sendFullScript) {
                this.executeFullScript(reader);
            } else {
                this.executeLineByLine(reader);
            }
        } finally {
            this.rollbackConnection();
        }

    }

    /**
     * 覆盖源码  抛异常
     *
     * @param reader
     * @author: humm23693
     * @date: 2022-09-27
     * @return: void
     */
    private void executeFullScript(Reader reader) throws Exception {
        StringBuilder script = new StringBuilder();

        String line;
        BufferedReader lineReader = new BufferedReader(reader);

        while((line = lineReader.readLine()) != null) {
            script.append(line);
            script.append(LINE_SEPARATOR);
        }

        String command = script.toString();
        this.println(command);
        this.executeStatement(command);
        this.commitConnection();
    }

    /**
     * 覆盖源码  抛异常
     *
     * @param reader
     * @author: humm23693
     * @date: 2022-09-27
     * @return: void
     */
    private void executeLineByLine(Reader reader) throws Exception {
        StringBuilder command = new StringBuilder();

        String line;
        BufferedReader lineReader = new BufferedReader(reader);

        while((line = lineReader.readLine()) != null) {
            this.handleLine(command, line);
        }

        this.commitConnection();
        this.checkForMissingLineTerminator(command);
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (Exception var2) {
        }

    }

    private void setAutoCommit() {
        try {
            if (this.autoCommit != this.connection.getAutoCommit()) {
                this.connection.setAutoCommit(this.autoCommit);
            }

        } catch (Throwable var2) {
            throw new RuntimeSqlException("Could not set AutoCommit to " + this.autoCommit + ". Cause: " + var2, var2);
        }
    }

    private void commitConnection() {
        try {
            if (!this.connection.getAutoCommit()) {
                this.connection.commit();
            }

        } catch (Throwable var2) {
            throw new RuntimeSqlException("Could not commit transaction. Cause: " + var2, var2);
        }
    }

    private void rollbackConnection() {
        try {
            if (!this.connection.getAutoCommit()) {
                this.connection.rollback();
            }
        } catch (Throwable var2) {
        }

    }

    private void checkForMissingLineTerminator(StringBuilder command) {
        if (command != null && command.toString().trim().length() > 0) {
            throw new RuntimeSqlException("Line missing end-of-line terminator (" + this.delimiter + ") => " + command);
        }
    }

    private void handleLine(StringBuilder command, String line) throws Exception {
        String trimmedLine = line.trim();
        if (this.lineIsComment(trimmedLine)) {
            Matcher matcher = DELIMITER_PATTERN.matcher(trimmedLine);
            if (matcher.find()) {
                this.delimiter = matcher.group(5);
            }

            this.println(trimmedLine);
        } else if (this.commandReadyToExecute(trimmedLine)) {
            command.append(line.substring(0, line.lastIndexOf(this.delimiter)));
            command.append(LINE_SEPARATOR);
            this.println(command);
            this.executeStatement(command.toString());
            command.setLength(0);
        } else if (trimmedLine.length() > 0) {
            command.append(line);
            command.append(LINE_SEPARATOR);
        }

    }

    private boolean lineIsComment(String trimmedLine) {
        return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
    }

    private boolean commandReadyToExecute(String trimmedLine) {
        return !this.fullLineDelimiter && trimmedLine.contains(this.delimiter) || this.fullLineDelimiter && trimmedLine.equals(this.delimiter);
    }

    /**
     * 覆盖源码  抛异常
     *
     * @param command
     * @author: humm23693
     * @date: 2022-09-27
     * @return: void
     */
    private void executeStatement(String command) throws Exception {
        boolean hasResults = false;
        Statement statement = this.connection.createStatement();
        statement.setEscapeProcessing(this.escapeProcessing);
        String sql = command;
        if (this.removeCRs) {
            sql = command.replaceAll("\r\n", "\n");
        }

        if (this.stopOnError) {
            hasResults = statement.execute(sql);
            if (this.throwWarning) {
                SQLWarning warning = statement.getWarnings();
                if (warning != null) {
                    throw warning;
                }
            }
        } else {
            hasResults = statement.execute(sql);
        }

        this.printResults(statement, hasResults);

        try {
            statement.close();
        } catch (Exception var7) {
        }

    }

    private void printResults(Statement statement, boolean hasResults) {
        try {
            if (hasResults) {
                ResultSet rs = statement.getResultSet();
                if (rs != null) {
                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();

                    int i;
                    String value;
                    for(i = 0; i < cols; ++i) {
                        value = md.getColumnLabel(i + 1);
                        this.print(value + "\t");
                    }

                    this.println("");

                    while(rs.next()) {
                        for(i = 0; i < cols; ++i) {
                            value = rs.getString(i + 1);
                            this.print(value + "\t");
                        }

                        this.println("");
                    }
                }
            }
        } catch (SQLException var8) {
            this.printlnError("Error printing results: " + var8.getMessage());
        }

    }

    private void print(Object o) {
        if (this.logWriter != null) {
            this.logWriter.print(o);
            this.logWriter.flush();
        }

    }

    private void println(Object o) {
        if (this.logWriter != null) {
            this.logWriter.println(o);
            this.logWriter.flush();
        }

    }

    private void printlnError(Object o) {
        if (this.errorLogWriter != null) {
            this.errorLogWriter.println(o);
            this.errorLogWriter.flush();
        }

    }
}