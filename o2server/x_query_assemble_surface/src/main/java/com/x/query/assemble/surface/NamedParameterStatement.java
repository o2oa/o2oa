package com.x.query.assemble.surface;
/*
http://www.javased.com/index.php?source_dir=monits-commons%2Fsrc%2Fmain%2Fjava%2Fcom%2Fmonits%2Fcommons%2FNamedParameterStatement.java
Copyright 2011 Monits 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * NamedParameterStatement.java
 * 
 * This class wraps around a {@link PreparedStatement} and allows the
 * programmer to set parameters by name instead
 * of by index. This eliminates any confusion as to which parameter index
 * represents what. This also means that
 * rearranging the SQL statement or adding a parameter doesn't involve
 * renumbering your indices.
 * 
 * The original code was modified to wrap other methods of
 * {@link PreparedStatement}
 * 
 * @author adam_crume
 * @link http://www.javaworld.com/
 */
public class NamedParameterStatement {
    /** The statement this object is wrapping. */
    private final PreparedStatement statement;

    /**
     * Maps parameter names to arrays of ints which are the parameter indices.
     */
    private final Map<String, int[]> indexMap;

    /**
     * Creates a NamedParameterStatement. Wraps a call to
     * c.{@link Connection#prepareStatement(java.lang.String)
     * prepareStatement}.
     * 
     * @param connection the database connection
     * @param query      the parameterized query
     * @throws SQLException if the statement could not be created
     */
    public NamedParameterStatement(Connection connection, String query) throws SQLException {
        indexMap = new HashMap<String, int[]>();
        String parsedQuery = parse(query, indexMap);
        statement = connection.prepareStatement(parsedQuery);
    }

    /**
     * Parses a query with named parameters. The parameter-index mappings are
     * put into the map, and the
     * parsed query is returned. DO NOT CALL FROM CLIENT CODE. This
     * method is non-private so JUnit code can
     * test it.
     * 
     * @param query    query to parse
     * @param indexMap map to hold parameter-index mappings
     * @return the parsed query
     */
    /* default */ static final String parse(String query, Map<String, int[]> indexMap) {
        Map<String, List<Integer>> paramMap = new HashMap<String, List<Integer>>();
        int length = query.length();
        StringBuffer parsedQuery = new StringBuffer(length);
        int index = 1;

        for (int i = 0; i < length; i++) {
            char c = query.charAt(i);

            if (c == '\'' || c == '"') {
                // Consume quoted substrings...
                char original = c;
                do {
                    i++;
                    parsedQuery.append(c);
                } while (i < length && (c = query.charAt(i)) != original);
            } else if (c == ':' && i + 1 < length
                    && Character.isJavaIdentifierStart(query.charAt(i + 1))) {

                // Found a placeholder!
                String name = parseParameterName(query, i);
                c = '?'; // replace the parameter with a question mark
                i += name.length(); // skip past the end if the parameter

                List<Integer> indexList = paramMap.get(name);
                if (indexList == null) {
                    indexList = new LinkedList<Integer>();
                    paramMap.put(name, indexList);
                }

                indexList.add(index);

                index++;
            }

            parsedQuery.append(c);
        }

        toIntArrayMap(paramMap, indexMap);

        return parsedQuery.toString();
    }

    /**
     * Parses a name from the given query string starting at the given position.
     * 
     * @param query The query string from which to parse the parameter name
     * @param pos   The position at which it was detected a parameter starts
     * @return The name of the parameter parsed
     */
    private static String parseParameterName(String query, int pos) {
        int j = pos + 2;
        while (j < query.length() && Character.isJavaIdentifierPart(query.charAt(j))) {
            j++;
        }

        return query.substring(pos + 1, j);
    }

    /**
     * Moves all values from a map having a list of ints, to one having an array of
     * ints
     * 
     * @param inMap  The input map, having a list of ints for values.
     * @param outMap The output map, on which to put the same values as an array of
     *               ints.
     */
    private static void toIntArrayMap(Map<String, List<Integer>> inMap,
            Map<String, int[]> outMap) {
// replace the lists of Integer objects with arrays of ints 
        for (Entry<String, List<Integer>> entry : inMap.entrySet()) {
            List<Integer> list = entry.getValue();

            int[] indexes = new int[list.size()];
            int i = 0;
            for (Integer integer : list) {
                indexes[i++] = integer.intValue();
            }

            outMap.put(entry.getKey(), indexes);
        }
    }

    /**
     * Returns the indexes for a parameter.
     * 
     * @param name parameter name
     * @return parameter indexes
     * @throws IllegalArgumentException if the parameter does not exist
     */
    private int[] getIndexes(String name) {
        int[] indexes = indexMap.get(name);
        if (indexes == null) {
            throw new IllegalArgumentException("Parameter not found: " + name);
        }

        return indexes;
    }

    /**
     * Sets a parameter.
     * 
     * @param name  parameter name
     * @param value parameter value
     * @throws SQLException             if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setObject(int, java.lang.Object)
     */
    public void setObject(String name, Object value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setObject(indexes[i], value);
        }
    }

    /**
     * Sets a parameter.
     * 
     * @param name  parameter name
     * @param value parameter value
     * @throws SQLException             if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setString(int, java.lang.String)
     */
    public void setString(String name, String value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setString(indexes[i], value);
        }
    }

    /**
     * Sets a parameter.
     * 
     * @param name  parameter name
     * @param value parameter value
     * @throws SQLException             if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setInt(int, int)
     */
    public void setInt(String name, int value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setInt(indexes[i], value);
        }
    }

    /**
     * Sets a parameter.
     * 
     * @param name  parameter name
     * @param value parameter value
     * @throws SQLException             if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setInt(int, int)
     */
    public void setLong(String name, long value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setLong(indexes[i], value);
        }
    }

    /**
     * Sets a parameter.
     * 
     * @param name  parameter name
     * @param value parameter value
     * @throws SQLException             if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setTimestamp(int, java.sql.Timestamp)
     */
    public void setTimestamp(String name, Timestamp value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setTimestamp(indexes[i], value);
        }
    }

    /**
     * Sets a parameter.
     * 
     * @param name  parameter name
     * @param value parameter value
     * @throws SQLException             if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setTimestamp(int, java.sql.Timestamp)
     */
    public void setDate(String name, Date value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setDate(indexes[i], value);
        }
    }

    /**
     * Returns the underlying statement.
     * 
     * @return the statement
     */
    public PreparedStatement getStatement() {
        return statement;
    }

    /**
     * Executes the statement.
     * 
     * @return true if the first result is a {@link ResultSet}
     * @throws SQLException if an error occurred
     * @see PreparedStatement#execute()
     */
    public boolean execute() throws SQLException {
        return statement.execute();
    }

    /**
     * Executes the statement, which must be a query.
     * 
     * @return the query results
     * @throws SQLException if an error occurred
     * @see PreparedStatement#executeQuery()
     */
    public ResultSet executeQuery() throws SQLException {
        return statement.executeQuery();
    }

    /**
     * Executes the statement, which must be an SQL INSERT, UPDATE or DELETE
     * statement;
     * or an SQL statement that returns nothing, such as a DDL statement.
     * 
     * @return number of rows affected
     * @throws SQLException if an error occurred
     * @see PreparedStatement#executeUpdate()
     */
    public int executeUpdate() throws SQLException {
        return statement.executeUpdate();
    }

    /**
     * Closes the statement.
     * 
     * @throws SQLException if an error occurred
     * @see Statement#close()
     */
    public void close() throws SQLException {
        statement.close();
    }

    /**
     * Adds the current set of parameters as a batch entry.
     * 
     * @throws SQLException if something went wrong
     */
    public void addBatch() throws SQLException {
        statement.addBatch();
    }

    /**
     * Executes all of the batched statements.
     * 
     * See {@link Statement#executeBatch()} for details.
     * 
     * @return update counts for each statement
     * @throws SQLException if something went wrong
     */
    public int[] executeBatch() throws SQLException {
        return statement.executeBatch();
    }
}
