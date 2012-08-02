package org.espresso.token;

import org.espresso.FunctionExtension;
import org.espresso.SqlNodeVisitor;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * Represents a Date object.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class SqlDate<E> implements SqlExpressionNode<E> {
    private final String dateString;
    private Date convertedDate = null;

    /**
     * Given the column name, creates the column
     * @param dateString the name of the column
     * @throws IllegalArgumentException if the column name is null
     */
    public SqlDate(final String dateString) {
        if (null == dateString)
            throw new IllegalArgumentException("SqlColumn: column name cannot be null");
        this.dateString = dateString.replace('-', '/');
    }

    /**
     * Accessor to the column name
     * @return the column name
     */
    public String getDateString() {
        return dateString.substring(1, dateString.length()-1);
    }

    /**
     * @return the column name
     */
    @Override
    public String toString() {
        return "toDate(" + dateString + ")";
    }

    /**
     * Returns the date wrapped by this object.
     *
     * @param row Reference to the current object
     * @param functions Function extensions, passed down the expression tree
     * @return the date object
     * @throws SQLException wraps all types of errors that may happen
     */
    @Override
    public Object eval(final E row, final Map<String, FunctionExtension> functions) throws SQLException {
        if (null != convertedDate)
            return convertedDate;
        final FunctionExtension dateConverter = functions.get("toDate");
        if (null == dateConverter)
            throw new SQLException("Missing toDate() extension to handle date conversions");
        try {
            convertedDate = (Date) dateConverter.invoke(getDateString());
            return convertedDate;
        } catch (IllegalAccessException e) {
            throw new SQLException("toDate() extension is not public", e);
        } catch (InvocationTargetException e) {
            throw new SQLException("Could not invoke toDate()", e);
        }
    }

    /**
     * Accept method for the visitor pattern. Call pre-, then visit, then post-
     * to give the visitor a chance to push/pop state associated with recursion.
     * @param visitor the visitor to this class.
     */
    @Override
    public void accept(final SqlNodeVisitor<E> visitor) throws SQLException {
        visitor.visit(this);
    }

}